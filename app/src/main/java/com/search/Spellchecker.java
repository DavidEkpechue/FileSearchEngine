package com.search;


import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;


public class Spellchecker {

    private Set<String> englishWords;

      // Constructor to initialize the spell checker with the English words from a text file
      public Spellchecker(String filePath) {
        this.englishWords = loadEnglishWords(filePath);
    }

        // Method to load English words from a text file
        private Set<String> loadEnglishWords(String filePath) {
            Set<String> words = new HashSet<>();
            try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    words.add(line.trim().toLowerCase());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return words;
        }
    

    // Method to suggest a correction for a misspelled word
    public String suggestCorrection(String word) {
        // Check if the word is already spelled correctly
        if (englishWords.contains(word.toLowerCase())) {
            return null; // No suggestion needed, the word is correct
        }


        String suggestion = null;
        int minDistance = Integer.MAX_VALUE;

        for (String englishWord : englishWords) {
            int distance = levenshteinDistance(word, englishWord);
            if (distance < minDistance) {
                minDistance = distance;
                suggestion = englishWord;
            }
        }

        return suggestion;
    }

    // Method to calculate the Levenshtein distance between two strings
    private int levenshteinDistance(String word1, String word2) {
        int[][] dp = new int[word1.length() + 1][word2.length() + 1];

        for (int i = 0; i <= word1.length(); i++) {
            dp[i][0] = i;
        }

        for (int j = 0; j <= word2.length(); j++) {
            dp[0][j] = j;
        }

        for (int i = 1; i <= word1.length(); i++) {
            for (int j = 1; j <= word2.length(); j++) {
                int cost = (word1.charAt(i - 1) == word2.charAt(j - 1)) ? 0 : 1;
                dp[i][j] = Math.min(Math.min(dp[i - 1][j] + 1, dp[i][j - 1] + 1), dp[i - 1][j - 1] + cost);
            }
        }

        return dp[word1.length()][word2.length()];
    }
}
