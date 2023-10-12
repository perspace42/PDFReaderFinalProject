/*
Author: Scott Field
Name: Main
Date: 10/10/2023
Version: 1.0
Purpose:
A class for setting up the UI and the program
main loop using JavaFX
*/

import javafx.application.Application;
import javafx.application.Platform;
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
import javafx.scene.input.KeyCode;

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
        //remove the /**/ in this PDFReader to see the starting pdf
        PDFReader fileReader = new PDFReader(/*new File("temp/test.pdf")*/);

        //Set The Image to be displayed using the PDFViewer
        PDFViewer fileViewer = new PDFViewer(fileReader);

        //set the buttons to be cycle between pages
        Button previousPageButton = new Button("Previous");
        Button nextPageButton = new Button("Next");

        //set the labels and text entry to jump to a page
        Label leftPageLabel = new Label("Page");
        Label rightPageLabel = new Label(" /0 ");

        //uncomment this line to not see the starting pdf pages
        //rightPageLabel.setText("/" + fileReader.getPageCount());

        TextField jumpToPageEntry = new TextField();

        //Create The Formatter To Ensure That Only Numbers Can Be Entered Into The JumpToPage and ZoomLevel Field
        TextFormatter<String> formatter = new TextFormatter<>(change -> {
            String text = change.getText();
            if (text.matches("[0-9]*")) {
                return change;
            }
            return null;
        });

        //Add The Formatter To The Text Field
        jumpToPageEntry.setTextFormatter(formatter);
        //Set The Intial Value To 0
        jumpToPageEntry.setText("0");

        // Create the VBox to store the PDFViewer and the buttons for the test
        VBox pdfBox = new VBox();
        pdfBox.getChildren().addAll(fileViewer);
        pdfBox.setAlignment(Pos.CENTER);

        //Create the HBox to store the zoom set TextField Label and Buttons
        Label zoomLabel = new Label("Zoom %");
        TextField zoomTextEntry = new TextField();
        zoomTextEntry.setTextFormatter(formatter);

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
        MenuItem closeFileItem = new MenuItem("Close");
        MenuItem exitItem = new MenuItem("Exit");
        // Add the items to the file menu
        fileMenu.getItems().addAll(openFileItem, openFileUrlItem, saveAsFileItem, closeFileItem, exitItem);

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
        //set the methods for previous page
        previousPageButton.setOnAction(e ->{
            //go to the previous page
            binding.onPreviousPageButtonPressed(fileViewer);
            //set the page tracker entry to match
            jumpToPageEntry.setText(Integer.toString(fileViewer.fileReader.getCurrentPageNumber() + 1));
        });
        //set the methods for next page
        nextPageButton.setOnAction(e ->{
            //go to the next page
            binding.onNextPageButtonPressed(fileViewer);
            //set the page tracker entry to match
            jumpToPageEntry.setText(Integer.toString(fileViewer.fileReader.getCurrentPageNumber() + 1));
        });
        //set the method for jump to page when the enter key is clicked and the jumpToPageEntry is in scope
        jumpToPageEntry.setOnKeyPressed(e->{
            //When The Enter Key Is Pressed
            if (e.getCode() == KeyCode.ENTER){
                int pageNumber = -1;
                //Try To Get A Number From The Text Entry
                try{
                    pageNumber += Integer.parseInt(jumpToPageEntry.getText());
                }catch(NumberFormatException exception){
                    //Exit the loop if the number is not a valid integer number
                    return;
                }
                binding.onJumpToPage(fileViewer, pageNumber);
            }
        });

        //set the method for opening a file from a local path
        openFileItem.setOnAction(e-> {
            //Attempt to read the file selected by the user
            PDFReader newFileReader = binding.onOpenButtonPressed(primaryStage);
            //As long as there is something new to add
            if (newFileReader != null){
                //set the pdf viewer to use the new file reader
                fileViewer.setFileReader(newFileReader);
                //it is not necessary to clear the box
                //pdfBox.getChildren().clear();
                //pdfBox.getChildren().addAll(fileViewer);
                //adjust the labels to match
                rightPageLabel.setText("/" + fileViewer.fileReader.getPageCount());
                jumpToPageEntry.setText("1");
            }
        });

        //set the method for opening a file from a URL (Currently unfinished)
        openFileUrlItem.setOnAction(e ->{
            PDFReader newFileReader = binding.onOpenFromURLButtonPressed(fileReader);
            //As long as there is something new to add
            if (newFileReader != null){
                //set the pdf viewer to use the new file reader
                fileViewer.setFileReader(newFileReader);
                //it is not necessary to clear the box
                //pdfBox.getChildren().clear();
                //pdfBox.getChildren().addAll(fileViewer);
                rightPageLabel.setText("/" + fileViewer.fileReader.getPageCount());
                jumpToPageEntry.setText("1");
            }
        });

        //set the method for closing the currently opened file
        closeFileItem.setOnAction(e->{
            //clear the currently opened file and adjust the labels to match
            binding.onCloseButtonPressed(fileViewer, rightPageLabel, jumpToPageEntry);
        });

        //set the method for saving the currently opened file to a new location
        saveAsFileItem.setOnAction(e->{
            binding.onSaveAsButtonPressed(fileViewer.fileReader.getPDFFile());
        });

        //set the method for zooming in the currently opened file
        zoomInButton.setOnAction(e->{
            //get the current size
            double currentHeight = fileViewer.getFitHeight();
            double currentWidth = fileViewer.getFitWidth();

            //get the new size (current size plus 10%)
            double newHeight = currentHeight * 1.1;
            double newWidth = currentWidth * 1.1;

            //adjust the zoom level
            fileViewer.zoomLevel += 10;

            //output the zoom level to the zoome entry widget
            zoomTextEntry.setText(Integer.toString(fileViewer.zoomLevel));

            //set the file Viewer New Size
            fileViewer.setFitHeight(newHeight);
            fileViewer.setFitWidth(newWidth);
        });

        //set the method for zooming out in the currently opened file
        zoomOutButton.setOnAction(e->{
            //get the current size
            double currentHeight = fileViewer.getFitHeight();
            double currentWidth = fileViewer.getFitWidth();

            //get the new size (current size minus 10%)
            double newHeight = currentHeight * 0.9;
            double newWidth = currentWidth * 0.9;

            //adjust the zoom level
            fileViewer.zoomLevel -= 10;

            //output the zoom level to the zoome entry widget
            zoomTextEntry.setText(Integer.toString(fileViewer.zoomLevel));

            //set the file Viewer New Size
            fileViewer.setFitHeight(newHeight);
            fileViewer.setFitWidth(newWidth);
        });

        //set the method for zooming to a certain level

        //set the method for exiting the program
        exitItem.setOnAction(e->{
            //exit the program
            Platform.exit();
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
