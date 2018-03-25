package com.marist.jrm.client;

import com.marist.jrm.client.components.ConfirmBox;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
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

        MenuItem export = new MenuItem("Export...");
        export.setDisable(false);
        fileMenu.getItems().add(export);

        fileMenu.getItems().add(new SeparatorMenuItem());

        MenuItem close = new MenuItem("Close");
        close.setDisable(false);
        close.setOnAction(e -> {
            e.consume();
            this.closeProgram();
        });
        fileMenu.getItems().add(close);

        menu.getMenus().addAll(fileMenu);
        /* End of File Menu initialization */

        /* Tab Pane initialization */
        TabPane tabPane = new TabPane();
        Tab processesTab = new Tab("Processes");
        processesTab.setClosable(false);
        processesTab.setContent(new Rectangle(600, 700, Color.LIGHTGREY));

        TableColumn<Process, String> processNameCol = new TableColumn<>("Process Name");
        processNameCol.setMinWidth(200);
        processNameCol.setCellValueFactory(new PropertyValueFactory<>("processName"));

        TableColumn<Process, String> memoryCol = new TableColumn<>("Memory");
        memoryCol.setMinWidth(200);
        memoryCol.setCellValueFactory(new PropertyValueFactory<>("memory"));

        TableColumn<Process, String> threadCountCol = new TableColumn<>("Thread Count");
        threadCountCol.setMinWidth(200);
        threadCountCol.setCellValueFactory(new PropertyValueFactory<>("threadCount"));

        TableColumn<Process, String> descriptionCol = new TableColumn<>("Description");
        descriptionCol.setMinWidth(200);
        descriptionCol.setCellValueFactory(new PropertyValueFactory<>("description"));

        TableView<Process> processTable = new TableView<>();
        processTable.getColumns().addAll(processNameCol, memoryCol, threadCountCol, descriptionCol);
        processesTab.setContent(processTable);
        tabPane.getTabs().add(processesTab);

        Tab performanceTab = new Tab("Performance");
        performanceTab.setClosable(false);
        performanceTab.setContent(new Rectangle(600, 700, Color.LIGHTGREY));
        tabPane.getTabs().add(performanceTab);
        /* End of Tab Pane initialization */

        this.layout = new BorderPane();
        this.layout.setTop(menu);
        this.layout.setCenter(tabPane);
        // we must initialize the scene locally
        // TODO: which is better for a scene object, private or public
        Scene applicationScene = new Scene(this.layout, 600, 800);
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
