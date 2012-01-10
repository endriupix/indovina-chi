package project.wirelessnetwork;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

public class ConnectDevice extends Activity {
	
	private String TAG = "ConnectDevice";
	
	private final int REQUEST_ENABLE_BT = 2;
	private final int REQUEST_CONNECT = 1;
	private final int GAME_ACTIVITY_RESULT = 3;
	
	Intent intentSearch = null;
	
	public static final String DEVICE_NAME = "device_name";
	public static final String TOAST = "toast";
	public static final String TYPE_OF_FAILURE = "Failure";
	public String connectedDevice = null;
	
	// Message types sent from the BluetoothChatService Handler
    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_WRITE = 3;
    public static final int MESSAGE_DEVICE_NAME = 4;
    public static final int MESSAGE_TOAST = 5;
    public static final int CONNECTION_FAILURE = 6;
    public static final int CONNECTION_LOST = 7;
	
	private BluetoothAdapter bluetoothAdapter = null;
	public static BluetoothConnectionManager connectionManager = null;
	private MenuConnectionManager menuConnectionManager = new MenuConnectionManager();

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Log.d(TAG, "++ onCreate ++");
		
		setContentView(R.layout.connect_device);
		
		bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		
		if (bluetoothAdapter == null) {
			Log.d(TAG, "BT NON SUPPORTATO");
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle(R.string.title_bt_not_supported);
			builder.setMessage(R.string.text_bt_not_supported);
			builder.create().show();
			finish();
		}
		

		if (!bluetoothAdapter.isEnabled()) {
			Log.d(TAG, "BT non attivo");
			Intent activateBluetoothIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
			startActivityForResult(activateBluetoothIntent, REQUEST_ENABLE_BT);
		}
		else {
			connectionManager = new BluetoothConnectionManager(ConnectDevice.this, mHandler);
		}
		
	}
	
	public void onStart() {
		super.onStart();
		
		Log.d(TAG, "++ onStart ++");
		
		if (connectionManager.getState() == 0) {
		new AlertDialog.Builder(this).setTitle("Seleziona Opzione")
				.setItems(R.array.options_bluetooth, new DialogInterface.OnClickListener() {
					
					public void onClick(DialogInterface dialog, int i) {
						if (i == 0) {
							menuConnectionManager.startSearchingDevices();
						}
						else {
							menuConnectionManager.makeDiscoverable();
						}
					}
				}).show();
		}
	}
	
	public void onDestroy() {
		super.onDestroy();
		
		Log.d(TAG, "++ onDestroy ++");
		if (connectionManager != null) {
			connectionManager.stop();
		}
	}
	
	public synchronized void onPause() {
		super.onPause();
	}
	
	public void onStop() {
		super.onStop();
	}
	
	private class MenuConnectionManager {
		private String TAG = "MenuConnectionManager";
		
		public void startSearchingDevices() {
			Log.d(TAG, "startSearchingDevices");
			intentSearch = new Intent(ConnectDevice.this, DeviceListActivity.class);
			startActivityForResult(intentSearch, REQUEST_CONNECT);
		}
		public void makeDiscoverable() {
			Log.d(TAG, "makeDiscoverable");
			if (bluetoothAdapter.getScanMode() != BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
	            Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
	            discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
	            NewGameActivity.starter = false;
	            startActivity(discoverableIntent);
			}
		}
	};
	
	public synchronized void onResume() {
		Log.d(TAG, "++ OnResume ++");
		super.onResume();
		
		if (connectionManager != null) {
			if (connectionManager.getState() == BluetoothConnectionManager.STATE_NONE) {
				Log.d(TAG, "Sto per fare start");
				connectionManager.start();
			}
		}
	}
	
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu, menu);
		return true;
	}
	
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()) {
		case R.id.menu_coonect: 
			menuConnectionManager.startSearchingDevices();
			return true;
		
		case R.id.menu_discoverable:
			menuConnectionManager.makeDiscoverable();
			return true;
		}
		return false;
	}
	
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		
		switch (requestCode) {
		
		case REQUEST_CONNECT: 
			
			if (resultCode == Activity.RESULT_OK) {
				
				Log.d(TAG, "REQUEST_CONNECT OK");
				
				NewGameActivity.starter = true;
				
				String address = data.getExtras().getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
				Log.d("ADDRESS DEVICE", address);
				BluetoothDevice device = bluetoothAdapter.getRemoteDevice(address);
				connectionManager.connect(device);
				
				View message = (View)findViewById(R.id.textview_connecting);
				message.setVisibility(1);
			}
			break;
		
		case REQUEST_ENABLE_BT:
			
			if (resultCode == Activity.RESULT_OK) {
				connectionManager = new BluetoothConnectionManager(ConnectDevice.this, mHandler);
			}
			else {
				Log.d(TAG, "Bluetooth: Autorizzazione negata");
				Toast.makeText(this, R.string.bt_not_enabled_leaving, Toast.LENGTH_LONG).show();
                finish();
			}
			break;
			
		/**
		 * Activity del gioco finita, termino anche activity che gestisce connessione bluetooth
		 */
		case GAME_ACTIVITY_RESULT:
			
			finish();
			break;
		}
		
	}
	
	private final Handler mHandler = new Handler() {
		
		private String TAG = "Handler";
        @Override
        public void handleMessage(Message msg) {
        	
        	Log.d(TAG, "++ handleMessage ++");
            switch (msg.what) {
            case MESSAGE_STATE_CHANGE:
                Log.d(TAG, "MESSAGE_STATE_CHANGE: " + msg.arg1);
                switch (msg.arg1) {
                case BluetoothConnectionManager.STATE_CONNECTED:
                	Log.d(TAG, "STATE_CONNECTED");
                	//devo iniziare activity del gioco perchè ho stabilito connessione
                	Intent newGame = new Intent(ConnectDevice.this, NewGameActivity.class);
                	startActivityForResult(newGame, GAME_ACTIVITY_RESULT);
                    break;
                case BluetoothConnectionManager.STATE_CONNECTING:
                    //connecting al dispositivo
                    break;
                case BluetoothConnectionManager.STATE_LISTEN:
                case BluetoothConnectionManager.STATE_NONE:
                    
                    break;
                }
                break;
            /*case MESSAGE_WRITE:
                byte[] writeBuf = (byte[]) msg.obj;
                // construct a string from the buffer
                
                break;
            case MESSAGE_READ:
                byte[] readBuf = (byte[]) msg.obj;
                // construct a string from the valid bytes in the buffer
                
                break;*/
            case MESSAGE_DEVICE_NAME:
                // save the connected device's name
                connectedDevice = msg.getData().getString(DEVICE_NAME);
                Toast.makeText(getApplicationContext(), "Connected to "
                               + connectedDevice, Toast.LENGTH_SHORT).show();
            	
                break;
            case MESSAGE_TOAST:
                Toast.makeText(getApplicationContext(), msg.getData().getString(ConnectDevice.TOAST),
                               Toast.LENGTH_SHORT).show();
                if (msg.getData().getInt(ConnectDevice.TYPE_OF_FAILURE) == ConnectDevice.CONNECTION_FAILURE) {
                	startActivityForResult(intentSearch, REQUEST_CONNECT);
                }
                break;
            }
        }
    };
    
    public BluetoothConnectionManager getConnectionManager() {
    	
    	return connectionManager;
    }
}
