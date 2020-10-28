(ns todolist-app.test-runner
  (:require
   [doo.runner :refer-macros [doo-tests]]
   [todolist-app.core-test]
   [todolist-app.common-test]))

(enable-console-print!)

(doo-tests 'todolist-app.core-test
           'todolist-app.common-test)
