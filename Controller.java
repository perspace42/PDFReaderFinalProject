/*
Author: Scott Field
Name: Controller
Date: 09/21/2023
Version: 1.0
Purpose:
A class for storing all of the methods to be used when
the buttons in the program are pressed
*/

import java.io.File;
import java.net.URL;
import java.util.Optional;

import javafx.geometry.Insets;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.scene.control.Alert;

public class Controller{
    //Controller shouldn't need any member variables because it is just storing functions for the various buttons that the UI will contain later
    public Controller(){
    }

    //Function to increase the size of the pdf by a set amount
    public void onZoomInButtonPressed(PDFViewer fileViewer){
        //zooming in and zooming out are not yet implemented
    }

    //Function to decrease the size of the pdf by a set amount
    public void onZoomOutButtonPressed(PDFViewer fileViewer){
        //zooming in and zooming out are not yet implemented
    }

    //Function to increase or decrease the size of a pdf by a user given zoom value
    public void onSetZoomPressed(double zoomValue){
        //zooming in by an inputed value has not yet been implemented
    }

    //Function to load the next page
    public void onNextPageButtonPressed(PDFViewer fileViewer){
        fileViewer.incrementPage();
    }

    //Function to load the previous page
    public void onPreviousPageButtonPressed(PDFViewer fileViewer){
        fileViewer.decrementPage();
    }

    //Function to jump to a specific page
    public void onJumpToPage(PDFViewer fileViewer, int pageNumber){
        fileViewer.jumpToPage(pageNumber);
    }

    //Function to open a new pdf file from local directory
    public PDFReader onOpenButtonPressed(Stage primaryStage){
        //open the file
        FileChooser fileDialog = new FileChooser();
        // Set the title of the file dialog
        fileDialog.setTitle("Open File");
        // Set the initial directory of the file dialog
        fileDialog.setInitialDirectory(new File(System.getProperty("user.home")));
        // Get the file the user selected
        File selectedFile = fileDialog.showOpenDialog(primaryStage);
        //Add the file to the PDF Viewer if it is not null
        if (selectedFile != null){
            PDFReader myPdfReader = new PDFReader(selectedFile);
            //If the file the user selected is a valid pdf
            if (myPdfReader.isValid){
                return myPdfReader;
            
            //Otherwise display an alert to the user and wait for them to close it
            }else{
                Alert alert = new Alert(AlertType.ERROR);
                alert.setHeaderText("Invalid FileType");
                alert.setContentText("The File You Selected Was Not A Valid PDF");
                alert.showAndWait();
            }
        }

        //otherwise return null
        return null;
    }

    //Function to open a new pdf file from a web url
    public PDFReader onOpenFromURLButtonPressed(PDFReader myPdfReader){
        //Define The Custom Dialog Box
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Open PDF from URL");

        // Set the button types
        ButtonType openButtonType = new ButtonType("Open", ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(openButtonType, ButtonType.CANCEL);

        // Create the content for the dialog box
        HBox content = new HBox();
        content.setSpacing(10);
        content.setPadding(new Insets(10, 10, 10, 10));
        Label urlLabel = new Label("PDF URL:");
        TextField urlTextField = new TextField();
        content.getChildren().addAll(urlLabel, urlTextField);

        //add the content to the dialog box
        dialog.getDialogPane().setContent(content);

        // Set the result converter for the dialog box
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == openButtonType) {
                return urlTextField.getText();
            }
            return null;
        });

        // Show the dialog box and wait for a response
        Optional<String> result = dialog.showAndWait();
            
        //If the user put anything in the dialog box
        if (result.isPresent()){
            String urlString = result.get();
            URL url;
            //Attempt to assign the URL with the provided url string
            try{
                url = new URL(urlString);
            }catch(Exception e){
                //Display An Error Message That The URL Was Not Valid
                Alert alert = new Alert(AlertType.ERROR);
                alert.setHeaderText("PDF Not Found");
                alert.setContentText("The URL You Supplied Did Not Lead To A Valid PDF");
                alert.showAndWait();
                //if the url is invalid return null
                return null;
            }
            myPdfReader = new PDFReader(url);
            return myPdfReader;
        }

        //return null if the result is not present
        return null;
    }

    //Function to save a currently opened pdf file to a local directory (and also rename it)
    public void onSaveAsButtonPressed(){
        //save currently opened pdf file is not yet implemented
    }

    //Function to close the currently opened pdf file
    public void onCloseButtonPressed(PDFViewer fileViewer,Label rightPageLabel, TextField jumpToPageEntry){
       //clear the file reader
       fileViewer.fileReader.clear();
       //adjust the page labels
       rightPageLabel.setText("/" + fileViewer.fileReader.getPageCount());
       jumpToPageEntry.setText("0");
       //clear the currently open image
       fileViewer.setImage(null);
    }
}
