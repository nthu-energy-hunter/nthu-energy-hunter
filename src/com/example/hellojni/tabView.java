package com.example.hellojni;

import java.util.ArrayList;
import java.util.Arrays;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TabHost;

public class tabView extends TabActivity{

	TabHost mTabHost;
	 private ListView mainListView ;  
	  private ArrayAdapter<String> listAdapter ;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tab);
		mTabHost = getTabHost();
		
	     mTabHost.addTab(mTabHost.newTabSpec("tab_test1").setIndicator("裝置狀態", getResources().getDrawable(R.drawable.icon)).setContent(R.id.textview1));
         mTabHost.addTab(mTabHost.newTabSpec("tab_test2").setIndicator("歷史紀錄", getResources().getDrawable(R.drawable.icon)).setContent(R.id.textview2));
        
         mTabHost.addTab(mTabHost.newTabSpec("tag").setIndicator("程序耗能").setContent(new Intent(this,ListViewActivity.class)));
         
         /*
         
         // Find the ListView resource.   
         mainListView = (ListView) findViewById( R.id.lstMain);  

         // Create and populate a List of planet names.  
         String[] planets = new String[] { "Mercury", "Venus", "Earth", "Mars",  
                                           "Jupiter", "Saturn", "Uranus", "Neptune"};    
         ArrayList<String> planetList = new ArrayList<String>();  
         planetList.addAll( Arrays.asList(planets) );  

         // Create ArrayAdapter using the planet list.  
        listAdapter = new ArrayAdapter<String>(this, R.id.lstMain, planetList);  

         // Set the ArrayAdapter as the ListView's adapter.  
         mainListView.setAdapter( listAdapter ); 
*/

      // End

	}
	
}
