package com.search;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import java.util.Comparator;





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
    private ListView<File> fileListView;

    @FXML
    private Button exitButton;

    @FXML
    private TableView<WordOccurrence> wordTableView;

    @FXML
    private TableColumn<WordOccurrence, String> wordColumn;

    @FXML
    private TableColumn<WordOccurrence, Integer> occurrenceColumn;
    



    private Stage primaryStage;
    private ObservableList<File> selectedFiles = FXCollections.observableArrayList();
    private Map<String, Integer> wordOccurrences = new HashMap<>();


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


    @FXML
    private void initialize() {
        wordColumn.setCellValueFactory(cellData -> cellData.getValue().wordProperty());
        occurrenceColumn.setCellValueFactory(cellData -> cellData.getValue().occurrenceProperty().asObject());
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
            selectedFiles.add(selectedFile);
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
        for (File file : selectedFiles) {
            filePaths.append(file.getAbsolutePath()).append("\n");
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
        String searchTerm = searchTermField.getText();
    
        // Clear previous results
        wordOccurrences.clear();
    
        // Search through selected files
        for (File file : selectedFiles) {
            try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = br.readLine()) != null) {
                    String[] words = line.split("\\s+");
                    for (String word : words) {
                        if (word.toLowerCase().contains(searchTerm.toLowerCase())) {
                            // Update word occurrences
                            wordOccurrences.put(word, wordOccurrences.getOrDefault(word, 0) + 1);
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace(); // Handle the exception appropriately
            }
        }
    
        // Update the word table view
        updateWordTableView();
    }
    
    

    
    

    private void displayFileContents(File file) {
        StringBuilder fileContents = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                fileContents.append(line).append("\n");
            }
        } catch (IOException e) {
            fileContents.append("Error reading the file: ").append(e.getMessage());
        }
        searchResultsArea.setText(fileContents.toString());
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

    private void updateWordTableView() {
        ObservableList<WordOccurrence> wordOccurrenceList = FXCollections.observableArrayList();
        for (Map.Entry<String, Integer> entry : wordOccurrences.entrySet()) {
            wordOccurrenceList.add(new WordOccurrence(entry.getKey(), entry.getValue()));
        }
        wordTableView.setItems(wordOccurrenceList);
    }
}

