package project.wirelessnetwork;
import java.util.Random;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;


public class ChoiceFaceActivity extends Activity {
	
	//aggiungere campo per id dell'immagine scelta che userò
	//nell'activity newgameactivity
	private String TAG = "ChoiceFaceActivity";
	private int imageID;
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.choice_face);
        
        final Button btnChoiceFace = (Button)findViewById(R.id.choice_face_button);
        btnChoiceFace.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				Log.d(TAG, "onClick btnChoiceFace");
				// avvia conteggio random e visualizza immagine corrispondente al numero generato
				
				int faces = NewGameActivity.getTotalFaces();
				
				Random generator = new Random();
				int faceIndex = generator.nextInt(faces);
				
				String faceName = NewGameActivity.getFaceName(faceIndex);
				int imageResource = NewGameActivity.getFaceID(faceIndex);
				imageID = imageResource;
				
				ImageView image = (ImageView)findViewById(R.id.image_presentation_face);
				image.setImageResource(imageResource);
				
				TextView text = (TextView)findViewById(R.id.name_presentation_face);
				text.setText(new String("Il tuo personaggio è: ").concat(faceName));
				text.setVisibility(View.VISIBLE);
				
				btnChoiceFace.setVisibility(View.GONE);
				Button btnStart = (Button)findViewById(R.id.start_button);
				btnStart.setVisibility(View.VISIBLE);
			}
		});
        
        
        Button btnStart = (Button)findViewById(R.id.start_button);
        btnStart.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.putExtra(NewGameActivity.IMAGE_OWN_FACE, imageID);
				setResult(Activity.RESULT_OK, intent);
				finish();
			}
		});
    }
}

