package com.marist.jrm.client;

import com.marist.jrm.client.components.ConfirmBox;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class GUIDriver extends Application {

    private Stage applicationWindow;
    private BorderPane layout;

    public static void main(String[] args) {
        launch(args);
    }

    /**
     *
     * @param primaryStage
     * @throws Exception
     */
    @Override
    public void start(Stage primaryStage) throws Exception {
        // we should make the application window
        // global to the class, it might make
        // passing variables from scenes easier
        this.applicationWindow = primaryStage;
        this.applicationWindow.setTitle("Java Resource Manager");
        this.applicationWindow.setOnCloseRequest(e -> {
           e.consume();
           this.closeProgram();
        });

        // to keep organization we should initialize the
        // top menus, making our way down the window
        MenuBar menu = new MenuBar();

        /* File Menu initialization and items */
        Menu fileMenu = new Menu("File");
        fileMenu.getItems().add(new MenuItem("Export..."));
        fileMenu.getItems().add(new SeparatorMenuItem());
        MenuItem close = new MenuItem("Close");
        close.setOnAction(e -> {
            e.consume();
            this.closeProgram();
        });
        fileMenu.getItems().add(close);

        menu.getMenus().addAll(fileMenu);

        this.layout = new BorderPane();
        this.layout.setTop(menu);
        // we must initialize the scene locally
        // TODO: which is better for a scene object, private or public
        Scene applicationScene = new Scene(this.layout, 400, 300);
        this.applicationWindow.setScene(applicationScene);
        this.applicationWindow.show();
    }

    /**
     *
     */
    private void closeProgram() {
        // TODO: post processing, we want to finish any transactions in progress
        boolean confirmed = ConfirmBox.display("Close Program", "Are you sure you want to exit?");

        if (confirmed)
            this.applicationWindow.close();
    }
}
