package com.search;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
//hiii
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
    private Button searchButton;

    @FXML
    private TextArea searchResultsArea;

    private Stage primaryStage;

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    @FXML
    private void initialize() {
    }

    @FXML
    private void selectFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select File");
        File selectedFile = fileChooser.showOpenDialog(primaryStage);
        if (selectedFile != null) {
            filePathArea.setText(selectedFile.getAbsolutePath());
        }
    }

    @FXML
    private void searchFile() {
        String filePath = filePathArea.getText();
        String searchTerm = searchTermField.getText();
        StringBuilder results = new StringBuilder();

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.contains(searchTerm)) {
                    results.append(line).append("\n");
                }
            }
        } catch (IOException e) {
            results.append("Error reading the file: ").append(e.getMessage());
        }

        searchResultsArea.setText(results.toString());
    }
}
