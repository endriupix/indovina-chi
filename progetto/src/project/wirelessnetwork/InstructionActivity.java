package project.wirelessnetwork;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebView;


public class InstructionActivity extends Activity {

	WebView mWebView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.instruction);


		mWebView = (WebView) findViewById(R.id.webview);    

		String text = "<html> <style type=\"text/css\">p {color:#ffffff;} </style>" 
				+ "<body bgcolor= #000000>"
				+ "<b> <p align=\"center\">"                
				+ getString(R.string.goal_game_title_string) 
				+ "</p> </b>"
				+ "<p align=\"justify\">"                
				+ getString(R.string.goal_game_text_string) 
				+ "</p> "
				+ "<b><p align=\"center\">"                
				+ getString(R.string.before_start_title_string) 
				+ "</p> </b>"
				+ "<p align=\"justify\">"                
				+ getString(R.string.before_start_text_string) 
				+ "</p> "
				+ "<b><p align=\"center\">"                
				+ getString(R.string.how_play_title_string) 
				+ "</p> </b>"
				+ "<p align=\"justify\">"                
				+ getString(R.string.how_play_text_string1) 
				+ "<br>"
				+ getString(R.string.how_play_text_string2)
				+ "<br>"
				+ getString(R.string.how_play_text_string3) 
				+ "<br>"
				+ getString(R.string.how_play_text_string4) 
				+ "<br>"
				+ getString(R.string.how_play_text_string5) 
				+ "<br>"
				+ "<br>"
				+ getString(R.string.how_play_text_string6) 
				+ "<br>"
				+ "<br>"
				+ getString(R.string.how_play_text_string7) 
				+ "</p> "
				+ "<b><p align=\"center\">"                
				+ getString(R.string.winner_title_string) 
				+ "</p> </b>"
				+ "<p align=\"justify\">"                
				+ getString(R.string.winner_text_string) 
				+ "</p> "
				+ "</body></html>";

		mWebView.loadData(text, "text/html", "utf-8");
	}
}

