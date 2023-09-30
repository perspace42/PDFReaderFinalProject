/*
Author: Scott Field
Name: PDFReader
Date: 09/21/2023
Version: 1.0
Purpose:
A class to implement the methods stored in the PDFDocument class
with the goal of being able to load the PDFDocument and
obtain all of its attributes. Then those attributes are stored in images that the
JavaFX UI can both read and display

*/
import java.io.File;
import java.net.*;
import java.awt.image.BufferedImage;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;


public class PDFReader extends PDFDocument {
    
    private Image currentPageImage;
    private int currentPageNumber = 0;
    private int pageCount = 0;

    //Store current page height
    private double currentPageHeight = 0;
    private double currentPageWidth = 0;
    
    //The arraylist of images to be displayed
    private Image[] pageImageList;

    //Default Constructor
    public PDFReader(){
    }

    //PDF From File
    public PDFReader(File file){
        //use the PDFDocument constructor to check the validity of the file
        super(file); 
        //If the pdf file is valid
        if (isValid){
            //Attempt to assign the member variables using the PDDocument
            try {
                //get the pdf
                PDDocument document = Loader.loadPDF(file);
                //set the page count
                pageCount = document.getNumberOfPages();
                //set the pdfRenderer   
                PDFRenderer pdfRenderer = new PDFRenderer(document);
                //set the page image list length
                pageImageList = new Image[pageCount];
                //for each page in the document
                for (int i = 0; i < pageCount; i++) {
                    //render the pdfPage at index i to an BufferedImage with a DPI of 300
                    BufferedImage bufferedImage = pdfRenderer.renderImageWithDPI(i, 300);
                    //convert the bufferedimage to a JavaFX image that can be displayed
                    Image image = SwingFXUtils.toFXImage(bufferedImage, null);
                    //add the image to the list of images
                    this.pageImageList[i] = image;
                }
                //set the current page image
                this.setPage(0);
                //close the document
                document.close();
            }catch (Exception e) {
                System.out.println("Something went wrong attempting to get a PDDocument from the PDF");
                e.printStackTrace();
            }
        }
    }

    //Load PDF From URL
    public PDFReader(URL url){
        //use the PDFDocument constructor to check the validity of the file
        super(url);
        //if the pdf file is valid
        if (isValid){
            //Attempt to assign the member variables using the PDDocument
            try {
                //get the pdf
                PDDocument document = Loader.loadPDF(pdfFile);
                //set the page count
                pageCount = document.getNumberOfPages();
                //set the pdfRenderer   
                PDFRenderer pdfRenderer = new PDFRenderer(document);
                //set the page image list length
                pageImageList = new Image[pageCount];
                //for each page in the document
                for (int i = 0; i <= pageCount; i++) {
                    //render the pdfPage at index i to an BufferedImage with a DPI of 600
                    BufferedImage bufferedImage = pdfRenderer.renderImageWithDPI(i, 600);
                    //convert the bufferedimage to a JavaFX image that can be displayed
                    Image image = SwingFXUtils.toFXImage(bufferedImage, null);
                    //add the image to the list of images
                    pageImageList[i] = image;
                }
                //set the current page to the first page
                this.setPage(0);
                //close the document
                document.close();
            }catch (Exception e) {
                System.out.println("Something went wrong attempting to get a PDDocument from the PDF");
                e.printStackTrace();
            }
        }
    }

    //get the current page
    public Image getCurrentPage(){
        return currentPageImage;
    }
    //get the requested page
    public Image getPage(int newPageNumber){
        //If the requested page is within the pdf
        if (newPageNumber < pageCount && newPageNumber >= 0){
            //get the image of the requested page
            Image requestedPage = pageImageList[newPageNumber]; //newPageNumber is subtracted by 1 since list indexes start at 0
            //return the page
            return requestedPage;
        //otherwise if the requested page is not within the pdf
        }else{
            //return the current page
            return currentPageImage;
        }
    }

    //set the current page to a new page
    public void setPage(int newPageNumber){
        currentPageImage = getPage(newPageNumber);
        //set the page number to the current page number
        currentPageNumber = newPageNumber;
        //adjust the height and width
        currentPageHeight = currentPageImage.getHeight(); 
        currentPageWidth = currentPageImage.getWidth(); 
    }

    //Get the number of pages in the PDF
    public int getPageCount() {
        return pageCount;
    }

    //Get the number of the current Page
    public int getCurrentPageNumber(){
        return currentPageNumber;
    }

    //Get the current page height
    public double getCurrentPageHeight(){
        return currentPageHeight;
    }

    //Get the current page width
    public double getCurrentPageWidth(){
        return currentPageWidth;
    }

    

}
