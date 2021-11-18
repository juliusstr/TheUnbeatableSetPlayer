package com.company;

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamDevice;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException {

        final double radians = Math.toRadians(90);
        Webcam webcam = Webcam.getWebcams().get(1);
        webcam.setViewSize(new Dimension(640,480));
        webcam.open();
        BufferedImage image = webcam.getImage();
        webcam.close();
        image = ImageHandler.rotateImage(image, 0);

        ImageIO.write(image, "PNG", new File("test.png"));

    }
}
