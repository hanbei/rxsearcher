[![Build Status](https://travis-ci.org/hanbei/rxsearcher.svg?branch=master)](https://travis-ci.org/hanbei/rxsearcher)
[![codecov](https://codecov.io/gh/hanbei/rxsearcher2/branch/master/graph/badge.svg)](https://codecov.io/gh/hanbei/rxsearcher2)
[![codebeat badge](https://codebeat.co/badges/f8fb89de-2ebe-46dd-b491-9093e873a293)](https://codebeat.co/projects/github-com-hanbei-rxsearcher-master)
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/c77615783ddb4d269c6a87d55ec6e01f)](https://www.codacy.com/app/hanbei/rxsearcher?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=hanbei/rxsearcher&amp;utm_campaign=Badge_Grade)


# RxSearcher #

Reactive metasearcher combining different search engines. Using RxJava and Vertx as proof of concept. There are some rules I try to abide to:

* be reactive 
* no state 
* everything is immutable
* don't block

## Assumptions & Goals ##

In addition to the above mentioned rules I have some assumptions and goals regarding the flow and the programming.

* The project is split into several modules that should work independently from each other 
if the do not depdend on each other. Especially not DI container should be assumed. 
* Each searcher and only the searcher is completely responsible for generating urls and parse the responses 
according to different use cases. 
* Every request should be runnable on one thread. Everything else would violate the no state rules. That includes 
caching searcher results. This is not possible here but should be solved via caching the requests to the system.   
* Keep it simple. Don't use fancy libraries to generate code for you (e.g. lombok et.al.). If there is a simple way 
to do it that needs more work, still do it. Don't use reflection or anythings else like a DI-Container to keep 
"configuration" simple but source code complex to understand.
* Keep your dependencies minimal. Learn one api before you assume that it's easier with another.
* Use coverage and static analysis tools to check your code. Minimize the violations and maximize coverage. 

## How do I get set up? ##
This project needs Java 8 or later.  

The project uses gradle to manage the build. So you can use the gradle commands to build it 
from the commandline. If you don't have gradle installed use the gradle wrapper. 
You can also import it into and IDE that supports gradle. 

To run the server start ```de.hanbei.rxsearch.server.VertxServer```.

### Run on heroku ###

Push the repository to a heroku machine and it should start. The necessary Procfile and settings.properties 
are included. The necessary stage task is already configured.

### Run from the command line ###

Execute 
    
    gradlew installDist
  
This copies all dependencies and run scripts to ```server/build/install/server```. Execute 
```server[.bat]``` or ```server.bat``` *(if you are so unfortuante to work on windows)* to 
run the server from the commandline. 

## Contribution guidelines ##

* **Writing tests**

  Yes please. Every push to is built by [wercker](https://app.wercker.com/hanbei/rxsearcher/runs). 
  Codecoverage can be seen on [codecov](https://codecov.io/bb/fschulz/rxsearcher). 
  Static Code Checking service is in evaluation. 
   
* **Contributing**

  Use pull requests for your additions.
