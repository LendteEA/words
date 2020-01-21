package com.bx.words;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {Words.class},version = 1,exportSchema = false)
public abstract class WordsDatabase extends RoomDatabase {
    public abstract WordsDao getWordsDao();

    private static WordsDatabase INSTANCE;

    static synchronized WordsDatabase getDatabase(Context context){
        if(INSTANCE==null){
            INSTANCE=Room.databaseBuilder(context.getApplicationContext(), WordsDatabase.class,"Words_database")
                    .build();
        }
        return INSTANCE;
    }
}
