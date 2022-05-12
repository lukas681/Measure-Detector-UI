# Measure-Detector-UI

Contains a Front-End for the Measure Detector developed along an Interdisciplinary Project in cooperation with the LMU and TU in Munich.
 
This application was generated using JHipster 7.4.0, you can find documentation and help at [https://www.jhipster.tech/documentation-archive/v7.4.0](https://www.jhipster.tech/documentation-archive/v7.4.0).

## Setting-up this repository
  
1) You might need to login with your LRZ Credentials as the project is not entirely public.

       git clone https://gitlab.lrz.de/ge82xib/measure-detector-ui

2) Make sure to have Docker installed. For Windows you can also use WSL 2 to get a nice Docker environment. Now build the MeasureDetector. This works also on Windows using Docker for Windows.


      $ cd measure-detector-docker

      # Build the image with the latest model
      $ docker build -t measure_detector .

      # Run in container (change port to `XXXX:8080` if needed):
      $ docker run -p 8080:8080 measure_detector
   
3) (Optional) Test the REST interface:

       curl --location --request POST 'localhost:8080/upload' --form 'image=@"path/to/example/image"'
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
# Interfaces

You have access to the following interfaces acompanying the application:

* Swagger: Login -> Administration/Api
* JobRunR (Tracks the background jobs) -> :8000
* Main Application ->

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

## Further links

[node.js]: https://nodejs.org/
[npm]: https://www.npmjs.com/
[webpack]: https://webpack.github.io/
[browsersync]: https://www.browsersync.io/
[jest]: https://facebook.github.io/jest/
[leaflet]: https://leafletjs.com/
[definitelytyped]: https://definitelytyped.org/
[angular cli]: https://cli.angular.io/