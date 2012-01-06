package project.wirelessnetwork;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import dialogs.IGuessWhoDialog;
import dialogs.OtherPlayerGuessWhoDialog;
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
	
	public static boolean starter = false;
	
	public static final String COMPOSED_QUESTION = "COMPOSED_QUESTION";
	public static final String IMAGE_OWN_FACE = "OWN_FACE";
	public static final int CODE_COMPOSE_QUESTION = 1;
	public static final int CODE_CHOOSE_FACE = 2;
	public static final int GIVE_QUESTION_ANSWER = 3;
	
	public static final int MESSAGE_READ = 2;
	public static final int SUPPOSED_FACE = 3;
	
	public static final int STATE_MAKE_QUESTION = 1;
	public static final int STATE_WAITING_ANSWER = 2;
	public static final int STATE_WAITING_QUESTION = 3;
	public static final int STATE_WAITING_CONCLUSION = 4;
	
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
	private String faceSupposed = "Alfred";
	private static String tagIndovino = "Indovino:";
	
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
						}
					}
				});
				
				container.addView(imgView);
				row.addView(container);
			}
			TableLayout table = (TableLayout)findViewById(R.id.table_faces_layout);
			table.addView(row, new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		}
		
		/**
		 * Bottone escludi che esclude (nasconde e rende non cliccabili)
		 * le immagini delle facce selezionate dal giocatore
		 */
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
		/**
		 * Se il giocatore non è quello che comincia, disattivo bottone per porre la domanda
		 */
		if (!starter) {
			btnMakeQuestion.setEnabled(false);
		}
		
		/**
		 * Bottone Chi e'? per ipotizzare chi è personaggio nascosto
		 */
		Button btnGuessWho = (Button)findViewById(R.id.btn_who_is);
		btnGuessWho.setOnClickListener(new GuessWhoListener());
		/**
		 * Se il giocatore non è quello che comincia, disattivo bottone per 
		 * ipotizzare chi è il personaggio
		 */
		if (!starter) {
			btnGuessWho.setEnabled(false);
		}
		
		/**
		 * Bottone "Leggi risposta" si occupa di visualizzare risposta 
		 * ricevuta
		 */
		Button btnReadAnswer = (Button)findViewById(R.id.btn_read_answer);
		//btnReadAnswer.setEnabled(false);
		btnReadAnswer.setOnClickListener(new View.OnClickListener() {
			
			/**
			 * Mostra il Dialog corretto con la risposta ricevuta
			 */
			public void onClick(View v) {
				
				final ShowAnswerDialog answerDialog = new ShowAnswerDialog(NewGameActivity.this);
				answerDialog.setQuestion(lastQuestionMade);
				answerDialog.setAnswer(lastAnswerReceived);
				
				answerDialog.show();
				
			}
		});
		
		Button btnReadQuestion = (Button)findViewById(R.id.btn_read_question);
		//btnReadQuestion.setEnabled(false);
		btnReadQuestion.setOnClickListener(new ShowQuestionListener());
		
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
				
				/**
				 * Disattivo i bottoni per porre una domanda o ipotizzare chi è il personaggio
				 * nascosto
				 */
				Button btnMakeQuestion = (Button) findViewById(R.id.btn_make_question);
				btnMakeQuestion.setEnabled(false);
				Button btnWhoIs = (Button)findViewById(R.id.btn_who_is);
				btnWhoIs.setEnabled(false);
				/**
				 * Recupero la domanda posta
				 */
				lastQuestionMade = data.getExtras().getString(COMPOSED_QUESTION);
				Log.d(TAG, lastQuestionMade);
				
				if (connectionManager != null) {
					Log.d(TAG, "Write on ConnectionManager");
					connectionManager.write(lastQuestionMade.getBytes());
				}
				
				/**
				 * Attivo bottone per leggere la risposta che verra' ricevuta
				 */
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
		
		private String TAG = "Handler";
		
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
					
					
					if (question.contains(tagIndovino)) {
						//Giocatore avversario sta provando ad indovinare chi è personaggio nascosto
						String supposedName = question.replace(tagIndovino, "");
						
						if (supposedName.equals(faces[indexOwnFace].getFaceName())) {
							//Avversario ha indovinato personaggio, giocatore ha perso
							OtherPlayerGuessWhoDialog dialog = new OtherPlayerGuessWhoDialog(NewGameActivity.this);
							dialog.setDialogInformations(ownFaceID, false, "", "");
							dialog.show();
						}
						else {
							//Avversario ha sbagliato, giocatore vince
							OtherPlayerGuessWhoDialog dialog = new OtherPlayerGuessWhoDialog(NewGameActivity.this);
							dialog.setDialogInformations(ownFaceID, true, supposedName, faces[indexOwnFace].getFaceName());
							dialog.show();
						}
						
						closeGame();
					}
					else {
						// Attivo bottone per permettere al giocatore di rispondere
						Button btnQuestion = (Button)findViewById(R.id.btn_read_question);
						btnQuestion.setEnabled(true);
						btnQuestion.setBackgroundResource(R.drawable.button_shape);
					}
					
				}
				
				if (state == STATE_WAITING_CONCLUSION) {
					Log.d(TAG, "Risultato Ipotesi Faccia");
					//messaggio ricevuto è esito della supposizione = "Si" o "No"
					String result = new String(readBuff, 0, msg.arg1);
					
					if (result.equals("Si")) {
						Log.d(TAG, "Indovinato");
						//Supposizione corretta, giocatore vince
						//Devo recuperare ID immagine da visualizzare
						int facePosition = -1; boolean end = false;
						for (int i = 0; i < faces.length && !end; i++) {
							if (faceSupposed.equals(faces[i].getFaceName())) {
								end = true;
								facePosition = i;
							}
						}
						IGuessWhoDialog dialog = new IGuessWhoDialog(NewGameActivity.this);
						dialog.setDialogInformations(facesID[facePosition], true, faceSupposed, "");
						dialog.show();
					}
					else {
						//Supposizione errata, giocatore perde
						//result contiene nome personaggio corretto
						Log.d(TAG, "Non Indovinato");
						int facePosition = -1; boolean end = false;
						for (int i = 0; i < faces.length && !end; i++) {
							if (result.equals(faces[i].getFaceName())) {
								end = true;
								facePosition = i;
							}
						}
						IGuessWhoDialog dialog = new IGuessWhoDialog(NewGameActivity.this);
						dialog.setDialogInformations(facesID[facePosition], false, faceSupposed, result);
						dialog.show();
					}
					
					closeGame();
				}
				break;
				
			case SUPPOSED_FACE:
				
				/**
				 * Giocatore ha scelto faccia che crede nascosta
				 */
				String name = (String)msg.obj;
				Log.d("Handler", new String("Faccia Supposta: ").concat(name));
				setState(STATE_WAITING_CONCLUSION);
				
				faceSupposed = name;
				
				if (connectionManager != null) {
					String stringToSend = tagIndovino.concat(faceSupposed);
					connectionManager.write(stringToSend.getBytes());
				}
				
				Log.d(TAG, "Supposizione Faccia Completa");
			}
		}
	};
	
	/**
	 * Si occupa di concludere la partita, ovvero disattivare tutti i bottoni
	 * e rendere le immagini non cliccabili
	 */
	private void closeGame() {
		
		Button btn = (Button)findViewById(R.id.btn_escludi);
		btn.setEnabled(false);
		btn = (Button)findViewById(R.id.btn_make_question);
		btn.setEnabled(false);
		btn = (Button)findViewById(R.id.btn_who_is);
		btn.setEnabled(false);
		btn = (Button)findViewById(R.id.btn_read_answer);
		btn.setEnabled(false);
		btn = (Button)findViewById(R.id.btn_read_question);
		btn.setEnabled(false);
		
		for (int i = 0; i < faces.length; i++) {
			if (faces[i].isChoosable()) {
				faces[i].noMoreChoosable();
			}
		}
		
	}
	
	/**
	 * Classe che si occupa di mostrare la domanda posta dall'altro 
	 * giocatore, e di gestire anche la risposta fornita mandandola
	 * poi all'avversario. 
	 */
	private class ShowQuestionListener implements View.OnClickListener {
		
		/**
		 * Si occupa di gestire la risposta data alla domanda attraverso 
		 * i due bottoni Si e No
		 *
		 */
		private class AnswerListener implements View.OnClickListener {
			
			private boolean answer = false;
			private Dialog dialog;
			
			public AnswerListener(boolean answer, Dialog dialog) {
				this.answer = answer; this.dialog = dialog;
			}

			public void onClick(View v) {
				String answerString = "Si";
				if (!answer) {
					answerString = "No";
				}
				
				Log.d("Answer Question", answerString);
				
				if (connectionManager != null) {
					connectionManager.write(answerString.getBytes());
				}
				
				Button btnReadQuestion = (Button)findViewById(R.id.btn_read_question);
				btnReadQuestion.setEnabled(false);
				
				dialog.dismiss();
				
				setState(STATE_MAKE_QUESTION);
				
			}
		}
		
		public void onClick(View v) {
			
			final Dialog questionDialog = new Dialog(NewGameActivity.this);
			questionDialog.setContentView(R.layout.dialog_answer_question);
			questionDialog.setTitle("Una domanda per te..");
			
			Button btnYes = (Button)questionDialog.findViewById(R.id.btn_answer_yes);
			Button btnNo = (Button)questionDialog.findViewById(R.id.btn_answer_no);
			
			btnYes.setOnClickListener(new AnswerListener(true, questionDialog));
			btnNo.setOnClickListener(new AnswerListener(false, questionDialog));
			
			TextView text = (TextView)questionDialog.findViewById(R.id.text_to_give_answer);
			text.setText(lastQuestionReceived);
			
			ImageView image = (ImageView)questionDialog.findViewById(R.id.image_to_give_answer);
			image.setImageResource(ownFaceID);
			
			questionDialog.show();
			
		}
	}
	
	private class GuessWhoListener implements View.OnClickListener {

		public void onClick(View v) {
			
			/**
			 * Recupero l'elenco delle nomi dei personaggi che sono ancora in gioco
			 * ovvero, quelli che non sono stati scartati dal giocatore. 
			 */
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
			    	handlerRead.obtainMessage(SUPPOSED_FACE, names[item]).sendToTarget();
			    	
			    	dialog.dismiss();
			    }
			});
			AlertDialog alert = builder.create();
			alert.show();
		}
		
	}
}
