package edu.buffalo.cse.cse486586.groupmessenger;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;


import android.util.Log;

public class Sequencer {

	private static int pqkey;

	private final static String[] REMOTE_PORTS={"11108","11112","11116","11120","11124"};
	
	Sequencer(){
    
	   pqkey=0;
	
	}


	static void multicast(String... msg)
	{
		try {
            
        	for(int i=0;i<5;i++)
        	{
        	String remotePort = REMOTE_PORTS[i];
            
         
            Socket socket = new Socket(InetAddress.getByAddress(new byte[]{10, 0, 2, 2}),
                    Integer.parseInt(remotePort));
            int temp=pqkey+10;                  // incrementing by ten to support single and double digit key attachment to message
            String msgToSend = "11108"+temp+msg[0];
         
                        
            if(socket.isConnected())
            {
            BufferedWriter bw=new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
           
            bw.write(msgToSend);  
            bw.flush();
            
           
            }
            
            socket.close();
            
          }
        	 pqkey++;
             
        } catch (UnknownHostException e) {
            Log.e("Sequencer", "ClientTask UnknownHostException");
        } catch (IOException e) {
            Log.e("Sequencer",e.getMessage());
        }

		
	}
	
}
