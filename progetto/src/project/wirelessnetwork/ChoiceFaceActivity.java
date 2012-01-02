package project.wirelessnetwork;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;


public class ChoiceFaceActivity extends Activity {
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.choice_face);
        
        Button btnChoiceFace = (Button)findViewById(R.id.choice_face_button);
        btnChoiceFace.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				// avvia conteggio random e visualizza immagine corrispondente al numero generato
			}
		});
        
        
        Button btnStart = (Button)findViewById(R.id.start_button);
        btnStart.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				Intent actNewGame = new Intent(v.getContext(), NewGameActivity.class);
				v.getContext().startActivity(actNewGame);
			}
		});
    }
}

