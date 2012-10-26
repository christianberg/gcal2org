(ns gcal2org.test-helper
  (:use midje.sweet
        gcal2org.core)
  (:import (com.google.api.client.auth.oauth2 Credential
                                              CredentialStore)))

(defn credential->map [credential]
  {:access-token (.getAccessToken credential)
   :refresh-token (.getRefreshToken credential)
   :expiration-time (.getExpirationTimeMilliseconds credential)})

(defn update-credential! [credential cred-map]
  (-> credential
      (.setAccessToken (:access-token cred-map))
      (.setRefreshToken (:refresh-token cred-map))
      (.setExpirationTimeMilliseconds (:expiration-time cred-map))))

(defn in-memory-credential-store [initial-value]
  (let [state (atom initial-value)]
    (reify CredentialStore
      (load [this user-id credential]
        (if-let [cred-map (@state user-id)]
          (do
            (update-credential! credential cred-map)
            true)
          false))
      (store [this user-id credential]
        (swap! state assoc user-id (credential->map credential))))))
