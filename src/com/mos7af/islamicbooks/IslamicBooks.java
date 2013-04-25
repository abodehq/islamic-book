package com.mos7af.islamicbooks;
import android.app.AlertDialog;
import android.app.TabActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabHost.TabSpec;

public class IslamicBooks extends TabActivity 
{
	
	public static int tabIndex = 0;
	private TabHost tabHost;
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ly_islamicbooks);
        if(Utils.isConnectingToInternet(IslamicBooks.this))
        {
        	AddAppTabs();
        }else
        {
        	showAlertDialog(this, "No Internet Connection",
					"You don't have internet connection.", false);
        }
    }
    public void AddAppTabs()
    {
    	tabHost = getTabHost();
    
   
    	
    	tabHost.setOnTabChangedListener(new OnTabChangeListener() 
    	{
			public void onTabChanged(String tabId)
			{
				//not used for this app
			}
		});
        // Tab for books
        TabSpec booksSpec = tabHost.newTabSpec("books");
        booksSpec.setIndicator("المكتبة", getResources().getDrawable(R.drawable.icon_books_tab));
        Intent recitersIntent = new Intent(this, BooksActivity.class);
        booksSpec.setContent(recitersIntent);
        
        // Tab for authors
        TabSpec authorsSpec = tabHost.newTabSpec("authors");
        authorsSpec.setIndicator("المؤلفون", getResources().getDrawable(R.drawable.icon_authors_tab));
        Intent  playerIntent = new Intent(this, AuthorsActivity.class);
        authorsSpec.setContent(playerIntent);
      
        // Tab for publishers
        TabSpec publishersSpec = tabHost.newTabSpec("publishers");
        publishersSpec.setIndicator("دور النشر", getResources().getDrawable(R.drawable.icon_publishers_tab));
        Intent  surasIntent = new Intent(this, PublishersActivity.class);
        publishersSpec.setContent(surasIntent);
        
        // Tab for playlists
        TabSpec playlistsSpec = tabHost.newTabSpec("playlists");
        playlistsSpec.setIndicator("المفضلة", getResources().getDrawable(R.drawable.icon_playlists_tab));
        Intent playlistIntent = new Intent(this, PlaylistActivity.class);
        playlistsSpec.setContent(playlistIntent);
        
        // Adding all TabSpec to TabHost
        tabHost.addTab(booksSpec); 
        tabHost.addTab(authorsSpec); 
        tabHost.addTab(publishersSpec); 
        tabHost.addTab(playlistsSpec); 
        
        tabHost.setCurrentTab(tabIndex);
    }
    public void switchTab(int tab)
    {
        tabHost.setCurrentTab(tab);
    }
    public void ShowErrorDialog()
    {
    	showAlertDialog(this, "No Internet Connection",
				"You don't have internet connection.", false);
    }
    private void showAlertDialog(Context context, String title, String message, Boolean status) 
    {
		AlertDialog alertDialog = new AlertDialog.Builder(context).create();
		alertDialog.setCancelable(false);
		// Setting Dialog Title
		alertDialog.setTitle(title);

		// Setting Dialog Message
		alertDialog.setMessage(message);
		
		// Setting alert dialog icon
		alertDialog.setIcon( R.drawable.fail);
			
		 alertDialog.setButton("Try Again!!", new DialogInterface.OnClickListener() {
		      public void onClick(DialogInterface dialog, int which) {
		 
		    	  if(Utils.isConnectingToInternet(IslamicBooks.this))
		          {
		          	AddAppTabs();
		          }else
		          {
		        	  ShowErrorDialog();
		          }
		 
		    } });
		alertDialog.show();
	}

  
}