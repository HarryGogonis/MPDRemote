package com.hgogonis.mpdremote;

import java.util.ArrayList;

import android.util.Log;

public class MPDStatus {
	private float elapsed;
	private int volume, playlist, song;
	private boolean repeat, random, single, consume, play;
	private Song currentSong;
	
	public MPDStatus(ArrayList<String> data) {
		
		// Import status from MPD
		for (String entry : data) {
			
			if (entry.startsWith("volume:"))
				volume = Integer.parseInt(entry.substring(8));
			
			else if (entry.startsWith("repeat:")) {
				if (entry.endsWith("1"))
					repeat = true;
				else if (entry.endsWith("0"))
					repeat = false;
			}
			
			else if (entry.startsWith("random")) {
				if (entry.endsWith("1"))
					random = true;
				else if (entry.endsWith("0"))
					random = false;
			}
			
			else if (entry.startsWith("elapsed:")) 
				elapsed = Float.parseFloat(entry.substring(9));
			
			else if (entry.startsWith("file:")) {
				int start = data.indexOf(entry);
				currentSong = new Song(data.subList(start, data.size()));
			}
			
		}
		//Log.d("MPD Status", toString());
	}
	
	public Song getSong() {
		return currentSong;
	}
	
	public float getTimeElapsed() {
		return elapsed;
	}
	
	public float getTotalTime() {
		return currentSong.getTime();
	}
	
	public int getVolumeLevel() {
		return volume;
	}
	
	public boolean playing() {
		return play;
	}
	
	
	public String toString() {
		String data = new String();
		
		if (currentSong != null)
			data += currentSong.toString() + "\n\n";

		data += "volume: " + volume + "%\t" ;
		
		if (play) 
			data += "[playing]\n";
		else
			data += "[paused]\n";
		
		if (currentSong != null)
			data += Song.getHumanTime(elapsed) + "/" +currentSong.getHumanTime() + "\n";
			
		data += "repeat: " + repeat + "\n" 
			+ "random: " + random + "\n"
			+ "single: " + single + "\n" 
			+ "consume: " + consume + "\n";
		return data;
	}
}
