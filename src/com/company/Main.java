package com.company;

import com.github.sarxos.webcam.Webcam;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException, PosException {

        /*final double radians = Math.toRadians(90);
        Webcam webcam = Webcam.getWebcams().get(1);
        webcam.setViewSize(new Dimension(640,480));
        webcam.open();
        BufferedImage image = webcam.getImage();
        webcam.close();
        image = ImageHandler.rotateImage(image, 0);

        ImageIO.write(image, "PNG", new File("test.png"));*/

        /*BufferedImage bacgeoud = ImageIO.read(new File("test.png"));
        BufferedImage card = ImageIO.read(new File("test1.png"));
        BufferedImage image = ImageHandler.mergeBackgroudAndCardPicture(bacgeoud,card, 200,10);
        ImageIO.write(image, "PNG", new File("test2.png")); */

        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        Mat mat = Mat.eye(5, 5, CvType.CV_8UC1);
        System.out.println("mat = " + mat.dump());

    }
}
