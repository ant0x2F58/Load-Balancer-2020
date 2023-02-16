
package nodeObjects;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import nodeObjects.JobPool;


/**
 * Cleaner thread java file
 * @author Antonio Marigliano
 */
public class Cleaner implements Runnable{
    private JobPool jp;
    private DatagramSocket socket;
    private DatagramPacket packet;
    private int port;
    
    /*
    This is a thread intended to run simultaneously on each node machine for the purpose
    of finding compelted jobs in the job pool and sending an internal payload to begin
    transmission of the job data back to the load balancer. It takes the local jobpool,
    socket, packet and port data as parameters for construction.
    */
    public Cleaner(JobPool jp, DatagramSocket socket, DatagramPacket packet,int port){
        this.jp = jp;
        this.socket = socket;
        this.packet = packet;
        this.port = port;

    }
    
    @Override
    public void run(){
        while (true){
            long sleepy = 2L; 
            TimeUnit time = TimeUnit.SECONDS; 
            //The thread has a 2 second buffer to avoid sending too many internal messages
            //in rapid succession
            try{
            time.sleep(sleepy); 
            }catch(InterruptedException e){}
            
            //The thread loops round to this function that will either return the ID of 
            //a completed job or a value of -1
            int jobID = this.jp.returnCompletedJob();
            if (jobID != -1){
                System.out.printf("CLEANER: completed job found in job pool!\n ");
                String result = this.jp.getJobByID(jobID).getMessage();            
                System.out.printf("CLEANER: completed job #%d:\"%s\" found in job pool! Preparing internal resolution payload...\n ",jobID,result);
                String message = "resolution£" + result + "£" + jobID;
                try{
                    DatagramPacket resolvedJobPacket = new DatagramPacket(message.getBytes(),
                        message.getBytes().length,
                        InetAddress.getByName("127.0.0.1"),this.port);
                System.out.printf("CLEANER: Sending job resolution data\n ");

                this.socket.send(resolvedJobPacket);
                }catch(IOException e){}
            }
        }
    }
}
