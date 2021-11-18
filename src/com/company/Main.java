package com.company;

import com.github.sarxos.webcam.Webcam;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;

public class Main {

    public static void main(String[] args) throws IOException {

        List<Webcam> webcamlist = Webcam.getWebcams();
        for (int i = 0; i < webcamlist.size(); i++) {
            System.out.println(i + " " + webcamlist.get(i).getName());
            System.out.println("opløsninger:");
            for (int j = 0; j < webcamlist.get(i).getViewSizes().length; j++) {
                System.out.println("   " + webcamlist.get(i).getViewSizes()[j].width + " X " + webcamlist.get(i).getViewSizes()[j].height);
            }

        }
        /*Webcam webcam = webcamlist.get(1);
        webcam.open();
        BufferedImage image = webcam.getImage();
        ImageIO.write(image, "PNG", new File("test.png"));
        */
    }
}
