package com.kalopsian.autostrawdraw;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
public class ImageSplitter {
    private static List<int[]> colorArray;
    public static BufferedImage[] splitImageByColor(BufferedImage inputImage) {
        int width = inputImage.getWidth();
        int height = inputImage.getHeight();
        Set<Integer> uniqueColors = new HashSet<>();
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int argb = inputImage.getRGB(x, y);
                uniqueColors.add(argb);
            }
        }
        colorArray = new ArrayList<>();
        BufferedImage[] images = new BufferedImage[uniqueColors.size()];
        int index = 0;
        for (int color : uniqueColors) {
            BufferedImage singleColorImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
            int r = (color >> 16) & 0xFF;
            int g = (color >> 8) & 0xFF;
            int b = color & 0xFF;
            colorArray.add(new int[]{r, g, b});
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    int argb = inputImage.getRGB(x, y);
                    if (argb == color) {
                        singleColorImage.setRGB(x, y, color);
                    } else {
                        singleColorImage.setRGB(x, y,0x000000);
                    }
                }
            }
            images[index++] = singleColorImage;
        }
        return images;
    }
    public List<int[]> getColorArray() {
        return colorArray;
    }
}
