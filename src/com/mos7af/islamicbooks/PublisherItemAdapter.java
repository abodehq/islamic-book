package com.mos7af.islamicbooks;

import java.util.ArrayList;
import java.util.HashMap;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class PublisherItemAdapter extends BaseAdapter {
    
    private Activity activity;
    private ArrayList<HashMap<String, String>> data;
    private static LayoutInflater inflater=null;

    public PublisherItemAdapter(Activity a, ArrayList<HashMap<String, String>> _source) {
        activity = a;
        data=_source;
        inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
            vi = inflater.inflate(R.layout.ly_publisher_item, null);

        TextView title = (TextView)vi.findViewById(R.id.title); 
        TextView content = (TextView)vi.findViewById(R.id.content); 
        
        HashMap<String, String> publisher = new HashMap<String, String>();
        publisher = data.get(position);
        
        title.setText(publisher.get("publisherName"));
        content.setText(publisher.get("addedDate"));
        return vi;
    }
}