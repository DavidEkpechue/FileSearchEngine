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
import javafx.scene.control.ListView;
import javafx.scene.control.TableColumn;
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
        
        // Listener for fileListView selection change
        fileListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                // Update word occurrences for the focused file
                updateWordTableView(newValue);
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
        //updateWordTableView();
        // Rank files based on search results
        rankFilesBySearchResults();
    }
    
    

    private void rankFilesBySearchResults() {
        // Create a comparator to compare files based on the number of search results
        Comparator<File> fileComparator = (file1, file2) -> {
            int results1 = countSearchResults(file1);
            int results2 = countSearchResults(file2);
            return Integer.compare(results2, results1); // Descending order
        };
    
        // Sort the selectedFiles list using the comparator
        selectedFiles.sort(fileComparator);
    
        // Update the file list view with the sorted files
        fileListView.setItems(selectedFiles);
    }
    
    private int countSearchResults(File file) {
        int count = 0;
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] words = line.split("\\s+");
                for (String word : words) {
                    if (word.toLowerCase().contains(searchTermField.getText().toLowerCase())) {
                        count++;
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace(); // Handle the exception appropriately
        }
        return count;
    }
    
    
    

    private void displayFileContents(File file) {
        if (file == null) {
            searchResultsArea.setText(""); // Clear the search results area
            return; // Return immediately if file is null
        }
    
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

    private void updateWordTableView(File file) {
        ObservableList<WordOccurrence> wordOccurrenceList = FXCollections.observableArrayList();
        Map<String, Integer> wordOccurrences = new HashMap<>(); // Clear previous results
    
        String[] searchTerms = searchTermField.getText().toLowerCase().split(",");
    
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] words = line.split("\\s+");
                for (String searchTerm : searchTerms) {
                    for (String word : words) {
                        if (word.toLowerCase().contains(searchTerm.trim())) { // Trim the term to remove leading/trailing spaces
                            wordOccurrences.put(word, wordOccurrences.getOrDefault(word, 0) + 1);
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace(); // Handle the exception appropriately
        }
    
        // Populate wordOccurrenceList with word occurrences
        for (Map.Entry<String, Integer> entry : wordOccurrences.entrySet()) {
            wordOccurrenceList.add(new WordOccurrence(entry.getKey(), entry.getValue()));
        }
    
        // Update the word table view
        wordTableView.setItems(wordOccurrenceList);
    }
    
    
}

