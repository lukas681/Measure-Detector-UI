# Measure Detector and Docker

This document explains how to setup the Measure Detector together with Docker (and WSL) 2. So make sure to have the `Windows Subsystem for Linux` installed on your system, if you are working on a Windows OS. Otherwise, you just need the Docker engine. 

**Installed Requirements**: 

* Docker 
* Make

All the required files can be found under the folder `measure-detector-docker/`. 

## Building and Starting the Container

We are using a Makefile to simplfy the process of managing the container instance. You can set some properties there

* PORT: The port on which the measure detector will listen on.

        make build-docker
