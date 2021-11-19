package com.company;

import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class ImageHandler {
    private final double cornerYmin=2.5;
    private final double cornerYmax=23;

    private final int zoom=4;
    private final int cardW = (int) 57 * zoom;
    private final int cardH = (int) 87 * zoom;
    /*private final int cornerXmin = (int) 2*zoom;
    private final int cornerXmax = (int) (10.5*zoom);
    private final int cornerYmin = (int) (2.5*zoom);
    private final int cornerYmax = int (cornerYmax*zoom);//todo slet mig måske*/

    public static BufferedImage rotateImage(BufferedImage buffImage, double angle) {
        double radian = Math.toRadians(angle);
        double sin = Math.abs(Math.sin(radian));
        double cos = Math.abs(Math.cos(radian));

        int width = buffImage.getWidth();
        int height = buffImage.getHeight();

        int nWidth = (int) Math.floor((double) width * cos + (double) height * sin);
        int nHeight = (int) Math.floor((double) height * cos + (double) width * sin);

        BufferedImage rotatedImage = new BufferedImage(
                nWidth, nHeight, BufferedImage.TYPE_INT_ARGB);

        Graphics2D graphics = rotatedImage.createGraphics();

        graphics.setRenderingHint(
                RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_BICUBIC);

        graphics.translate((nWidth - width) / 2, (nHeight - height) / 2);
        // rotation around the center point
        graphics.rotate(radian, (double) (width / 2), (double) (height / 2));
        graphics.drawImage(buffImage, 0, 0, null);
        graphics.dispose();

        return rotatedImage;
    }

    public static BufferedImage mergeBackgroudAndCardPicture (BufferedImage backgroud, BufferedImage card, int xpos, int ypos) throws PosException{
        int w = Math.max(backgroud.getWidth(), card.getWidth());
        int h = Math.max(backgroud.getHeight(), card.getHeight());
        BufferedImage combined = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        if (xpos+card.getWidth()>w || ypos+ card.getHeight()>h){
            throw new PosException("card out of bounds");
        }
        // paint both images, preserving the alpha channels
        Graphics g = combined.getGraphics();
        g.drawImage(backgroud, 0, 0, null);
        g.drawImage(card, xpos, ypos, null);

        g.dispose();
        return combined;
    }

    public static double varianceOfLaplacian (Mat image){
        int ddepth = CvType.CV_64F;
        int kernel_size = 3;
        int scale = 1;
        int delta = 0;

        Mat grayScaleImage = new Mat();
        Imgproc.cvtColor(image, grayScaleImage, Imgproc.COLOR_RGB2GRAY);
        Mat lap = new Mat();
        Imgproc.Laplacian( grayScaleImage, lap, ddepth, kernel_size, scale, delta, Core.BORDER_DEFAULT );
        /*double mean = 0;
        for (int i = 0; i < image.rows(); i++) {
            for (int j = 0; j < image.cols(); j++) {
                System.out.println(Double.parseDouble(Arrays.toString(image.get(i,j)).replace("[","").replace("]","")));
                mean += Double.parseDouble(Arrays.toString(image.get(i,j)).replace("[","").replace("]",""));
            }
        }
        mean = mean / image.total();

        System.out.println(mean);*/
        //converting back to CV_8U generate the standard deviation
        Mat absLplImage = new Mat();
        Core.convertScaleAbs(lap, absLplImage);

        // get the standard deviation of the absolute image as input for the sharpness score
        MatOfDouble median = new MatOfDouble();
        MatOfDouble std = new MatOfDouble();
        Core.meanStdDev(absLplImage, median, std);

        return Math.pow(std.get(0, 0)[0], 2);
    }

    /*public static Mat BufferedImage2Mat(BufferedImage image) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ImageIO.write(image, "jpg", byteArrayOutputStream);
        byteArrayOutputStream.flush();
        return Imgcodecs.imdecode(new MatOfByte(byteArrayOutputStream.toByteArray()), Imgcodecs.CV_LOAD_IMAGE_UNCHANGED);
    }*/

    public static Mat extract_card ( Mat img, double min_focus) throws NotInFocus, NoCardDetected{
        if (min_focus>varianceOfLaplacian(img)){
            throw new NotInFocus ("The picture is not in focus");
        } else {
            Mat gray = new Mat();
            Imgproc.cvtColor(img, gray, Imgproc.COLOR_RGB2GRAY);
            Mat noise = new Mat();
            Imgproc.bilateralFilter(gray, noise,11,17,17);

            Mat edge = new Mat();
            Imgproc.Canny(noise,edge,30,200);
            Imgcodecs.imwrite("edge.png", edge); //todo fjern
            List<MatOfPoint> contours = new ArrayList<>();
            Mat hierarchy = new Mat();
            Imgproc.findContours(edge.clone(),contours, hierarchy, Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE);
            MatOfPoint conture = new MatOfPoint();
            /*for (int i = 0; i < contours.size(); i++) {
                if (Imgproc.contourArea(contours.get(i))>Imgproc.contourArea(conture)){
                    conture = contours.get(i);
                }
            }*/
            double maxArea = 0;
            int index = 0;
            for (int i = 0; i < contours.size(); i++) {
                System.out.println(Imgproc.contourArea(contours.get(i)));
                if (Imgproc.contourArea(contours.get(i))>maxArea){
                    maxArea = Imgproc.contourArea(contours.get(i));
                    index = i;
                }
            }
            MatOfPoint cnt = contours.get(index);
            MatOfPoint2f cnt2f = new MatOfPoint2f(cnt.toArray());
            RotatedRect rect = Imgproc.minAreaRect(cnt2f);
            Mat box = new Mat();
            Imgproc.boxPoints(rect,box);
            double areaCnt = Imgproc.contourArea(cnt);
            double areaBox = Imgproc.contourArea(box);
            boolean valid = areaCnt/areaBox>0.95;

            if (!valid){
                throw new NoCardDetected ("No rectangle was detected in img");
            } else {
                int xr = (int) rect.center.x;
                int yr = (int) rect.center.y;
                int wr = (int) rect.size.width;
                int hr = (int) rect.size.height;
                double thetar = rect.angle;

            }

            return img;
        }

    }
}


