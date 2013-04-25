package com.mos7af.islamicbooks;

import java.util.ArrayList;
import java.util.HashMap;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHandler extends SQLiteOpenHelper {
    // Database Version
    private static final int DATABASE_VERSION = 1;
 
    // Database Name
    private static final String DATABASE_NAME = "islamicbooks";
 
    // Labels table name
    private static final String TABLE_LABELS = "bf2_playlists";
 
    // Labels Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_PLAYLISTS_NAME = "playlists_name";
    private static final String KEY_PLAYLISTS_TIME = "playlists_added_time";
    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
 
    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
    	// Category table create query
    	String CREATE_PLAYLISTS_TABLE = "CREATE TABLE IF NOT EXISTS bf2_playlist_books (" +
    			"vid INTEGER PRIMARY KEY AUTOINCREMENT, " +
    			"bookId TEXT, " +
    			"bookTitle TEXT, " +
    			"playlistId TEXT," +
    			"authorId TEXT," +
    			"authorName TEXT," +
    			"publisherId TEXT," +
    			"publishersName TEXT," +
    			"languageName TEXT," +
    			"addedDate TEXT," +
    			"languageId TEXT," +
    			"bookCoverImage TEXT," +
    			"bookCoverThumb TEXT" +
    			");" ;
    	db.execSQL(CREATE_PLAYLISTS_TABLE);	
    	String CREATE_PLAYLISTSSuras_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_LABELS + "("
        		+ KEY_ID + " INTEGER PRIMARY KEY," + KEY_PLAYLISTS_NAME + " TEXT," + KEY_PLAYLISTS_TIME + " TEXT)";
    	db.execSQL(CREATE_PLAYLISTSSuras_TABLE);
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LABELS);
 
        // Create tables again
        onCreate(db);
    }
    
    /**
     * Inserting new lable into lables table
     * */
    public void insertPlaylist(String label){
    	SQLiteDatabase db = this.getWritableDatabase();
    	
    	ContentValues values = new ContentValues();
    	values.put(KEY_PLAYLISTS_NAME, label);
    	values.put(KEY_PLAYLISTS_TIME, "10-10-2010");
    	// Inserting Row
        db.insert(TABLE_LABELS, null, values);
        
        db.close(); // Closing database connection
    }
    
    public void UpdatePlaylist(String id , String label){
    	SQLiteDatabase db = this.getWritableDatabase();
    	
    	ContentValues values = new ContentValues();
    	values.put(KEY_PLAYLISTS_NAME, label);
        db.update(TABLE_LABELS, values, KEY_ID+"="+id, null);
        
        db.close(); // Closing database connection
    }
    /**
     * Getting all labels
     * returns list of labels
     * */
    public ArrayList<HashMap<String, String>> getAllPlaylists()
    {
		
    	ArrayList<HashMap<String, String>> surasList = new ArrayList<HashMap<String, String>>();
    	
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_LABELS;
     
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
     
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
            	
            	HashMap<String, String> map = new HashMap<String, String>();
            	
        		map.put("playlistId", cursor.getString(0));
        		map.put("playlistName", cursor.getString(1));
        	
        		surasList.add(map);
            } while (cursor.moveToNext());
        }
        
        // closing connection
        cursor.close();
        db.close();
    	
    	// returning lables
    	return surasList;
    }
    public boolean  deletePlaylist(String playlistId)
    {
    	boolean result = false;
        SQLiteDatabase db = this.getReadableDatabase();
        db.delete("bf2_playlist_books","playlistId="+playlistId, null);
        result =  db.delete(TABLE_LABELS, KEY_ID + "="+playlistId, null)>0;
        db.close();
        return result;
    	
    }
    public boolean  deletePlaylistSura(String playlistId,String suraId)
    {
    	boolean result = false;
        SQLiteDatabase db = this.getReadableDatabase();
        result=  db.delete("bf2_playlist_books","playlistId="+playlistId + " AND bookId=" +suraId , null)>0;
        db.close();
        return result;
    	
    }
   
    
    public void InsertPlayListSura(HashMap<String, String> book,String playlistId)
	{
    	
    	
		SQLiteDatabase db = this.getWritableDatabase();
    	ContentValues values = new ContentValues();
    	values.put("bookId", book.get("bookId"));
    	values.put("bookTitle", book.get("bookTitle"));
    	values.put("playlistId", playlistId);
    	values.put("authorId", book.get("authorId"));
    	values.put("authorName", book.get("authorName"));
    	values.put("publisherId", book.get("publisherId"));
    	values.put("publishersName", book.get("publishersName"));
    	values.put("languageName", book.get("bookLanguageName"));
    	values.put("addedDate", book.get("bookAddedDate"));
    	values.put("languageId", book.get("languageId"));
    	values.put("bookCoverImage", book.get("bookCoverImage"));
    	values.put("bookCoverThumb", book.get("bookCoverThumb"));
        db.insert("bf2_playlist_books", null, values);
        
        db.close();
			
		
	}
    public ArrayList<HashMap<String, String>> getPlaylistSuras(String playlistId)
    {
    	ArrayList<HashMap<String, String>> booksList = new ArrayList<HashMap<String, String>>();
        String selectQuery = "select  * from  bf2_playlist_books WHERE playlistId = " + playlistId;
     
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
     
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
            	
            	HashMap<String, String> book = new HashMap<String, String>();
            	book.put("bookId", cursor.getString(1));
            	book.put("bookTitle", cursor.getString(2));
            	book.put("playlistId", cursor.getString(3));
            	book.put("authorId", cursor.getString(4));
            	book.put("authorName", cursor.getString(5));
            	book.put("publisherId", cursor.getString(6));
            	book.put("publishersName", cursor.getString(7));
            	book.put("languageName", cursor.getString(8));
        		book.put("addedDate", cursor.getString(9));
        		book.put("languageId", cursor.getString(10));
        		book.put("bookCoverImage", cursor.getString(11));
        		book.put("bookCoverThumb", cursor.getString(12));
        		booksList.add(book);
            } while (cursor.moveToNext());
        }
        
        // closing connection
        cursor.close();
        db.close();
    	// returning lables
    	return booksList;
    }
    
}
