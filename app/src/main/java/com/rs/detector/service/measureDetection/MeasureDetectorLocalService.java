package com.rs.detector.service.measureDetection;

import com.rs.detector.web.api.model.ApiMeasureDetectorResult;
import org.springframework.stereotype.Service;
import org.tensorflow.*;
import org.tensorflow.types.UInt8;
import processing.core.PImage;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import processing.core.*;

@Service
/**
 * This is a very experimental version of the MeasureDetector which currently does not work, yet. Maybe, we can
 * directly embedd the MeasureDetecture in the future, but for a start, we will use an external service to connect
 * to, namely a docker container.
 *
 * Furthermore, an interface is provided to ensure later compatability if we can really switch to an embedded version.
 *
 */
public class MeasureDetectorLocalService implements MeasureDetectorService{

    Path modelPath = Path.of("build/measure-detector-tf-model/md-model.pb");

    private static void bgr2rgb(byte[] data) {
        for (int i = 0; i < data.length; i += 3) {
            byte tmp = data[i];
            data[i] = data[i + 2];
            data[i + 2] = tmp;
        }
    }

    private static Tensor<?> makeImageTensor(BufferedImage img) throws IOException {
//        if (img.getType() == BufferedImage.TYPE_BYTE_INDEXED
//            || img.getType() == BufferedImage.TYPE_BYTE_BINARY
//            || img.getType() == BufferedImage.TYPE_BYTE_GRAY
//            || img.getType() == BufferedImage.TYPE_USHORT_GRAY) {

            BufferedImage bgr = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_3BYTE_BGR);
//            bgr.getGraphics().drawImage(img, 0, 0, null);
//            img = bgr;
//        }
//        if (img.getType() != BufferedImage.TYPE_3BYTE_BGR) {
//            throw new IOException(
//                String.format(
//                    "Expected 3-byte BGR encoding in BufferedImage, found %d. This code could be made more robust",
//                    img.getType()));
//        }
        byte[] data = ((DataBufferByte) bgr.getData().getDataBuffer()).getData();
        // ImageIO.read seems to produce BGR-encoded images, but the model expects RGB.

        bgr2rgb(data);
        final long BATCH_SIZE = 1;
        final long CHANNELS = 3;
        long[] shape = new long[]{BATCH_SIZE, bgr.getHeight(), bgr.getWidth(), CHANNELS};
        System.out.println("Wrapped Data " + ByteBuffer.wrap(data));

        return Tensor.create(UInt8.class, shape, ByteBuffer.wrap(data));
    }

//    public Tensor createTensor(BufferedImage img) {
//        int w = img.getWidth();
//        int h = img.getHeight();
//            for (int i = 0; i < h; i++) {
//                for (int j = 0; j < w; j++) {
//                    imageBytes[i * 3 + 0] = (byte) ((img.pixels[i] >> 16) & 0xff);  // R
//                    imageBytes[i * 3 + 1] = (byte) ((img.pixels[i] >> 8) & 0xff);   // G
//                    imageBytes[i * 3 + 2] = (byte) (img.pixels[i] & 0xff);          // B
//                }
//        }

        // convert image into tensor
//        final long[] shape = {img.height, img.width, 3};
//        return Tensor.create(UInt8.class, shape, ByteBuffer.wrap(imageBytes));
//    }


    @Override
    // TODO Increase computing time by reusing this session!
    //  https://stackoverflow.com/questions/49819047/sess-run-is-too-slow
    // TODO Change signature
    public ApiMeasureDetectorResult process(BufferedImage imgPassed) {

        Tensor inputTensor = null;
        List<Tensor<?>> output = null;

       try(Graph graph = new Graph()) {
           graph.importGraphDef(Files.readAllBytes(modelPath) );
           byte[] pngBytes = Files.readAllBytes(Path.of("build/res/testTitle/split/_243.png"));

           // Opens up the session
           try (Session ses = new Session(graph)) {
               BufferedImage img = null;
               try {
                   img = ImageIO.read(new File("build/res/testTitle/split/_245.png"));
                   System.out.println(img);
               } catch (IOException e) {
                   e.printStackTrace();
               }
               long t = System.currentTimeMillis();


//               byte[] bytes = Files.readAllBytes(Path.of("build/res/testTitle/split/_243.png"));
              inputTensor = makeImageTensor(img);
//               inputTensor = this.createTensor(parent.loadImage("build/res/testTitle/split/_243.png"));

                  output = ses.runner()
                      .feed("image_tensor", inputTensor)
                      .fetch("num_detections")
                      .fetch("detection_boxes")
                      .fetch("detection_scores")
                      .fetch("detection_classes")
                      .run();
                  float[][] scores = new float[1][600];
                  float[][][] boxes = new float[1][600][4];

               output.get(2).copyTo(scores);
               output.get(1).copyTo(boxes);

               System.out.println(System.currentTimeMillis()-t);
           }
       } catch (IOException e) {
           e.printStackTrace();
       }
        System.out.println("Test");
        System.out.println(inputTensor);
        System.out.println(output);
        return null;
   }
}
