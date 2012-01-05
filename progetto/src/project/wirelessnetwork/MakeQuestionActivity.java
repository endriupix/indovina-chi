package project.wirelessnetwork;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

public class MakeQuestionActivity extends Activity implements OnItemSelectedListener {

	private boolean firstTime = true;
	private String categories[];
	private String questionStringCategories[];
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
		
		categories = resources.getStringArray(R.array.options_categories);
		questionStringCategories = resources.getStringArray(R.array.question_strings_category);
		
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
				
				String finalQuestion = new QuestionComposer().createQuestion();
				
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
		secondSpinner.setVisibility(View.VISIBLE);
		textSecondSpinner.setVisibility(View.VISIBLE);
		btnMakeQuestion.setVisibility(View.VISIBLE);
		
		if (firstStepString.equals(resources.getString(R.string.no_one_choosed))) {
			secondSpinner.setVisibility(View.INVISIBLE);
			textSecondSpinner.setVisibility(View.INVISIBLE);
			btnMakeQuestion.setVisibility(View.INVISIBLE);
		}
		if (firstStepString.equals(resources.getString(R.string.category_sex))) {
			secondOptions = resources.getStringArray(R.array.category_sex);
		}
		if (firstStepString.equals(resources.getString(R.string.category_accessories))) {
			secondOptions = resources.getStringArray(R.array.category_accessories);
		}
		if (firstStepString.equals(resources.getString(R.string.category_beard))) {
			secondOptions = resources.getStringArray(R.array.category_beard);
		}
		if (firstStepString.equals(resources.getString(R.string.category_hairs))) {
			secondOptions = resources.getStringArray(R.array.category_hairs);
		}
		if (firstStepString.equals(resources.getString(R.string.category_eyes))) {
			secondOptions = resources.getStringArray(R.array.category_eyes);
		}
		if (firstStepString.equals(resources.getString(R.string.category_moustache))) {
			secondOptions = resources.getStringArray(R.array.category_moustache);
		}
		if (firstStepString.equals(resources.getString(R.string.category_particular_signs))) {
			secondOptions = resources.getStringArray(R.array.category_particular_signs);
		}
		
		ArrayAdapter<String> aa = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, secondOptions);
		aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		secondSpinner.setAdapter(aa);
		
	}

	public void onNothingSelected(AdapterView<?> arg0) {
		
		secondSpinner.setVisibility(View.INVISIBLE);
		textSecondSpinner.setVisibility(View.INVISIBLE);
		btnMakeQuestion.setVisibility(View.INVISIBLE);
		
	}
	
	private class QuestionComposer {
		
		public String createQuestion() {
			
			String question = "";
			
			if (firstStepString.equals(resources.getString(R.string.category_hairs)) ||
					firstStepString.equals(resources.getString(R.string.category_eyes)) || 
					firstStepString.equals(resources.getString(R.string.category_moustache)) ||
					firstStepString.equals(resources.getString(R.string.category_beard)) ) {
				
				int firstIndex = firstSpinner.getSelectedItemPosition();
				
				/**
				 * Inizializzo la domanda, ovvero la prima parte (Ha)
				 */
				question = resources.getString(R.string.first_part_have).concat(" ");
				
				/**
				 * Seconda parte della domanda, prende il soggetto (ovvero la categoria)
				 * 
				 */
				question = question.concat(questionStringCategories[firstIndex - 1]);
				
				/* 
				 * La domanda posta è ad esempio Ha i capelli arancioni?, quindi
				 * aggiungo la seconda parte (arancioni) alla domanda
				 */
				if (!secondStepString.equals(resources.getString(R.string.yes_no_question))) {
					question = question.concat(" ").concat(secondStepString.toLowerCase());
				}
			}
			
			if (firstStepString.equals(resources.getString(R.string.category_sex))) {
				question = questionStringCategories[0].concat(" ");
				
				if (secondStepString.equals(resources.getString(R.string.sex_male))) {
					question = question.concat(resources.getString(R.string.question_string_male));
				}
				else {
					question = question.concat(resources.getString(R.string.question_string_female));
				}
			}
			
			if (firstStepString.equals(resources.getString(R.string.category_accessories))) {
				
				question = resources.getString(R.string.question_string_accessories).concat(" ");
				String secondOptionsStrings[] = resources.getStringArray(R.array.question_string_accessories);
				
				question = question.concat(secondOptionsStrings[secondSpinner.getSelectedItemPosition()]);
			}
			
			if (firstStepString.equals(resources.getString(R.string.category_particular_signs))) {
				
				question = resources.getString(R.string.question_string_particular_signs).concat(" ");
				
				String secondOptionsStrings[] = resources.getStringArray(R.array.question_string_particular_signs);
				question = question.concat(secondOptionsStrings[secondSpinner.getSelectedItemPosition()]);
			}
			
			return question.concat("?");
		}
	}
	
	
}
