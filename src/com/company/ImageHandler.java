package com.company;

import java.awt.*;
import java.awt.image.BufferedImage;

public class ImageHandler {

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

}


