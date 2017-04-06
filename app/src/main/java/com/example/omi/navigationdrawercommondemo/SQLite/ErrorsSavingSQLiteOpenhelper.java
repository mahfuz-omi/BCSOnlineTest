package com.example.omi.navigationdrawercommondemo.SQLite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.omi.navigationdrawercommondemo.Model.Error;
import com.example.omi.navigationdrawercommondemo.Model.QuestionErrorPair;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by omi on 5/20/2016.
 */
public class ErrorsSavingSQLiteOpenhelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "QuestionsDatabase";
    private static final String TABLE_NAME = "QuestionData";
    private static final String KEY_ID = "id";
    private static final String KEY_QUESTION = "question";
    private static final String KEY_ANSWER = "answer";


    public ErrorsSavingSQLiteOpenhelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + "("
                + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + KEY_QUESTION + " TEXT," + KEY_ANSWER + " TEXT" + ")";
        db.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public void saveListOfError(List<Error> errors)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = null;

        for(Error error:errors)
        {
            values = new ContentValues();
            values.put(KEY_QUESTION, error.getQuestion());
            values.put(KEY_ANSWER,error.getAnswer());
            db.insert(TABLE_NAME, null, values);
        }
        db.close();
    }


    public List<QuestionErrorPair> getQuestionErrorPairList()
    {
        List<QuestionErrorPair> pairList = new ArrayList<>();
        String selectQuery = "SELECT  * FROM " + TABLE_NAME;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if(cursor.moveToFirst())
        {
            do
            {
                QuestionErrorPair pair = new QuestionErrorPair();
                pair.setQuestion(cursor.getString(1));
                pair.setAnswer(cursor.getString(2));
                pairList.add(pair);
            }while(cursor.moveToNext());
        }
        return pairList;
    }
}
