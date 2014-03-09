package edu.buffalo.cse.cse486586.groupmessenger;



//import edu.buffalo.cse.cse486586.simplemessenger.R;
//import edu.buffalo.cse.cse486586.simplemessenger.SimpleMessengerActivity.ClientTask;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;



import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.graphics.Color;
import android.telephony.TelephonyManager;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


public class GroupMessengerActivity extends Activity {
	
	static final String TAG = GroupMessengerActivity.class.getSimpleName();
    static int key=0;
	static final String REMOTE_PORT0 = "11108";
    static final String REMOTE_PORT1 = "11112"; 
    static final String REMOTE_PORT2 = "11116";
    static final String REMOTE_PORT3 = "11120";
    static final String REMOTE_PORT4 = "11124";
    static final String[] REMOTE_PORTS = {REMOTE_PORT0,REMOTE_PORT1,REMOTE_PORT2,REMOTE_PORT3,REMOTE_PORT4};
    static final int SERVER_PORT = 10000;

    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_messenger);
        
        TelephonyManager tel = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
        String portStr = tel.getLine1Number().substring(tel.getLine1Number().length() - 4);
        final String myPort = String.valueOf((Integer.parseInt(portStr) * 2));
        
     
        
        try {
            /*
             * Create a server socket as well as a thread (AsyncTask) that listens on the server
             * port.
             * 
             * AsyncTask is a simplified thread construct that Android provides. 
             */
            ServerSocket serverSocket = new ServerSocket(SERVER_PORT);
            new ServerTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, serverSocket);
        } catch (IOException e) {
           
            Log.e(TAG, "Can't create a ServerSocket");
            return;
        }
        
        
        
        
        
        
        //---------------------------------- send onclick event listner----------------------------
        TextView tv = (TextView) findViewById(R.id.textView1);
        tv.setMovementMethod(new ScrollingMovementMethod());
        
        tv.setBackgroundColor(Color.parseColor("#E5E8EF"));
       tv.setTextColor(Color.parseColor("#222222"));
        final EditText editText = (EditText) findViewById(R.id.editText1);
        //findViewById(R.id.button4).setOnClickListener(l);
        Button send=(Button) findViewById(R.id.button4);
        send.setBackgroundColor(Color.parseColor("#345290"));
        send.setTextColor(Color.parseColor("#FFFFFF"));
        send.setOnClickListener(new OnClickListener(){

        
			@Override
			public void onClick(View view) {
				view= findViewById(R.id.textView1);
				String msg = editText.getText().toString()+"\n";
                editText.setText(""); // This is one way to reset the input box.
                if(!msg.trim().equalsIgnoreCase("")){
                ((TextView) view).append("Sent to self = "+msg); // This is one way to display a string.
                new ClientTask().executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, msg, myPort);}
                
			}
		});
        
        editText.setOnKeyListener(new OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                        (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    /*
                     * If the key is pressed (i.e., KeyEvent.ACTION_DOWN) and it is an enter key
                     * (i.e., KeyEvent.KEYCODE_ENTER), then we display the string. Then we create
                     * an AsyncTask that sends the string to the remote AVD.
                     */
                    String msg = editText.getText().toString() + "\n";
                    editText.setText(""); // This is one way to reset the input box.
                    TextView localTextView = (TextView) findViewById(R.id.textView1);
                    
                    if(!msg.trim().equalsIgnoreCase("")){
                    localTextView.append(msg.trim()); // This is one way to display a string.

                    /*
                     * Note that the following AsyncTask uses AsyncTask.SERIAL_EXECUTOR, not
                     * AsyncTask.THREAD_POOL_EXECUTOR as the above ServerTask does. 
                     */
                    new ClientTask().executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, msg, myPort);
                    return true;}
                }
                return false;
            }
        });
        
        /*
         * Registers OnPTestClickListener for "button1" in the layout, which is the "PTest" button.
         * OnPTestClickListener demonstrates how to access a ContentProvider.
         */
        findViewById(R.id.button1).setOnClickListener(
                new OnPTestClickListener(tv, getContentResolver()));
        
   
       
        
        
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_group_messenger, menu);
        return true;
    }

    private class ServerTask extends AsyncTask<ServerSocket, String, Void> {

        @Override
        protected Void doInBackground(ServerSocket... sockets) {
            ServerSocket serverSocket = sockets[0];
        
            
         
            try{
            	 while(true)
                 {
            	Socket serv= serverSocket.accept();
           
       
              BufferedReader br=new BufferedReader(new InputStreamReader(serv.getInputStream()));
              String str=br.readLine();              //sequencer message format [src_port|key+10|message]
              
              if(str.substring(0,5).equalsIgnoreCase("11108"))
              {
                                        	  
              int rec_key;
              rec_key=Integer.parseInt(str.substring(5,7));   // extracting key from sequencer message
              rec_key=rec_key-10;
              
              ContentResolver cr=getContentResolver();
              ContentValues cv=new ContentValues();
              if((!str.trim().equalsIgnoreCase("") && str!=null))
              {
              cv.put("key",rec_key);
              cv.put("value", str.substring(7, str.length()));   // inserting extracted value from sequencer message
             
              
               Uri ur=buildUri("content", "edu.buffalo.cse.cse486586.groupmessenger.provider");
               cr.insert(ur,cv);
               publishProgress(str);
               }
              }
              else{
            	  
            	   Sequencer.multicast(str);
            	  
                  }
        	 }    	   
           
            }
            catch(Exception e)
            {
              publishProgress("Exception caused by : " + e);
            }
            
            return null;
        }

        private Uri buildUri(String scheme, String authority) {
            Uri.Builder uriBuilder = new Uri.Builder();
            uriBuilder.authority(authority);
            uriBuilder.scheme(scheme);
            return uriBuilder.build();
        }
        protected void onProgressUpdate(String...strings) {
            /*
             * The following code displays what is received in doInBackground().
             */
            String strReceived = strings[0].trim();
            
            TextView localTextView = (TextView) findViewById(R.id.textView1);
            localTextView.append(strReceived+"\n");
            
            /*
             * The following code creates a file in the AVD's internal storage and stores a file.
             * 
             * For more information on file I/O on Android, please take a look at
             * http://developer.android.com/training/basics/data-storage/files.html
             */
            
            String filename = "GroupMessengerOutput";
            String string = strReceived + "\n";
            FileOutputStream outputStream;

            try {
                outputStream = openFileOutput(filename, Context.MODE_PRIVATE);
                outputStream.write(string.getBytes());
                outputStream.close();
            } catch (Exception e) {
                Log.e(TAG, "File write failed");
            }

            return;
        }
    }

    /***
     * ClientTask is an AsyncTask that should send a string over the network.
     * It is created by ClientTask.executeOnExecutor() call whenever OnKeyListener.onKey() detects
     * an enter key press event.
     * 
     */
    private class ClientTask extends AsyncTask<String, Void, Void> {

    	
        @Override
        protected Void doInBackground(String... msgs) {
try {
                
           
            	String remotePort = REMOTE_PORT0;   // sending to sequencer at avd 0
                
             
                Socket socket = new Socket(InetAddress.getByAddress(new byte[]{10, 0, 2, 2}),
                        Integer.parseInt(remotePort));
                
                String msgToSend =msgs[0];
                           
                if(socket.isConnected())
                {
                BufferedWriter bw=new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
               
                bw.write(msgToSend);  
                bw.flush();
                }
                
                socket.close();
                
              
                 
            } catch (UnknownHostException e) {
                Log.e(TAG, "ClientTask UnknownHostException");
            } catch (IOException e) {
                Log.e(TAG,e.getMessage());
            }

            return null;
        }
    }
}






