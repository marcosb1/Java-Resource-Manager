/**
 * The GUIDriver will be the class that is run when the application is run. The GUIDriver contains all the tie-ins from
 * the other classes in this project.
 *
 * @author Marcos Barbieri
 * @version 0.0.5
 */

package com.marist.jrm.client;

import com.marist.jrm.application.SQLiteDBInit;
import com.marist.jrm.application.SQLiteDBUtil;
import com.marist.jrm.client.components.ConfirmBox;
import com.marist.jrm.model.ApplicationModel;
import com.marist.jrm.model.MemoryMetrics;
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

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GUIDriver extends Application {

    private TableView<ApplicationModel> applicationsTable;
    private TableView<ProcessModel> processTable;

    private final static Logger LOGGER = Logger.getLogger(GUIDriver.class.getName());
    private final static Level CURRENT_LEVEL = Level.INFO;
    private final int REFRESH_INTERVAL = 1000;
    private final int BYTES_TO_GB = 1073741824;

    private Stage applicationWindow;
    private BorderPane layout;
    private LineChart<Number, Number> cpuUsageLineChart;
    private LineChart<Number, Number> memoryUsageLineChart;
    private Label totalMemoryValueLabel = new Label();
    private Label memoryUsedValueLabel = new Label();
    private Label memoryAvailableValueLabel = new Label();
    private Label numThreadsValueLabel = new Label();
    private Label numProcessesValueLabel = new Label();
    private Label upTimeValueLabel = new Label();

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
     * Launches and initializes window/application with all widgets/components
     * @param primaryStage where the widget(s) and scene(s) are going to displayed
     * @throws Exception the application will throw a generic JavaFX error
     */
    @Override
    public void start(Stage primaryStage) throws Exception {
        // we should make the application window
        // global to the class, it might make
        // passing variables from scenes easier

        //Start initializing the database tables
        SQLiteDBInit.initDB();

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

        TableColumn<ApplicationModel, String> applicationNameCol = new TableColumn<>("Name");
        applicationNameCol.setMinWidth(300);
        applicationNameCol.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<ApplicationModel, String> applicationStatusCol = new TableColumn<>("Status");
        applicationStatusCol.setMinWidth(300);
        applicationStatusCol.setCellValueFactory(new PropertyValueFactory<>("status"));

        applicationsTable = new TableView<>();
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
        this.cpuXAxis.setForceZeroInRange(false);
        this.cpuXAxis.setAnimated(false);
        this.cpuXAxis.setAutoRanging(false);
        this.cpuXAxis.setTickUnit(200000);

        this.cpuYAxis.setLowerBound(0);
        this.cpuYAxis.setUpperBound(100);
        this.cpuYAxis.setAutoRanging(false);
        this.cpuYAxis.setTickUnit(20);

        this.cpuUsageLineChart = new LineChart<Number, Number>(this.cpuXAxis, this.cpuYAxis);
        this.cpuUsageLineChart.setCreateSymbols(false);
        this.cpuUsageLineChart.setAnimated(false);
        this.cpuUsageLineChart.setTitle("CPU Usage");

        this.cpuUsageSeries.setName("CPU Consumption");
        this.cpuUsageLineChart.getData().add(this.cpuUsageSeries);

        //      Memory Usage Line Chart
        this.memoryXAxis.setForceZeroInRange(false);
        this.memoryXAxis.setAnimated(false);
        this.memoryXAxis.setAutoRanging(false);
        this.memoryXAxis.setTickUnit(200000);

        this.memoryYAxis.setLowerBound(0);
        this.memoryYAxis.setUpperBound(this.hal.getMemory().getTotal() / this.BYTES_TO_GB);
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


        HBox totalMemoryBox = new HBox(new Label("Total Memory: "), this.totalMemoryValueLabel);
        HBox memoryUsedBox = new HBox(new Label("Memory Used: "), this.memoryUsedValueLabel);
        HBox memoryAvailable = new HBox(new Label("Memory Available: "), this.memoryAvailableValueLabel);
        VBox physicalMemoryComponents = new VBox();
        physicalMemoryComponents.getChildren().addAll(totalMemoryBox, memoryUsedBox, memoryAvailable);
        TitledPane physicalMemoryViewer = new TitledPane("Physical Memory", physicalMemoryComponents);
        physicalMemoryViewer.setMinWidth(300);
        physicalMemoryViewer.setMinHeight(200);
        physicalMemoryViewer.setCollapsible(false);

        HBox threadsBox = new HBox(new Label("Threads: "), this.numThreadsValueLabel);
        HBox processesBox = new HBox(new Label("Processes: "), this.numProcessesValueLabel);
        HBox upTimeBox = new HBox(new Label("Up Time: "), this.upTimeValueLabel);
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

    /**
     * The update method will act as a "loop"-style method which will be placed in a Timeline to execute on
     * an interval, defined by refreshInterval
     *
     * NOTE: PLEASE PUT ALL METHODS REGARDING UI UPDATING IN HERE
     */
    public void update() {
        this.applicationsTable.getItems().clear();
        this.processTable.getItems().clear();

        MemoryMetrics memoryValues = SystemCallDriver.getMemoryMetrics(hal.getMemory());
        this.setTotalMemoryValue(memoryValues.getTotalMemory());
        this.setMemoryUsedValue(memoryValues.getMemoryUsed());
        this.setMemoryAvailableValue(memoryValues.getMemoryAvailable());
        this.setNumThreadsValue(os.getThreadCount());
        this.setNumProcessesValue(os.getProcessCount());
        this.setUpTimeValue(FormatUtil.formatElapsedSecs(hal.getProcessor().getSystemUptime()));
        // get clock ticks and put it in array [clockTickValue,cpuUsageVal] and [clockTickVal, memUsage]
        this.updateCPULineChart(SystemCallDriver.getCPUUsage(this.hal));
        this.updateMemoryLineChart(SystemCallDriver.getMemoryUsage(this.hal.getMemory()));

        List<ApplicationModel> applications = SystemCallDriver
                .getApplications(SystemCallDriver
                        .getProcesses(this.os, this.hal.getMemory()));
        // TODO: From there nest Process list in Application model insert into db





        // Application TODO

        //function used to insert a System into the System table taking in the current system time, syscpuusage, the system uptime, total physical memory, free memory, total number of threads and processes
        double sysTime=0;
        double sysUpTime=0;
        double sysCpuUsage=0;
        double sysTotalMem=memoryValues.getTotalMemory();
        double sysFreemem=memoryValues.getMemoryAvailable();
        int sysNumThreads= os.getThreadCount();
        int sysNumProc=os.getProcessCount();
        try {
            int curSysId = SQLiteDBUtil.insertSystem(sysTime, sysUpTime, sysCpuUsage, sysTotalMem, sysFreemem, sysNumThreads, sysNumProc);
           // List<ApplicationModel> applications = applications = SystemCallDriver.getApplications(processes);
            // TODO: From there nest Process list in Application model insert into db
            for (ApplicationModel app : applications) {
                // update the table on the GUI and database at once to avoid creating two separate for-loops
                this.setApplicationTableContents(app);
                String appName = app.getName();
                String appStatus = app.getStatus();


                //function used to insert a Application into the Application table taking in the Applications name, the applications description , and the  System ID of the parent System
                int appID = SQLiteDBUtil.insertApplication(appName, appStatus, curSysId);

                List<ProcessModel> appProcesses = app.getProcesses();
                for (ProcessModel proc : appProcesses) {
                    this.setProcessTableContents(proc);
                    double procMem = Double.parseDouble(proc.getMemory());
                    ArrayList<Integer> procThreads = proc.getThreadUsages();
                    String procDesc = proc.getDescription();
                    int threadCount = Integer.parseInt(proc.getThreadCount());
                    String procStatus = proc.getState().toString();
                    int procId = SQLiteDBUtil.insertProcess(appID, procMem,threadCount , procDesc, procStatus);
                    //loop each thread to insert them and set mem usage to the procmem/n
                    for (int i = 0; i< threadCount;i++) {

                        int curThreadUsage;
                        if(procMem%threadCount!=0){
                            if(i==0){
                                curThreadUsage= (int) ((threadCount/threadCount)+procMem%threadCount);
                            }
                            else{
                                curThreadUsage= (int)threadCount/threadCount;
                            }

                        }
                        else{
                            curThreadUsage = (int) (procMem/threadCount);
                        }

                        SQLiteDBUtil.insertThread(procId,curThreadUsage);
                    }

                }

            }

        }
        catch (SQLException e) {
            e.printStackTrace();
        }

        // @everyone TODO
        // TODO: Figure out how we're gonna do system time
    }

    /**
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

    /**
     * @param active List containing the active processes to be placed in the table
     */
    public void setProcessTableContents(ProcessModel active) {
        this.processTable.getItems().add(active);
    }

    public void setApplicationTableContents(ApplicationModel active) {
        if (!this.applicationsTable.getItems().contains(active)) {
            this.applicationsTable.getItems().add(active);
        }
    }

    /**
     * @param newValue new total memory value
     */
    public void setTotalMemoryValue(long newValue) {
        this.totalMemoryValueLabel.setText(newValue + "");
    }

    /**
     * @param newValue new memory used value
     */
    public void setMemoryUsedValue(long newValue) { this.memoryUsedValueLabel.setText(newValue + ""); }

    /** setMemoryAvailableValue
     *
     * @param newValue new memory available value
     */
    public void setMemoryAvailableValue(double newValue) { this.memoryAvailableValueLabel.setText(newValue + ""); }

    /**
     * @param newValue new number of threads value
     */
    public void setNumThreadsValue(int newValue) {
        this.numThreadsValueLabel.setText(newValue + "");
    }

    /**
     * @param newValue new number of processes value
     */
    public void setNumProcessesValue(int newValue) {
        this.numProcessesValueLabel.setText(newValue + "");
    }

    /**
     * @param newValue new up time value
     */
    public void setUpTimeValue(String newValue) {
        this.upTimeValueLabel.setText(newValue + "");
    }

    /**
     * Creates an update scroll effect for the CPU usage line chart by resetting the upper and lower bounds of the
     * chart with new values
     * @param newUsage new value to be added to the line chart
     */
    private void updateCPULineChart(long[] newUsage) {
        this.cpuUsages.add(newUsage);
        long x = newUsage[0];
        long y = newUsage[1];
        this.cpuUsageSeries.getData().add(new XYChart.Data(x, y));
        this.cpuXAxis.setLowerBound(this.cpuUsages.get(0)[0]);
        this.cpuXAxis.setUpperBound(this.cpuUsages.get(this.cpuUsages.size() - 1)[0]);

        if (this.cpuUsages.size() > 5)
            this.cpuUsages.remove(0);
    }

    /**
     * Creates an update scroll effect for the memory usage line chart by resetting the upper and lower bounds of the
     * chart with new values
     * @param newUsage new value to be added to the line chart
     */
    private void updateMemoryLineChart(long[] newUsage) {
        this.memoryUsages.add(newUsage);
        long x = newUsage[0];
        long y = newUsage[1];
        this.memoryUsageSeries.getData().add(new XYChart.Data(x, y));
        this.memoryXAxis.setLowerBound(this.memoryUsages.get(0)[0]);
        this.memoryXAxis.setUpperBound(this.memoryUsages.get(this.memoryUsages.size() - 1)[0]);

        if (this.memoryUsages.size() > 5)
            this.memoryUsages.remove(0);
    }
}
