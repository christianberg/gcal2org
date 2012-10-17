(defproject gcal2org "0.1.0-SNAPSHOT"
  :description "Export Google Calendar events to Emacs Org mode."
  :url "http://github.com/christianberg/gcal2org"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :aot [gcal2org.core]
  :main gcal2org.core
  :dependencies [[org.clojure/clojure "1.4.0"]
                 [com.google.apis/google-api-services-calendar "v3-rev16-1.8.0-beta"]
                 [com.google.http-client/google-http-client-jackson2 "1.11.0-beta"]
                 [com.google.oauth-client/google-oauth-client-jetty "1.11.0-beta"]]
  :repositories [["google-api-services" "http://mavenrepo.google-api-java-client.googlecode.com/hg"]])
