package com.example.chinesesurnames;

/**
 * Created by hang dong on 11/05/2016.
 */
public class Surname {
    private String numOfBooks;
    private String character;
    private String numOfOccurranceInNames;
    private String [] workInfoArr;
    private String gifUrl;

    void setCharacter(String character){
        this.character = character;
    }

    void setNumOfBooks(String numOfBooks){
        this.numOfBooks = numOfBooks;
    }

    void setNumOfOccurranceInNames(String numOfOccurranceInNames){this.numOfOccurranceInNames = numOfOccurranceInNames; }

    void setWorkInfo(String [] workInfoArr){
        this.workInfoArr = workInfoArr;
    }

    void setGifUrl(String gifUrl){ this.gifUrl = gifUrl; }

    String getNumOfBooks(){
        return numOfBooks;
    }

    String getCharacter(){
        return character;
    }

    String getNumOfOccurranceInNames() {return numOfOccurranceInNames;}

    String [] getWorkInfoArr(){
        return workInfoArr;
    }

    String getGifUrl() { return gifUrl; }
}
