(ns gcal2org.core
  (:gen-class)
  (:require [clojure.java.io :as io])
  (:import (com.google.api.client.http.javanet NetHttpTransport)
           (com.google.api.client.json.jackson2 JacksonFactory)
           (com.google.api.client.googleapis.auth.oauth2 GoogleClientSecrets
                                                         GoogleAuthorizationCodeFlow$Builder)
           (com.google.api.client.extensions.java6.auth.oauth2 FileCredentialStore
                                                               AuthorizationCodeInstalledApp)
           (com.google.api.client.extensions.jetty.auth.oauth2 LocalServerReceiver)
           (com.google.api.client.util DateTime)
           (com.google.api.services.calendar CalendarScopes
                                             Calendar$Builder)))

(def http-transport (NetHttpTransport.))

(def json-factory (JacksonFactory.))

(def default-credential-store (FileCredentialStore. (io/file (System/getProperty "user.home")
                                                             ".credentials" "calendar.json")
                                                    json-factory))

(defn authorize [credential-store]
  (let [client-secrets (GoogleClientSecrets/load json-factory
                                                 (io/input-stream
                                                  (io/resource "client_secrets.json")))
        flow (.. (GoogleAuthorizationCodeFlow$Builder. http-transport
                                                       json-factory
                                                       client-secrets
                                                       [CalendarScopes/CALENDAR_READONLY])
                 (setCredentialStore credential-store)
                 build)]
    (..
     (AuthorizationCodeInstalledApp. flow (LocalServerReceiver.))
     (authorize "user"))))

(defn client [credentials]
  (.. (Calendar$Builder. http-transport
                         json-factory
                         credentials)
      (setApplicationName "gcal2org")
      build))

(defn events [calendar-id start end]
  (let [client (client (authorize default-credential-store))]
    (seq
     (get (.. client
              events
              (list calendar-id)
              (setTimeMin (DateTime. start))
              (setTimeMax (DateTime. end))
              (setSingleEvents true)
              execute)
          "items"))))

(defn events-for-year [calendar-id year]
  (let [date-format "%s-01-01T00:00:00Z"
        start (format date-format year)
        end (format date-format (inc year))]
    (events calendar-id start end)))

(defn event-date-time->org-date [datetime]
  (if-let [date (get datetime "date")]
    (str "<" date ">")
    (let [[_ date time] (re-matches #"(\d+-\d+-\d+)T(\d+:\d+).*"
                                    (str (get datetime "dateTime")))]
      (str "<" date " " time ">"))))

(defn timerange [event]
  (let [start (get event "start")
        end (get event "end")]
    (str (event-date-time->org-date start)
         "--"
         (event-date-time->org-date end))))

(defn event->org-entry [event]
  (format "* %s
  %s
  :PROPERTIES:
  :ID: %s
  :LOCATION: %s
  :END:
%s
"
          (get event "summary")
          (timerange event)
          (get event "iCalUID")
          (get event "location")
          (get event "description")))

(defn -main [& args]
  (doseq
      [event (events-for-year "<dummy-calendar-id>" 2012)]
    (print (event->org-entry event)) ))