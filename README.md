# Measure-Detector-UI

Contains a Front-End for the Measure Detector developed along an Interdisciplinary Project in cooperation with the LMU and TU in Munich.
 
This application was generated using JHipster 7.4.0, you can find documentation and help at [https://www.jhipster.tech/documentation-archive/v7.4.0](https://www.jhipster.tech/documentation-archive/v7.4.0).


# Main Functionalities


* Manage Projects and Editions containing musical context

  <img src="doc/measure-detector-docs/images/editions.png" width="500">
* Automatic Measure Detection with the Measure Detector: https://github.com/OMR-Research/MeasureDetector
* Provides a Front-End for managing the counting of measures of any musical work
* Export into MEI / PDF with Annotations

  <img src="doc/measure-detector-docs/images/generated.png" width="300">

## Setting-up this repository
  
1) Clone the repository: 

       git clone https://github.com/lukas681/Measure-Detector-UI.git 

3) Make sure to have **Docker** installed. For Windows you can also use WSL 2 to get a nice Docker environment. Now build the MeasureDetector. This works also on Windows using Docker for Windows. Use the provided Makefile.

        $ cd docker/measure-detector-docker
        $ make build-docker


   Otherwise, just build the container manually:

        $ cd measure-detector-docker
        # Build the image with the latest model
        $ docker build --no-cache --tag mdet:1 -t measure_detector .

        # Run in container (change port to `XXXX:8080` if needed):
        $ docker run --name mdet -d -p ${PORT}:8080 measure_detector
   
3) (Optional) Test the REST interface:

        curl --location --request POST 'localhost:8080/upload' --form 'image=@"path/to/example/image"'

# Necessary Configurations (Might apply ...)

You need to follow these steps, to set-up the application correctly:

* By now, the upload mechanism is not implemented optimally. Therefore, you have to set the max-recieving filelength manually, which depends on your available RAM.

If you have for example 16GB RAM on your Computer, so I can without troubles set it to 5GB (whcih would support score files size to 5GB). Therefore, open application.yml (application-prod, application-dev, resp.) and change the following line:


      spring:
         codec:
            max-in-memory-size: 5GB

# Quick Start

In order to run the whole application inside a docker container, promt the following: Adjust the path to the cloned repository, as relative paths are not supported any more.

      cd measure-detector-ui/app
      docker run --network="host" -v ${PWD}:/home/jhipster/app --rm jhipster/jhipster .\gradlew bootRun --args='--spring.profiles.active=dev'

Voila, the application should already start ...

> Note: --network="host" will embedd the hosts localhost inside the container. For saver usage consider using a VNetwork


# Starting Development Mode
Start the application manually: Therefore, we just have to call the gradle wrapper which will setup the enire environment.

       cd app
      ./gradlew bootRun --args='--spring.profiles.active=dev'

       # wait for finish
       npm i --save-dev @types/openseadragon@2.4.8
       npm start

       --------------------------------------
       Local: http://localhost:8080
       External: http://192.168.2.148:8080
       --------------------------------------
       UI: http://localhost:3001
       UI External: http://localhost:3001
        --------------------------------------

# Running in Productive Mode

Note: In most cases it is easier to use the development mode (uses a h2 file database). In case you want to deploy the application on a large scale level, consider using this prod profile.
Currently, the productive mode is configured to use a local mysql instance. Observe the corresponding line in the application.yml:

      liquibase:
         contexts: prod
         url: jdbc:mysql://localhost:3306/MeasureDetector?useUnicode=true&characterEncoding=utf8&useSSL=false&useLegacyDatetimeCode=false&serverTimezone=UTC&createDatabaseIfNotExist=true

In order to run against a local h2 database (Should also be sufficient as we do not store large data in the database so far...), just run in development mode.

# Interfaces

You have access to the following interfaces acompanying the application (Standard Ports):

* Swagger: Login -> Administration/Api
* JobRunR (Tracks the background jobs) -> :8000
* Main Application ->
* measure detector listener: 8081

# Software Stack

The following frameworks are used for building this application. In general, **jhipster 7.4.0** was used to generate the backbone of the application

| Name  	      | Version 	 |   Required	|
|--------------|-----------|---	|
| NodeJS 	     | 14 	      |  yes 	|
| Docker 	     | 	         |  yes 	|
| Java         | 	14       |  yes 	|
| TensorFlow 	 | 1.13.1 	  |   via gradle	|

# File Structure

* doc/**: Contains all the required documentation generate with mkdocs which enables simple web rendering of the whole documentation contents
  
* app/**: Contains the whole application

## Building for production

### Packaging as jar

To build the final jar and optimize the MeasureDetector application for production, run:

```
./gradlew -Pprod clean bootJar
```

This will concatenate and minify the client CSS and JavaScript files. It will also modify `index.html` so it references these new files.
To ensure everything worked, run:

## Testing

To launch your application's tests, run:

```
./gradlew test integrationTest jacocoTestReport
```


# Experimental: Setting it up in a Docker Container without any Build Script

This function can be used to really debug all the necessary steps.

     $ docker run ubuntu /bin/bash

     apt update
     apt install gradle 
     cd ~
     git clone https://gitlab.lrz.de/ge82xib/measure-detector-ui # dding TUM Credentials
     cd measure-detector-ui/app     
     chmod +x gradlew 
     ./gradlew
   
## Further links

[node.js]: https://nodejs.org/
[npm]: https://www.npmjs.com/
[webpack]: https://webpack.github.io/
[browsersync]: https://www.browsersync.io/
[jest]: https://facebook.github.io/jest/
[leaflet]: https://leafletjs.com/
[definitelytyped]: https://definitelytyped.org/
[angular cli]: https://cli.angular.io/