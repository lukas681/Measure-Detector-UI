FROM tensorflow/tensorflow:1.13.1-py3

RUN apt-get update && apt-get install -y jq curl

RUN pip3 install pillow hug
RUN mkdir -p /usr/src/app

WORKDIR /usr/src/app
COPY inference_server.py .

# Get latest model

RUN curl -L https://github.com/OMR-Research/MeasureDetector/releases/download/v1.0/2019-05-16_faster-rcnn-inception-resnet-v2.pb --output model.pb

EXPOSE 8080
CMD ["hug", "-p=8080", "-f=inference_server.py"]