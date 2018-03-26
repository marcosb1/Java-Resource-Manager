package com.marist.jrm.client;

import com.marist.jrm.client.components.ConfirmBox;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

public class GUIDriver extends Application {

    private Stage applicationWindow;
    private BorderPane layout;
    private LineChart<Number, Number> cpuUsageLineChart;
    private LineChart<Number, Number> memoryUsageLineChart;

    private int totalMemoryValue = 0;
    private int memoryUsedValue = 0;
    private int memoryAvailableValue = 0;
    private int numThreadsValue = 0;
    private int numProcessesValue = 0;
    private String upTimeValue = "";

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

        // Applications Tab
        Tab applicationsTab = new Tab("Applications");
        applicationsTab.setClosable(false);
        applicationsTab.setContent(new Rectangle(600, 700, Color.LIGHTGREY));

        TableColumn<Application, String> applicationNameCol = new TableColumn<>("Name");
        applicationNameCol.setMinWidth(300);
        applicationNameCol.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<Application, String> applicationStatusCol = new TableColumn<>("Status");
        applicationStatusCol.setMinWidth(300);
        applicationStatusCol.setCellValueFactory(new PropertyValueFactory<>("status"));

        TableView<Application> applicationsTable = new TableView<>();
        applicationsTable.getColumns().addAll(applicationNameCol, applicationStatusCol);
        applicationsTab.setContent(applicationsTable);
        tabPane.getTabs().add(applicationsTab);

        // Processes Tab
        Tab processesTab = new Tab("Processes");
        processesTab.setClosable(false);
        processesTab.setContent(new Rectangle(600, 700, Color.LIGHTGREY));

        TableColumn<Process, String> processNameCol = new TableColumn<>("Process Name");
        processNameCol.setMinWidth(150);
        processNameCol.setCellValueFactory(new PropertyValueFactory<>("processName"));

        TableColumn<Process, String> memoryCol = new TableColumn<>("Memory");
        memoryCol.setMinWidth(150);
        memoryCol.setCellValueFactory(new PropertyValueFactory<>("memory"));

        TableColumn<Process, String> threadCountCol = new TableColumn<>("Thread Count");
        threadCountCol.setMinWidth(150);
        threadCountCol.setCellValueFactory(new PropertyValueFactory<>("threadCount"));

        TableColumn<Process, String> descriptionCol = new TableColumn<>("Description");
        descriptionCol.setMinWidth(150);
        descriptionCol.setCellValueFactory(new PropertyValueFactory<>("description"));

        TableView<Process> processTable = new TableView<>();
        processTable.getColumns().addAll(processNameCol, memoryCol, threadCountCol, descriptionCol);
        processesTab.setContent(processTable);
        tabPane.getTabs().add(processesTab);

        // Performance Tab
        Tab performanceTab = new Tab("Performance");
        performanceTab.setClosable(false);
        performanceTab.setContent(new Rectangle(600, 700, Color.LIGHTGREY));

        //      CPU Usage Line Chart
        final NumberAxis cpuXAxis = new NumberAxis();
        final NumberAxis cpuYAxis = new NumberAxis();
        this.cpuUsageLineChart = new LineChart<Number, Number>(cpuXAxis, cpuYAxis);
        this.cpuUsageLineChart.setTitle("CPU Usage");

        //      Memory Usage Line Chart
        final NumberAxis memoryXAxis = new NumberAxis();
        final NumberAxis memoryYAxis = new NumberAxis();
        this.memoryUsageLineChart = new LineChart<Number, Number>(memoryXAxis, memoryYAxis);
        this.memoryUsageLineChart.setTitle("Memory Usage");

        //      Data Panels Line Chart
        HBox performancePanelsLayout = new HBox();

        HBox totalMemoryBox = new HBox(new Label("Total Memory: "), new Label(this.totalMemoryValue + ""));
        HBox memoryUsedBox = new HBox(new Label("Memory Used: "), new Label(this.memoryUsedValue + ""));
        HBox memoryAvailable = new HBox(new Label("Memory Available: "), new Label(this.memoryAvailableValue + ""));
        VBox physicalMemoryComponents = new VBox();
        physicalMemoryComponents.getChildren().addAll(totalMemoryBox, memoryUsedBox, memoryAvailable);
        TitledPane physicalMemoryViewer = new TitledPane("Physical Memory", physicalMemoryComponents);
        physicalMemoryViewer.setMinWidth(300);
        physicalMemoryViewer.setMinHeight(200);
        physicalMemoryViewer.setCollapsible(false);

        HBox threadsBox = new HBox(new Label("Threads: "), new Label(this.numThreadsValue + ""));
        HBox processesBox = new HBox(new Label("Processes: "), new Label(this.numProcessesValue + ""));
        HBox upTimeBox = new HBox(new Label("Up Time: "), new Label(this.upTimeValue));
        VBox systemViewerComponents = new VBox();
        systemViewerComponents.getChildren().addAll(threadsBox, processesBox, upTimeBox);
        TitledPane systemViewer = new TitledPane("System", systemViewerComponents);
        systemViewer.setMinWidth(300);
        systemViewer.setMinHeight(200);
        systemViewer.setCollapsible(false);

        performancePanelsLayout.getChildren().addAll(physicalMemoryViewer, systemViewer);

        VBox performanceLayout = new VBox();
        performanceLayout.getChildren().addAll(this.cpuUsageLineChart, this.memoryUsageLineChart, performancePanelsLayout);

        performanceTab.setContent(performanceLayout);
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

    /**
     *
     * @param newValue
     */
    public void setTotalMemoryValue(int newValue) {
        this.totalMemoryValue = newValue;
    }

    /**
     *
     * @param newValue
     */
    public void setMemoryUsedValue(int newValue) {
        this.memoryUsedValue = newValue;
    }

    /**
     *
     * @param newValue
     */
    public void setMemoryAvailableValue(int newValue) {
        this.memoryAvailableValue = newValue;
    }

    /**
     *
     * @param newValue
     */
    public void setNumThreadsValue(int newValue) {
        this.numThreadsValue = newValue;
    }

    /**
     *
     * @param newValue
     */
    public void setNumProcessesValue(int newValue) {
        this.numProcessesValue = newValue;
    }

    /**
     *
     * @param newValue
     */
    public void setUpTimeValue(String newValue) {
        this.upTimeValue = newValue;
    }
}
