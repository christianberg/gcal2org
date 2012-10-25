(ns gcal2org.core-test
  (:use midje.sweet
        gcal2org.core))

(fact "A date with time is correctly formatted."
      (event-date-time->org-date {"dateTime" "2012-10-03T16:00:00.000+02:00"})
      => "<2012-10-03 16:00>")

(fact "A date without time is correctly formatted."
      (event-date-time->org-date {"date" "2012-10-03"})
      => "<2012-10-03>")

(fact "A timerange without times is correctly formatted."
      (timerange {"start" {"date" "2012-01-01"}
                  "end"   {"date" "2012-02-02"}})
      => "<2012-01-01>--<2012-02-02>")

(fact "A timerange with times is correctly formatted."
      (timerange {"start" {"dateTime" "2012-01-01T10:00:00.000+02:00"}
                  "end"   {"dateTime" "2012-01-01T11:30:00.000+02:00"}})
      => "<2012-01-01 10:00>--<2012-01-01 11:30>")