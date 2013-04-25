package com.mos7af.islamicbooks;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class PagingAdapter extends BaseAdapter {
    
    private Activity activity;
    private String[] data;
    private static LayoutInflater inflater=null;
    
    public PagingAdapter(Activity _source) {
        activity = _source;
        inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    public void SetData(int pagesCount)
    {
    	data = new String[pagesCount];
     	 for(int i=0;i<data.length;i++)
     	 {
     		 
     		data[i]=Integer.toString(i+1);
     		
     	 }
    }

    public int getCount() {
    	return data.length;
    }

    public Object getItem(int position) {
        return position;
    }

    public long getItemId(int position) {
        return position;
    }
    
    public View getView(int position, View convertView, ViewGroup parent) {
    	
    	View vi=convertView;
    	
		vi = inflater.inflate(R.layout.viewitem, null);
		if(position!=-1)
		{
			TextView button = (TextView) vi.findViewById(R.id.clickbutton);
			button.setText(data[position]);
		}

		return vi;
    }
}