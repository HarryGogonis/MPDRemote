package com.hgogonis.mpdremote;

import java.util.ArrayList;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.support.v4.app.NavUtils;

public class ArtistBrowser extends LibraryBrowser {
	
	public final static String ARTIST_TAG = "com.example.hgogonis.mpdremote.ARTIST";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_artist_browser);
		// Show the Up button in the action bar.
		setupActionBar();
		
		updateView();
	}

	/**
	 * Set up the {@link android.app.ActionBar}.
	 */
	private void setupActionBar() {
		getActionBar().setDisplayHomeAsUpEnabled(true);
	}

	private void updateView() {
		MPDConnection mpd = new MPDConnection(this) {
			
			@Override
			protected void onPostExecute(ArrayList<String> result) {
				
				final String TAG = "AlbumArtist: ";
				
				// Alphabetizes and formats the list of Artists
				formatList(result, TAG);
				ListView libraryView = (ListView) findViewById(R.id.artists_list);
				
				// Set list adapter
				ArrayAdapter<String> adapter = new ArrayAdapter<String>(this.getContext(),
				        android.R.layout.simple_list_item_1, result);
				libraryView.setAdapter(adapter);
				libraryView.setFastScrollEnabled(true);
				libraryView.setFastScrollAlwaysVisible(true);

				
				libraryView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

					  @Override
					  public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
						  //TODO Open Artist Albums
						  final String ARTIST = arg0.getItemAtPosition(position).toString();
						  Log.d("Artist Click Listener", "\"" + ARTIST + "\"");
						  
						  Intent intent = new Intent(arg1.getContext(), AlbumBrowser.class);
						  intent.putExtra(ARTIST_TAG, ARTIST);
				          startActivity(intent);
					    
					  }
					});
			}
		};
		
		mpd.execute("list AlbumArtist");
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.artist_browser, menu);
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

}
