package project.wirelessnetwork;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebView;
import android.widget.LinearLayout;


public class InstructionActivity extends Activity {

	WebView mWebView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.instruction);


		mWebView = (WebView) findViewById(R.id.webview);    

		String text = "<html> <style type=\"text/css\"> p {color: #" + getString(R.color.background) + ";} </style>" 
				+ "<body bgcolor=\"" + getString(R.color.textColor) +"\">"
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

