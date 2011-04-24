package com.example.hellojni;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import nthu.cs.EnHunter.perProcess.ProcessCrowler;

import android.app.Activity;
import android.app.ListActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;


import android.content.Context;
import android.content.pm.*;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;

public class ListViewActivity  extends ListActivity{

	class TagView {
		ImageView image;
		TextView app_name;
		TextView text;
		
		public TagView(ImageView v,TextView t1, TextView t2) {
			image = v;
			app_name = t1;
			text = t2;
		}
		
		
	}
	
	class mAdapter extends BaseAdapter {

		List<String> appList = new ArrayList<String>();
		LayoutInflater iftr;
		List<TagView> tagList;
		PackageManager pm ;
		public mAdapter(Context c, List list,PackageManager pm ) {
			this.iftr = LayoutInflater.from(c);
			for(int i=0 ; i<list.size(); i++){
				appList.add((String) list.get(i));
				Log.d("MTK",i+" "+(String) list.get(i));
			}
			this.pm = pm;
	/*		tagList = new ArrayList<TagView>();
			for(int i=0 ; i<appList.size();i++)
			{
				TagView t = new TagView(
						(ImageView)	findViewById(R.id.ItemImage),
						(TextView)	findViewById(R.id.ItemText),
						(TextView)	findViewById(R.id.ItemTitle)
				);
			
				
			}*/
		}
		
		public int getCount() {
			// TODO Auto-generated method stub
			return appList.size();
		}

		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return null;
		}

		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			TagView tag;
			Log.d("MTK","in getView...");
			
			if(convertView == null){
				convertView = this.iftr.inflate(R.layout.entry, null);
				tag = new TagView(
						(ImageView)	convertView.findViewById(R.id.ItemImage),
						(TextView)	convertView.findViewById(R.id.ItemText),
						(TextView)	convertView.findViewById(R.id.ItemTitle)
						);
				convertView.setTag(tag);
			}
			else{
				tag = (TagView)convertView.getTag();
			}
			
			Log.d("MTK","in getView...tag = "+ tag);
			try {
				
			
				Log.d("MTK",position+ " "+pm.getApplicationIcon(appList.get(position)));
				tag.image.setImageDrawable(pm.getApplicationIcon(appList.get(position)));
			
			
			
			} catch (NameNotFoundException e) {
				// TODO Auto-generated catch block
				tag.image.setImageResource(R.drawable.icon);
			
				Log.d("MTK","A_A");
				e.printStackTrace();
			}
			String pkg_name = "unknown";
			try{
				//	pkg_name =pm.getApplicationLabel(a);
				PackageInfo p  =pm.getPackageInfo(appList.get(position), -1);
				pkg_name = p.applicationInfo.loadLabel(getPackageManager()).toString();
		//	tag.app_name.setText(pm.getInstallerPackageName(appList.get(position)));
			
			}catch(Exception e) {
				pkg_name = "System";
			}
			tag.text.setText(pkg_name);
			tag.app_name.setText(appList.get(position));
			return convertView;
		}
		
	}
	
	
	mAdapter baseAdapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.list);  
		
		PackageManager pm =  this.getPackageManager();
		setListAdapter(new mAdapter(this,ProcessCrowler.getInstance().getCurrentPkgList(),pm));
		
		Log.d("Hunter","in ListView onCreate()");
		//ListView list = (ListView) findViewById(R.id.ListView01);
		
		
		
		
	/*	
		try {
		
			PackageManager pm =  this.getPackageManager();
			
			
			
			//List<PackageInfo> pkg = getPackageManager().getInstalledPackages(0);
			
		List<String> appList = ProcessCrowler.getInstance().getCurrentPkgList();	
		
			
		
		Log.d("Hunter","appList size = "+appList.size());
			
		ArrayList<HashMap<String, Object>> listItem = new ArrayList<HashMap<String, Object>>();  
		
		for(int i=0 ; i<appList.size();i++)
		{
			HashMap<String, Object> map = new HashMap<String, Object>();  
			
			int found =1;
			try {
				
				
			Drawable pi = pm.getApplicationIcon(appList.get(i));
			PackageInfo p = pm.getPackageInfo(appList.get(i), -1);
	
			//  ImageView myImageView = (ImageView)findViewById(R.id.ItemImage);
			///  myImageView.setImageDrawable(pi);
			  //myImageView.setImageURI(pi);
			getResources().getIdentifier("flag", "drawable", pi);
			map.put("ItemImage", myImageView);
		
			}catch(Exception e) {
				found =0;
			}
			if(found==0) {
				map.put("ItemImage",R.drawable.icon);
			}
			map.put("ItemTitle", appList.get(i));
			map.put("ItemText", appList.get(i)); 
			listItem.add(map);  
		}*/
	/*	for(int i=0;i<10;i++)  {
			HashMap<String, Object> map = new HashMap<String, Object>();  
         //   map.put("ItemImage", R.drawable.checked);
            map.put("ItemTitle", "Level "+i);  
            map.put("ItemText", "Finished in 1 Min 54 Secs, 70 Moves! ");  
            listItem.add(map);  
		}
		  */
	
    /*    SimpleAdapter listItemAdapter = new SimpleAdapter(this,
        	listItem,
        	
            R.layout.entry, 
            new String[] {"ItemImage","ItemTitle", "ItemText"},   
            new int[] {,R.id.ItemTitle,
        				R.id.ItemText} 
        );  
        
        list.setAdapter(listItemAdapter);  
       for(int i=0 ; i<list.getCount();i++) {
        ImageView   iv   =   (ImageView)   list.getChildAt(i).findViewById (R.id.ItemImage); 
       }
        list.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				setTitle("click "+arg2+"item");
			}  
        	
        	
        }); 
		}catch(Exception e) {
			e.printStackTrace();
			
		}*/
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}

	@Override
	protected void onRestart() {
		// TODO Auto-generated method stub
		super.onRestart();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
	}

}
