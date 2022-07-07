package com.example.contactbook;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

public class DatabaseAdapter  {

    private DatabaseHelper dbHelper;
    private SQLiteDatabase database;

    public DatabaseAdapter(Context context){
        dbHelper = new DatabaseHelper(context.getApplicationContext());
    }

    public DatabaseAdapter open(){
        database = dbHelper.getWritableDatabase();
        return this;
    }

    public void close(){
        dbHelper.close();
    }

    private Cursor getAllEntries(){
        String[] columns = new String[] {DatabaseHelper.COLUMN_ID, DatabaseHelper.COLUMN_NAME,
                DatabaseHelper.COLUMN_SURNAME,DatabaseHelper.COLUMN_PATRONYMIC,DatabaseHelper.COLUMN_PHONE,DatabaseHelper.COLUMN_AVATAR};
        return  database.query(DatabaseHelper.TABLE, columns, null, null, null, null, null);
    }

    public ArrayList<Person> getPersons(){
        ArrayList<Person> persons = new ArrayList<>();
        Cursor cursor = getAllEntries();
        while (cursor.moveToNext()){
            @SuppressLint("Range") int id = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_ID));
            @SuppressLint("Range") String name = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_NAME));
            @SuppressLint("Range") String family = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_SURNAME));
            @SuppressLint("Range") String patronymic = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_PATRONYMIC));
            @SuppressLint("Range") String phone = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_PHONE));
            persons.add(new Person(id, name, family,patronymic,phone,null));
        }
        cursor.close();
        return persons;
    }

    public long getCount(){
        return DatabaseUtils.queryNumEntries(database, DatabaseHelper.TABLE);
    }

    public Person getPerson(long id){
        Person person = null;
        String query = String.format("SELECT * FROM %s WHERE %s=?",DatabaseHelper.TABLE, DatabaseHelper.COLUMN_ID);
        Cursor cursor = database.rawQuery(query, new String[]{ String.valueOf(id)});
        if(cursor.moveToFirst()){
            @SuppressLint("Range") String name = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_NAME));
            @SuppressLint("Range") String family = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_SURNAME));
            @SuppressLint("Range") String patronymic = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_PATRONYMIC));
            @SuppressLint("Range") String phone = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_PHONE));
            @SuppressLint("Range") byte[] avatar = cursor.getBlob(cursor.getColumnIndex(DatabaseHelper.COLUMN_AVATAR));
            person = new Person((int)id, name, family,patronymic,phone,avatar);
        }
        cursor.close();
        return  person;
    }

    public long insert(Person person){

        ContentValues cv = new ContentValues();
        cv.put(DatabaseHelper.COLUMN_NAME, person.getName());
        cv.put(DatabaseHelper.COLUMN_SURNAME, person.getSurname());
        cv.put(DatabaseHelper.COLUMN_PATRONYMIC, person.getPatronymic());
        cv.put(DatabaseHelper.COLUMN_PHONE, person.getPhone());
        cv.put(DatabaseHelper.COLUMN_AVATAR, person.getImageBytes());
        return  database.insert(DatabaseHelper.TABLE, null, cv);
    }

    public long delete(long personId){

        String whereClause = "_id = ?";
        String[] whereArgs = new String[]{String.valueOf(personId)};
        return database.delete(DatabaseHelper.TABLE, whereClause, whereArgs);
    }

    public ArrayList<Person> search(String text)
    {
        ArrayList<Person> persons = new ArrayList<>();
        Cursor cursor = database.rawQuery("select * from " + DatabaseHelper.TABLE + " where " +
                DatabaseHelper.COLUMN_NAME + " LIKE ? OR "+ DatabaseHelper.COLUMN_SURNAME+
                " LIKE ? OR "+DatabaseHelper.COLUMN_PATRONYMIC+" LIKE ? ", new String[]{"%" + text + "%","%" + text + "%","%" + text + "%"});
        while (cursor.moveToNext()){
            @SuppressLint("Range") int id = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_ID));
            @SuppressLint("Range") String name = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_NAME));
            @SuppressLint("Range") String family = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_SURNAME));
            @SuppressLint("Range") String patronymic = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_PATRONYMIC));
            @SuppressLint("Range") String phone = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_PHONE));
            @SuppressLint("Range") byte[] avatar = cursor.getBlob(cursor.getColumnIndex(DatabaseHelper.COLUMN_AVATAR));
            persons.add(new Person(id, name, family,patronymic,phone,avatar));
        }
        return persons;
    }

    public long update(Person person){

        String whereClause = DatabaseHelper.COLUMN_ID + "=" + person.getId();
        ContentValues cv = new ContentValues();
        cv.put(DatabaseHelper.COLUMN_NAME, person.getName());
        cv.put(DatabaseHelper.COLUMN_SURNAME, person.getSurname());
        cv.put(DatabaseHelper.COLUMN_PATRONYMIC, person.getPatronymic());
        cv.put(DatabaseHelper.COLUMN_PHONE, person.getPhone());
        cv.put(DatabaseHelper.COLUMN_AVATAR, person.getImageBytes());
        return database.update(DatabaseHelper.TABLE, cv, whereClause, null);
    }

}
