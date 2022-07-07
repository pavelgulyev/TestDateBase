package com.example.contactbook;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static String DB_NAME = "contacts.db";
    private static final int SCHEMA = 1; // версия базы данных
    static final String TABLE = "Person"; // название таблицы в бд
    // названия столбцов
    static final String COLUMN_ID = "_id";
    static final String COLUMN_NAME = "name";
    static final String COLUMN_SURNAME = "surname";
    static final String COLUMN_PATRONYMIC = "patronymic";
    static final String COLUMN_PHONE = "phone";
    static final String COLUMN_AVATAR = "avatar";

    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, SCHEMA);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL("CREATE TABLE " + TABLE + " (" + COLUMN_ID
                + " INTEGER PRIMARY KEY AUTOINCREMENT," + COLUMN_NAME
                + " TEXT, " + COLUMN_SURNAME + " TEXT, " + COLUMN_PATRONYMIC
                + " TEXT, " + COLUMN_PHONE + " TEXT, " + COLUMN_AVATAR + " BLOB  );");
        // добавление начальных данных
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion,  int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+TABLE);
        onCreate(db);
    }
}