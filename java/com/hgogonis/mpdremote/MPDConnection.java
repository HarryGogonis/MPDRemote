package com.hgogonis.mpdremote;

import java.net.Socket;
import java.util.ArrayList;
import java.io.*;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;

class MPDConnection extends AsyncTask<String, Void, ArrayList<String> >{
	
	private Context context;	
	private final String HOST_NAME;
	private final int PORT;
	
	public MPDConnection(Context context)  {
		
		// Get preferences
		SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
		HOST_NAME = sharedPref.getString("host", "");
		PORT = Integer.parseInt(sharedPref.getString("port", "6600"));
				
		this.context = context;
	}
	
	
	public Context getContext() {
		return context;
	}
	
	public final String getHost() {
		return HOST_NAME;
	}
	
	public final int getPort() {
		return PORT;
	}
	
	
	/**
	 * Main thread of execution
	 */
	@Override
	protected ArrayList<String> doInBackground(String... args) {
		
		// Initialize variables
		ArrayList<String> data = new ArrayList<String>();
		Socket mpdSocket;
		String command = args[0];

		
		try {					
			// Connect to a MPD socket
			mpdSocket = new Socket(HOST_NAME, PORT);
			mpdSocket.setKeepAlive(false);
			
			// Debugging Message
			//Log.d("Connection ", "Attempting to send command \"" + command 
			//		+ "\" to host " + HOST_NAME + " on port " + PORT );
			
			
			// Write to socket
			PrintWriter out = new PrintWriter(mpdSocket.getOutputStream(), true);
			out.println(command + "\nclose");
	
				
			String response = new String();
			BufferedReader in = new BufferedReader(new InputStreamReader(mpdSocket.getInputStream()));
			
			// Read from the socket
			while ( (response=in.readLine()) != null) {
				if (isCancelled()) break;
				else if (response.startsWith("OK MPD")) {
					//Log.d("Connection", "Connected");
				 }
				else if (response.startsWith("OK")) {
					//Log.d("Connection", "Got OK response");
					return data;
				}
				else if (response.startsWith("ACK")) {
					data.add(response.substring(3));
					Log.e("Error Response from MPD:", response.substring(3));
				}
				else {
					//Log.d("Response from Server", response);
					data.add(response);
				}
			}
						
		//Disconnect Socket & IO
		mpdSocket.close();
		out.close();
		in.close();


		
		} catch (IOException e) {
			data.add(e.getMessage());
			Log.e("IO Exception", e.getMessage(), e);
		} catch (Exception e) {
			Log.e("Connection Exception", e.getMessage(), e);
		}
		
		return data;
	}	
	
	/**
	 * Removes loading popup
	 */
	@Override
	protected void onPostExecute(ArrayList<String> result) {
		
	}
}




