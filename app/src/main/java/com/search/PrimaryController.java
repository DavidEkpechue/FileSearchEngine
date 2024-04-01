package com.search;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
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


    private Stage primaryStage;
    private ObservableList<String> selectedFiles = FXCollections.observableArrayList();

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

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

    private void updateFilePathArea() {
        StringBuilder filePaths = new StringBuilder();
        for (String filePath : selectedFiles) {
            filePaths.append(filePath).append("\n");
        }
        filePathArea.setText(filePaths.toString());
    }

    @FXML
    private void searchFile() {
        String searchTerm = searchTermField.getText().toLowerCase(); // Convert to lowercase for case-insensitive search
        ObservableList<String> filteredFiles = FXCollections.observableArrayList();
    
        for (String filePath : selectedFiles) {
            try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
                String line;
                while ((line = br.readLine()) != null) {
                    if (line.toLowerCase().contains(searchTerm)) {
                        filteredFiles.add(filePath);
                        break; // Once a match is found, no need to continue searching this file
                    }
                }
            } catch (IOException e) {
                // Handle IOException if needed
            }
        }
    
        fileListView.setItems(filteredFiles);
    }
    

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
