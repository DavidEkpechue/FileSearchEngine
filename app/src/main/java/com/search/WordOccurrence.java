package com.search;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class WordOccurrence {
    private final StringProperty word;
    private final IntegerProperty occurrence;

    public WordOccurrence(String word, int occurrence) {
        this.word = new SimpleStringProperty(word);
        this.occurrence = new SimpleIntegerProperty(occurrence);
    }

    public String getWord() {
        return word.get();
    }

    public StringProperty wordProperty() {
        return word;
    }

    public void setWord(String word) {
        this.word.set(word);
    }

    public int getOccurrence() {
        return occurrence.get();
    }

    public IntegerProperty occurrenceProperty() {
        return occurrence;
    }

    public void setOccurrence(int occurrence) {
        this.occurrence.set(occurrence);
    }
}

