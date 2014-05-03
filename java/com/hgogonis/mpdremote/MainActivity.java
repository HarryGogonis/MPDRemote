package com.hgogonis.mpdremote;
import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.PopupMenu.OnMenuItemClickListener;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

public class MainActivity extends Activity {
	
	//private HashMap<String,String> status = new HashMap<String, String>();
	//private ArrayList<String> status;
	//private long statusTime;

	protected static final long TIMEBAR_INTERVAL_MS = 5000;
	
	private boolean isPlaying;
	private int volume;
	private Song nowPlaying;
	
	private MPDStatus mpdStatus;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		//Initialize variables
		//statusTime = 0L;
	    /*getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, 
	                            WindowManager.LayoutParams.FLAG_FULLSCREEN
	                           );*/
		
		connect();
		
    
      
 	}
	
	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle item selection
	    switch (item.getItemId()) {
	   		case R.id.action_library:
	   			Intent libraryIntent = new Intent(this, ArtistBrowser.class);
	            startActivity(libraryIntent);
	   			return true;
	        case R.id.action_search:
	            //TODO Implement search action
	            return true;
	        case R.id.action_settings:
	        	Intent settingsIntent = new Intent(this, SettingsActivity.class);
	            startActivity(settingsIntent);
	            return true;
	        case R.id.action_status:
	        	Intent statusIntent = new Intent(this, StatusActivity.class);
	            startActivity(statusIntent);
	            return true;
	        case R.id.action_queue:
	        	Intent queueIntent = new Intent(this, PlaylistActivity.class);
	            startActivity(queueIntent);
	            return true;
	        case R.id.action_connect:
	        	connect();
	        	return true;
	        case R.id.action_update:
 	   			new MPDConnection(this).execute("update");
 	   			return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}
	
	//public boolean mpdIsConnected() {
	//	return getStatus().size() > 1;
	//}

	/*
	 *  Connects to MPD
	 *  Updates the UI with current song, playlist, and button states
	 */
	public void connect() {
		MPDConnection mpd = new MPDConnection(this) {
			
			@Override
			protected void onPostExecute(ArrayList<String> result) {				
				if (result.size() > 1) {
				
					// Set now playing
					updateCurrentSong();
			    	
					// Get play state
			    	if (result.get(10).equals("state: play")) {
			    		isPlaying = true;
			    	}
			    	
			    	// Get volume 
			    	if (result.get(0).startsWith("volume:")) {
			    		volume = Integer.parseInt(result.get(0).substring(8));
			    	}
			    	
			    	// Get play queue
			    	updatePlayQueue();
			    	
			    	// Updates UI Buttons
			    	updateButtons();
			    	
			    	// Updates time slider
			    	updateTimeBar();
			    	
				}
				else if (result.size() == 1) {
					new ConnectionErrorDialog(getContext()).show(getFragmentManager(), result.get(0));
				}
			}
		};
		mpd.execute("status");

	}
	
	private void updateStatus() {
				
		MPDConnection mpd = new MPDConnection(this) {
			
			@Override
			protected void onPostExecute(ArrayList<String> result) {				
				mpdStatus = new MPDStatus(result);
			}
		};
		mpd.execute("status");
	}
	
	/* Update queue */
	protected void updatePlayQueue() {
		
		MPDConnection mpd = new MPDConnection(this) {
		
			@Override
			protected void onPostExecute(ArrayList<String> result) {
								
			    ArrayList<Song> song_queue = new ArrayList<Song>();

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
			    	song_queue.add(new Song(song));
			    }
			    
			   /* Log.d("Playlist Debugging", "Raw Result: " + result.toString() + 
			    		"\nStartPoints:" + startPoints.toString()
			    		+ "\nPlaylist: " + playlist.toString()); */
				

			    // Show playlist view
				ListView playlistView = (ListView) findViewById(R.id.song_queue);
				PlaylistAdapter adapter = new PlaylistAdapter(getBaseContext(), song_queue);
				playlistView.setAdapter(adapter);
				
				//playlistView.smoothScrollToPosition(10);
				
				/*
				for (int i=0; i<playlistView.getCount(); i++) {
						Song tempSong = (Song) playlistView.getItemAtPosition(i);
						adapter.
						//View songView = adapter.getView(i, playlistView);
					//if (tempSong.getId() == nowPlaying.getId())
						//songView.setBackgroundResource(R.drawable.ic_action_play);
				}
				Log.e("MainActivity playlist","Current ID " + nowPlaying.getId() + "ID 0:"+((Song) playlistView.getItemAtPosition(0)).getId());
				*/
			}
		};
		
		mpd.execute("playlistinfo");	
	}

	//TODO Make status class
	/*
	 * Gets the status from mpd
	
	protected HashMap<String,String> getStatus() {
		
		
	
		long elapsedTime = (System.nanoTime() - statusTime)/1000000L;
		
		if (elapsedTime > 4000) {
			// Update status
			statusTime = System.nanoTime();
		
			MPDConnection mpd = new MPDConnection(this) {
				@Override
				protected void onPostExecute(ArrayList<String> result) {
					 if (result.size() > 1) {
					
						//Log.d("Debugging", statusData.toString());
						status.put("volume", result.get(0).substring(8));
						status.put("repeat", result.get(1).substring(8));
						status.put("random", result.get(2).substring(8));
						status.put("single", result.get(3).substring(8));
						status.put("consume", result.get(4).substring(9));
						status.put("state", result.get(10).substring(7));
						//status.put("time", statusData.get(13).substring(6).substring(0,4));
						//status.put("elapsed", statusData.get(15).substring(9));
						
						
						
					} else if (result.size() == 1) {
						status.put("Error", result.get(0));
					}
				}
			};
			mpd.execute("status");
		}
		return this.status;
	} */

	
	/* 
	 * Updates the current playing song
	 * 
	*/
	protected void updateCurrentSong() {
		MPDConnection mpd = new MPDConnection(this) {
			@Override
			protected void onPostExecute(ArrayList<String> result) {
				
				mpdStatus = new MPDStatus(result);
				Song nowPlaying = mpdStatus.getSong();
				
				TextView song_title = (TextView) findViewById(R.id.song_title);
				TextView song_artist = (TextView) findViewById(R.id.song_artist);
				TextView song_album = (TextView) findViewById(R.id.song_album);
				
				if (nowPlaying != null) {
	
					if (nowPlaying.getTitle() != null) 
						song_title.setText(nowPlaying.getTitle());
					else if (nowPlaying.getTitle() != null)
						song_title.setText(nowPlaying.getTitle());
					else 
						song_title.setText("No song");
					
					if (nowPlaying.getArtist() != null && nowPlaying.getAlbum() != null) {
						song_artist.setText(nowPlaying.getArtist());
						song_album.setText(nowPlaying.getAlbum());
					}
					else 
						song_artist.setText("Host: " + getHost() + " Port: " + getPort());
				}
			}
		};
		
		mpd.execute("command_list_begin\nstatus\ncurrentsong\ncommand_list_end");
	}
	
	
	
	private void updateTimeBar() {
		
		final Context context = findViewById(R.id.time_seekBar).getContext();
		
		//TODO
		// Thread should only increment the time each second until reaching max
		// Not communicating with server
		// Communicate only when reach end
		  new Thread(new Runnable() {
			    public void run() {
			    	
			    	while (true) {
			    		try {
					           MPDConnection mpd = new MPDConnection((Context) context) {
								@Override
								protected void onPostExecute(ArrayList<String> result) {
									
												
									
											if (result.size()<14) {
												  return;
											  }
											// UI Elements
											SeekBar timeBar = (SeekBar)findViewById(R.id.time_seekBar);
										    TextView currentTime = (TextView)findViewById(R.id.time_current);
										    TextView totalTime = (TextView)findViewById(R.id.time_total);
										    
										    // Parses song id from server response
										    int id = Integer.parseInt(result.get(12).substring(8));
											if (nowPlaying == null || id != nowPlaying.getId()) {
												// Update song
												nowPlaying = new Song(result);
												// Update Now Playing & Playlist
												updateCurrentSong();
												updatePlayQueue();
											}
									        //timeBar.setOnSeekBarChangeListener(new VolumeListener());     
							
									        // Parses current time of song from server response
									        float progress = Float.parseFloat(result.get(14).substring(9));
									        float max = mpdStatus.getTotalTime();
									        
									        timeBar.setMax((int) max);
									        timeBar.setProgress((int) progress);  
									        
									        currentTime.setText(Song.getHumanTime(progress));
									        totalTime.setText(Song.getHumanTime(max));
									        
											
										
									}
								};
								mpd.execute("status");
								Thread.sleep(TIMEBAR_INTERVAL_MS);

			    		} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (Exception e) {
							Log.e("Timebar Thread", e.getMessage());
						}
			    	}
			    }
		  }).start();
	}
	
	protected void updateButtons() {
		/*
        final Button playPause = (Button) findViewById(R.id.play_pause);
		if (getStatus().get("state").equals("play")) {
			playPause.setBackgroundResource(R.drawable.ic_action_pause);
		} else {
			playPause.setBackgroundResource(R.drawable.ic_action_play);
		}
		
		final Button repeat = (Button) findViewById(R.id.repeat);
		if (getStatus().get("repeat").equals("1")) {
			repeat.setBackgroundResource(R.drawable.ic_action_repeat);
		} 
		
		final Button shuffle = (Button) findViewById(R.id.shuffle);
		if (getStatus().get("random").equals("1")) {
			shuffle.setBackgroundResource(R.drawable.ic_action_shuffle);
		}
		*/
		
		
    	// Set current volume
        //SeekBar volumeBar = (SeekBar)findViewById(R.id.volume_seekBar);
        //volumeBar.setOnSeekBarChangeListener(new VolumeListener());     
    	int progress = volume;
       // volumeBar.setProgress(progress);
        
        
        // Update time seekbar
        
       
       
	}
	
	
	
	/* 
	 * Button Controls
	
	public void repeat(final View view) {
		
		if (mpdIsConnected()) {
			// Repeat On
			if (getStatus().get("repeat").equals("0")) {
				new MPDConnection(view.getContext()){
					@Override 
					protected void onPostExecute(ArrayList<String> result) {
						view.setBackgroundResource(R.drawable.ic_action_repeat);
					}
				}.execute("repeat 1");
			}
			
			// Repeat Off
			else  {
				new MPDConnection(view.getContext()){
					@Override 
					protected void onPostExecute(ArrayList<String> result) {
						view.setBackgroundResource(R.drawable.ic_action_repeat_unselected);
					}
				}.execute("repeat 0");
			}
		}
	} */
	

	public void previous(View view) {
		new MPDConnection(this) {
			@Override 
			protected void onPostExecute(ArrayList<String> result) {
				updateCurrentSong();
			}
		}.execute("previous");
	}
	
	public void playPause(final View view) {
		if (!isPlaying) {
			// Play
			new MPDConnection(view.getContext()){
				@Override 
				protected void onPostExecute(ArrayList<String> result) {
					view.setBackgroundResource(R.drawable.ic_action_pause);
					isPlaying = true;
					updateCurrentSong();
				}
			}.execute("play");
		}
		else {
			// Pause
			new MPDConnection(view.getContext()){
				@Override 
				protected void onPostExecute(ArrayList<String> result) {
					view.setBackgroundResource(R.drawable.ic_action_play);
					isPlaying = false;
				}
			}.execute("pause");				
		}
	}
		

	public void next(View view) {
			new MPDConnection(this) {
				@Override 
				protected void onPostExecute(ArrayList<String> result) {
					updateCurrentSong();
				}
			}.execute("next");
	}
	/*
	public void shuffle(View view) {
		if (mpdIsConnected()) {
			if (getStatus().get("random").equals("0")) {
				new MPDConnection(this).execute("random 1");
				view.setBackgroundResource(R.drawable.ic_action_shuffle);
			}
			else {
				new MPDConnection(this).execute("random 0");
				view.setBackgroundResource(R.drawable.ic_action_shuffle_unselected);
			}
		}
	}
	 */
	
	public void controlOverflow(final View view) {
		   PopupMenu popup = new PopupMenu(this, view);
	       popup.getMenuInflater().inflate(R.menu.song_overflow_popup, popup.getMenu());
	       popup.setOnMenuItemClickListener(new OnMenuItemClickListener() {
	    	   
            @Override
            public boolean onMenuItemClick(final MenuItem item) {

         	   		String title = (String) item.getTitle();
         	   		int id = item.getItemId();
         	   		//Log.d("Overflow click handler", title +" "+  id);
         	   		//Log.d("Consume:","" + R.id.Consume);
         	   		item.setChecked(true);
         	   		
         	   		// Clears play queue
         	   		if (id == R.id.Clear_Queue) {
         	   			new MPDConnection(view.getContext()).execute("clear");
         	   			updatePlayQueue();
         	   		}
         	   		else if (id == R.id.Consume) {
         	   			Log.d("Overflow Click", "Consume 1");
         	   			//if ()
	         	   		new MPDConnection(view.getContext()) {
	        				@Override 
	        				protected void onPostExecute(ArrayList<String> result) {
	        				}
	        			}.execute("consume 1");
         	   		}
		
                return true;
            }
        }); 

    // Showing the popup menu 
    popup.show();
    
	}
	
	/*
	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
	    if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
	    	Log.d("Key Event", "Volume Up");
	    	return false;
	    }
	    else if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
	    	Log.d("Key Event", "Volume Down");
	       return true;
	    }
	    //return super.onKeyUp(keyCode, event);
	    return true;
	}*/
    @Override  
    public boolean dispatchKeyEvent(KeyEvent event) {  
        if (event.getAction() == KeyEvent.ACTION_DOWN) {  
            switch (event.getKeyCode()) {  
            case KeyEvent.KEYCODE_VOLUME_UP:  
    	  		new MPDConnection(this).execute("setvol " + (volume++));
    	  		updateButtons();
                return true;  
            case KeyEvent.KEYCODE_VOLUME_DOWN:  
    	  		new MPDConnection(this).execute("setvol " + (volume--));
    	  		updateButtons();
                return true;  
            }  
        }  
        if (event.getAction() == KeyEvent.ACTION_UP   
            && (event.getKeyCode() == KeyEvent.KEYCODE_VOLUME_UP   
                || event.getKeyCode() == KeyEvent.KEYCODE_VOLUME_DOWN)) {  
            return true;  
        }  
        return super.dispatchKeyEvent(event);  
    }  
}

class ConnectionErrorDialog extends DialogFragment {
	
	final Context context;
	String cause;
	
	public ConnectionErrorDialog(Context context) {
		this.context = context;
	}

	
	 @Override
	    public Dialog onCreateDialog(Bundle savedInstanceState) {
		 
		 	// Get cause of error
			try {
				cause = new MPDConnection(context).execute("status").get().get(0);
			} catch (Exception e) {
				cause = e.getMessage();
			}

	        // Use the Builder class for convenient dialog construction
	        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
	        
	        builder.setMessage("Failed to connect to MPD Server.\n" + cause)
	               .setPositiveButton(R.string.retry, new DialogInterface.OnClickListener() {
	                   @Override
					public void onClick(DialogInterface dialog, int id) {
	                       // TODO Reconnect
	                	   ((MainActivity) getActivity()).connect();
	                   }
	               })
	               .setNeutralButton(R.string.action_settings, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// Open Settings
						Intent settingsIntent = new Intent(context, SettingsActivity.class);
			            startActivity(settingsIntent);
					}
				})
	               
	               .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
	                   @Override
					public void onClick(DialogInterface dialog, int id) {
	                       // Dialog Canceled
	                   }
	               });
	        // Create the AlertDialog object and return it
	        return builder.create();
	    }

}

class TimeListener implements SeekBar.OnSeekBarChangeListener {
	
	//private float time_elapsed, total_time;
	private int progress;

	@Override
	public void onProgressChanged(SeekBar seekBar, int progress,
			boolean fromUser) {
		this.progress = progress;
	}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
		// TODO Auto-generated method stub
		Log.d("Time Listener", "Progress: " + progress);
	}
	
}

class VolumeListener implements SeekBar.OnSeekBarChangeListener {
	
	private int volumeLevel;

	@Override
	public void onProgressChanged(SeekBar seekBar, int progress,
			boolean fromUser) {
		volumeLevel = progress;
	}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
		// TODO Auto-generated method stub
		Log.d("Debugging", "Volume: " + volumeLevel);
		
		if (volumeLevel >= 0)
			new MPDConnection(seekBar.getContext()).execute("setvol " + volumeLevel);

	}
	
}

class TimeBarRunnable implements Runnable {
	Context context;
	
	TimeBarRunnable(Context context) {
		this.context = context;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		//if (mpdStatus == null)
		
	}
	
	
}
