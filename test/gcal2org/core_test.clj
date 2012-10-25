(ns gcal2org.core-test
  (:use midje.sweet
        gcal2org.core))

(fact "A date with time is correctly formatted."
      (event-date-time->org-date {"dateTime" "2012-10-03T16:00:00.000+02:00"})
      => "<2012-10-03 16:00>")

(fact "A date without time is correctly formatted."
      (event-date-time->org-date {"date" "2012-10-03"})
      => "<2012-10-03>")