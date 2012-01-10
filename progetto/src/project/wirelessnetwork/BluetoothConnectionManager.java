package project.wirelessnetwork;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class BluetoothConnectionManager {

	private String TAG = "BtConnManager";

	private static final String NAME = "IndovinaChi";
	private static final UUID MY_UUID = UUID.fromString("fa87c0d0-afac-11de-8a39-0800200c9a66");
	
	private final BluetoothAdapter mAdapter;
	private final Handler handler;
	private Handler handlerRead;
	private AcceptThread acceptThread;
	private ConnectThread connectThread;
	private ConnectedThread connectedThread;
	private int mState;
	
	public static final int STATE_NONE = 0;
	public static final int STATE_LISTEN = 1;
	public static final int STATE_CONNECTING = 2;
	public static final int STATE_CONNECTED = 3;
	
	public BluetoothConnectionManager(Context context, Handler handler) {
		
		Log.d(TAG, "++ Constructor ++");
		mAdapter = BluetoothAdapter.getDefaultAdapter();
		mState = STATE_NONE;
		this.handler = handler;
	}
	
	public synchronized void setState(int state) {
		Log.d("SET STATE", new Integer(state).toString());
		mState = state;
		handler.obtainMessage(ConnectDevice.MESSAGE_STATE_CHANGE, state, -1).sendToTarget();
	}
	
	public synchronized int getState() {
		return mState;
	}
	
	public void setHandlerRead(Handler handler) {
		handlerRead = handler;
	}
	
	public synchronized void start() {
		
		Log.d(TAG, "++ start ++");
		
		if (connectThread != null) { connectThread.cancel(); connectThread = null; }
		if (connectedThread != null) { connectedThread.cancel(); connectedThread = null; }
		
		if (acceptThread == null) {
			acceptThread = new AcceptThread();
			acceptThread.start();
		}
		
		setState(STATE_LISTEN);
	}
	
	public synchronized void connect(BluetoothDevice device) {
		
		Log.d(TAG, "++ connect ++");
		
		if (mState == STATE_CONNECTING) {
			if (connectThread != null) {
				connectThread.cancel();
				connectThread = null;
			}
		}
		
		if (connectedThread != null) {
			connectedThread.cancel();
			connectedThread = null;
		}
		
		connectThread = new ConnectThread(device);
		connectThread.start();
		setState(STATE_CONNECTING);
	}
	
	public synchronized void connected(BluetoothSocket socket, BluetoothDevice device) {
		
		Log.d(TAG, "++ connected ++");
		
		if (connectThread != null) { connectThread.cancel(); connectThread = null; }
		if (connectedThread != null) { connectedThread.cancel(); connectedThread = null; }
		if (acceptThread != null) { acceptThread.cancel(); acceptThread = null; }
		
		connectedThread = new ConnectedThread(socket);
		connectedThread.start();
		
		Message msg = handler.obtainMessage(ConnectDevice.MESSAGE_DEVICE_NAME);
		Bundle bundle = new Bundle();
		bundle.putString(ConnectDevice.DEVICE_NAME, device.getName());
		msg.setData(bundle);
		handler.sendMessage(msg);
		
		setState(STATE_CONNECTED);
		
	}
	
	public synchronized void stop() {
		
		Log.d(TAG, "++ stop ++");
		
		if (connectThread != null) {
            connectThread.cancel();
            connectThread = null;
        }

        if (connectedThread != null) {
            connectedThread.cancel();
            connectedThread = null;
        }

        if (acceptThread != null) {
            acceptThread.cancel();
            acceptThread = null;
        }

        setState(STATE_NONE);
	}
	
	public void write(byte[] out) {
		
		Log.d(TAG, "++ write ++");
		
		ConnectedThread r;
		synchronized(this) {
			if (mState != STATE_CONNECTED) {
				Log.d(TAG, "Not STATE_CONNECTED ".concat(new Integer(mState).toString()));
				return;
			}
			r = connectedThread;
		}
		r.write(out);
	}
	
	private void connectionFailed() {
		Log.d(TAG, "CONNECTION FAILED");
		setState(STATE_LISTEN);
		
		Message msg = handler.obtainMessage(ConnectDevice.MESSAGE_TOAST);
		Bundle bundle = new Bundle();
		bundle.putString(ConnectDevice.TOAST, "Connection Failed");
		bundle.putInt(ConnectDevice.TYPE_OF_FAILURE, ConnectDevice.CONNECTION_FAILURE);
		msg.setData(bundle);
		handler.sendMessage(msg);
		
	}
	
	private void connectionLost() {
		
		Log.d(TAG, "CONNECTION LOST");
		setState(STATE_LISTEN);
		
		Message msg = handler.obtainMessage(ConnectDevice.MESSAGE_TOAST);
		Bundle bundle = new Bundle();
		bundle.putString(ConnectDevice.TOAST, "Connection Lost");
		bundle.putInt(ConnectDevice.TYPE_OF_FAILURE, ConnectDevice.CONNECTION_LOST);
		msg.setData(bundle);
		if (handlerRead != null) {
			handlerRead.sendMessage(msg);
		}
		else {
			handler.sendMessage(msg);
		}
		
	}
	
	class AcceptThread extends Thread {
		
		private final BluetoothServerSocket serverSocket;
		private String TAG = "AcceptThread";
		
		public AcceptThread() {
			Log.d("AcceptThread", "Constructor");
			BluetoothServerSocket tmp = null;
			
			try {
				tmp = mAdapter.listenUsingRfcommWithServiceRecord(NAME, MY_UUID);
				Log.d(TAG, "Complete Listen");
			}
			catch(IOException exc) {
				Log.d(TAG, "Exception on AcceptThread() " + exc.getMessage());
			}
			serverSocket = tmp;
		}
		
		public void run() {
			
			Log.d(TAG, "Run");
			setName("AcceptThread");
			
			BluetoothSocket socket = null;
			
			while (mState != STATE_CONNECTED) {
				try {
					Log.w("AcceptThread", "Waiting Accept");
					socket = serverSocket.accept();
					NewGameActivity.starter = false;
				}
				catch(IOException ec) {
					Log.d(TAG, "Exception in AcceptThread - run() - serverSocket.accept(): " + ec.getMessage());
					break;
				}
				
				if (socket != null) {
					Log.d("AcceptThread", "Socket ok");
					synchronized (BluetoothConnectionManager.this) {
						
						switch(mState) {
						case STATE_LISTEN:
						case STATE_CONNECTING:
							Log.d(TAG, "STATE_CONNECTING");
							connected(socket, socket.getRemoteDevice());
							break;
						case STATE_NONE:
						case STATE_CONNECTED:
							try {
								socket.close();
							}
							catch(IOException ec) {
								Log.d(TAG, "Exception in AcceptThread - run() - serverSocket.close()");
							}
							break;
						}
						
					}
				}
			}
			Log.d(TAG, "End AcceptThread");
		}
		
		public void cancel() {
			try {
				serverSocket.close();
			}
			catch(IOException ec) {
				Log.d(TAG, "Exception in AcceptThread - cancel()");
			}
		}
	}
	
	class ConnectThread extends Thread {
		
		private final BluetoothSocket socket;
		private final BluetoothDevice device;
		
		public ConnectThread(BluetoothDevice device) {
			Log.d("ConnectThread", "Costruttore");
			
			this.device = device;
			BluetoothSocket tmp = null;
		
		
			try {
				tmp = device.createRfcommSocketToServiceRecord(MY_UUID);
			}
			catch (Exception e) {
				Log.d("ConnectThread", "Exception rfComm");
			}
			socket = tmp;
		}
		
		public void run() {
			Log.d("ConnectThread", "run");
			
			setName("ConnectThread");
			mAdapter.cancelDiscovery();
			
			//Try connection to BluetoothSocket
			try {
				socket.connect();
			}
			catch(IOException ex) {
				Log.d("ConnectionFailed", ex.getMessage());
				ex.printStackTrace();
				
				connectionFailed();
				try {
					socket.close();
				}
				catch(IOException ex1) {
					
				}
				
				BluetoothConnectionManager.this.start();
				return;
			}
			
			synchronized (BluetoothConnectionManager.this) {
				connectThread = null;
			}
			
			connected(socket, device);
		}
		
		public void cancel() {
			try {
				socket.close();
			}
			catch (IOException e) {
				//Log
			}
		}
		
	}

	class ConnectedThread extends Thread {
		
		private final BluetoothSocket bluetoothSocket;
		private final InputStream inputStream;
		private final OutputStream outputStream;
		
		public ConnectedThread(BluetoothSocket socket) {
			bluetoothSocket = socket;
			InputStream tmpIn = null; 
			OutputStream tmpOut = null;
			
			try {
				tmpIn = socket.getInputStream();
				tmpOut = socket.getOutputStream();
			}
			catch(IOException e) {
				Log.d("ConnectedThread", "No socket");
			}
			
			inputStream = tmpIn;
			outputStream = tmpOut;
		}
		
		public void run() {
			
			byte[] buffer = new byte[1024];
			int bytes;
			
			while (true) {
				try {
					Log.d("ConnectedThread", "dentro run");
					bytes = inputStream.read(buffer);
					Log.d("Messaggio ricevuto", "Messagio ricevuto");
					handlerRead.obtainMessage(ConnectDevice.MESSAGE_READ, bytes, -1, buffer).sendToTarget();
				}
				catch(IOException ex) {
					Log.d(TAG, "Lost run ConnectedThread" + ex.getMessage());
					connectionLost();
					break;
				}
			}
		}
		
		public void write(byte[] buffer) {
			try {
				
				outputStream.write(buffer);
				//handler.obtainMessage(ConnectDevice.MESSAGE_WRITE, -1, -1, buffer).sendToTarget();
			}
			catch(IOException exc) { 
				Log.d("ConnectedThread", "Eccezione write".concat(exc.getMessage()));
			}
			Log.d("ConnectedThread", "Scrittura completata");
		}
		
		public void cancel() {
			try {
				bluetoothSocket.close();
			}
			catch(Exception e) {
				//log
			}
		}
		
	}
}

