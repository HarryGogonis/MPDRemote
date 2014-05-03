package com.hgogonis.mpdremote;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.PopupWindow;
import android.widget.ProgressBar;

public class LoadingPopup extends Activity {
	
	
	protected void onCreate(Context context) {

		PopupWindow window;
		LayoutInflater  inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.loading_layout, null, false);
			
		ProgressBar progress_bar = (ProgressBar) ((Activity) context).findViewById(R.id.loading_progress);
			
			window = new PopupWindow(view);
			//pw.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.my_bg));
			window.setOutsideTouchable(true);
			window.showAtLocation(((Activity) context).findViewById(R.id.wrapper), Gravity.TOP,
			0,0);
			
		
			progress_bar.setIndeterminate(true);
		
		}
}
