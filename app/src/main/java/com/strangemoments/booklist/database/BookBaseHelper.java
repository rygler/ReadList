package com.strangemoments.booklist.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.strangemoments.booklist.database.BookDbSchema.BookTable;

/**
 * Created by Raphael on 5/17/2016.
 */
public class BookBaseHelper extends SQLiteOpenHelper {

    private static final int VERSION = 1;
    private static final String DATABASE_NAME = "bookBase.db";

    public BookBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + BookTable.NAME + "(" +
                " _id integer primary key autoincrement, " +
                BookTable.Cols.UUID + ", " +
                BookTable.Cols.TITLE + ", " +
                BookTable.Cols.AUTHOR + ", " +
                BookTable.Cols.READ + ", " +
                BookTable.Cols.NOTE + ", " +
                BookTable.Cols.DATE +
                ")"
        );
    }

    private static final String DATABASE_ALTER_1 = "ALTER TABLE " + BookTable.NAME + " ADD COLUMN "
            + BookTable.Cols.DATE;

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
