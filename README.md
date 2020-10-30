# App to create and manage TODO lists
To run the app:
lein run & lein figwheel

To run the app in dev mode in the repl:\
(go)\
(fig-start)

App is served at http://localhost.com:5000/login

In memory datomic is populated at startup with 2 users and their todo lists. 

To setup datomic dev-local:
https://docs.datomic.com/cloud/dev-local.html \
1) Download [dev-tools](https://cognitect.com/dev-tools)
2) To install the downloaded jar locally: install dev-local with the install script from the zip directory \
   ./install
