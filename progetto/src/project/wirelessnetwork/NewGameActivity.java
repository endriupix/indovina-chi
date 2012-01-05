package project.wirelessnetwork;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import dialogs.ShowAnswerDialog;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.TableRow.LayoutParams;
import android.widget.Toast;

public class NewGameActivity extends Activity {
	
	private String TAG = "GAME_MANAGER";
	public static final String COMPOSED_QUESTION = "COMPOSED_QUESTION";
	public static final String IMAGE_OWN_FACE = "OWN_FACE";
	public static final int CODE_COMPOSE_QUESTION = 1;
	public static final int CODE_CHOOSE_FACE = 2;
	public static final int GIVE_QUESTION_ANSWER = 3;
	
	public static final int MESSAGE_READ = 2;
	
	public static final int STATE_MAKE_QUESTION = 1;
	public static final int STATE_WAITING_ANSWER = 2;
	public static final int STATE_WAITING_QUESTION = 3;
	
	private int state = 0;
	
	private Integer[] facesID = {R.drawable.chi_alex, R.drawable.chi_alfred, 
			R.drawable.chi_anita, R.drawable.chi_anne, R.drawable.chi_bernard, 
			R.drawable.chi_bill, R.drawable.chi_charles, R.drawable.chi_claire,
			R.drawable.chi_david, R.drawable.chi_eric, R.drawable.chi_frans,
			R.drawable.chi_george, R.drawable.chi_herman, R.drawable.chi_joe,
			R.drawable.chi_maria, R.drawable.chi_max, R.drawable.chi_paul, 
			R.drawable.chi_peter, R.drawable.chi_philip, R.drawable.chi_richard, 
			R.drawable.chi_robert, R.drawable.chi_sam, R.drawable.chi_susan, R.drawable.chi_tom};
	
	private String names[];
	private FaceConteiner[] faces;
	private int ownFaceID; 
	private int indexOwnFace;
	private BluetoothConnectionManager connectionManager;
	private String lastQuestionMade = "Porta il cappello?";
	private String lastQuestionReceived = "ha i capelli arancioni?";
	private String lastAnswerReceived = "Si";
	
	protected void setState(int newState) {
		state = newState;
	}
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
		Log.d(TAG, "++ onCreate ++");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_game);
        
        connectionManager = ConnectDevice.connectionManager;
        
        names = this.getResources().getStringArray(R.array.images_name);
		faces = new FaceConteiner[facesID.length];
		
		int facesPerRow = 4;
		int totalRows = facesID.length / facesPerRow;
		if ((facesID.length % facesPerRow) != 0) {
			totalRows++;
		}
		
		for (int i = 0; i < totalRows; i++) {
			
			TableRow row = new TableRow(this);
			for (int j = 0; j < facesPerRow; j++) {
				
				FrameLayout container = new FrameLayout(this);
				container.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
				container.setBackgroundResource(R.color.image_not_selected);
				container.setForegroundGravity(Gravity.CENTER);
				
				FaceConteiner imgView = new FaceConteiner(names[i*facesPerRow + j], this);
				faces[i*facesPerRow + j] = imgView;
				imgView.setImageResource(facesID[i* facesPerRow + j]);
				imgView.setPadding(10, 10, 10, 10);
				imgView.setLayoutParams(new LayoutParams(75, 100, 5));
				imgView.setClickable(true);
				imgView.setOnClickListener(new OnClickListener() {
					
					public void onClick(View v) {
						FaceConteiner face = (FaceConteiner) v;
						if (face.isChoosable()) {
							face.toggle();
							/*View parent = (View) v.getParent();
							if (face.isChecked()) {
								parent.setBackgroundResource(R.color.image_selected);
							}
							else {
								parent.setBackgroundResource(R.color.image_not_selected);
							}*/
						}
					}
				});
				
				container.addView(imgView);
				row.addView(container);
			}
			TableLayout table = (TableLayout)findViewById(R.id.table_layout);
			table.addView(row, new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		}
		
		Button btnEscludi = (Button)findViewById(R.id.btn_escludi);
		btnEscludi.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				for (int i = 0; i < faces.length; i++) {
					if (faces[i].isChoosable() && faces[i].isChecked()) {
						faces[i].noMoreChoosable();
						faces[i].setImageResource(R.drawable.indovina_chi);
					}
				}
				
			}
		});
		
		Button btnMakeQuestion = (Button) findViewById(R.id.btn_make_question);
		btnMakeQuestion.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				
				Intent intent = new Intent(NewGameActivity.this, MakeQuestionActivity.class);
				startActivityForResult(intent, CODE_COMPOSE_QUESTION);
			}
		});
		
		Button btnGuessWho = (Button)findViewById(R.id.btn_who_is);
		btnGuessWho.setOnClickListener(new GuessWhoListener());
		
		Button btnReadAnswer = (Button)findViewById(R.id.btn_read_answer);
		//btnReadAnswer.setEnabled(false);
		btnReadAnswer.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				
				final ShowAnswerDialog answerDialog = new ShowAnswerDialog(NewGameActivity.this);
				answerDialog.setQuestion(lastQuestionMade);
				answerDialog.setAnswer(lastAnswerReceived);
				
				answerDialog.show();
				
			}
		});
		
		Button btnReadQuestion = (Button)findViewById(R.id.btn_read_question);
		//btnReadQuestion.setEnabled(false);
		btnReadQuestion.setOnClickListener(new View.OnClickListener() {
			
			/**
			 * Si occupa di mostrare la domanda che viene posta dall'avversario
			 */
			public void onClick(View v) {
				
				final Dialog questionDialog = new Dialog(NewGameActivity.this);
				questionDialog.setContentView(R.layout.dialog_answer_question);
				questionDialog.setTitle("Una domanda per te..");
				
				Button btnYes = (Button)questionDialog.findViewById(R.id.btn_answer_yes);
				Button btnNo = (Button)questionDialog.findViewById(R.id.btn_answer_no);
				
				btnYes.setOnClickListener(new OnClickListener() {
					
					public void onClick(View v) {
						
						String answer = "Si";
						Log.d("Answer Question", answer);
						
						if (connectionManager != null) {
							connectionManager.write(answer.getBytes());
						}
						questionDialog.dismiss();
					}
				});
				btnNo.setOnClickListener(new OnClickListener() {
					
					public void onClick(View v) {
						
						String answer = "No";
						Log.d("Answer Question", answer);
						
						if (connectionManager != null) {
							connectionManager.write(answer.getBytes());
						}
						questionDialog.dismiss();
					}
				});
				
				TextView text = (TextView)questionDialog.findViewById(R.id.text_to_give_answer);
				text.setText(lastQuestionReceived);
				
				ImageView image = (ImageView)questionDialog.findViewById(R.id.image_to_give_answer);
				image.setImageResource(ownFaceID);
				
				questionDialog.show();
				
			}
		});
		
		if (connectionManager != null) {
			connectionManager.setHandlerRead(handlerRead);
		}
        
		/**
		 * Avvio activity per scelta del personaggio associato al giocatore 
		 */
		Intent intent = new Intent(this, ChoiceFaceActivity.class);
		startActivityForResult(intent, CODE_CHOOSE_FACE);
    }
	
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.d(TAG, "OnActivityResult");
		
		switch(requestCode) {
		
		/**
		 * Operazione composizione domanda conclusa correttamente, ho domanda pronta da inviare
		 */
		case CODE_COMPOSE_QUESTION:
			
			if (resultCode == Activity.RESULT_OK) {
				
				Button btnMakeQuestion = (Button) findViewById(R.id.btn_make_question);
				btnMakeQuestion.setEnabled(false);
				Button btnWhoIs = (Button)findViewById(R.id.btn_who_is);
				btnWhoIs.setEnabled(false);
				lastQuestionMade = data.getExtras().getString(COMPOSED_QUESTION);
				Log.d(TAG, lastQuestionMade);
				
				if (connectionManager != null) {
					Log.d(TAG, "Write on ConnectionManager");
					connectionManager.write(lastQuestionMade.getBytes());
				}
				
				Button btnReadAnswer = (Button)findViewById(R.id.btn_read_answer);
				btnReadAnswer.setEnabled(true);
			}
			
			break;
			
		/**
		 * Assegnato personaggio all'utente, recupero ID e cerco sua posizione
		 * all'interno dell'array delle facce
		 */
		case CODE_CHOOSE_FACE:
			
			if (resultCode == Activity.RESULT_OK) {
				ownFaceID = data.getExtras().getInt(IMAGE_OWN_FACE);
				
				indexOwnFace = Arrays.binarySearch(facesID, ownFaceID);
				
			}
			else {
				Toast.makeText(this, "Non hai scelto il personaggio", Toast.LENGTH_SHORT).show();
				finish();
			}
			break;
			
		}
	}
	
	/**
	 * Handler che si occupa di ricevere i messaggi durante il gioco da parte 
	 * dell'avversario
	 */
	private Handler handlerRead = new Handler() {
		
		public void handleMessage(Message msg) {
			
			switch(msg.what) {
			
			case MESSAGE_READ:
				byte[] readBuff = (byte[]) msg.obj;
				if (state == STATE_WAITING_ANSWER) {
					// messaggio ottenuto è la risposta alla domanda
					
					lastAnswerReceived = new String(readBuff, 0, msg.arg1);
					
					Button btnReadAnswer = (Button)findViewById(R.id.btn_read_answer);
					btnReadAnswer.setEnabled(true);
					btnReadAnswer.setBackgroundResource(R.drawable.button_shape);
					
					setState(STATE_WAITING_QUESTION);
					
				}
				if (state == STATE_WAITING_QUESTION) {
					//messaggio ricevuto è la domanda posta dall'utente
					String question = new String(readBuff, 0, msg.arg1);
					lastQuestionReceived = question;
					
					Log.d(TAG, "Domada ricevuta: ".concat(question));
					
					// Attivo bottone per permettere al giocatore di rispondere
					Button btnQuestion = (Button)findViewById(R.id.btn_read_question);
					btnQuestion.setEnabled(true);
					btnQuestion.setBackgroundResource(R.drawable.button_shape);
					
				}
				break;
			}
		}
	};
	
	private class GuessWhoListener implements View.OnClickListener {

		public void onClick(View v) {
			
			
			
			/*Dialog guessWhoDialog = new GuessWhoDialog(NewGameActivity.this, validNames);
			guessWhoDialog.setTitle("Chi e\' ??");
			
			guessWhoDialog.show();*/
			
			List<String> validNames = new ArrayList<String>();
			
			for (int i = 0; i < faces.length; i++) {
				
				if (faces[i].isChoosable()) {
					validNames.add(faces[i].getFaceName());
				}
			}
			
			final String[] names = new String[validNames.size()];
			
			for (int i = 0; i < validNames.size(); i++) {
				names[i] = validNames.get(i);
			}
			
			AlertDialog.Builder builder = new AlertDialog.Builder(NewGameActivity.this);
			builder.setTitle("Chi e' ??");
			builder.setSingleChoiceItems(names, -1, new DialogInterface.OnClickListener() {
			    public void onClick(DialogInterface dialog, int item) {
			        
			    	Log.d(TAG, "Supposto: ".concat(names[item]));
			    	
			    	dialog.dismiss();
			    }
			});
			AlertDialog alert = builder.create();
			alert.show();
			
		}
		
	}
}
