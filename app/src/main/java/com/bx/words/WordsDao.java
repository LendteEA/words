package com.bx.words;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface WordsDao {

    @Insert
    void insertWords(Words...words);

    @Update
    void updateWords(Words...words);

    @Delete
    void deleteWords(Words...words);

    @Query("DELETE FROM  Words")
    void deleteAllWords();

    @Query("SELECT * FROM Words ORDER BY ID DESC")
    LiveData<List<Words>> getAllWordsLiveData();

    @Query("SELECT * FROM Words WHERE english_words || chinese_meaning LIKE :patten ORDER BY ID")
    LiveData<List<Words>>findWordsWithpatten(String patten);

}
