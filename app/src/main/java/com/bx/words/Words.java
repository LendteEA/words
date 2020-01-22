package com.bx.words;


import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Words {
    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "english_words")
    private String englishWords;

    @ColumnInfo(name = "chinese_meaning")
    private String chineseMeaning;

    @ColumnInfo(name = "chinese_invisiable")
    private boolean chineseInvisiable;

    Words(String englishWords, String chineseMeaning) {
        this.englishWords = englishWords;
        this.chineseMeaning = chineseMeaning;
    }

    int getId() {
        return id;
    }

    void setId(int id) {
        this.id = id;
    }

    String getEnglishWords() {
        return englishWords;
    }

//    public void setEnglishWords(String englishWords) {
//        this.englishWords = englishWords;
//    }

    String getChineseMeaning() {
        return chineseMeaning;
    }


    boolean isChineseInvisiable() {
        return chineseInvisiable;
    }

    void setChineseInvisiable(boolean chineseInvisiable) {
        this.chineseInvisiable = chineseInvisiable;
    }
}
