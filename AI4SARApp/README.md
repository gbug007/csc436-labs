# Project Notes
I have some more thorough documentation in the functions themselves. This includes a quick project intro, my Figma design, and possible expansions.


## Intro to Project
The architecture is standard as we learned in class for Retrofit, although I put the logic for logging in in a separate LaunchedEffect because I was having issues getting it to properly register logging in and out. There's one ViewModel, a few data classes in separate files, one repository, and one service.



### Here's a basic list of features I have:

* Log in/Sign up page
* Sign up fields plus talking to backend
* Account information and page refresh (for both incidents and forms)
* Incidents page
* Forms page for each incident



### Possible future expansions:

* Formatting for forms
* Support for adding incidents
* Support for adding forms
* Support for removing forms (requiring proper authorization)




## Figma design:
![Sign-In Screen](FigmaProto/SignInScreen.png) ![Evidence Screen](FigmaProto/EvidenceScreen.png) ![Submit Evidence](FigmaProto/EvidenceSubmit.png)


## Features used/Dependencies:
* Retrofit2
* GsonConverterFactory (for Retrofit)
* Backend API from AI4SAR (plus all required Nodejs and npm libraries)
    * link to backend GitHub: [text](https://github.com/iSearch-CalPoly/mobile-backend)


## Final notes:
I'm happy to pass the torch for this project to anyone, but please credit me if you decide to keep working on it.