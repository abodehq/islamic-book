package com.mos7af.islamicbooks;

import java.util.ArrayList;
import java.util.HashMap;
import android.app.Dialog;
import android.app.ListActivity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;
import android.widget.Toast;

public class PlayListSurasActivity extends ListActivity 
{
	public ArrayList<HashMap<String, String>> booksList = new ArrayList<HashMap<String, String>>();
	private final int CONTEXT_MENU_ID = 1;
	private IconContextMenu iconContextMenu = null;
	private final int MENU_ITEM_1_ACTION = 1;
	private final int MENU_ITEM_2_ACTION = 2;
	private DatabaseHandler db ;
	private int selectedIndex = 0;
	private ListView lv ;
	private PlayerPlaylistItemAdapter	playerPlaylistItemAdapter;
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ly_player_playlist);
		db = new DatabaseHandler(getApplicationContext());
		playerPlaylistItemAdapter = new PlayerPlaylistItemAdapter(this );
		
		lv = getListView();
		lv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				ShowPdfViewer(position);
			}
		});

		lv.setOnItemLongClickListener(new OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					int pos, long arg3) {
				// TODO Auto-generated method stub
				selectedIndex = pos;
				showDialog(CONTEXT_MENU_ID);
				return false;
			}});
		
		  Resources res = getResources();
        
          iconContextMenu = new IconContextMenu(this, CONTEXT_MENU_ID);
          iconContextMenu.addItem(res, getString(R.string.fav_show_book), R.drawable.ic_1, MENU_ITEM_1_ACTION);
          iconContextMenu.addItem(res, getString(R.string.fav_delete_book), R.drawable.ic_delete, MENU_ITEM_2_ACTION);

          //set onclick listener for context menu
          iconContextMenu.setOnClickListener(new IconContextMenu.IconContextMenuOnClickListener() {
     			@Override
     			public void onClick(int menuId) {
     				
     				BookslistManager bookslistManager=BookslistManager.getInstance();
     				switch(menuId) 
     				{
	     				case MENU_ITEM_1_ACTION:
	     					
	     					ShowPdfViewer(selectedIndex);
	     					break;
	     				case MENU_ITEM_2_ACTION:
	     					boolean result =  db.deletePlaylistSura(booksList.get(selectedIndex).get("playlistId"),booksList.get(selectedIndex).get("bookId"));
	     					if(result)
	     						Toast.makeText(getApplicationContext(), getString(R.string.fav_delete_book_success),
	         							Toast.LENGTH_SHORT).show();
	     					ArrayList<HashMap<String, String>> _booksList= db.getPlaylistSuras(booksList.get(0).get("playlistId"));
	     					if(_booksList.isEmpty())
	     					{
	     						Intent in = new Intent(getApplicationContext(),
	         							PlaylistActivity.class);
	         					setResult(300, in);
	         					finish();
	     					}
	     					else
	     					{
	     						bookslistManager.SetBooksList(_booksList);
	     						UpdatePlayList();	
	     					}
	     				
	     					break;
     				
     				}
     			}});
          UpdatePlayList();
         
        
	}
	private void ShowPdfViewer(int bookPosition) {
		Intent i = new Intent(getApplicationContext(), ContentActivity.class);
		i.putExtra("book_id", booksList.get(bookPosition).get("bookId"));
		startActivityForResult(i, 100);	
	}
	private void UpdatePlayList()
	{
		BookslistManager bookslistManager = BookslistManager.getInstance();
		this.booksList = bookslistManager.getPlayListBooks();
		playerPlaylistItemAdapter.SetData(booksList);
		setListAdapter(playerPlaylistItemAdapter);
	}
	@Override
	protected Dialog onCreateDialog(int id) {
		if (id == CONTEXT_MENU_ID) {
			return iconContextMenu.createMenu(getString(R.string.context_menu_title));
		}
		
		return super.onCreateDialog(id);
	}
}
