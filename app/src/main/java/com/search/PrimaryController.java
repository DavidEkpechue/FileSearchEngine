package com.search;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class PrimaryController {

    @FXML
    private Label searchTermLabel;

    @FXML
    private TextField searchTermField;

    @FXML
    private Label filePathLabel;

    @FXML
    private TextArea filePathArea;

    @FXML
    private Button selectFileButton;

    @FXML
    private TextArea searchResultsArea;

    @FXML
    private ListView<String> fileListView;

    @FXML
    private Button exitButton;
    @FXML
    private Pane myPane;
    @FXML
    private ColorPicker myColorPicker;


    private Stage primaryStage;
    private ObservableList<String> selectedFiles = FXCollections.observableArrayList();

    /**
     * Sets the primary stage for the JavaFX application.
     *
     * @param  primaryStage  the primary stage to be set
     */
    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    /**
     *
     * Option to exit application.
     */
    public void exit(ActionEvent event){
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Exit");
        alert.setContentText("Are you sure you want to exit?");

        if(alert.showAndWait().get() == ButtonType.OK){
            primaryStage = (Stage) exitButton.getScene().getWindow();
            System.out.println("You Have Exited This Application!");
            primaryStage.close();
        }

    }

    /**
     *
     * Option to change background colour.
     */
    public void setMyColorPicker(ActionEvent event){
        Color myColor = myColorPicker.getValue();
        myPane.setBackground(new Background(new BackgroundFill(myColor, null,null)));
    }

    /**
     * Initializes the file list view and sets up the selection mode and listener for displaying file contents.
     */
    @FXML
    private void initialize() {
        fileListView.setItems(selectedFiles);
        fileListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        fileListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                displayFileContents(newValue);
            }
        });
    }

    /**
     * Selects a file using a FileChooser and adds the selected file's absolute path to a list of selected files.
     *
     * @param  None
     * @return None
     */
    @FXML
    private void selectFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select File");
        File selectedFile = fileChooser.showOpenDialog(primaryStage);
        if (selectedFile != null) {
            selectedFiles.add(selectedFile.getAbsolutePath());
            updateFilePathArea();
        }
    }

    /**
     * Updates the file path area with the selected files.
     *
     * @param  None
     * @return None
     */
    private void updateFilePathArea() {
        StringBuilder filePaths = new StringBuilder();
        for (String filePath : selectedFiles) {
            filePaths.append(filePath).append("\n");
        }
        filePathArea.setText(filePaths.toString());
    }

    /**
     * Perform a case-insensitive search through selected files for a given search term,
     * and display the matching files in the file list view.
     *
     */
    @FXML
    private void searchFile() {
        String searchTerm = searchTermField.getText().toLowerCase(); 
        String[] searchWords = searchTerm.split("\\s*,\\s*"); 
        ObservableList<String> filteredFiles = FXCollections.observableArrayList();
    
        for (String filePath : selectedFiles) {
            try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
                boolean matchFound = true; 
                for (String word : searchWords) {
                    boolean wordFound = false; 
                    String line;
                    while ((line = br.readLine()) != null) {
                        if (line.toLowerCase().contains(word.trim())) {
                            wordFound = true;
                            break; 
                        }
                    }
                    if (!wordFound) {
                        matchFound = false; 
                    }
                }
                if (matchFound) {
                    filteredFiles.add(filePath);
                }
            } catch (IOException e) {
            }
        }
    
        fileListView.setItems(filteredFiles);
    }
    
    

    /**
     * Displays the contents of a file in a text area.
     *
     * @param  filePath  the path of the file to be displayed
     */ 
    @FXML
    private void displayFileContents(String filePath) {
        StringBuilder fileContents = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                fileContents.append(line).append("\n");
            }
        } catch (IOException e) {
            fileContents.append("Error reading the file: ").append(e.getMessage());
        }
        searchResultsArea.setText(fileContents.toString());
    }
}
