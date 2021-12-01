package com.company;

import org.opencv.core.*;
import org.opencv.core.Point;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static javax.swing.WindowConstants.DISPOSE_ON_CLOSE;

public class ImageHandler {
    private final double cornerYmin=2.5;
    private final double cornerYmax=23;

    private static int zoom=4;
    private static int cardW = (int) 57 * zoom;
    private static int cardH = (int) 87 * zoom;
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

    public static void extract_card_to_png (Mat img, double min_focus, String path) throws NotInFocus, NoCardDetected{
        if (min_focus>varianceOfLaplacian(img)){
            throw new NotInFocus ("The picture is not in focus");
        } else {
            Mat gray = new Mat();
            Imgproc.cvtColor(img, gray, Imgproc.COLOR_RGB2GRAY);
            Mat noise = new Mat();
            Imgproc.bilateralFilter(gray, noise,11,17,17);

            Mat edge = new Mat();
            Imgproc.Canny(noise,edge,30,200);
            List<MatOfPoint> contours = new ArrayList<>();
            Mat hierarchy = new Mat();
            Imgproc.findContours(edge.clone(),contours, hierarchy, Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE);
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
                throw new NoCardDetected ("No rectangle was detected in img  " + areaCnt/areaBox + "%");
            } else {
                int xr = (int) rect.center.x;
                int yr = (int) rect.center.y;
                int wr = (int) rect.size.width;
                int hr = (int) rect.size.height;
                double thetar = rect.angle;

                int cardHcell = 4;
                int cardWcell = 2;

                Mat refCard = new Mat(4, 2, CvType.CV_32FC1);
                Mat refCardRot = new Mat(4, 2, CvType.CV_32FC1);

                Mat mp = new Mat();

                refCard.put(0,0,0); refCard.put(0,1,cardW);
                refCard.put(1,0,0); refCard.put(1,1,0);
                refCard.put(2,0,cardH); refCard.put(2,1,0);
                refCard.put(3,0,cardH); refCard.put(3,1,cardW);

                refCardRot.put(0,0,0); refCardRot.put(0,1,cardH);
                refCardRot.put(1,0,0); refCardRot.put(1,1,0);
                refCardRot.put(2,0,cardW); refCardRot.put(2,1,0);
                refCardRot.put(3,0,cardW); refCardRot.put(3,1,cardH);

                Mat imgWarp = new Mat();
                if (wr>hr){
                    mp = Imgproc.getPerspectiveTransform(box,refCard);
                    Imgproc.warpPerspective(img,imgWarp, mp, new Size(cardH,cardW));
                    Core.rotate(imgWarp, imgWarp, Core.ROTATE_90_CLOCKWISE);
                } else {
                    mp = Imgproc.getPerspectiveTransform(box,refCardRot);
                    Imgproc.warpPerspective(img,imgWarp, mp, new Size(cardW,cardH));
                }




                Imgcodecs.imwrite(path, imgWarp); //todo fjern


            }
        }

    }

}


