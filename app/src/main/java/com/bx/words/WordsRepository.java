package com.bx.words;

import android.content.Context;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import java.util.List;

/**
 * @author LendteEA
 * @since 2020/1/21 10:54
 * 数据库存取
 **/
class WordsRepository {
    private LiveData<List<Words>> allWordsLive;
    private WordsDao wordsDao;

    WordsRepository(Context context) {
        WordsDatabase wordsDatabase = WordsDatabase.getDatabase(context.getApplicationContext());
        wordsDao = wordsDatabase.getWordsDao();
        allWordsLive = wordsDao.getAllWordsLiveData();
    }

    LiveData<List<Words>> getAllWordsLive() {
        return allWordsLive;
    }
    //模糊搜索
    LiveData<List<Words>> findWordsWithPatten(String patten){
        return wordsDao.findWordsWithpatten("%"+patten+"%");    //模糊搜索需要在前后加上 % 这样才不会匹配整个字段
    }

    void insertWords(Words... words) {
        new InsertAsyncTask(wordsDao).execute(words);
    }

    void updateWords(Words... words) {
        new UpdateAsyncTack(wordsDao).execute(words);
    }

    void deleteWords(Words... words) {
        new DeleteAsyncTack(wordsDao).execute(words);
    }

    void deleteAllWords() {
        new DeleteAllAsyncTack(wordsDao).execute();
    }


    // 插入操作
    static class InsertAsyncTask extends AsyncTask<Words, Void, Void> {
        private WordsDao wordsDao;

        InsertAsyncTask(WordsDao wordsDao) {
            this.wordsDao = wordsDao;
        }

        @Override
        protected Void doInBackground(Words... words) {
            wordsDao.insertWords(words);
            return null;
        }
    }

    // 更新操作
    static class UpdateAsyncTack extends AsyncTask<Words, Void, Void> {
        private WordsDao wordsDao;

        UpdateAsyncTack(WordsDao wordsDao) {
            this.wordsDao = wordsDao;
        }

        @Override
        protected Void doInBackground(Words... words) {
            wordsDao.updateWords(words);
            return null;
        }
    }

    //删除操作
    static class DeleteAsyncTack extends AsyncTask<Words, Void, Void> {
        private WordsDao wordsDao;

        DeleteAsyncTack(WordsDao wordsDao) {
            this.wordsDao = wordsDao;
        }

        @Override
        protected Void doInBackground(Words... words) {
            wordsDao.deleteWords(words);
            return null;
        }
    }

    //清空操作
    static class DeleteAllAsyncTack extends AsyncTask<Void, Void, Void> {
        private WordsDao wordsDao;

        DeleteAllAsyncTack(WordsDao wordsDao) {
            this.wordsDao = wordsDao;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            wordsDao.deleteAllWords();
            return null;
        }
    }
}
