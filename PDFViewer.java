/*
Author: Scott Field
Name: PDFViewer
Date: 09/21/2023
Version: 1.0
Purpose:
A class to manage the viewing of a PDF Document by taking a
PDFReader as an argument. and then displays the PDF Document
pages by extending ImageView
*/

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class PDFViewer extends ImageView{
    public PDFReader fileReader;
    double zoomFactor = 0.0;

    //empty constructor
    public PDFViewer(){
    }

    //constructor with PDFReader
    public PDFViewer(PDFReader newFileReader){
        //call an empty ImageView constructor
        super();
        if (newFileReader.getCurrentPage() != null){
            this.setImage(newFileReader.getCurrentPage());
        }else{
            System.out.println("No Image Was Found In The PDF Reader");
        }
        //set the fileReader
        fileReader = newFileReader;
    }
    
    //jump to a given page
    public void jumpToPage(int page){
        //If the current page is not equal to the page to be jumped to
        if (fileReader.getCurrentPageNumber() != page){
            //Set the image to the new page (getPage returns the same Image if the requested page is out of the document page range)
            Image newImage = fileReader.getPage(page);
            //If the images are different
            if (newImage != fileReader.getCurrentPage()){
                this.setImage(fileReader.getPage(page));
                fileReader.setPage(page);

            }
        }
    }
    
    //load the next page
    public void incrementPage(){
        this.jumpToPage(fileReader.getCurrentPageNumber() + 1);
    }

    //load the previous page
    public void decrementPage(){
        this.jumpToPage(fileReader.getCurrentPageNumber() - 1);
    }

}
