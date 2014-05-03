package com.hgogonis.mpdremote;

import java.util.ArrayList;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.PopupMenu.OnMenuItemClickListener;

public class PlaylistActivity extends Activity {
	
	private Song nowPlaying;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_playlist);
		
		updatePlaylist();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.playlist, menu);
		return true;
	}
	
	protected void updatePlaylist() {
		
		MPDConnection mpd = new MPDConnection(this) {
		
			@Override
			protected void onPostExecute(ArrayList<String> result) {
								
			    ArrayList<Song> playlist = new ArrayList<Song>();

			    // Seperate individual song from raw playlist data
				ArrayList<Integer> startPoints = new ArrayList<Integer>();
			    for (int i=0; i < result.size(); i++) {
			    	if (result.get(i).startsWith("file:")) 
			    		startPoints.add(i);
			    }
		    	startPoints.add(result.size());
		    	
		    	// Add song to playlist 
			    for (int i=0; i < startPoints.size()-1; i++) {
			    	ArrayList<String> song = new ArrayList<String>(result.subList(startPoints.get(i), startPoints.get(i+1)));
			    	playlist.add(new Song(song));
			    }
			    
			   /* Log.d("Playlist Debugging", "Raw Result: " + result.toString() + 
			    		"\nStartPoints:" + startPoints.toString()
			    		+ "\nPlaylist: " + playlist.toString()); */
				

			    // Show playlist view
				ListView playlistView = (ListView) findViewById(R.id.playlist);
				PlaylistAdapter adapter = new PlaylistAdapter(getBaseContext(), playlist);
				playlistView.setAdapter(adapter);
	
			}
		};
		
		mpd.execute("playlistinfo");	
	}

}

class PlaylistAdapter extends ArrayAdapter<Song> {
	private Context context;
	private ArrayList<Song> playlist;
	
	public PlaylistAdapter(Context context, ArrayList<Song> list) {
		super(context, R.layout.playlist_layout, list);
		this.context = context;
		playlist = list;
	}
	
	public View getView(int position, View convertView) {
	     
	     
	    TextView songName = (TextView) convertView.findViewById(R.id.songName);
	    TextView artistName = (TextView) convertView.findViewById(R.id.artistName);
	    
	    Song song = playlist.get(position);
	   
	    songName.setText(song.getTitle());
	    
	    //if (song.get("Artist") != null && song.get("Album") != null)
	    	artistName.setText(song.getArtist() + " - " + song.getAlbum());
	   
	    	final int pos = song.getId();
	    
	    // Button click listener
	    LinearLayout songEntry = (LinearLayout) convertView.findViewById(R.id.songEntry);
	    		songEntry.setOnClickListener(new View.OnClickListener() {
	    	        @Override
	    	        public void onClick(View view) {  	
			 
			  			//TODO Fix context
			  			new MPDConnection(context).execute("playid " + pos);
			  			//((MainActivity) view.getContext()).updateCurrentSong();		 
	    	        }
	    	    });
	     
	     
	    return convertView;
	}	
	
		
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
	     
	    if (convertView == null) {
	        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	        convertView = inflater.inflate(R.layout.playlist_layout, parent, false);
	    }
	    	     
	    TextView songName = (TextView) convertView.findViewById(R.id.songName);
	    TextView artistName = (TextView) convertView.findViewById(R.id.artistName);
	    
	    Song song = playlist.get(position);
	    
	    Button overflowButton = (Button) convertView.findViewById(R.id.songOverflow);
	    
	    /*
	     * Creates popup menu for overflow button
	     */
	    overflowButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(final View v) {
               PopupMenu popup = new PopupMenu(v.getContext(),v);
     	       popup.getMenuInflater().inflate(R.menu.playlist_overflow_popup, popup.getMenu());
     	       popup.setOnMenuItemClickListener(new OnMenuItemClickListener() {
     	    	   
                 @Override
                 public boolean onMenuItemClick(final MenuItem item) {
              	   		int id = item.getItemId();
  
              	   		if (id == R.id.delete) {
             	   			new MPDConnection(v.getContext()).execute("delete " + position);
              	   		}
              	   		     		
                     return true;
                 }
             }); 

         // Showing the popup menu 
         popup.show();
            }
        });


	    /*
	    MPDConnection mpd = new MPDConnection(this.context) {
	    	Song nowPlaying;
			@Override
			protected void onPostExecute(ArrayList<String> result) {
				
				nowPlaying = new Song(result);
			    Button nowPlaying = (Button) ((Activity) getContext()).findViewById(R.id.nowPlayingButton);
			    
				
	
			}
		};
		
		mpd.execute("currentsong");*/
	    //Song nowPlaying = MainActivty.getNowPlaying();
	
	    //if (song.getId() == currentSong())
	 
		
	
	   // if (song.get("Title") != null)
	    	songName.setText(song.getTitle());
	    
	    //if (song.get("Artist") != null && song.get("Album") != null)
	    	artistName.setText(song.getArtist() + " - " + song.getAlbum());
	   
	    	final int pos = song.getId();
	    
	    // Button click listener
	    LinearLayout songEntry = (LinearLayout) convertView.findViewById(R.id.songEntry);
	    		songEntry.setOnClickListener(new View.OnClickListener() {
	    	        @Override
	    	        public void onClick(View view) {  	
			 
			  			//TODO Fix context
			  			new MPDConnection(context).execute("playid " + pos);
			  			
			  			//((MainActivity) view.getContext()).updateCurrentSong();		 
	    	        }
	    	    });
	     
	     
	    return convertView;
	}	
}
