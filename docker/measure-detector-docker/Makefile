# Makefile for managing the Measure Detector
#
PORT=8181

build-docker:
	docker build --no-cache --tag measure_detector:1 -t measure_detector .
	docker run --name mdet -d -p ${PORT}:8080 measure_detector:1

stop-docker:
	docker stop mdet
remove-container:
	docker stop mdet
	docker rm mdet

debug:
	@echo 'The script works!'
