package com.bx.words;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

public class WordsViewModel extends AndroidViewModel {
    private WordsRepository wordsRepository;
    public WordsViewModel(@NonNull Application application) {
        super(application);
        wordsRepository=new WordsRepository(application);
    }

    LiveData<List<Words>>getAllWords(){
        return wordsRepository.getAllWordsLive();
    }
    LiveData<List<Words>>findWordsWithPatten(String patten){
        return wordsRepository.findWordsWithPatten(patten);
    }

    void insertWord(Words...words){
        wordsRepository.insertWords(words);
    }
    void updateWord(Words...words){
        wordsRepository.updateWords(words);
    }
    void deleteWord(Words...words){
        wordsRepository.deleteWords(words);
    }
    void deleteAllWord(){
        wordsRepository.deleteAllWords();
    }
}
