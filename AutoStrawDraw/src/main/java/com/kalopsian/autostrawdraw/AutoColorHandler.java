package com.kalopsian.autostrawdraw;

import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.util.concurrent.CountDownLatch;
public class AutoColorHandler {
    private Point colorWheelLocation;
    private Point redBoxLocation;
    private Point greenBoxLocation;
    private Point blueBoxLocation;
    private Point okButtonLocation;

    public AutoColorHandler() {
    }

    public void setLocations(Point colorWheel, Point redBox, Point greenBox, Point blueBox, Point okButton) {
        this.colorWheelLocation = colorWheel;
        this.redBoxLocation = redBox;
        this.greenBoxLocation = greenBox;
        this.blueBoxLocation = blueBox;
        this.okButtonLocation = okButton;

        System.out.println("[DEBUG] Locations set: ");
        System.out.println("Color Wheel: " + colorWheel);
        System.out.println("Red Box: " + redBox);
        System.out.println("Green Box: " + greenBox);
        System.out.println("Blue Box: " + blueBox);
        System.out.println("OK Button: " + okButton);
    }


    void setColor(Robot bot, int[] currentColor, CountDownLatch latch) throws InterruptedException {
        System.out.println("[DEBUG] Setting color: R=" + currentColor[0] + ", G=" + currentColor[1] + ", B=" + currentColor[2]);
        try{
        // Click on the color wheel to activate the color window
        bot.mouseMove(500, 500);
        bot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
        bot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
        bot.mouseMove(colorWheelLocation.x, colorWheelLocation.y);
        bot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
        bot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
        Thread.sleep(100);

        // Set Red value
        bot.mouseMove(redBoxLocation.x, redBoxLocation.y);
        bot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
        bot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
        Thread.sleep(20);

        // Select all and clear existing value
        bot.keyPress(KeyEvent.VK_CONTROL);
        bot.keyPress(KeyEvent.VK_A);
        bot.keyRelease(KeyEvent.VK_A);
        bot.keyRelease(KeyEvent.VK_CONTROL);
        Thread.sleep(20);

        bot.keyPress(KeyEvent.VK_BACK_SPACE);
        bot.keyRelease(KeyEvent.VK_BACK_SPACE);

        // Type the new Red value
        String redValue = String.valueOf(currentColor[0]);
        for (char c : redValue.toCharArray()) {
            bot.keyPress(KeyEvent.getExtendedKeyCodeForChar(c));
            bot.keyRelease(KeyEvent.getExtendedKeyCodeForChar(c));
        }

        // Set Green value
        bot.mouseMove(greenBoxLocation.x, greenBoxLocation.y);
        bot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
        bot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
        Thread.sleep(20);

        // Select all and clear existing value
        bot.keyPress(KeyEvent.VK_CONTROL);
        bot.keyPress(KeyEvent.VK_A);
        bot.keyRelease(KeyEvent.VK_A);
        bot.keyRelease(KeyEvent.VK_CONTROL);
        Thread.sleep(20);

        bot.keyPress(KeyEvent.VK_BACK_SPACE);
        bot.keyRelease(KeyEvent.VK_BACK_SPACE);

        // Type the new Green value
        String greenValue = String.valueOf(currentColor[1]);
        for (char c : greenValue.toCharArray()) {
            bot.keyPress(KeyEvent.getExtendedKeyCodeForChar(c));
            bot.keyRelease(KeyEvent.getExtendedKeyCodeForChar(c));
        }

        // Set Blue value
        bot.mouseMove(blueBoxLocation.x, blueBoxLocation.y);
        bot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
        bot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
        Thread.sleep(20);

        // Select all and clear existing value
        bot.keyPress(KeyEvent.VK_CONTROL);
        bot.keyPress(KeyEvent.VK_A);
        bot.keyRelease(KeyEvent.VK_A);
        bot.keyRelease(KeyEvent.VK_CONTROL);
        Thread.sleep(20);

        bot.keyPress(KeyEvent.VK_BACK_SPACE);
        bot.keyRelease(KeyEvent.VK_BACK_SPACE);

        // Type the new Blue value
        String blueValue = String.valueOf(currentColor[2]);
        for (char c : blueValue.toCharArray()) {
            bot.keyPress(KeyEvent.getExtendedKeyCodeForChar(c));
            bot.keyRelease(KeyEvent.getExtendedKeyCodeForChar(c));
        }

        // Confirm color
        bot.mouseMove(okButtonLocation.x, okButtonLocation.y);
        bot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
        bot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
        Thread.sleep(10);
        System.out.println("[DEBUG] Color set successfully.");
        } finally {
        latch.countDown(); // Signal that color setting is complete
        }
    }
}
