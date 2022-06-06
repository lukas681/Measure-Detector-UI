# Measure-Detector-UI

Contains a Front-End for the Measure Detector developed along an Interdisciplinary Project in cooperation with the LMU and TU in Munich.
 
This application was generated using JHipster 7.4.0, you can find documentation and help at [https://www.jhipster.tech/documentation-archive/v7.4.0](https://www.jhipster.tech/documentation-archive/v7.4.0).

## Setting-up this repository
  
1) You might need to login with your LRZ Credentials as the project is not entirely public.

       git clone https://gitlab.lrz.de/ge82xib/measure-detector-ui

2) Make sure to have Docker installed. For Windows you can also use WSL 2 to get a nice Docker environment. Now build the MeasureDetector. This works also on Windows using Docker for Windows. If you have Make installed, then you can also use the provided Makefile. Adjust the port to your needs. Standard is 8181 where it is going to listen to incoming requests.


      $ cd measure-detector-docker
      $ make build-docker

   Otherwise, just build the container manually:


      $ cd measure-detector-docker

      # Build the image with the latest model
	$ docker build --no-cache --tag mdet:1 -t measure_detector .

      # Run in container (change port to `XXXX:8080` if needed):
	$ docker run --name mdet -d -p ${PORT}:8080 measure_detector
   
3) (Optional) Test the REST interface:

       curl --location --request POST 'localhost:8080/upload' --form 'image=@"path/to/example/image"'

# Necessary Configurations

You need to follow these steps, to set up the application correctly:

* By now, the upload mechanism is not implemented optimally. Therefore, you have to set the max-recieving filelength manually, which depends on your available RAM.

In my case, I have around 16GB RAM on my Computer, so I can without troubles set it to 5GB (whcih would support score files size to 5GB). Therefore, open application.yml (application-prod, application-dev, resp.) and change the following line:


      spring:
         codec:
            max-in-memory-size: 5GB

# Quick Start

In order to run the whole application inside a docker container, promt the following: Adjust the path to the cloned repository, as relative paths are not supported any more.

      docker run -v /mnt/wsl/docker-desktop-bind-mounts/Ubuntu-20.04/5b34cac1f4eceeed64b176538b277248cab7ff01961f27cb589fd8c218e73d03/measure-detector-ui/app:/home/jhipster/app --rm jhipster/jhipster ./gradlew bootRun -Pdev

Voila, the application should already start ...
# Starting Development Mode

For developing it might makes sense to spawn npm in a second process.
      
       .\gradlew -Pdev
       # wait for finish
       npm i --save-dev @types/openseadragon@2.4.8
       npm start

       --------------------------------------
       Local: http://localhost:9000
       External: http://192.168.2.148:9000
       --------------------------------------
       UI: http://localhost:3001
       UI External: http://localhost:3001
        --------------------------------------

# Running in Productive Mode

Currently, the productive mode is conigured to use a local mysql instance. Observe the corresponing line in the application.yml:

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

| Name  	|   Version 	|   Required	|
|---	|---	|---	|
|  NodeJS 	|  14 	|  yes 	|
|  Docker 	|  	|  yes 	|
|  TensorFlow 	|  1.13.1 	|   via gradle	|

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