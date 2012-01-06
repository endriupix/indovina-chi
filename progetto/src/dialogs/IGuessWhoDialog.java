package dialogs;

import project.wirelessnetwork.R;
import android.app.Dialog;
import android.content.Context;
import android.widget.ImageView;
import android.widget.TextView;

public class IGuessWhoDialog extends Dialog {

	public IGuessWhoDialog(Context context) {
		super(context);
		setContentView(R.layout.dialog_i_guess_who);
		
	}
	
	public void setDialogInformations(int faceID, boolean winner, String supposed, String truth) {
		
		ImageView image = (ImageView) findViewById(R.id.image_end_game);
		image.setImageResource(faceID);
		
		TextView textWhoIs = (TextView)findViewById(R.id.text_who_is_final);
		TextView textWhoSay = (TextView)findViewById(R.id.text_supposed);
		
		if (winner) {
			setTitle("HAI VINTO !!");
			textWhoIs.setText(new String("Il personaggio nascosto e\': ".concat(supposed)));
			textWhoSay.setText("CONGRATULAZIONI !! ");
		}
		else {
			setTitle("HAI PERSO !!");
			textWhoIs.setText(new String("Il personaggio nascosto era: ").concat(truth));
			textWhoSay.setText(new String("Avevi detto: ".concat(supposed)));
		}
		
	}

}
