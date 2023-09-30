/*
Author: Scott Field
Name: PDFDocument
Date: 09/21/2023
Version: 1.0
Purpose:
A class to storing the methods for loading a PDF from a File
or from a web URL
*/
import java.net.*;
import java.io.File;
import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.FileInputStream;
import java.io.InputStreamReader;

public abstract class PDFDocument {
    // Declare pdfFile as an instance variable
    protected File pdfFile;
    protected String pdfPath;
    protected Boolean isValid = false;

    //Empty Constructor
    public PDFDocument() {
        // Initialize pdfFile and pdfPath in the constructor
        pdfFile = null;
        pdfPath = null;
        isValid = false;
    }

    //Local File Constructor
    public PDFDocument(File newFile){
        isValid = this.loadPDF(newFile);
    }

    //URL Constructor
    public PDFDocument(URL myUrl){
        isValid = this.loadPDF(myUrl);
    }

    //Load A PDF from a local file path 
    public boolean loadPDF(File newFile){
        if (isPDF(newFile)){
            pdfFile = newFile;
            pdfPath = newFile.getAbsolutePath();
            return true;
        }else{
            return false;
        }
    }
    
    //Load a PDF from a URL
    public boolean loadPDF(URL myUrl) {
        try {
            //Open a connection to the URL
            URLConnection connection = myUrl.openConnection();
            connection.connect();
    
            // Check if the content type is PDF
            String contentType = connection.getContentType();
            if (contentType != null && contentType.equals("application/pdf")) {
                // Attempt to Read the PDF content from the URL
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String line;
                StringBuilder stringBuilder = new StringBuilder();

                while ((line = bufferedReader.readLine()) != null) {
                    stringBuilder.append(line);
                }

                String pdfContent = stringBuilder.toString();
    
                // Create a temporary file to store the PDF content
                File tempFile = File.createTempFile("temp", ".pdf");
                // Add the contents to the temporary file
                FileWriter fileWriter = new FileWriter(tempFile);
                fileWriter.write(pdfContent);
                fileWriter.close();

                if (isPDF(tempFile)) {
                    //If so save the temporary file to the class variables
                    pdfFile = tempFile;
                    pdfPath = tempFile.getAbsolutePath(); 
                    return true;

                //Otherwise output the error and return false
                }else{
                    System.out.println("Error: the file at URL " + myUrl.toString() + " is not a PDF file");
                    return false;
                }

            //If the content type is not a pdf or doesn't exist then their is no pdf file to load
            }else{
                System.out.println("Error: the URL " + myUrl.toString() + " does not point to a PDF file");
                return false;
            }
  
        //If their are any problems throw the error    
        }catch (Exception e) {
            System.out.println("Error: a problem occurred while loading the PDF file from URL " + myUrl.toString());
            e.printStackTrace();
            return false;
        }
    }
    
    //Checks the header of the file to determine if the file is a PDF
    private boolean isPDF(File file){
        String filePath = "";

        try{
            filePath = file.getAbsolutePath();
        }catch(Exception e){
            System.out.println("The file contains no file path");
            e.printStackTrace();
            return false;
        }

        //Ensures That The File Path Leads To A File Not To A Directory
        if (!file.isFile()) {
            System.out.println("Error: the file at location: " + filePath + " does not lead to a file");
            return false;
        }
        //Ensures That The File Is Set To Readable (and therefore can be read)
        if (!file.canRead()) {
            System.out.println("Error: the file at location: " + filePath + " is not readable");
            return false;
        }
        //Ensures that the file has a minimum length of 4 bytes (The PDF Header is %PDF)
        if (file.length() < 4) {
            System.out.println("Error: the file at location: " + filePath + " does not contain the %PDF header");
            return false;
        }

        //set the variable to store the header
        byte[] header = new byte[4];
        //attempt to get the header from the input stream
        try {
            FileInputStream inputStream = new FileInputStream(file);
            inputStream.read(header, 0, 4);
            inputStream.close();
        }
        //If a problem occurs while reading the file return false
        catch(Exception e){
            System.out.println("Error: a problem occured reading the file at location: " + filePath);
            e.printStackTrace();
            return false;
        }

        //Convert the header to a String
        String headerString = new String(header);

        //Return if the file is a pdf (if the header contains %PDF)
        return headerString.equals("%PDF");
    }
}


