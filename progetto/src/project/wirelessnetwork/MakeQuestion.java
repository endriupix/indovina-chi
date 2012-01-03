package project.wirelessnetwork;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

public class MakeQuestion extends Activity implements OnItemSelectedListener {

	private boolean firstTime = true;
	private String categories[];
	private String secondOptions[];
	private String firstStepString;
	private String secondStepString;
	private Spinner firstSpinner;
	private Spinner secondSpinner;
	private View textSecondSpinner;
	private Button btnMakeQuestion;
	private Resources resources;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_question);
		resources = this.getResources();
		
		String[] categories = resources.getStringArray(R.array.options_categories);
		textSecondSpinner = (View)findViewById(R.id.txt_choose_question);
		secondSpinner = (Spinner)findViewById(R.id.spinner_choose_question);
		
		firstSpinner = (Spinner) findViewById(R.id.spinner_category);
		firstSpinner.setOnItemSelectedListener(this);
		
		ArrayAdapter<String> aa = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, categories);
		aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		
		firstSpinner.setAdapter(aa);
		
		btnMakeQuestion = (Button)findViewById(R.id.btn_make_question);
		btnMakeQuestion.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				
				secondStepString = secondOptions[secondSpinner.getSelectedItemPosition()];
				
				QuestionComposer composer = new QuestionComposer(firstStepString, secondStepString);
				String finalQuestion = composer.createQuestion();
				
				Intent intent = new Intent();
				intent.putExtra(NewGameActivity.COMPOSED_QUESTION, finalQuestion);
				setResult(Activity.RESULT_OK, intent);
				
				finish();
			}
		});
		
	}

	public void onItemSelected(AdapterView<?> parent, View view, int position,
			long id) {
		
		if (firstTime) { firstTime = false; return; }
		
		firstStepString = categories[position];
		secondSpinner.setVisibility(0);
		textSecondSpinner.setVisibility(0);
		btnMakeQuestion.setVisibility(0);
		
		if (firstStepString.equals(resources.getString(R.string.no_one_choosed))) {
			secondSpinner.setVisibility(1);
			textSecondSpinner.setVisibility(1);
			btnMakeQuestion.setVisibility(1);
			return;
		}
		if (firstStepString.equals(resources.getString(R.string.category_sex))) {
			secondOptions = resources.getStringArray(R.array.category_sex);
			return;
		}
		if (firstStepString.equals(resources.getString(R.string.category_accessories))) {
			secondOptions = resources.getStringArray(R.array.category_accessories);
			return;
		}
		if (firstStepString.equals(resources.getString(R.string.category_beard))) {
			secondOptions = resources.getStringArray(R.array.category_beard);
			return;
		}
		if (firstStepString.equals(resources.getString(R.string.category_hairs))) {
			secondOptions = resources.getStringArray(R.array.category_hairs);
			return;
		}
		if (firstStepString.equals(resources.getString(R.string.category_eyes))) {
			secondOptions = resources.getStringArray(R.array.category_eyes);
			return;
		}
		if (firstStepString.equals(resources.getString(R.string.category_moustache))) {
			secondOptions = resources.getStringArray(R.array.category_moustache);
			return;
		}
		if (firstStepString.equals(resources.getString(R.string.category_particular_signs))) {
			secondOptions = resources.getStringArray(R.array.category_particular_signs);
			return;
		}
		
		ArrayAdapter<String> aa = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, secondOptions);
		aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		secondSpinner.setAdapter(aa);
		
	}

	public void onNothingSelected(AdapterView<?> arg0) {
		
		secondSpinner.setVisibility(1);
		textSecondSpinner.setVisibility(1);
		btnMakeQuestion.setVisibility(1);
		
	}
	
	private class QuestionComposer {
		private String firstString;
		private String secondString;
		
		public QuestionComposer(String first, String second) {
			firstString = first; secondString = second;
		}
		
		public String createQuestion() {
			
			if (firstString.equals(resources.getString(R.string.category_hairs)) ||
					firstString.equals(resources.getString(R.string.category_eyes)) || 
					firstString.equals(resources.getString(R.string.category_moustache)) ||
					firstString.equals(resources.getString(R.string.category_beard)) ) {
				
			}
			return "";
		}
	}
	
	
}
