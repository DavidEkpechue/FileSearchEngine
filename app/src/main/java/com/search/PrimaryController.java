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
    private Button useSuggestedWordButton;
    
    @FXML
    private TextArea searchHistoryArea;

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

    @FXML
    private Label suggestedWordLabel;

    private String suggestedWordString;

    

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
    private void useSuggestion(ActionEvent event) {
        String suggestedWord = suggestedWordLabel.getText();
        // Split the label text by ":" to get the suggested word
        String[] parts = suggestedWord.split(":");
        if (parts.length > 1) {
            // Extract the suggested word
            String word = parts[1].trim();
            if (!word.isEmpty()) {
                searchTermField.setText(word);
                suggestedWordLabel.setVisible(false);
            }
        }
    }

    /**
     * This method initializes the JavaFX components and sets up listeners
     * for changes in the file selection. It is called automatically by the
     * JavaFX framework when the FXML file is loaded.
     *
     * @param  None  This method takes no parameters.
     * @return None  This method does not return anything.
     */
    @FXML
    private void initialize() {
        // Set the value factory for the 'wordColumn'. The value factory
        // is responsible for providing the values to be displayed in the
        // column. Here, we specify that the values should be obtained from
        // the 'word' property of each WordOccurrence object in the table.
        wordColumn.setCellValueFactory(cellData -> cellData.getValue().wordProperty());

        // Set the value factory for the 'occurrenceColumn'. Similar to above,
        // we specify that the values should be obtained from the 'occurrence'
        // property of each WordOccurrence object, which is of type Integer.
        occurrenceColumn.setCellValueFactory(cellData -> cellData.getValue().occurrenceProperty().asObject());

        // Set the items of the 'fileListView' to be the 'selectedFiles' list.
        // This list is of type ObservableList, which is a special type of List
        // that automatically notifies any registered listeners of any changes
        // made to the list.
        fileListView.setItems(selectedFiles);

        // Set the selection mode of the 'fileListView' to allow only a single
        // item to be selected at a time.
        fileListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

        // Add a listener to the 'selectedItemProperty' of the 'fileListView'
        // selection model. This listener will be called whenever the selected
        // item changes. Here, we specify that if a new item is selected, we
        // want to update the word occurrences for that file and display its
        // contents in the 'searchResultsArea'.
        fileListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                updateWordTableView(newValue);
                displayFileContents(newValue);
            }
        });


        suggestedWordLabel.setVisible(false);


    }
    

    /**
     * This method is an event handler for the "Select File" button. 
     * When the user clicks this button, a FileChooser is displayed 
     * allowing the user to select a file. If a file is selected, 
     * its absolute path is added to a list of selected files. The 
     * selectedFiles list is an ObservableList which is bound to 
     * the fileListView component, so the new file is immediately 
     * visible in the UI. At the same time, the absolute path of the 
     * selected file is added to the filePathArea TextArea, which is
     * updated whenever a new file is selected. 
     *
     * @param  None
     * @return None
     */
    @FXML
    private void selectFile() {
        // Create a new FileChooser
        FileChooser fileChooser = new FileChooser();

        // Set the title of the FileChooser dialog
        fileChooser.setTitle("Select File");

        // Show the FileChooser dialog and get the selected file
        File selectedFile = fileChooser.showOpenDialog(primaryStage);

        // If a file was selected
        if (selectedFile != null) {
            // Add its absolute path to the list of selected files
            selectedFiles.add(selectedFile);

        }
    }


    /**
     * This method performs a case-insensitive search through the selected files
     * for a given search term, and displays the matching files in the file list view.
     *
     * The search is performed by iterating over each selected file, reading its contents
     * line by line, and splitting each line into words. Each word is then checked
     * against the search term, and if it contains the search term (case-insensitively),
     * an occurrence of the word is recorded in the 'wordOccurrences' map. The word
     * is used as the key in the map, and the value represents the number of occurrences.
     *
     * After the search is complete, the 'wordOccurrences' map is used to rank the files
     * based on the number of search results. This is done by calling the
     * 'rankFilesBySearchResults' method.
     *
     * IMPORTANT: When handling exceptions, it's important to handle them appropriately
     * to prevent the program from crashing. In this case, the exception is simply
     * printed to the console, but in a real-world application, you would likely want to
     * display an error message to the user or log the exception for later analysis.
     */
    @FXML
    private void searchFile() {
        // Get the search term from the search term field
        String searchTerm = searchTermField.getText();
        
        Spellchecker spellchecker = new Spellchecker(getClass().getResource("/words_alpha.txt").getPath());

   // Perform spell check on the search term
   String suggestedWord = spellchecker.suggestCorrection(searchTerm);
   if (suggestedWord != null) {
       searchTerm = suggestedWord; // Use the suggested word if available
       suggestedWordLabel.setText("Suggested Word: " + suggestedWord); // Set the suggested word to the label
       suggestedWordLabel.setVisible(true); // Show the label
   } else {
       suggestedWordLabel.setVisible(false); // Hide the label if no suggested word
   }
        
        // Clear previous results
        wordOccurrences.clear();
        
        // Iterate over each selected file
        for (File file : selectedFiles) {
            try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                // Read each line of the file
                String line;
                while ((line = br.readLine()) != null) {
                    // Split the line into words
                    String[] words = line.split("\\s+");
                    // Check each word against the search term
                    for (String word : words) {
                        if (word.toLowerCase().contains(searchTerm.toLowerCase())) {
                            // Record an occurrence of the word
                            wordOccurrences.put(word, wordOccurrences.getOrDefault(word, 0) + 1);
                        }
                    }
                }
            } catch (IOException e) {
                // Print the stack trace to the console (change this in a real application)
                e.printStackTrace(); 
            }
        }
    
        // Rank files based on search results
        rankFilesBySearchResults();
    }
    
    
    
    

    /**
     * This method ranks the selected files based on the number of search results and updates the file list view.
     *
     * It first creates a comparator that compares two files based on the number of search results. 
     * This is done by counting the number of search results for each file using the 'countSearchResults' method.
     * The comparator sorts the files in descending order of the number of search results.
     *
     * Next, the 'selectedFiles' list is sorted using the comparator.
     *
     * Finally, the file list view is updated with the sorted files.
     */
    private void rankFilesBySearchResults() {
        // A comparator that compares files based on the number of search results
        Comparator<File> fileComparator = (file1, file2) -> {
            // Count the number of search results for file1
            int results1 = countSearchResults(file1);
            // Count the number of search results for file2
            int results2 = countSearchResults(file2);
            // Compare the number of search results for file2 and file1
            // Returns a negative integer, zero, or a positive integer as file2 has more, equal, or less search results than file1
            return Integer.compare(results2, results1); 
        };
    
        // Sort the selectedFiles list using the comparator
        selectedFiles.sort(fileComparator);
    
        // Update the file list view with the sorted files
        fileListView.setItems(selectedFiles);
    }



    
    /**
     * This method counts the number of times a given word occurs in a file.
     * It takes a File object as a parameter and returns an int, which represents the number of times the word occurs.
     *
     * It opens the file using a BufferedReader, reads each line of the file, and splits each line into an array of words.
     * For each word in the array, it checks if the word contains the search term (case-insensitive).
     * If it does, it increments the count variable by 1.
     *
     * If an IOException occursres while reading the file, it prints a stack trace to indicate the exception.
     *
     * @param  file  the File object representing the file to be read
     * @return       the number of times the search term occurs in the file
     */
    private int countSearchResults(File file) {
        int count = 0; // Initialize a counter variable to 0
        try (BufferedReader br = new BufferedReader(new FileReader(file))) { // Open the file using a BufferedReader
            String line; // Declare a variable to hold each line of the file
            while ((line = br.readLine()) != null) { // Read each line of the file
                String[] words = line.split("\\s+"); // Split the line into an array of words
                for (String word : words) { // For each word in the array
                    if (word.toLowerCase().contains(searchTermField.getText().toLowerCase())) { // Check if the word contains the search term (case-insensitive)
                        count++; // If it does, increment the count variable by 1
                    }
                }
            }
        } catch (IOException e) { // If an IOException occursres while reading the file
            e.printStackTrace(); // Print a stack trace to indicate the exception
        }
        return count; // Return the count variable, which represents the number of times the search term occurs in the file
    }
    
    
    

    /**
     * This method displays the contents of a file in a text area.
     * It takes a File object as a parameter, which represents the file to be displayed.
     * If the file parameter is null, it clears the search results area and returns immediately.
     * Otherwise, it reads the contents of the file using a BufferedReader.
     * It appends each line of the file to a StringBuilder object, and then sets the text of the search results area to the contents of the StringBuilder.
     * If an IOException occursres while reading the file, it appends an error message to the StringBuilder indicating the exception.
     * 
     * @param  file  the File object representing the file to be displayed
     */
    private void displayFileContents(File file) {
        
        // Check if the file parameter is null
        if (file == null) {
            
            // Clear the search results area
            searchResultsArea.setText(""); 
            
            // Return immediately if file is null
            return; 
        }
    
        // Create a StringBuilder object to hold the contents of the file
        StringBuilder fileContents = new StringBuilder();
        
        try {
            // Create a BufferedReader object to read the file using a FileReader
            try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                
                // Declare a variable to hold each line of the file
                String line;
                
                // Read each line of the file and append it to the StringBuilder object
                while ((line = br.readLine()) != null) {
                    fileContents.append(line).append("\n");
                }
            }
        } catch (IOException e) {
            
            // Append an error message to the StringBuilder indicating the exception
            fileContents.append("Error reading the file: ").append(e.getMessage());
        }
        
        // Set the text of the search results area to the contents of the StringBuilder
        searchResultsArea.setText(fileContents.toString());
    }
    

    /**
     * This method is responsible for displaying the contents of a file in a text area.
     * It takes a file path as a parameter, which is the path of the file to be displayed.
     * The method begins by creating a new StringBuilder object to hold the contents of the file.
     * It then opens the file using a BufferedReader and reads each line of the file, appending it to the StringBuilder object.
     * If an IOException occursres while reading the file, it appends an error message to the StringBuilder indicating the exception.
     * Finally, it sets the text of the search results area to the contents of the StringBuilder, effectively displaying the contents of the file in the text area.
     *
     * @param  filePath  the path of the file to be displayed
     */ 
    @FXML
    private void displayFileContents(String filePath) {
        // Create a StringBuilder object to hold the contents of the file
        StringBuilder fileContents = new StringBuilder();

        try {
            // Open the file using a BufferedReader and read each line of the file, appending it to the StringBuilder object
            try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
                String line;
                while ((line = br.readLine()) != null) {
                    fileContents.append(line).append("\n");
                }
            }
        } catch (IOException e) {
            // If an IOException occurres while reading the file, append an error message to the StringBuilder indicating the exception
            fileContents.append("Error reading the file: ").append(e.getMessage());
        }

        // Set the text of the search results area to the contents of the StringBuilder, effectively displaying the contents of the file in the text area
        searchResultsArea.setText(fileContents.toString());
    }




    /**
     * This method updates the word table view with word occurrences from a given file. 
     * 
     * The process involves the following steps:
     * 1. Clear the previous results.
     * 2. Get the search terms from the search term field, convert them to lowercase, and split them by commas.
     * 3. Read the given file line by line.
     * 4. Split each line into words using regular expressions.
     * 5. For each search term, iterate over each word in the line.
     * 6. Check if the word contains the search term, ignoring case, and trim leading/trailing spaces.
     * 7. If a match is found, increment the occurrence count of the word in the wordOccurrences map.
     * 8. After processing the file, populate the wordOccurrenceList with WordOccurrence objects.
     * 9. Finally, update the word table view with the populated list.
     *
     * @param  file  the file to process for word occurrences
     */
    private void updateWordTableView(File file) {
        // Create an empty list to hold word occurrences
        ObservableList<WordOccurrence> wordOccurrenceList = FXCollections.observableArrayList();
        
        // Create an empty map to hold word occurrences
        Map<String, Integer> wordOccurrences = new HashMap<>();
        
        // Get the search terms from the search term field, convert them to lowercase, and split them by commas
        String[] searchTerms = searchTermField.getText().toLowerCase().split(",");
        
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line; // Variable to hold each line of the file
            
            // Loop through each line of the file
            while ((line = br.readLine()) != null) {
                // Split each line into words using regular expressions
                String[] words = line.split("\\s+");
                
                // Loop through each search term
                for (String searchTerm : searchTerms) {
                    // Loop through each word in the line
                    for (String word : words) {
                        // Check if the word contains the search term, ignoring case, and trim leading/trailing spaces
                        if (word.toLowerCase().contains(searchTerm.trim())) {
                            // Increment the occurrence count of the word in the wordOccurrences map
                            wordOccurrences.put(word, wordOccurrences.getOrDefault(word, 0) + 1);
                        }
                    }
                }
            }
        } catch (IOException e) {
            // If an IOException occursres while reading the file, print the stack trace
            e.printStackTrace();
        }
        
        // Populate wordOccurrenceList with WordOccurrence objects
        for (Map.Entry<String, Integer> entry : wordOccurrences.entrySet()) {
            wordOccurrenceList.add(new WordOccurrence(entry.getKey(), entry.getValue()));
        }
        
        // Update the word table view with the populated list
        wordTableView.setItems(wordOccurrenceList);
    }
    
    
}

