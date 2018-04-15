package com.marist.jrm.client;

import com.marist.jrm.client.GUIDriver;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.stage.Stage;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class GUIDriverTest {

    @Test
    public void testLaunch() throws InterruptedException {
        Thread thread = new Thread(new Runnable() {

            @Override
            public void run() {
                new JFXPanel(); // Initializes the JavaFx Platform
                Platform.runLater(new Runnable() {

                    @Override
                    public void run() {
                        try {
                            new GUIDriver().start(new Stage());
                            assertEquals(true, true);
                        } catch (Exception e) {
                            e.printStackTrace();
                            assertEquals(true, false);
                        }
                    }
                });
            }
        });
        // Initialize the thread
        thread.start();
        // Time to use the app, with out this, the thread will be killed before you can tell.
        Thread.sleep(10000);
    }

}