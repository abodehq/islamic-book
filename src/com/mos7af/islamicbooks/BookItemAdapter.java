package com.mos7af.islamicbooks;

import java.util.ArrayList;
import java.util.HashMap;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class BookItemAdapter extends BaseAdapter {
    
    private Activity activity;
    private ArrayList<HashMap<String, String>> data;
    private static LayoutInflater inflater=null;
    public ImageLoader imageLoader; 
    
    public BookItemAdapter(Activity a, ArrayList<HashMap<String, String>> _source) {
        activity = a;
        data=_source;
        inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        imageLoader=new ImageLoader(activity.getApplicationContext());
    }

    public int getCount() {
        return data.size();
    }

    public Object getItem(int position) {
        return position;
    }

    public long getItemId(int position) {
        return position;
    }
    
    public View getView(int position, View convertView, ViewGroup parent) {
        View vi=convertView;
        if(convertView==null)
            vi = inflater.inflate(R.layout.ly_book_item, null);

        TextView title = (TextView)vi.findViewById(R.id.title); 
        TextView content = (TextView)vi.findViewById(R.id.content); 
        ImageView img=(ImageView)vi.findViewById(R.id.img);
        
        HashMap<String, String> sura = new HashMap<String, String>();
        sura = data.get(position);
        
        title.setText(sura.get("bookTitle"));
        content.setText(sura.get("authorName"));
        imageLoader.DisplayImage(sura.get("bookCoverThumb"), img);
        return vi;
    }
}