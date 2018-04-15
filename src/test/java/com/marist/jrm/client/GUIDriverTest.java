package com.marist.jrm.client;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.stage.Stage;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class GUIDriverTest {

    /** testLaunch
     * This will test the launching of the GUI
     * @throws InterruptedException
     */
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
        // We only want it to run for 5 seconds because by then everything should be updated, so we will know if it
        // launched correctly
        Thread.sleep(5000);
    }

}