package com.hgogonis.mpdremote;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import android.app.Activity;
import android.view.KeyEvent;

public class LibraryBrowser extends Activity {
	
	/**
	 * Formats a list of items (artists/albums)
	 * @param TAG_LENGTH the length of the mpd tag. Ex: "Album: ".length()
	 */
	public void formatList(ArrayList<String> result, final String TAG) {
		final int TAG_LENGTH = TAG.length();
	
		for (int i=0; i<result.size(); i++) {
			// Remove blank items
			if (result.get(i).length() <= TAG_LENGTH)
				result.remove(i);
			
			// Remove "AlbumArtist: & Capitalize first letter"
			// Useful for sorting correctly
			String item = result.get(i).substring(TAG_LENGTH);
			//String replacement = Character.toUpperCase(item.charAt(0)) + item.substring(1);
			String replacement = item;		
			result.set(i,replacement);
		}
		
		
		// Sort album artists alphabetically
		Collections.sort(result,new Comparator<String>() {
		    @Override
		    public int compare(String o1, String o2) {              
		        return o1.compareToIgnoreCase(o2);
		    }
		});
	}
	
	protected void queueAlbum(final String artist, final String album) {
		MPDConnection mpd = new MPDConnection(getBaseContext());
		mpd.execute("add" + " \"" + artist + "/" + album + "\"");
	}
	
	 @Override  
	    public boolean dispatchKeyEvent(KeyEvent event) {  
	        if (event.getAction() == KeyEvent.ACTION_DOWN) {  
	            switch (event.getKeyCode()) {  
	            case KeyEvent.KEYCODE_VOLUME_UP:  
	    	  		//new MPDConnection(this).execute("setvol " + (volume++));
	                return true;  
	            case KeyEvent.KEYCODE_VOLUME_DOWN:  
	    	  		//new MPDConnection(this).execute("setvol " + (volume--));
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
