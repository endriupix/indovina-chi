package dialogs;

import project.wirelessnetwork.R;
import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class ShowAnswerDialog extends Dialog {

	public ShowAnswerDialog(Context context) {
		super(context);
		setContentView(R.layout.dialog_show_answer);
		setTitle("Hai ricevuto una risposta ..");
		
		Button btnClose = (Button)findViewById(R.id.btn_close_show_answer);
		btnClose.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				dismiss();
			}
		});
	}
	
	public void setQuestion(String question) {
		question = new String("Tua Domanda: ").concat(question);
		TextView viewQuestion = (TextView)findViewById(R.id.text_last_question);
		viewQuestion.setText(question);
	}
	
	public void setAnswer(String answer) {
		
		answer = new String("Risposta: ").concat(answer);
		TextView viewAnswer = (TextView)findViewById(R.id.text_show_answer);
		viewAnswer.setText(answer);
	}

}
