# Measure-Detector-UI

Contains a front-end the Measure Detector developed in an interdisciplinary Project in cooperation with the LMU and TU in Munich.
 
This application was generated using JHipster 7.4.0. You can find documentation and help at [https://www.jhipster.tech/documentation-archive/v7.4.0](https://www.jhipster.tech/documentation-archive/v7.4.0).

## Setting-up this repository
  
1) Clone the repository

       git clone https://github.com/lukas681/Measure-Detector-UI

2) Make sure to have Docker installed. WSL 2 + Docker for Windows recommended. Now build the Measure Detector Docker Container with the following commands. If you have "Make" installed, then you can also use the provided Makefile where you can adjust the required ports to your needs. Standard is 8181 where it will listen to incoming requests.


      $ cd measure-detector-docker
      $ make build-docker

   Alternatively, build the container manually:


      $ cd measure-detector-docker
      # Build the image with the latest model
      $ docker build --no-cache --tag mdet:1 -t measure_detector .
      # Run in container (change port to `XXXX:8080` if needed):
      $ docker run --name mdet -d -p ${PORT}:8080 measure_detector
   
3) (Optional) Test the REST interface to the Measure Detector

       curl --location --request POST 'localhost:8080/upload' --form 'image=@"path/to/example/image"'

# Necessary Configurations

Now we need to configure a few things to make the application run correctly.

* Unfortunately, the upload mechanism is not implemented optimally. Therefore, you have to set the max-recieving file length manually. To fully utilze the available RAM, we have to set a parameter.

   If you have for example ~16GB RAM on my Computer, you can savely set it to 5GB (which would support files of size up to 5GB). To do so, open the application.yml file (application-prod, application-dev, resp.) and change the following line:


      spring:
         codec:
            max-in-memory-size: 5GB

# Quick Start

In order to run the whole application inside a docker container, use the following command, but first adjust the path to the cloned repository, as relative paths are not supported by docker any more. We are using the official jhopster image.

      docker run --network="host" -p 8080:8080 -v /mnt/wsl/docker-desktop-bind-mounts/Ubuntu-20.04/5b34cac1f4eceeed64b176538b277248cab7ff01961f27cb589fd8c218e73d03/measure-detector-ui/app:/home/jhipster/app --rm jhipster/jhipster .\gradlew bootRun --args='--spring.profiles.active=dev'


Voila, the application should already start ...

> Note: --network="host" will embedd the hosts localhost inside the container. A safe option would be to consider using a virtual Network


# Starting Development Mode
If you like to start the development mode, we can simply switch the profile to dev. This will load the application-dev.yml profile.

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


Currently, the productive mode is configured to use a local mysql instance, but you can safely also use a local h2 database. Observe the corresponding line in the application.yml:

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