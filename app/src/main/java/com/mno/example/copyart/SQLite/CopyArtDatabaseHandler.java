package com.mno.example.copyart.SQLite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.mno.example.copyart.model.Picture;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by m-dev on 3/15/17.
 */

public class CopyArtDatabaseHandler extends SQLiteOpenHelper {


    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "contactsManager";

    // Contacts table name
    private static final String TABLE_PICTURES = "pictures";

    // Contacts Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_NAME = "name";
    private static final String KEY_LOCAL_IMAGE_URI = "loacalImageUri";

    public CopyArtDatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_CONTACTS_TABLE = "CREATE TABLE " + TABLE_PICTURES + "("
                + KEY_ID + " STRING PRIMARY KEY," + KEY_NAME + " TEXT,"
                + KEY_LOCAL_IMAGE_URI + " TEXT" + ")";
        db.execSQL(CREATE_CONTACTS_TABLE);
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PICTURES);

        // Create tables again
        onCreate(db);
    }

    public void addPictures(Picture picture) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_NAME, picture.getName()); // SCAN Name
        values.put(KEY_LOCAL_IMAGE_URI, picture.getPath()); // LOCAL IMAGE URI

        // Inserting Row
        db.insert(TABLE_PICTURES, null, values);
        db.close(); // Closing database connection
    }

    public List<Picture> getScanList() {
        List<Picture> contactList = new ArrayList<Picture>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_PICTURES;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Picture picture = new Picture();
                picture.setId(cursor.getString(0));
                picture.setName(cursor.getString(1));
                picture.setPath(cursor.getString(2));
                // Adding contact to list
                contactList.add(picture);
            } while (cursor.moveToNext());
        }

        // return contact list
        return contactList;
    }


    public int getCountofPictures() {
        String countQuery = "SELECT  * FROM " + TABLE_PICTURES;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int count = cursor.getCount();
        cursor.close();

        // return count
        return count;
    }
}
