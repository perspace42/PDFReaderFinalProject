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
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
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
    //Controller doesn't need any member variables because it is just storing functions for the various buttons that the UI uses
    public Controller(){
    }

    //Function to increase the size of the pdf by a set amount
    public void onZoomInButtonPressed(PDFViewer fileViewer, TextField zoomTextEntry){
        //get the current size
        double originalHeight = fileViewer.getFitHeight() / (fileViewer.zoomLevel / 100.0);
        double originalWidth  = fileViewer.getFitWidth() / (fileViewer.zoomLevel / 100.0);

        //adjust the zoom level
        fileViewer.zoomLevel += 10;

        //get the new size (current size minus 10%)
        double newHeight = originalHeight * (fileViewer.zoomLevel / 100.0);
        double newWidth = originalWidth * (fileViewer.zoomLevel / 100.0);

        //output the zoom level to the zoome entry widget
        zoomTextEntry.setText(Integer.toString(fileViewer.zoomLevel));

        //set the file Viewer New Size
        fileViewer.setFitHeight(newHeight);
        fileViewer.setFitWidth(newWidth);
    }

    //Function to decrease the size of the pdf by a set amount
    public void onZoomOutButtonPressed(PDFViewer fileViewer, TextField zoomTextEntry){
        //Ensure that the zoom level is not reduced to 0
        if (fileViewer.zoomLevel >= 11){
            //get the current size
            double originalHeight = fileViewer.getFitHeight() / (fileViewer.zoomLevel / 100.0);
            double originalWidth  = fileViewer.getFitWidth() / (fileViewer.zoomLevel / 100.0);

            //adjust the zoom level
            fileViewer.zoomLevel -= 10;

            //get the new size (current size minus 10%)
            double newHeight = originalHeight * (fileViewer.zoomLevel / 100.0);
            double newWidth = originalWidth * (fileViewer.zoomLevel / 100.0);

            //output the zoom level to the zoome entry widget
            zoomTextEntry.setText(Integer.toString(fileViewer.zoomLevel));

            //set the file Viewer New Size
            fileViewer.setFitHeight(newHeight);
            fileViewer.setFitWidth(newWidth);
        //Alert The User That Zoom Cannot Be Decreased Below 1% (if zoom is < 11 decreasing it by 10% will put it below 1%)
        }else{
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setHeaderText("Zoom Level Exception");
            alert.setContentText("Zoom cannot be decreased by 10% if that would place it below 1%");
            alert.showAndWait();
        }
    }

    //Function to increase or decrease the size of a pdf by a user given zoom value
    public void onSetZoomPressed(PDFViewer fileViewer, TextField zoomTextEntry){
        String inputString = zoomTextEntry.getText();
        //if the user has input something
        if (!inputString.isEmpty()) {
            try {
                //attempt to get the number from the input
                int inputNumber = Integer.parseInt(inputString);
                //as long as the number is greater than 0 calculate the zoom level
                if (inputNumber > 0) {
                    //get the original height and width
                    double originalHeight = fileViewer.getFitHeight() / (fileViewer.zoomLevel / 100.0);
                    double originalWidth = fileViewer.getFitWidth() / (fileViewer.zoomLevel / 100.0);
                            
                    //get the new height and width from that
                    double newHeight = originalHeight * (inputNumber / 100.0);
                    double newWidth = originalWidth * (inputNumber / 100.0);

                    //set the file viewer new size
                    fileViewer.zoomLevel = inputNumber;
                    fileViewer.setFitHeight(newHeight);
                    fileViewer.setFitWidth(newWidth);
                }
            //If there is an error exit the loop
            }catch (NumberFormatException ex) {
                return;
            }
        }
        /* 
        This is placed here in case the user inputs 0 or "" to reset the zoom text entry to its original value
        It will also set the new zoom value provided the user has entered a valid number 
        */
        zoomTextEntry.setText(Integer.toString(fileViewer.zoomLevel));
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
            //If The URL did not lead to a valid pdf or was invalid
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

    //Function to save a currently opened pdf file to a local directory (and also rename it if the user changes the name)
    public void onSaveAsButtonPressed(File pdfFile){
        Alert alert;

        if (pdfFile != null){
            //Set the file save dialog
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Save PDF File");
            fileChooser.setInitialFileName(pdfFile.getName());
            //Set the dialog to show that the file will be saved as a pdf file
            fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("PDF Files", "*.pdf"));            
            //Open the file save dialog
            File selectedFile = fileChooser.showSaveDialog(null);
            
            //If the user selects a file location
            if (selectedFile != null) {
                //If the selected file name doesn't end with .pdf
                if (!selectedFile.getName().endsWith(".pdf")) {
                    //add .pdf to the end of the file
                    selectedFile = new File(selectedFile.getAbsolutePath() + ".pdf");
                }
                // Copy the pdfFile data to the selectedFile data
                try {
                    Files.copy(pdfFile.toPath(), selectedFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                // If Their is a problem copying the data log the error and alert the user
                } catch (IOException e) {
                    e.printStackTrace();
                    alert = new Alert(AlertType.ERROR);
                    alert.setHeaderText("File Data Save Error");
                    alert.setContentText("A was a problem saving the file data to the new file location, please try again later.");
                    alert.showAndWait();
                }
            }

        //If no file has been loaded to the user Alert them that they need to load a file before saving one
        }else{
            alert = new Alert(AlertType.INFORMATION);
            alert.setHeaderText("No File Loaded");
            alert.setContentText("Please open a PDF File before attempting to Save it to a new location ");
            alert.showAndWait();
        }
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
