/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nodemachine;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.InputMismatchException;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;
import nodeObjects.*;

/**
 *
 * @author Antonio Marigliano
 */
public class NodeMachine {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws SocketException, UnknownHostException, IOException, InterruptedException {
        DatagramSocket nodeSocket = new DatagramSocket(0);
        int timeout = 5000;
        nodeSocket.setSoTimeout(timeout);
        Scanner input = new Scanner(System.in);
        byte[] buffer = new byte[512]; 
        DatagramPacket incMess = new DatagramPacket(buffer,512);
        boolean syncConnect = false;
        long sleepy = 10L; 
        JobPool jp = new JobPool();
        int thisPort = -1;
        int proCap = 50;

        System.out.printf("Welcome to the COMP2011 DNA&OS module Load Balancer coursework program by Antonio Marigliano.\n"
               + "This is the Node Machine version of the program. In order for this node to function pleasure ensure the"
                + "Load balancer has been initialized and the correct IP and port numbers are entered before running."
                + "Please select an integer value for the simulated capacity of the node machine. Invalid selection will\n"
               + "default to 50.\n"
               + "DECISION: ");
        try{
        proCap = input.nextInt();
        }catch(InputMismatchException e){
            proCap = 50;
        }
        while(syncConnect == false){

        String message = "node£" + proCap;
        
        DatagramPacket nodeConnectPacket = new DatagramPacket(message.getBytes(),
                message.getBytes().length,
                InetAddress.getByName("127.0.0.1"),9001);               //NOTE: This is the default address of the load balancer, please 
        System.out.printf("Sending initiation data\n ");
        
        nodeSocket.send(nodeConnectPacket);

        try{
        nodeSocket.receive(incMess);
        String messString = new String(incMess.getData(),incMess.getOffset(),incMess.getLength());
        String[] messArray = messString.split("£");
        thisPort = Integer.parseInt(messArray[1]);
        if (messArray[0].contains("confirm")){
            syncConnect= true;
        }
        } catch (SocketTimeoutException exception) {
           System.out.printf("Load balancer response timed out, resending initiation data.\n ");
               
        nodeSocket.setSoTimeout(timeout += 20);}
        
        }
        
        System.out.printf("Node successfully synchronised with load balancer!\n ");
        nodeSocket.setSoTimeout(0);
        System.out.printf("Setting up job pool cleaner...\n ");
        Thread cleaner = new Thread(new Cleaner(jp, nodeSocket, incMess, thisPort));
                    cleaner.start();
        while (true){

            System.out.printf("Awaiting message... \n");    
            nodeSocket.receive(incMess);
            String messString = new String(incMess.getData(),incMess.getOffset(),incMess.getLength());
            String[] messArray = messString.split("£");
            
            String message = messArray[0];
            switch (message){
                case "job":
                    int jobID = Integer.parseInt(messArray[2]); 
                    System.out.printf("Received job ID#%d: \"%s\" \n ",jobID,messArray[1]);
                    jp.add(new Job(messArray[1], jobID));
                    message = "received";
                    DatagramPacket receiptPacket = new DatagramPacket(message.getBytes(),
                    message.getBytes().length,
                    InetAddress.getByName("127.0.0.1"),9001);
                    System.out.printf("Sending confirmation of job receipt to load balancer...\n ");

                    nodeSocket.send(receiptPacket);
                    
                    System.out.printf("Setting up job worker thread...\n ");
                    Thread worker = new Thread(new Worker(jp.getJobByID(jobID), nodeSocket, incMess, thisPort));
                    worker.start();
                    
                    break;
                case "completion":                
                    
                    int completedJobID = Integer.parseInt(messArray[2]);
                    String completedMessage = messArray[1];
                    System.out.printf("Job completion data received: Job ID#%d:\"%s\"\n ", completedJobID, completedMessage);
                    jp.getJobByID(completedJobID).setMessage(completedMessage);
                    jp.getJobByID(completedJobID).setComplete(true);
                    System.out.printf("Job ID#%d now logged as complete\n ",completedJobID);
                    break;
                case "receipt":
                    int finishedJob = Integer.parseInt(messArray[1]);
                    System.out.printf("Received receipt for completed Job ID#%d removing from job pool.\n ", finishedJob);
                    jp.removeJobByID(finishedJob);
                    jp.display();
                    break;
                case "resolution":
                    //this is an internal message that will check the job pool for jobs that still need sending back
                    //this comes from the "Cleaner" thread
                    message = messArray[1];
                    jobID = Integer.parseInt(messArray[2]);
                    System.out.printf("Cleaner has returned Job ID#%d: \"%s\" ready for transmission to load balancer!\n ", jobID, message);
                    message = "payload£" + message + "£" + jobID;
                    DatagramPacket jobConfirmPacket = new DatagramPacket(message.getBytes(),
                        message.getBytes().length,
                        InetAddress.getByName("127.0.0.1"),9001);
                    nodeSocket.send(jobConfirmPacket);
                    System.out.printf("Completed Job ID#%d returned to load balancer\n ",jobID);
                    
            }

            }
            
        }

    }
    



