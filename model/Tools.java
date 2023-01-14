package model;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;


import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Optional;

/**
 * A class containing useful tools that are needed across multiple classes.
 */
public class Tools
{
    // Build Info: Can be used to print information such as version number
    // Demonstration: Use to show proper functionality
    // Error: Use to signal program has encountered an issue
    // Info: Use to show useful information
    public enum MsgType {BUILD_INFO, DEMONSTRATION, ERROR, INFO}


    /**
     *  Print error message with OK button.
     * @param messageTitle A title given to the error.
     * @param messageDesc Provide a description explaining the error.
     */
    public static void errorMessage(String messageTitle, String messageDesc)  // Print error message with OK button
    {
        // Title is on top, description is below next to OK button
        Alert errorAlert = new Alert(Alert.AlertType.ERROR);

        errorAlert.setTitle("Issue");
        errorAlert.setHeaderText(messageTitle);
        errorAlert.setContentText(messageDesc);
        errorAlert.showAndWait();
    }


    /**
     *  Prints confirmation message with OK and CANCEL button.
     * @param messageTitle A title given to message.
     * @param messageDesc Provide a description about the buttons, appears below messageTitle.
     * @return returns true if user presses OK, false if user selects CANCEL.
     */
    public static boolean confirmationMessage(String messageTitle, String messageDesc)
    {
        Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);

        confirmationAlert.setTitle("Confirmation");
        confirmationAlert.setHeaderText(messageTitle);
        confirmationAlert.setContentText(messageDesc);

        Optional<ButtonType> result = confirmationAlert.showAndWait();
        ButtonType button = result.orElse(ButtonType.CANCEL);


        if (button == ButtonType.OK)
        {
            return(true);
        }
        else
        {
            return(false);
        }
    }


    /**
     *  Prints informative message, can be used to tell the user an event has occurred.
     * @param messageTitle A title given to message.
     * @param messageDesc Provide a description, appears below messageTitle.
     */
    public static void infoMessage(String messageTitle, String messageDesc)
    {
        Alert infoAlert = new Alert(Alert.AlertType.INFORMATION);

        infoAlert.setTitle("Notification");
        infoAlert.setHeaderText(messageTitle);
        infoAlert.setContentText(messageDesc);
        infoAlert.showAndWait();
    }


    /**
     *  Prints informative message, can be used to tell the user an event has occurred.
     * @param msgType An enum with three types: DEMONSTRATION, INFO, ERROR
     * @param messageDesc A description.
     * @param location The class and method from where the message is being printed.
     */
    public static void consoleMessage(MsgType msgType, String messageDesc, String location)  // Prints message to console if Globals.debugMode is true
    {
        if (Globals.debugMode) {

            if(msgType != MsgType.BUILD_INFO) {  // BUILD_INFO does not have same visual formatting
                System.out.println("____");
            }
            System.out.println(msgType.name() + ":> " + messageDesc + " @:> " + location);
        }
    }


    /**
     *  Attempts to create a file that can be written to.
     *  If filename already exists, nothing will be created.
     * @param pathName The name of the file to be created.
     * @return true if new file was created, false if filename already exists, or creation failed.
     */
    public static boolean createFile(String pathName) {

        if (!Globals.generateFiles) {  // file generation can be disabled
            return false;
        }

        try {

            File file = new File(pathName);  // Attempt to create file

            if (file.createNewFile()) {  // File does not exist
                consoleMessage(MsgType.DEMONSTRATION, "New file created: " + pathName, "Tools.createFile()");
                return true;
            }  else {  // File already exists
                return false;
            }
        }
        catch (IOException e) {
            consoleMessage(MsgType.ERROR, "Failed creating file", "Tools.createFile()");
        }

        return false;
    }


    /**
     *  Attempts to write string data to a file.
     * @param filename The name of the file to write to.
     * @param contentsToWrite String to be written to file.
     * @param endWithNewLine The string written can be terminated with a new line.
     */
    public static void writeToFile(String filename, String contentsToWrite, Boolean endWithNewLine) {

        if (!Globals.generateFiles) {  // file generation can be disabled
            return;
        }

        try {

            FileWriter fileWriter = new FileWriter(filename, true); // Attempt to open file

            if (endWithNewLine) {
                fileWriter.write(contentsToWrite + "\n");
            }
            else {
                fileWriter.write(contentsToWrite);
            }

            fileWriter.close();  // Always close file after using it

            consoleMessage(MsgType.DEMONSTRATION, "\"" + contentsToWrite + "\" Has been written to file: " + filename, "Tools.writeToFile()");

        } catch (IOException e) {

            consoleMessage(MsgType.ERROR, "Failed writing to file", "Tools.writeToFile()");
        }

    }
}
