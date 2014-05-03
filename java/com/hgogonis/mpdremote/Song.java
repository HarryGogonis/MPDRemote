package com.hgogonis.mpdremote;

import java.util.List;

import android.util.Log;

public class Song {

	private String artist,
				   albumArtist,
				   album,
				   title,
				   file;
				
				 
	private int id, track, totalTrack;
	private float time;
	
	public Song(List<String> list) {
		
		for (String data : list) {
			if (data.startsWith("AlbumArtist: "))
				albumArtist = data.substring(13);
			else if (data.startsWith("Artist: ")) 
				artist = data.substring(8);
			else if (data.startsWith("Title: "))
				title = data.substring(7);
			else if (data.startsWith("Album: "))
				album = data.substring(7);
			else if (data.startsWith("Track: ")) {
					String trackData = data.substring(7);
					if (trackData.contains("/")) 
						track = Integer.parseInt(trackData.split("/")[0]);
					else
						track = Integer.parseInt(trackData);
						
					
					
					//totalTrack = Integer.parseInt(trackData[1]);
				
			}
			else if (data.startsWith("Id: ")) 
				id = Integer.parseInt(data.substring(4));
			else if (data.startsWith("file: "))
				file = data.substring(6);
			else if (data.startsWith("Time: "))
				time = Float.parseFloat(data.substring(6));
		}
	}
	
	public String getArtist() {
		return artist;
	}
	
	public String getAlbumArtist() {
		return albumArtist;
	}
	
	public String getAlbum() {
		return album;
	}
	
	public String getTitle() {
		return title;
	}
	
	public int getTrack() {
		return track;
	}
	
	public String getFile() {
		return file;
	}
	
	public int getId() {
		return id;
	}
	
	public float getTime() {
		return time;
	}
	
	public String toString() {
		return 
				// "(" + track + "/" + totalTrack + ") " + 
			   title + "\n" + 
			   artist + " - " + album + "\n" +
			   "ID:" + id;
	}
	
	/*
	 * Returns time in human format
	 * Ex: 1:30
	 */
	public static String getHumanTime(float time) {
		String data;
		int min = (int) (time/60);
		int sec = (int) (time%60);
		
		if (sec < 10) {
			data = min + ":0" + sec;
		} else {
			data = min + ":" + sec;
		}
		
		return data;
	}
	
	public String getHumanTime() {
		String data;
		int min = (int) (time/60);
		int sec = (int) (time%60);
		
		if (sec < 10) {
			data = min + ":0" + sec;
		} else {
			data = min + ":" + sec;
		}
		
		return data;
	}
	
}
