package com.hgogonis.mpdremote;

import java.util.ArrayList;

import android.os.Bundle;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.PopupMenu.OnMenuItemClickListener;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v4.app.NavUtils;

public class SongBrowser extends LibraryBrowser {
	
	private static String ARTIST;
	private static String ALBUM;
	private ArrayList<Song> songList;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_song_browser);
		// Show the Up button in the action bar.
		setupActionBar();
		
		// Get Artist name from intent
	    Intent intent = getIntent();
	    String[] extra = intent.getExtras().getStringArray(AlbumBrowser.ALBUM_TAG);
	    
	    ALBUM = extra[0];
	    ARTIST = extra[1];
		
		// Populate list of Albums
		updateView();
		
		// Button Listeners
		Button queueButton = (Button) findViewById(R.id.queue_album);
 		queueButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				queueAlbum(ARTIST,ALBUM);
			}
 		});
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
				// Shows list of songs from artist
				
				ListView songListView = (ListView) findViewById(R.id.songs_list);
				
				TextView albumView = (TextView) findViewById(R.id.album_title);
				TextView artistView = (TextView) findViewById(R.id.album_artist);
				
				albumView.setText(ALBUM);
				artistView.setText(ARTIST);
								
				ArrayList<Song> songList = formatList(result);
				//Log.d("Song list", songList.toString());

				// Set list adapter
				SongAdapter adapter = new SongAdapter(getBaseContext(), songList);

				songListView.setAdapter(adapter);
				
				//TODO implement click handler
				
				/*
				songListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

					
					  public void onListItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
						  //TODO play song
						  final String SONG = arg0.getItemAtPosition(position).toString();
						  Log.d("Album Click Listener", "\"" + SONG + "\"");
						  Toast.makeText(SongBrowser.this,
			                        "Added: " + SONG, Toast.LENGTH_SHORT)
			                        .show();
						  new MPDConnection(arg1.getContext()).execute("add" + " \"" + SONG + "\"" );
					    
					  }
					});q	e*/
	
			}
		};
		
		mpd.execute("find album " + "\"" + ALBUM + "\"");
	}

	public ArrayList<Song> formatList(ArrayList<String> result) {
		/*
		final int TAG_LENGTH = TAG.length();
	
		for (int i=0; i<result.size(); i++) {
			// Remove blank items
			if (result.get(i).length() <= TAG_LENGTH)
				result.remove(i);
			
			// Remove tag
			String item = result.get(i).substring(TAG_LENGTH);
			String replacement = item;		
			result.set(i,replacement);
			*/
	    	ArrayList<Song> songList = new ArrayList<Song>();

			ArrayList<Integer> startPoints = new ArrayList<Integer>();
		    for (int i=0; i < result.size(); i++) {
		    	if (result.get(i).startsWith("file:")) 
		    		startPoints.add(i);
		    }
	    	startPoints.add(result.size());
	    	
	    	// Add song to list 
		    for (int i=0; i < startPoints.size()-1; i++) {
		    	ArrayList<String> song = new ArrayList<String>(result.subList(startPoints.get(i), startPoints.get(i+1)));
		    	songList.add(new Song(song));
		    }
		    
		    return songList;
		}
}

class SongAdapter extends ArrayAdapter<Song> {
	private Context context;
	private ArrayList<Song> songList;
	
	public SongAdapter(Context context, ArrayList<Song> list) {
		super(context, R.layout.song_list_layout, list);
		this.context = context;
		songList = list;
	}
		
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
	     
	    if (convertView == null) {
	        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	        convertView = inflater.inflate(R.layout.song_list_layout, parent, false);
	    }
	     
	    TextView songName = (TextView) convertView.findViewById(R.id.song_title);
	    TextView artistName = (TextView) convertView.findViewById(R.id.song_artist);
	    TextView songTime = (TextView) convertView.findViewById(R.id.song_time);
	    TextView songTrack = (TextView) convertView.findViewById(R.id.song_track);


	    
	    Song song = songList.get(position);	
	    
	    final String title = song.getTitle();
	    final String file = song.getFile();
	    final String artist = song.getArtist();

	
	   //if (title != null)
	    	songName.setText(title);
	   // Log.d("song", "Artist=" + artist + ",Album=" + song.getAlbumArtist());
	    if (!(artist.equals(song.getAlbumArtist())))
	    	artistName.setText(artist);
	    else
	    	artistName.setVisibility(View.GONE);

	   
	    songTrack.setText(""+song.getTrack());
	    songTime.setText(""+song.getHumanTime());
	    
	    // Button click listener
	   LinearLayout songEntry = (LinearLayout) convertView.findViewById(R.id.song_info);
	    		songEntry.setOnClickListener(new View.OnClickListener() {
	    	        @Override
	    	        public void onClick(View view) {  
			  			new MPDConnection(context).execute("add \""  + file + "\"");
			  	        Toast.makeText(context, "Added: " + title, Toast.LENGTH_SHORT).show();
	    	        }
	    	  
	    	    });
	   

	    		/*
	    		 * Overflow menu
	    		 */
		   /* Button overflow = (Button) convertView.findViewById(R.id.song_overflow);
		   overflow.setOnClickListener(new View.OnClickListener() {
			   @Override
			   public void onClick(View view) {  
				   PopupMenu popup = new PopupMenu(context, view);
			       popup.getMenuInflater().inflate(R.menu.song_popup, popup.getMenu());
			       popup.setOnMenuItemClickListener(new OnMenuItemClickListener() {
			    	   
	                   @Override
	                   public boolean onMenuItemClick(MenuItem item) {
	                	   		String title = (String) item.getTitle();
	                	   		if (title.equals("Add"))
	                	   			new MPDConnection(context).execute("add \""  + file + "\"");
	                	   			Toast.makeText(context, "Added: " + title, Toast.LENGTH_SHORT).show();
	                       return true;
	                   }
	               }); 

               // Showing the popup menu 
               popup.show();
				}
			});*/
		     	     
	     
	    return convertView;
	}	

}

