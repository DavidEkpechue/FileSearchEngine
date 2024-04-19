# FileSearchEngine
 App to search through files

Introduction:
This project is a search engine application designed to search through a collection of text files, autocorrect the entered search query using a built-in spell checker, and rank the files based on the frequency of the search term's occurrence.

Core Functionality:
Search Capability - Users can enter a search term, and the engine will find all occurrences across various files.
Autocorrection - Implements a spellchecker to suggest corrections to misspelled search terms before performing the search.
Ranking System - Files are ranked and displayed from the highest to the lowest frequency of the search term.

List of Classes:
App: Initializes and launches the application's user interface.
PrimaryController - Manages user interactions with the graphical user interface. This includes receiving user inputs, displaying results, and initiating searches.
Spellchecker: Provides autocorrection for search terms by suggesting the most likely corrections for misspelled words.
WordOccurrence - Handles the logic for parsing files, counting word occurrences, and ranking files based on the count of search terms.

Optional Functionality:
Interactive GUI - A graphical user interface (described in primary.fxml) for easy interaction with the search engine.
Real-Time Suggestions - Offers real-time autocorrect suggestions as the user types the search term.
Search Optimization - Techniques like caching previous searches for faster retrieval and reduced computational overhead.

Setup and Installation

Prerequisites
Java Development Kit (JDK) - Ensure that JDK is installed on your system. You can download the latest version from Oracle's official website.
JavaFX  This project uses JavaFX for its graphical user interface. Download the JavaFX SDK from OpenJFX.

Installation Steps

Download JavaFX:
Visit the OpenJFX website and download the JavaFX SDK for your operating system.
Extract the downloaded file to a known location on your system.

Setting up the Environment:
Add the path to the lib folder of the JavaFX SDK to your system's PATH variable.
Set the JAVA_HOME environment variable to the path where your JDK is installed.

Configuring Your IDE:
For IDEs like Eclipse, IntelliJ IDEA, and NetBeans, add the JavaFX SDK as a library.

Running the Application:
Open App.java in your IDE.
Compile and run App.java with VM options for JavaFX.

Usage:
Adding Files - Use the 'Add File' button to browse and select text files.
Managing Files - View and manage your list of added files.
Using the Search Bar - Type in the search query and initiate a search.
Viewing Search Results - Results show word counts and file rankings.
File Interaction: Select any file to view detailed text within.

Future Enhancements:
Search History - Implementing a feature for viewing and managing search history.
Integrated Text Editor - Adding capabilities for in-app text editing and modifications.
Advanced Search Options - Including support for regular expressions and multi-threaded searches.
