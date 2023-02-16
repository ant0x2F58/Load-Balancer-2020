
package inputClass;

import java.net.DatagramPacket;
import java.net.DatagramSocket;


/**
 * Receiver thread java file - input machine
 * @author Antonio Marigliano
 */
public class Receiver implements Runnable{
    private DatagramSocket socket;
    private DatagramPacket packet;
    public Receiver(DatagramSocket socket, DatagramPacket packet){
        this.socket = socket;
        this.packet = packet;

    }
    
    @Override
    public void run(){
        while (true){
        try{
            this.socket.receive(this.packet);
            String messString = new String(this.packet.getData(),this.packet.getOffset(),this.packet.getLength());
            System.out.printf("I received: %s\n",messString);
        }catch(Exception e){
            
            }
        }
    }
}
