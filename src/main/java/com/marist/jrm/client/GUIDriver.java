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
import oshi.util.FormatUtil;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GUIDriver extends Application {

    public TableView<ProcessModel> processTable;

    private final static Logger LOGGER = Logger.getLogger(GUIDriver.class.getName());
    private final static Level CURRENT_LEVEL = Level.INFO;
    private final int REFRESH_INTERVAL = 2000;

    private Stage applicationWindow;
    private BorderPane layout;
    private LineChart<Number, Number> cpuUsageLineChart;
    private LineChart<Number, Number> memoryUsageLineChart;

    private Double totalMemoryValue = 0.0;
    private Double memoryUsedValue = 0.0;
    private Double memoryAvailableValue = 0.0;
    private int numThreadsValue = 0;
    private int numProcessesValue = 0;
    private String upTimeValue = "";
    private NumberAxis cpuXAxis = new NumberAxis();
    private NumberAxis cpuYAxis = new NumberAxis();
    private final NumberAxis memoryXAxis = new NumberAxis();
    // TODO: We need to retrieve the total amount of memory in the system to set as the upperBound
    private final NumberAxis memoryYAxis = new NumberAxis();
    private XYChart.Series cpuUsageSeries = new XYChart.Series<>();
    private XYChart.Series memoryUsageSeries = new XYChart.Series<>();
    private ArrayList<long[]> cpuUsages = new ArrayList<>();
    private ArrayList<long[]> memoryUsages = new ArrayList<>();

    // System Call initializer data
    private SystemInfo si = new SystemInfo();
    private HardwareAbstractionLayer hal = si.getHardware();
    private OperatingSystem os = si.getOperatingSystem();

    public static void main(String[] args) {
        LOGGER.setLevel(CURRENT_LEVEL);
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
        processNameCol.setMinWidth(200);
        processNameCol.setCellValueFactory(new PropertyValueFactory<>("processName"));

        TableColumn<ProcessModel, String> memoryCol = new TableColumn<>("Memory");
        memoryCol.setMinWidth(200);
        memoryCol.setCellValueFactory(new PropertyValueFactory<>("memory"));

        TableColumn<ProcessModel, String> threadCountCol = new TableColumn<>("Thread Count");
        threadCountCol.setMinWidth(200);
        threadCountCol.setCellValueFactory(new PropertyValueFactory<>("threadCount"));

        processTable = new TableView<>();
        processTable.getColumns().addAll(processNameCol, memoryCol, threadCountCol);
        processesTab.setContent(processTable);
        tabPane.getTabs().add(processesTab);

        // Performance Tab
        Tab performanceTab = new Tab("Performance");
        performanceTab.setClosable(false);
        performanceTab.setContent(new Rectangle(600, 700, Color.LIGHTGREY));

        //      CPU Usage Line Chart
        this.cpuUsageLineChart = new LineChart<Number, Number>(this.cpuXAxis, this.cpuYAxis);
        this.cpuUsageLineChart.setTitle("CPU Usage");
        this.cpuUsageSeries.setName("CPU Consumption");
        this.cpuUsageLineChart.getData().add(this.cpuUsageSeries);

        //      Memory Usage Line Chart
        this.memoryXAxis.setForceZeroInRange(false);
        this.memoryXAxis.setAnimated(false);
        this.memoryXAxis.setAutoRanging(false);
        this.memoryXAxis.setTickUnit(200000);

        this.memoryYAxis.setLowerBound(0);
        this.memoryYAxis.setUpperBound(this.hal.getMemory().getTotal() / 1073741824);
        this.memoryYAxis.setAutoRanging(false);
        this.memoryYAxis.setTickUnit(4);

        this.memoryUsageLineChart = new LineChart<Number, Number>(this.memoryXAxis, this.memoryYAxis);
        this.memoryUsageLineChart.setCreateSymbols(false);
        this.memoryUsageLineChart.setAnimated(false);
        this.memoryUsageLineChart.setTitle("Memory Usage");

        this.memoryUsageSeries.setName("Memory Consumption");
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
                new KeyFrame(new Duration(this.REFRESH_INTERVAL),
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
        this.applicationWindow.setResizable(false);
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
        this.setProcessTableContents(SystemCallDriver.getProcesses(this.os, this.hal.getMemory()));
        // System Call TODO
        Double[] memoryValues = SystemCallDriver.getMemoryMetrics(hal.getMemory());
        this.totalMemoryValue = memoryValues[0];
        this.memoryUsedValue = memoryValues[1];
        this.memoryAvailableValue = memoryValues[2];
        this.numThreadsValue = os.getThreadCount();
        this.numProcessesValue = os.getProcessCount();
        this.upTimeValue = FormatUtil.formatElapsedSecs(hal.getProcessor().getSystemUptime());
        // get clock ticks and put it in array [clockTickValue,cpuUsageVal] and [clockTickVal, memUsage]
        // TODO: update CPU line chart
        this.updateMemoryLineChart(SystemCallDriver.getMemoryUsage(this.os, this.hal.getMemory()));

        // Application TODO
        // TODO: List of applications for application package
        // TODO: From there nest Process list in Application model insert into db
        // TODO: Get num threads for that + usages

        // @everyone TODO
        // TODO: Figure out how we're gonna do system time
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
    public void setTotalMemoryValue(Double newValue) {
        this.totalMemoryValue = newValue;
    }

    /** getTotalMemoryValue
     * Use this getter to retrieve the total memory of the system (at a certain point) for statistics purposes
     * @return this.totalMemoryValue
     */
    public double getTotalMemoryValue() { return this.totalMemoryValue; }

    /** setMemoryUsedValue
     *
     * @param newValue
     */
    public void setMemoryUsedValue(Double newValue) { this.memoryUsedValue = newValue; }

    /** getMemoryUsedValue
     * Use this getter to retrieve the memory used value (out of the totalMemoryValue) for statistics purposes
     * @return this.memoryUsedValue
     */
    public double getMemoryUsedValue() { return this.memoryUsedValue; }

    /** setMemoryAvailableValue
     *
     * @param newValue
     */
    public void setMemoryAvailableValue(double newValue) { this.memoryAvailableValue = newValue; }

    /** getMemoryAvailableValue
     * Use this getter to retrieve the memory available (memory left that can be used) of the system for statistics
     * purposes
     * @return this.memoryAvailableValue
     */
    public double getMemoryAvailableValue() { return this.memoryAvailableValue; }

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

    private void updateCPULineChart(long[] newUsage) {

    }

    private void updateMemoryLineChart(long[] newUsage) {
        this.memoryUsages.add(newUsage);
        long x = newUsage[0];
        long y = newUsage[1];
        this.memoryUsageSeries.getData().add(new XYChart.Data(x, y));
        this.memoryXAxis.setLowerBound(this.memoryUsages.get(0)[0]);
        this.memoryXAxis.setUpperBound(this.memoryUsages.get(this.memoryUsages.size() - 1)[0]);
    }
}
