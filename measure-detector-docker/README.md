# Building and Running the MeasureDetector

The second way to run inference is by firing up an inference server that exposes a REST API for easy consumption.

To do so, start the server script with hug -p=8080 -f=inference_server.py (see also run_server.bat) or create and run a docker container with the following steps:

	# Build the image with the latest model
	$ docker build -t measure_detector .

	# Run in container (change port to `XXXX:8080` if needed):
	$ docker run -p 8080:8080 measure_detector
