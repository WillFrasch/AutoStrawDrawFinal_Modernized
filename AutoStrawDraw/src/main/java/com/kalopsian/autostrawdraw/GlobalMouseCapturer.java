package com.kalopsian.autostrawdraw;

import com.github.kwhat.jnativehook.GlobalScreen;
import com.github.kwhat.jnativehook.NativeHookException;
import com.github.kwhat.jnativehook.mouse.NativeMouseEvent;
import com.github.kwhat.jnativehook.mouse.NativeMouseListener;

import java.awt.*;
import java.util.concurrent.CountDownLatch;

public class GlobalMouseCapturer implements NativeMouseListener {
    private volatile Point clickedPoint; // Stores the captured click point
    private CountDownLatch latch; // Synchronizes click detection

    /**
     * Captures a global mouse click asynchronously.
     *
     * @param callback Callback to execute after capturing the click.
     */
    public void captureGlobalClickAsync(Runnable callback) {
        clickedPoint = null; // Reset the clicked point
        latch = new CountDownLatch(1); // Reset the latch

        // Start a new thread to capture the click
        new Thread(() -> {
            try {
                // Register the global listener
                if (!GlobalScreen.isNativeHookRegistered()) {
                    GlobalScreen.registerNativeHook();
                }
                GlobalScreen.addNativeMouseListener(this);

                // Wait for the latch to be released
                latch.await();

                // Call the callback after capturing the click
                if (callback != null) {
                    callback.run();
                }
            } catch (NativeHookException | InterruptedException e) {
                e.printStackTrace();
            } finally {
                // Unregister the listener
                GlobalScreen.removeNativeMouseListener(this);
            }
        }).start();
    }

    @Override
    public void nativeMouseClicked(NativeMouseEvent e) {
        // Capture the global mouse location
        clickedPoint = MouseInfo.getPointerInfo().getLocation();
        System.out.println("Mouse clicked at: " + clickedPoint);

        // Release the latch to proceed
        latch.countDown();
    }

    public Point getLastCapturedPoint() {
        return clickedPoint;
    }
}
