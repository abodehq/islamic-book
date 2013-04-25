package com.mos7af.islamicbooks;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import java.util.ArrayList;
import java.util.HashMap;
import flex.messaging.io.amf.client.AMFConnection;
import flex.messaging.io.amf.client.exceptions.ClientStatusException;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Gallery;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import org.json.JSONArray;
import org.json.JSONObject;

public class AuthorsActivity extends Activity {

	private AuthorsActivity _scope;
	private GetTask getTask;

	private ArrayList<HashMap<String, String>> booksList;
	private ListView list;
	private AuthorItemAdapter authorItemAdapter;
	private Gallery listview;
	private int selectedImagePosition = -1;
	private int lastselectedImagePosition = -1;
	private double booksCount = 0;
	private double _booksCount = 0;

	private ImageButton btnNext;
	private ImageButton btnPrevious;
	private LinearLayout footer;

	// Data base
	private DatabaseHandler db;

	// Book Menu
	private final int CONTEXT_MENU_ID = 1;
	private IconContextMenu iconContextMenu = null;
	private final int MENU_ITEM_1_ACTION = 1;
	private final int MENU_ITEM_2_ACTION = 2;
	private int selectedBookIndex = 0;

	private ProgressDialog mProgressDialog;
	private ProgressBar loading;
	private ArrayList<HashMap<String, String>> playlists;
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ly_books);
		_scope = this;

		mProgressDialog = new ProgressDialog(this);
		mProgressDialog.setMessage("");
		mProgressDialog.setIndeterminate(false);
		mProgressDialog.setMax(100);
		mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);

		Resources res = getResources();
		// init the menu
		iconContextMenu = new IconContextMenu(this, CONTEXT_MENU_ID);
		iconContextMenu.addItem(res, getString(R.string.context_menu_author_books), R.drawable.ic_1,
				MENU_ITEM_1_ACTION);
		iconContextMenu.addItem(res, getString(R.string.context_menu_author_info),
				R.drawable.ic_info, MENU_ITEM_2_ACTION);
	
		iconContextMenu
				.setOnClickListener(new IconContextMenu.IconContextMenuOnClickListener() {
					@Override
					public void onClick(int menuId) {

						switch (menuId) {
						case MENU_ITEM_1_ACTION:
							ShowPdfViewer(selectedBookIndex);
							break;
						case MENU_ITEM_2_ACTION:
							showAddToPlaylist();
							break;
						

						}
					}
				});

		list = (ListView) findViewById(R.id.list);

		footer = (LinearLayout) this.findViewById(R.id.footer);
		footer.setVisibility(LinearLayout.INVISIBLE);

		loading = (ProgressBar) this.findViewById(R.id.loading);

		
		getTask = new GetTask();
		getTask.execute();

		btnNext = (ImageButton) findViewById(R.id.btnNext);
		btnNext.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (selectedImagePosition < listview.getCount() - 1) {

					selectedImagePosition = selectedImagePosition + 1;
				}
				listview.setSelection(selectedImagePosition, false);

			}
		});
		btnPrevious = (ImageButton) findViewById(R.id.btnPrevious);
		btnPrevious.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (selectedImagePosition > 0) {

					selectedImagePosition = selectedImagePosition - 1;
				}
				listview.setSelection(selectedImagePosition, false);

			}
		});

		db = new DatabaseHandler(getApplicationContext());
	}

	private void changeBorderForSelectedImage() {

		int count = listview.getChildCount();

		for (int i = 0; i < count; i++) {

			LinearLayout imageView = (LinearLayout) listview.getChildAt(i);
			if (imageView != null) {
				imageView.setBackgroundDrawable(getResources().getDrawable(
						R.drawable.image_border));
				imageView.setPadding(3, 3, 3, 3);
			}
		}
		LinearLayout imageView = (LinearLayout) listview.getSelectedView();
		if (imageView != null) {
			imageView.setBackgroundDrawable(getResources().getDrawable(
					R.drawable.selected_image_border));
			imageView.setPadding(3, 3, 3, 3);
		}
	}
	private void setPagingAdapter() {
		if (_booksCount == 0) {
			_booksCount = booksCount;
			int pagesCount = (int) Math.ceil(booksCount / 10);

			listview = (Gallery) findViewById(R.id.listview);
			PagingAdapter pagingAdapter = new PagingAdapter(_scope);
			pagingAdapter.SetData(pagesCount);

			listview.setAdapter(pagingAdapter);
			listview.setOnItemSelectedListener(new OnItemSelectedListener() {

				@Override
				public void onItemSelected(AdapterView<?> parent, View view,
						int pos, long id) {
					changeBorderForSelectedImage();
					selectedImagePosition = pos;
					myHandler.removeCallbacks(mMyRunnable);
					myHandler.postDelayed(mMyRunnable, 1500);

				}

				@Override
				public void onNothingSelected(AdapterView<?> arg0) {

				}

			});
			footer.setVisibility(LinearLayout.VISIBLE);
		}
	}

	private Handler myHandler = new Handler();
	private Runnable mMyRunnable = new Runnable() {
		@Override
		public void run() {
			try {
				if (lastselectedImagePosition != selectedImagePosition) {
					lastselectedImagePosition = selectedImagePosition;
					list.setAdapter(null);
					getTask.cancel(true);
					loading.setVisibility(LinearLayout.VISIBLE);
					getTask = new GetTask();
					getTask.execute();
				}

			} catch (Exception e) {

			}
		}
	};

	private class GetTask extends AsyncTask<Void, Void, ReturnModel> {
		@Override
		protected ReturnModel doInBackground(Void... params) {
			return GetData();
		}

		@Override
		protected void onPostExecute(ReturnModel result) {
			loading.setVisibility(LinearLayout.INVISIBLE);
			setPagingAdapter();
			booksList = result.getheadlines();
			authorItemAdapter = new AuthorItemAdapter(_scope, booksList);
			list.setAdapter(authorItemAdapter);
			list.setOnItemLongClickListener(new OnItemLongClickListener() {
				@Override
				public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
						int pos, long arg3) {
					// TODO Auto-generated method stub
					selectedBookIndex = pos;
					showDialog(CONTEXT_MENU_ID);
					return false;
				}
			});

			list.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					ShowPdfViewer(position);

				}
			});

		}
	}

	private void ShowPdfViewer(int bookPosition) {
		AuthorsBooksActivity.authorId = booksList.get(bookPosition).get("authorId");
		Intent i = new Intent(getApplicationContext(), AuthorsBooksActivity.class);
		startActivityForResult(i, 100);
	}
	private void showAddToPlaylist() {
		String authorInfo = booksList.get(selectedBookIndex).get("authorInfo").toString();
		if(authorInfo.length()==0)
			authorInfo = getString(R.string.author_no_info);
		ErrorDialog errorDialog= new ErrorDialog(AuthorsActivity.this,authorInfo);
		errorDialog.show();
	}

	private ReturnModel GetData() {
		
		booksList = new ArrayList<HashMap<String, String>>();

		AMFConnection amfConnection = new AMFConnection();
		try {
			amfConnection
					.connect("http://mos7af.com/IslamicBooksApi/index.php/amf/gateway");
		} catch (ClientStatusException cse) {
			System.out.println("Error while connecting");
			// return false;
		}

		try {
			if (lastselectedImagePosition == -1)
				lastselectedImagePosition = 0;
			Object result = amfConnection.call("AuthorsServices.getAllAuthorsWithDetails",
					new Object[] { lastselectedImagePosition, 10 });

			try {

				JSONArray jsonArray = new JSONArray(result.toString());
				if (booksCount == 0) {

					JSONObject booksCountObj = jsonArray.getJSONObject(0);
					booksCount = Integer.parseInt(booksCountObj
							.getString("count"));

				}
				for (int i = 1; i < jsonArray.length(); i++) {
					JSONObject jsonObject = jsonArray.getJSONObject(i);
					HashMap<String, String> book = new HashMap<String, String>();
					book.put("authorId", jsonObject.getString("authorId"));
					book.put("authorName", jsonObject.getString("authorName"));
					book.put("addedDate", jsonObject.getString("addedDate"));
					book.put("authorInfo", jsonObject.getString("authorInfo"));
					booksList.add(book);
				}
			} catch (Exception e) {
				e.printStackTrace();

			}

		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

		ReturnModel returnModel = new ReturnModel();
		returnModel.setheadlines(booksList);
		return returnModel;
	}

	private class ReturnModel {
		private ArrayList<HashMap<String, String>> booksList;

		public ArrayList<HashMap<String, String>> getheadlines() {
			return booksList;
		}

		public void setheadlines(ArrayList<HashMap<String, String>> _songsList) {
			this.booksList = _songsList;
		}

	}

	@Override
	protected Dialog onCreateDialog(int id) {
		if (id == CONTEXT_MENU_ID) {
			return iconContextMenu.createMenu(getString(R.string.context_menu_author_title));
		}
		return super.onCreateDialog(id);
	}

}