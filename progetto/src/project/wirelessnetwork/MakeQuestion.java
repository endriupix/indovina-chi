package project.wirelessnetwork;

import android.app.Activity;
import android.os.Bundle;

public class MakeQuestion extends Activity {

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_question);
	
		String[] categories = this.getResources().getStringArray(R.array.options_categories);
		
		
	}
	
	
}
