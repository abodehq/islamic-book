package com.mos7af.islamicbooks;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;

public class ContentActivity extends Activity {

	public static int selectedImagePosition = 0;
	private WebView webview;
	public static int zoomFactor = -1;
	ImageView leftArrowImageView;
	ImageView rightArrowImageView;
	String book_id ="1";
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.ly_book_content);
		webview = (WebView) findViewById(R.id.webkit);
		Bundle extras = getIntent().getExtras();
		book_id = extras.getString("book_id");
		int position = 0;
		get_book_content(0);
		
	}

	private void setSelectedImage(int selectedImagePosition) {

	
		myHandler.removeCallbacks(mMyRunnable);
		myHandler.postDelayed(mMyRunnable, 10);

	}
	String html;
	private Handler myHandler = new Handler();
	private Runnable mMyRunnable = new Runnable() {
		@Override
		public void run() {
			try {
				String myPDFURL = "https://s3.amazonaws.com/islamicbooks/"+ book_id+"/1.pdf";
            	String link="";
            	try {
            	    link = "http://docs.google.com/viewer?url="
            	    + URLEncoder.encode(myPDFURL, "UTF-8")
            	    + "&embedded=true";
            	} catch (Exception e) {
            	    e.printStackTrace();
            	}
            	webview.setWebViewClient(new myWebClient());
            	webview.getSettings().setJavaScriptEnabled(true);
            	webview.loadUrl(link);
				webview.requestFocus();
			} catch (Exception e) {

			}
		}

	};

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == 100) {
			selectedImagePosition = data.getExtras().getInt("index");
			setSelectedImage(data.getExtras().getInt("index"));
		}
		if (resultCode == 200) {
			get_book_content(0);
		}

	}

	private void get_book_content(int pos) {
	
		myHandler.removeCallbacks(mMyRunnable);
		myHandler.postDelayed(mMyRunnable, 10);
	}
	  public class myWebClient extends WebViewClient
    {
    	@Override
    	public void onPageStarted(WebView view, String url, Bitmap favicon) {
    		// TODO Auto-generated method stub
    		super.onPageStarted(view, url, favicon);
    	}
    	
    	@Override
    	public boolean shouldOverrideUrlLoading(WebView view, String url) {
    		// TODO Auto-generated method stub
    		view.loadUrl(url);
    		return true;
    	}
    	
    	@Override
    	public void onPageFinished(WebView view, String url) {
    		// TODO Auto-generated method stub
    		super.onPageFinished(view, url);
    	}
    }
}