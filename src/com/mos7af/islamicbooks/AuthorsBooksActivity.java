package com.mos7af.islamicbooks;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import flex.messaging.io.amf.client.AMFConnection;
import flex.messaging.io.amf.client.exceptions.ClientStatusException;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Gallery;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;
import org.json.JSONArray;
import org.json.JSONObject;

public class AuthorsBooksActivity extends Activity {

	private AuthorsBooksActivity _scope;
	private GetTask getTask;

	private ArrayList<HashMap<String, String>> booksList;
	private ListView list;
	private BookItemAdapter bookItemAdapter;
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
	private final int MENU_ITEM_3_ACTION = 3;
	private int selectedBookIndex = 0;

	private boolean _bFirstLoad = false;
	private ProgressDialog mProgressDialog;
	public static String authorId = "1";
	private ProgressBar loading;
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
		iconContextMenu.addItem(res,getString(R.string.context_menu_read_book), R.drawable.ic_1,
				MENU_ITEM_1_ACTION);
		iconContextMenu.addItem(res, getString(R.string.context_menu_add_fav),
				R.drawable.ic_2, MENU_ITEM_2_ACTION);
		iconContextMenu.addItem(res, getString(R.string.context_menu_download), R.drawable.ic_3,
				MENU_ITEM_3_ACTION);
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
						case MENU_ITEM_3_ACTION:
							DownloadBook(selectedBookIndex);
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

	private void DownloadBook(int _selectedBookIndex) {
		String myPDFURL = "https://s3.amazonaws.com/islamicbooks/"
				+ booksList.get(_selectedBookIndex).get("bookId") + "/1.pdf";
		Toast.makeText(
				getApplicationContext(),
				getString(R.string.download_title)
						+ booksList.get(_selectedBookIndex).get("bookTitle"),
				1000).show();
		mProgressDialog.setMessage(getString(R.string.download_path_msg));
		mProgressDialog.setTitle(booksList.get(_selectedBookIndex).get(
				"bookTitle"));
		DownloadFile downloadFile = new DownloadFile();
		downloadFile.execute(myPDFURL,
				booksList.get(_selectedBookIndex).get("bookTitle"), booksList
						.get(_selectedBookIndex).get("bookId"));
	}

	private class DownloadFile extends AsyncTask<String, Integer, String> {
		@Override
		protected String doInBackground(String... sUrl) {
			try {
				File folder = new File(
						Environment.getExternalStorageDirectory()
								+ "/المكتبة_الأسلامية");
				if (!folder.exists()) {
				}
				URL url = new URL(sUrl[0]);
				URLConnection connection = url.openConnection();
				connection.connect();
				// this will be useful so that you can show a typical 0-100%
				// progress bar
				int fileLength = connection.getContentLength();

				// download the file
				InputStream input = new BufferedInputStream(url.openStream());
				OutputStream output = new FileOutputStream(
						"/sdcard/المكتبة_الأسلامية/" + sUrl[1] + ".pdf");

				byte data[] = new byte[1024];
				long total = 0;
				int count;
				while ((count = input.read(data)) != -1) {
					total += count;
					publishProgress((int) (total * 100 / fileLength));
					output.write(data, 0, count);
				}

				output.flush();
				output.close();
				input.close();
			} catch (Exception e) {
			}
			return null;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			mProgressDialog.show();

		}

		@Override
		protected void onProgressUpdate(Integer... progress) {
			super.onProgressUpdate(progress);
			mProgressDialog.setProgress(progress[0]);
		}

		@Override
		protected void onPostExecute(String result) {
			mProgressDialog.cancel();
			Toast.makeText(getApplicationContext(), getString(R.string.download_finish),
					1000).show();
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
					loading.setVisibility(LinearLayout.VISIBLE);
					getTask.cancel(true);
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
			bookItemAdapter = new BookItemAdapter(_scope, booksList);
			list.setAdapter(bookItemAdapter);
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
		Intent i = new Intent(getApplicationContext(), ContentActivity.class);
		i.putExtra("book_id", booksList.get(bookPosition).get("bookId"));
		startActivityForResult(i, 100);
		String myPDFURL = "https://s3.amazonaws.com/islamicbooks/"
				+ booksList.get(bookPosition).get("bookId") + "/1.pdf";
	}

	ArrayList<HashMap<String, String>> playlists;

	private void showAddToPlaylist() {
		final Dialog dialog = new Dialog(this);
		dialog.setTitle(getString(R.string.dialog_add_fav_title));
		ListView modeList = new ListView(this);
		modeList.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

				int playlistIndex = position;
				HashMap<String, String> book = booksList.get(selectedBookIndex);
				HashMap<String, String> playlist = playlists.get(playlistIndex);
				db.InsertPlayListSura(book, playlist.get("playlistId"));
				Toast.makeText(getApplicationContext(),
						getString(R.string.dialog_fav_add), 1000).show();
				dialog.cancel();
			}
		});

		playlists = db.getAllPlaylists();
		if (playlists.isEmpty()) {
			Toast.makeText(getApplicationContext(), getString(R.string.dialog_fav_empty),
					1000).show();
			dialog.cancel();
			IslamicBooks.tabIndex = 3;
			IslamicBooks islamicBooks = (IslamicBooks) getParent();
			islamicBooks.switchTab(3);
		} else {
			PlaylistItemAdapter adapter1 = new PlaylistItemAdapter(this);
			adapter1.SetData(playlists);
			modeList.setAdapter(adapter1);
			dialog.getWindow().setFlags(
					WindowManager.LayoutParams.FLAG_FULLSCREEN,
					WindowManager.LayoutParams.FLAG_FULLSCREEN);
			dialog.setContentView(modeList);
			dialog.show();
		}
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
			Object result = amfConnection.call("BooksServices.getAuthorBooks",
					new Object[] {authorId,  lastselectedImagePosition, 10 });

			try {

				JSONArray jsonArray = new JSONArray(result.toString());
				
				if (booksCount == 0) {

					JSONObject booksCountObj = jsonArray.getJSONArray(0).getJSONObject(0);
					booksCount = Integer.parseInt(booksCountObj
							.getString("count"));

				}
				JSONObject authorObj = jsonArray.getJSONArray(0).getJSONObject(1);
				jsonArray =   jsonArray.getJSONArray(1);
				for (int i = 0; i < jsonArray.length(); i++) {
					JSONObject jsonObject = jsonArray.getJSONObject(i);
					HashMap<String, String> book = new HashMap<String, String>();
					book.put("bookId", jsonObject.getString("bookId"));
					book.put("bookTitle", jsonObject.getString("bookTitle"));
					book.put("publishersName", authorObj.getString("authorName"));
					book.put("bookLanguageName",
							jsonObject.getString("languageName"));
					book.put("bookAddedDate", jsonObject.getString("addedDate"));
					book.put("bookCoverThumb",
							jsonObject.getString("bookCoverThumb"));
					book.put("bookCoverImage",
							jsonObject.getString("bookCoverImage"));
					book.put("authorId", authorObj.getString("authorId"));
					book.put("publisherId", jsonObject.getString("publisherId"));
					book.put("authorName",
							jsonObject.getString("publisherName"));
					book.put("languageId", jsonObject.getString("languageId"));
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
			return iconContextMenu.createMenu(getString(R.string.context_menu_title));
		}
		return super.onCreateDialog(id);
	}

}