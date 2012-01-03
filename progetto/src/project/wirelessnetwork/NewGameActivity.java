package project.wirelessnetwork;


import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TableRow.LayoutParams;

public class NewGameActivity extends Activity {
	
	private String TAG = "NewGameActivity";
	public static final String COMPOSED_QUESTION = "COMPOSED_QUESTION";
	public static final int CODE_COMPOSE_QUESTION = 1;
	
	private Integer[] facesID = {R.drawable.chi_alex, R.drawable.chi_alfred, 
			R.drawable.chi_anita, R.drawable.chi_anne, R.drawable.chi_bernard, 
			R.drawable.chi_bill, R.drawable.chi_charles, R.drawable.chi_claire,
			R.drawable.chi_david, R.drawable.chi_eric, R.drawable.chi_frans,
			R.drawable.chi_george, R.drawable.chi_herman, R.drawable.chi_joe,
			R.drawable.chi_maria, R.drawable.chi_max, R.drawable.chi_paul, 
			R.drawable.chi_peter, R.drawable.chi_philip, R.drawable.chi_richard, 
			R.drawable.chi_robert, R.drawable.chi_sam, R.drawable.chi_susan, R.drawable.chi_tom};
	
	private FaceConteiner[] faces;
	private BluetoothConnectionManager connectionManager;
	
	BluetoothConnectionManager bluetoothManager;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
		Log.d(TAG, "++ onCreate ++");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_game);
        
        connectionManager = ConnectDevice.connectionManager;
        
        String[] names = this.getResources().getStringArray(R.array.images_name);
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
			TableLayout table = (TableLayout) findViewById(R.id.table_layout);
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
        
       /* Devo avviare activity per determinare personaggio che viene assegnato al giocatore */
    }
}
