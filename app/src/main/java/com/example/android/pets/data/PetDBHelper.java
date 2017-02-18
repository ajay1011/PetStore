package com.example.android.pets.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static com.example.android.pets.data.PetContract.PetEntry.*;


/**
 * Created by AJAY OMKAR on 20-Dec-16.
 */

public class PetDBHelper extends SQLiteOpenHelper {
    // constant for database name
    public static final String DATABASE_NAME = "shelter.db";
    // constant for database version
    public static final int DATABASE_VERSION = 1;
    public PetDBHelper(Context context){
        super(context,DATABASE_NAME,null,DATABASE_VERSION);
    };
    @Override
    public void onCreate(SQLiteDatabase db) {
        // creates pets table in database
        db.execSQL(DATABASE_CREATE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
    // Constant for creating table in database
    public static final String DATABASE_CREATE =  "CREATE TABLE " + TABLE_NAME + " ("
            + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COLUMN_NAME + " TEXT NOT NULL, "
            + COLUMN_BREED + " TEXT, "
            + COLUMN_GENDER + " INTEGER NOT NULL, "
            + COLUMN_WEIGHT + " INTEGER NOT NULL DEFAULT 0);";
    // constant for dropping table in database
    public static final String DATABASE_TABLE_DROP = "DROP TABLE IF ALREDAY EXISTS" + TABLE_NAME;
}
