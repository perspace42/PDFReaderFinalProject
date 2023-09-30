/*
Author: Scott Field
Name: Main
Date: 09/30/2023
Version: 1.0
Purpose:
A class for setting up the UI and the program
main loop using JavaFX
*/

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.geometry.Pos;
import javafx.scene.layout.VBox;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;

import java.io.File;


public class Main extends Application {
    @Override
    public void start(Stage primaryStage){
        int windowHeight = 900;
        int windowWidth = 900;

        //If those tests don't result in errors run the main program

        //Set the Controller
        Controller binding = new Controller();

        //Load The Image using the PDFReader 
        //remove the arguments in this PDFReader to not see the starting pdf
        PDFReader fileReader = new PDFReader(new File("temp/test.pdf"));

        //Set The Image to be displayed using the PDFViewer
        PDFViewer fileViewer = new PDFViewer(fileReader);

        //set the buttons to be cycle between pages
        Button previousPageButton = new Button("Previous");
        Button nextPageButton = new Button("Next");

        //set the labels and text entry to jump to a page
        Label leftPageLabel = new Label("Page");
        Label rightPageLabel = new Label(" / ");
        //comment out this line to not see the starting pdf pages
        rightPageLabel.setText("/" + fileReader.getPageCount());
        TextField jumpToPageEntry = new TextField();

        //Create The Formatter To Ensure That Only Numbers Can Be Entered Into The JumpToPage Field
        TextFormatter<String> formatter = new TextFormatter<>(change -> {
            String text = change.getText();
            if (text.matches("[0-9]*")) {
                return change;
            }
            return null;
        });

        //Add The Formatter To The Text Field
        jumpToPageEntry.setTextFormatter(formatter);

        // Create the VBox to store the PDFViewer and the buttons for the test
        VBox pdfBox = new VBox();
        pdfBox.getChildren().addAll(fileViewer);
        pdfBox.setAlignment(Pos.CENTER);

        //Create the HBox to store the zoom set TextField Label and Buttons
        Label zoomLabel = new Label("Zoom");
        TextField zoomTextEntry = new TextField();
        Button zoomInButton = new Button("Zoom In");
        Button zoomOutButton = new Button("Zoom Out");
        HBox zoomDisplay = new HBox(zoomLabel,zoomTextEntry,zoomInButton,zoomOutButton);
        zoomDisplay.setAlignment(Pos.CENTER_RIGHT);
        
        // Create the HBox to store the buttons for cycling between pages
        HBox buttonDisplay = new HBox(previousPageButton, nextPageButton);
        buttonDisplay.setAlignment(Pos.CENTER);
        // Create the HBox to store the text entry for jumping to pages
        HBox jumpToPageDisplay = new HBox(leftPageLabel,jumpToPageEntry,rightPageLabel);
        jumpToPageDisplay.setAlignment(Pos.CENTER);
        
        // Create the VBox to store the HBox
        VBox bottomBox = new VBox(buttonDisplay,zoomDisplay,jumpToPageDisplay);

        // Create the file menu
        Menu fileMenu = new Menu("File");
        MenuItem openFileItem = new MenuItem("Open");
        MenuItem openFileUrlItem = new MenuItem("Open From URL");
        MenuItem saveAsFileItem = new MenuItem("Save As");
        MenuItem closeMenuItem = new MenuItem("Close");
        MenuItem exitItem = new MenuItem("Exit");
        // Add the items to the file menu
        fileMenu.getItems().addAll(openFileItem, openFileUrlItem, saveAsFileItem, closeMenuItem, exitItem);

        // Add the file menu to the menu bar
        MenuBar fileMenuBar = new MenuBar();
        fileMenuBar.getMenus().add(fileMenu);
        // create the VBox to store the menu bar
        VBox menuBox = new VBox(fileMenuBar);

         // Create the ScrollPane and set its content to the pdfBox
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setContent(pdfBox);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);

        // Create the BorderPane to store the menuBox, pdfBox, and bottomBox
        BorderPane root = new BorderPane();
        root.setTop(menuBox);
        root.setCenter(scrollPane);
        root.setBottom(bottomBox);

        // Set the PDFViewer to fit to the width and height of the pdfBox
        fileViewer.setPreserveRatio(true);
        fileViewer.setFitHeight(pdfBox.getHeight()-(menuBox.getHeight()+bottomBox.getHeight()));
        fileViewer.setFitWidth(windowWidth);

        // Create the Scene and set it on the Stage
        Scene scene = new Scene(root, windowHeight, windowWidth);
       
        primaryStage.setScene(scene);
        primaryStage.show();

        //Event Listeners
        //set the methods for previous and next page
        previousPageButton.setOnAction(e ->
            binding.onPreviousPageButtonPressed(fileViewer)
        );

        nextPageButton.setOnAction(e ->
            binding.onNextPageButtonPressed(fileViewer)
        );

        //set the method for opening a file from a local path
        openFileItem.setOnAction(e-> {
            PDFViewer newFileViewer = binding.onOpenButtonPressed(primaryStage,fileReader,fileViewer);
            //As long as there is something new to add
            if (newFileViewer != null){
                //clear the current fileViewer
                pdfBox.getChildren().clear();
                //add the new file viewer
                pdfBox.getChildren().addAll(newFileViewer);
                newFileViewer.setPreserveRatio(true);
                newFileViewer.setFitHeight(pdfBox.getHeight()-(menuBox.getHeight()+bottomBox.getHeight()));
                newFileViewer.setFitWidth(windowWidth);
                rightPageLabel.setText("/" + newFileViewer.fileReader.getPageCount());
                jumpToPageEntry.setText("1");
            }
        });

        //set the method for opening a file from a URL (Currently unfinished)
        openFileUrlItem.setOnAction(e ->{
            PDFViewer newFileViewer = binding.onOpenFromURLButtonPressed(fileReader, fileViewer);
            //As long as there is something new to add
            if (newFileViewer!= null){
                //clear the current fileViewer
                pdfBox.getChildren().clear();
                //add the new file viewer
                pdfBox.getChildren().addAll(newFileViewer);
                newFileViewer.setPreserveRatio(true);
                newFileViewer.setFitHeight(pdfBox.getHeight()-(menuBox.getHeight()+bottomBox.getHeight()));
                newFileViewer.setFitWidth(windowWidth);
                rightPageLabel.setText("/" + newFileViewer.fileReader.getPageCount());
                jumpToPageEntry.setText("1");
            }
        });

    }

    public static void main(String[] args) {       
        //Run tests on startup
        testInvalidInput();
        testValidInput();
        //run main program
        launch(args);
    }

    //Method to test classes on startup
    public static void testInvalidInput(){
        System.out.println("Invalid Input Test (Program should continue even with incorrect input and all values should be zero)");
        //test class validation
        PDFReader testReader = new PDFReader(new File("temp/IncorrectFileType.txt"));
        PDFViewer testViewer = new PDFViewer(testReader);

        //test class data
        System.out.println("Current Page Height: " + testReader.getCurrentPageHeight());
        System.out.println("Current Page Width: " + testReader.getCurrentPageWidth());
        System.out.println("Current Page Number: " + testReader.getCurrentPageNumber());
    }

    public static void testValidInput(){
        System.out.println("Valid Input Test (Program should continue with correct input and only Page Number should be zero)");
        //test class validation
        PDFReader testReader = new PDFReader(new File("temp/test.pdf"));
        PDFViewer testViewer = new PDFViewer(testReader);

        //test class data
        System.out.println("Current Page Height: " + testReader.getCurrentPageHeight());
        System.out.println("Current Page Width: " + testReader.getCurrentPageWidth());
        System.out.println("Current Page Number: " + testReader.getCurrentPageNumber());
    }

}
