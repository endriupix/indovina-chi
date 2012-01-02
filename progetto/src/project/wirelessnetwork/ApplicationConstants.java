package project.wirelessnetwork;

public class ApplicationConstants {

	public final int REQUEST_CONNECT = 1;
	
	public static final String DEVICE_NAME = "device_name";
	public static final String TOAST = "toast";
	public static final String TYPE_OF_FAILURE = "Failure";
	
	// Message types sent from the BluetoothConnectionManager Handler
    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_WRITE = 3;
    public static final int MESSAGE_DEVICE_NAME = 4;
    public static final int MESSAGE_TOAST = 5;
    public static final int CONNECTION_FAILURE = 6;
    public static final int CONNECTION_LOST = 7;
}
