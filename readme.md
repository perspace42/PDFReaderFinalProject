Dependencies
- JavaFX
- PDFBox 3.0.0

The command to run the jar file is as follows (substitute the path for your paths to javaFX and pdfBox):
java --module-path c:\\Users\\exampleUser\\Documents\\JavaFX\\openjfx-17.0.8_windows-x64_bin-sdk\\javafx-sdk-17.0.8\\lib/ --add-modules javafx.controls,javafx.base,javafx.fxml,javafx.graphics,javafx.media,javafx.web -classpath c:\\Users\\exampleUser\\Documents\\JavaFX\\openjfx-17.0.8_windows-x64_bin-sdk\\javafx-sdk-17.0.8\\lib\\pdfbox-app-3.0.0 -jar --enable-preview PDFReaderFinalProject.jar

Note:
The program in its current state outputs alot of warnings from PDFBox on load, and takes a few moments to compile and load the window, give it roughly 30 seconds to a minute and then the program will load.
