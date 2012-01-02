package project.wirelessnetwork;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class IndovinaChiActivity extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        Button btnNewGame = (Button)findViewById(R.id.newGame_button);
        btnNewGame.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				Intent actNewGame = new Intent(v.getContext(), ConnectDevice.class);
				v.getContext().startActivity(actNewGame);
			}
		});
        
        Button btnInstruction = (Button)findViewById(R.id.instruction_button);
        btnInstruction.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				Intent actInstruction = new Intent(v.getContext(), InstructionActivity.class);
				v.getContext().startActivity(actInstruction);
			}
		});
        
        Button btnStory = (Button)findViewById(R.id.story_button);
        btnStory.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				Intent actStory = new Intent(v.getContext(), StoryActivity.class);
				v.getContext().startActivity(actStory);
			}
		});
        
        Button btnExit = (Button)findViewById(R.id.exit_button);
        btnExit.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				System.exit(0);	
			}
		});
    }
}