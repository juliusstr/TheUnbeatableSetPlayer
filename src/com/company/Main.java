package com.company;

import com.github.sarxos.webcam.Webcam;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.imgcodecs.Imgcodecs;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class Main {

    public static void main(String[] args) throws IOException, PosException, NotInFocus, NoCardDetected {
        System.loadLibrary( Core.NATIVE_LIBRARY_NAME );

        Webcam webcam = Webcam.getWebcams().get(0);
        webcam.setViewSize(new Dimension(640,480));
        webcam.open();
        BufferedImage image = webcam.getImage();
        webcam.close();
        image = ImageHandler.rotateImage(image, 0);

        ImageIO.write(image, "PNG", new File("cardtest.PNG"));

        /*BufferedImage bacgeoud = ImageIO.read(new File("test.png"));
        BufferedImage card = ImageIO.read(new File("test1.png"));
        BufferedImage image = ImageHandler.mergeBackgroudAndCardPicture(bacgeoud,card, 200,10);
        ImageIO.write(image, "PNG", new File("test2.png"));*/

        Mat image_mat = Imgcodecs.imread("cardtest.png"); // BGR type
        //ImageHandler.varianceOfLaplacian(image_mat);
        //ImageHandler.extract_card_to_png(image_mat, 120., "imgWrap.png", 0.9);
        List<Mat> cards = ImageHandler.extractCards(image_mat, 120,0.9,100);
        for (int i = 0; i < cards.size(); i++) {
            Imgcodecs.imwrite("allCardsNr" + i +".png", cards.get(i));
        }

        /*System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        Mat mat = Mat.eye(5, 5, CvType.CV_8UC1);
        System.out.println("mat = " + mat.dump());*/

    }


}
