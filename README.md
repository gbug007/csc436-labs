Lab1 is gone for some reason.



\### Project Notes



The architecture is standard as we learned in class for Retrofit, although I put the logic for logging in in a separate LaunchedEffect because I was having issues getting it to properly register logging in and out. There's one ViewModel, a few data classes in separate files, one repository, and one service.



Here's a basic list of features I have:

* Log in/Sign up page
* Sign up fields plus talking to backend
* Account information and page refresh (for both incidents and forms)
* Incidents page
* Forms page for each incident



Possible future expansions:

* Formatting for forms
* Support for adding incidents
* Support for adding forms
* Support for removing forms (requiring proper authorization)
