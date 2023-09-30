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
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

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

    //Function to load the next page
    public void onPreviousPageButtonPressed(PDFViewer fileViewer){
        fileViewer.decrementPage();
    }

    //Function to open a new pdf file from local directory
    public PDFViewer onOpenButtonPressed(Stage primaryStage, PDFReader myPdfReader, PDFViewer myPdfViewer){
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
            myPdfReader = new PDFReader(selectedFile);
            //If the file the user selected is a valid pdf
            if (myPdfReader.isValid){
                //Add the pdf reader to the PDF Viewer
                myPdfViewer = new PDFViewer(myPdfReader);
                //return the updated pdf viewer
                return myPdfViewer;
            }
        }
        //otherwise return null
        return null;
    }

    //Function to open a new pdf file from a web url
    public PDFViewer onOpenFromURLButtonPressed(PDFReader myPdfReader, PDFViewer myPdfViewer){
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
                //if the url is invalid return null
                return null;
            }
            myPdfReader = new PDFReader(url);
            myPdfViewer = new PDFViewer(myPdfReader);
            return myPdfViewer;
        }

        //return null if the result is not present
        return null;
    }

    //Function to save a currently opened pdf file to a local directory (and also rename it)
    public void onSaveAsButtonPressed(){
        //save currently opened pdf file is not yet implemented
    }

    //Function to close the currently opened pdf file
    public void onCloseButtonPressed(){
        //close currently opened pdf file is not yet implemented
    }

    //Function to close the program
    public void onExitButtonPressed(){
        //close program is not yet implemented
    }

}
