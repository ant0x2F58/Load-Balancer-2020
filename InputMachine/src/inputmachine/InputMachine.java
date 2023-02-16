
package inputmachine;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.Scanner;
import inputClass.*;
/**
 *
 * @author Antonio Marigliano
 */
public class InputMachine {

    /**
     * @param args the command line arguments
     * @throws java.net.SocketException
     */
    public static void main(String[] args) throws SocketException, IOException {
        Scanner input = new Scanner(System.in);
        
        DatagramSocket nodeSocket = new DatagramSocket(9000);
        nodeSocket.setSoTimeout(500);
        byte[] buffer = new byte[512]; 
        DatagramPacket incMess = new DatagramPacket(buffer,512);
        Thread receiver = new Thread(new Receiver(nodeSocket,incMess));
        boolean syncConnect = false;
        long sleepy = 10L; 
        receiver.start();

        System.out.printf("Welcome to the COMP2011 DNA&OS module Loadbalancer coursework program by Antonio Marigliano.\n"
               + "This is the input machine to communicate messages for the load balancer. Each message will be received,\n"
                + "delivered to a node to work on for 30 seconds and then returned with the character sequence of the\n"
                + "original message reverse. Initialize the load balancer program and at least one version of the node\n"
                + "machine program to begin operation. Enjoy!");
        while(true){

        
        System.out.printf("Provide job message for load balancer:\n ");
        //this third segment of the string that reads "nil" is a placeholder for the loadbalancer's own internal bypass
        String message = "job£" + input.nextLine() + "£nil";
        
        DatagramPacket nodeConnectPacket = new DatagramPacket(message.getBytes(),
                message.getBytes().length,
                InetAddress.getByName("127.0.0.1"),9001);
        System.out.printf("Sending the message\n ");
        
        nodeSocket.send(nodeConnectPacket);
        
        }
    }
    
}
