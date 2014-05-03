package com.hgogonis.mpdremote;

import java.util.ArrayList;

import android.os.Bundle;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.support.v4.app.NavUtils;

public class AlbumBrowser extends LibraryBrowser {
	
	private static String ARTIST;
	public final static String ALBUM_TAG = "com.example.hgogonis.mpdremote.ALBUM";
	public final static String ARTIST_TAG = ArtistBrowser.ARTIST_TAG;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_album_browser);
		// Show the Up button in the action bar.
		setupActionBar();
		
		// Get Artist name from intent
	    Intent intent = getIntent();
	    ARTIST = intent.getStringExtra(ArtistBrowser.ARTIST_TAG);
		
		// Populate list of Albums
		updateView();
	}

	/**
	 * Set up the {@link android.app.ActionBar}.
	 */
	private void setupActionBar() {

		getActionBar().setDisplayHomeAsUpEnabled(true);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.album_browser, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			// This ID represents the Home or Up button. In the case of this
			// activity, the Up button is shown. Use NavUtils to allow users
			// to navigate up one level in the application structure. For
			// more details, see the Navigation pattern on Android Design:
			//
			// http://developer.android.com/design/patterns/navigation.html#up-vs-back
			//
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	/**
	 * Get the Albums list from MPD and updates the UI
	 */
	private void updateView() {
		MPDConnection mpd = new MPDConnection(this) {
			@Override
			protected void onPostExecute(ArrayList<String> result) {
				
				// Alphabetizes and formats the list of Artists
				formatList(result,"Album: ");
				ListView albumView = (ListView) findViewById(R.id.album_list);
				
				// Set list adapter
				ArrayAdapter<String> adapter = new ArrayAdapter<String>(this.getContext(),
				        android.R.layout.simple_list_item_1, result);
				albumView.setAdapter(adapter);
				
				albumView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

					  @Override
					  public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {						  
						  final String ALBUM = arg0.getItemAtPosition(position).toString();
						  Log.d("Album Click Listener", "\"" + ALBUM + "\"");
						  
						  Intent intent = new Intent(arg1.getContext(), SongBrowser.class);
						  
						  String[] extra = {ALBUM,ARTIST};
						  intent.putExtra(ALBUM_TAG, extra);
					
				          startActivity(intent);
					    
					  }
					});
			}
		};
		
		mpd.execute("list Album " + "\"" + ARTIST + "\"");
	}

}
