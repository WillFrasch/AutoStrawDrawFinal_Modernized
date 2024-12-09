package com.kalopsian.autostrawdraw;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PaletteReducer {
    public static BufferedImage reducePalette(BufferedImage inputImage, int desiredColors) {
        int width = inputImage.getWidth();
        int height = inputImage.getHeight();

        // Step 1: Extract all pixels
        List<int[]> pixels = new ArrayList<>();
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int argb = inputImage.getRGB(x, y);
                int r = (argb >> 16) & 0xFF;
                int g = (argb >> 8) & 0xFF;
                int b = argb & 0xFF;
                pixels.add(new int[]{r, g, b});
            }
        }
        List<int[]> palette = quantizeColors(pixels, desiredColors);
        BufferedImage outputImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int argb = inputImage.getRGB(x, y);
                int r = (argb >> 16) & 0xFF;
                int g = (argb >> 8) & 0xFF;
                int b = argb & 0xFF;

                int[] nearestColor = findNearestColor(new int[]{r, g, b}, palette);
                int quantizedArgb = (0xFF << 24) | (nearestColor[0] << 16) | (nearestColor[1] << 8) | nearestColor[2];
                outputImage.setRGB(x, y, quantizedArgb);
            }
        }
        return outputImage;
    }
    private static List<int[]> quantizeColors(List<int[]> pixels, int desiredColors) {
        // Sort pixels by luminance to approximate clusters
        pixels.sort((p1, p2) -> {
            int lum1 = (int) (0.2126 * p1[0] + 0.7152 * p1[1] + 0.0722 * p1[2]);
            int lum2 = (int) (0.2126 * p2[0] + 0.7152 * p2[1] + 0.0722 * p2[2]);
            return Integer.compare(lum1, lum2);
        });
        List<int[]> palette = new ArrayList<>();
        int pixelsPerCluster = pixels.size() / desiredColors;
        for (int i = 0; i < desiredColors; i++) {
            int start = i * pixelsPerCluster;
            int end = Math.min(start + pixelsPerCluster, pixels.size());
            List<int[]> cluster = pixels.subList(start, end);
            palette.add(averageColor(cluster));
        }
        return palette;
    }
    private static int[] findNearestColor(int[] color, List<int[]> palette) {
        int[] nearest = null;
        double minDistance = Double.MAX_VALUE;
        for (int[] paletteColor : palette) {
            double distance = colorDistance(color, paletteColor);
            if (distance < minDistance) {
                minDistance = distance;
                nearest = paletteColor;
            }
        }
        return nearest;
    }
    private static int[] averageColor(List<int[]> cluster) {
        int r = 0, g = 0, b = 0;
        for (int[] color : cluster) {
            r += color[0];
            g += color[1];
            b += color[2];
        }
        int size = cluster.size();
        return new int[]{r / size, g / size, b / size};
    }
    private static double colorDistance(int[] c1, int[] c2) {
        return Math.sqrt(
                Math.pow(c1[0] - c2[0], 2) +
                        Math.pow(c1[1] - c2[1], 2) +
                        Math.pow(c1[2] - c2[2], 2)
        );
    }
}