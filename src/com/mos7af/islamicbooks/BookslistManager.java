package com.mos7af.islamicbooks;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.HashMap;

public class BookslistManager {
	public static String reciterId = null;
	private static BookslistManager instance = null;
	private ArrayList<HashMap<String, String>> playerbooksList = new ArrayList<HashMap<String, String>>();
	private ArrayList<HashMap<String, String>> booksList = new ArrayList<HashMap<String, String>>();
   public static BookslistManager getInstance() {
      if(instance == null) {
         instance = new BookslistManager();
      }
      return instance;
   }
	// Constructor
	public BookslistManager(){
	
	}
	public ArrayList<HashMap<String, String>> getPlayList()
	{
		return playerbooksList;
	}
	public void AddNewSura1(HashMap<String, String> sura)
	{
		playerbooksList.add(sura);
	
	}
	public void AddNewSuraAt1(int index,HashMap<String, String> sura)
	{
		playerbooksList.add(index,sura);
	
	}
	public void deletAllBooks()
	{
		playerbooksList.clear();
	}
	public void SetSongs(ArrayList<HashMap<String, String>> books )
	{
		playerbooksList.clear();
		playerbooksList = books;
	}
	public void SetBooksList(ArrayList<HashMap<String, String>> books )
	{
		booksList.clear();
		booksList = books;
	}
	public ArrayList<HashMap<String, String>> getPlayListBooks()
	{
		
		return booksList;
	}
	/**
	 * Class to filter files which are having .mp3 extension
	 * */
	
}
