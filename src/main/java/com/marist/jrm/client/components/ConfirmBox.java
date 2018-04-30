package com.marist.jrm.client.components;

import javafx.stage.*;
import javafx.scene.*;
import javafx.scene.layout.*;
import javafx.scene.control.*;
import javafx.geometry.*;

/**
 * @author Marcos Barbieri
 */
public class ConfirmBox {

    public static boolean isConfirmed;

    /**
     * Will create a pop up confirm window box, that will require user input
     * @param title title of window box that will appear
     * @param msg message that will be written to window box
     * @return true if window box was confirmed, false if not
     */
    public static boolean display(String title, String msg) {
        Stage confirm = new Stage();

        confirm.initModality(Modality.APPLICATION_MODAL);
        confirm.setTitle(title);
        confirm.setMinWidth(250);
        confirm.setMaxWidth(250);
        confirm.setMinHeight(100);
        confirm.setMaxHeight(100);

        // START OF alert elements

        // main label attributes
        Label mainLabel = new Label();
        mainLabel.setText(msg);

        // yes button attributes
        Button confirmButton = new Button("Yes");
        confirmButton.setOnAction(e -> {
            isConfirmed = true;
            confirm.close();
        });

        // no button attributes
        Button cancelButton = new Button("No");
        cancelButton.setOnAction(e -> {
            isConfirmed = false;
            confirm.close();
        });

        HBox responseButtonsLayout = new HBox(20);
        responseButtonsLayout.getChildren().addAll(confirmButton, cancelButton);
        responseButtonsLayout.setAlignment(Pos.CENTER);

        VBox confirmBoxLayout = new VBox(10);
        confirmBoxLayout.getChildren().addAll(mainLabel, responseButtonsLayout);
        confirmBoxLayout.setAlignment(Pos.CENTER);

        Scene confirmBoxScene = new Scene(confirmBoxLayout);
        confirm.setScene(confirmBoxScene);
        confirm.showAndWait();

        // END OF alert elements

        return isConfirmed;
    }

}
