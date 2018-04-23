package com.marist.jrm.client;

import com.marist.jrm.client.components.ConfirmBox;
import com.marist.jrm.model.ProcessModel;
import com.marist.jrm.systemCall.SystemCallDriver;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import javafx.util.Duration;
import oshi.SystemInfo;
import oshi.hardware.HardwareAbstractionLayer;
import oshi.software.os.OperatingSystem;

import java.util.ArrayList;

public class GUIDriver extends Application {

    public TableView<ProcessModel> processTable;

    private final int refreshInterval = 2000;

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
    private XYChart.Series cpuUsageSeries = new XYChart.Series<>();
    private XYChart.Series memoryUsageSeries = new XYChart.Series<>();

    // System Call initializer data
    private SystemInfo si = new SystemInfo();
    private HardwareAbstractionLayer hal = si.getHardware();
    private OperatingSystem os = si.getOperatingSystem();

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

        TableColumn<ProcessModel, String> processNameCol = new TableColumn<>("Process Name");
        processNameCol.setMinWidth(150);
        processNameCol.setCellValueFactory(new PropertyValueFactory<>("processName"));

        TableColumn<ProcessModel, String> memoryCol = new TableColumn<>("Memory");
        memoryCol.setMinWidth(150);
        memoryCol.setCellValueFactory(new PropertyValueFactory<>("memory"));

        TableColumn<ProcessModel, String> threadCountCol = new TableColumn<>("Thread Count");
        threadCountCol.setMinWidth(150);
        threadCountCol.setCellValueFactory(new PropertyValueFactory<>("threadCount"));

        TableColumn<ProcessModel, String> descriptionCol = new TableColumn<>("Description");
        descriptionCol.setMinWidth(150);
        descriptionCol.setCellValueFactory(new PropertyValueFactory<>("description"));

        processTable = new TableView<>();
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
        this.cpuUsageLineChart.getData().add(this.cpuUsageSeries);

        //      Memory Usage Line Chart
        final NumberAxis memoryXAxis = new NumberAxis();
        // TODO: We need to retrieve the total amount of memory in the system to set as the upperBound
        final NumberAxis memoryYAxis = new NumberAxis();
        this.memoryUsageLineChart = new LineChart<Number, Number>(memoryXAxis, memoryYAxis);
        this.memoryUsageLineChart.setTitle("Memory Usage");
        this.memoryUsageLineChart.getData().add(this.memoryUsageSeries);

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

        /* Start of Timelines */

        Timeline timeline = new Timeline();
        timeline.getKeyFrames().add(
                new KeyFrame(new Duration(this.refreshInterval),
                        a -> update()));
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();

        /* End of Timelines */

        this.layout = new BorderPane();
        this.layout.setTop(menu);
        this.layout.setCenter(tabPane);
        // we must initialize the scene locally
        // TODO: which is better for a scene object, private or public?
        Scene applicationScene = new Scene(this.layout, 600, 800);
        this.applicationWindow.setScene(applicationScene);
        this.applicationWindow.show();
    }

    /** update
     * The update method will act as a "loop"-style method which will be placed in a Timeline to execute on
     * an interval, defined by refreshInterval
     *
     * NOTE: PLEASE PUT ALL METHODS REGARDING UI UPDATING IN HERE
     */
    public void update() {
        // update process table elements
        this.processTable.getItems().clear();
        this.setProcessTableContents(SystemCallDriver.getProcesses(os, hal.getMemory()));
        // System Call TODO
        // TODO: update totalMemoryValue
        // TODO: update memoryUsedValue
        // TODO: update memoryAvailableValue
        // TODO: update numThreadsValue
        // TODO: update numProcessesValue
        // TODO: update upTimeValue
        // get clock ticks and put it in array [clockTickValue,cpuUsageVal] and [clockTickVal, memUsage]
        // TODO: update CPU line chart
        // TODO: update memory line chart

        // Application TODO
        // TODO: List of applications for application package
        // TODO: From there nest Process list in Application model insert into db
        // TODO: Get num threads for that + usages

        // @everyone TODO
        // TODO: Figure out how we're gonna due system time
    }

    /** closeProgram
     * Will commit any left over transactions after prompting the user as to
     * whether they are sure they want to exit the program.
     */
    private void closeProgram() {
        // TODO: post processing, we want to finish any transactions in progress
        boolean confirmed = ConfirmBox.display("Close Program", "Are you sure you want to exit?");

        // TODO: Should we clear DB here?

        if (confirmed)
            this.applicationWindow.close();
    }

    /** setProcessTableContents
     *
     * @param active
     */
    public void setProcessTableContents(ArrayList<ProcessModel> active) {
        // Add processes to the table
        for (ProcessModel p : active) {
            this.processTable.getItems().add(p);
        }
    }

    /** setTotalMemoryValue
     *
     * @param newValue
     */
    public void setTotalMemoryValue(int newValue) {
        this.totalMemoryValue = newValue;
    }

    /** getTotalMemoryValue
     * Use this getter to retrieve the total memory of the system (at a certain point) for statistics purposes
     * @return this.totalMemoryValue
     */
    public int getTotalMemoryValue() {
        return this.totalMemoryValue;
    }

    /** setMemoryUsedValue
     *
     * @param newValue
     */
    public void setMemoryUsedValue(int newValue) {
        this.memoryUsedValue = newValue;
    }

    /** getMemoryUsedValue
     * Use this getter to retrieve the memory used value (out of the totalMemoryValue) for statistics purposes
     * @return this.memoryUsedValue
     */
    public int getMemoryUsedValue() { return this.memoryUsedValue; }

    /** setMemoryAvailableValue
     *
     * @param newValue
     */
    public void setMemoryAvailableValue(int newValue) {
        this.memoryAvailableValue = newValue;
    }

    /** getMemoryAvailableValue
     * Use this getter to retrieve the memory available (memory left that can be used) of the system for statistics
     * purposes
     * @return this.memoryAvailableValue
     */
    public int getMemoryAvailableValue() { return this.memoryAvailableValue; }

    /** setNumThreadsValue
     *
     * @param newValue
     */
    public void setNumThreadsValue(int newValue) {
        this.numThreadsValue = newValue;
    }

    /** getNumThreadsValue
     * Use this getter to retrieve the number of threads currently used by the system for statistics purposes
     * @return this.numThreadsValue
     */
    public int getNumThreadsValue() { return this.numThreadsValue; }

    /** setNumProcessesValue
     *
     * @param newValue
     */
    public void setNumProcessesValue(int newValue) {
        this.numProcessesValue = newValue;
    }

    /** getNumProcessesValue
     * Use this getter to retrieve the number of concurrent processes for statistics purposes
     * @return this.numProcessesValue
     */
    public int getNumProcessesValue() { return this.numProcessesValue; }

    /** setUpTimeValue
     *
     * @param newValue
     */
    public void setUpTimeValue(String newValue) {
        this.upTimeValue = newValue;
    }

    /** getUpTimeValue
     * Use this getter to retrieve the up time of the system for statistics purposes
     * @return this.upTimeValue
     */
    public String getUpTimeValue() { return this.upTimeValue; }

    public void updateCPULineChart() {
        // TODO: What do we need to get the CPU usage?
    }

    public void updateMemoryLineChart() {
        // TODO: What do we need to get the memory usage?
    }
}
