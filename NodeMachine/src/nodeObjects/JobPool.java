/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nodeObjects;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Collections;

/**
 * JobPool java class file - Node Machine version
 * @author Antonio Marigliano
 */
public class JobPool {
    private final ArrayList<Job> jobPool;        //the pool of jobs in progress
    private DatagramSocket socket;
    
    
        /**
     * Constructor - initiates the singular job pool for use
     */
    public JobPool() {
        jobPool = new ArrayList<>();        
    }
        
    /**
     * Adds a job to the pool
     * @param j the job to add
     */
    public void add(Job j) {
        this.jobPool.add(j);
    }
    
    /**
     * Displays currently active jobs in the job pool
     */
    public void display(){        
        System.out.printf("Job pool contents are are as follows: \n");
        for (int i=0;i<this.jobPool.size();i++) {
         String completion = this.jobPool.get(i).getComplete()? "complete" : "incomplete" ;
         System.out.printf("Job #%d ID:%d Completion:%s Arrival Time:%d content:%s \n",i, 
                 this.jobPool.get(i).getJobID(),
                 completion,
                 this.jobPool.get(i).getArrival(),
                 this.jobPool.get(i).getMessage());      
        }
    }

    /**
     * Retrieves a job based on the job ID number
     * @param ID the ID number of the job to return
     * @return the Job matching the Job ID
     */
    public Job getJobByID(int ID ) {
        int result = -1;
        for (int i=0;i<this.jobPool.size();i++) {
            if(this.jobPool.get(i).getJobID()== ID){
                result = i;
                break;
            }            
        }
        return this.jobPool.get(result);
    }
    
    /**
     * Removes a job based on the job ID number
     * @param ID the ID number of the job to remove
     */
    public void removeJobByID(int ID ) {
        int result = -1;
        for (int i=0;i<this.jobPool.size();i++) {
            if(this.jobPool.get(i).getJobID()== ID){
                result = i;
                break;
            }            
        }
        this.jobPool.remove(result);
    }
    

    /**
     * Returns one completed job from the job pool for transmission.
     * @return 
     */
    public int returnCompletedJob(){
        
        for (int i=0;i<this.jobPool.size();i++) {
            if(this.jobPool.get(i).getComplete() == true){
                System.out.printf("completed job detected\n ");
                return this.jobPool.get(i).getJobID();
                      
            }            
        }
        return -1;
    }

    /**
     * Converts the Job message to a string for display.
     * @param ID of the job
     * @return the message associated with the identified job
     */
    public String getMessageByID(int ID) {
        String result = this.getJobByID(ID).getMessage();
        return result;
    }
    
    /**
     * Checks for any incomplete jobs in the job pool
     * @param ID of the job
     * @return the cards represented as a string
     */
    public boolean jobsAvailable(){
        if (this.jobPool.size() == 0){
            return false;
        }
        for (int i=0;i<this.jobPool.size();i++) {
            if(this.jobPool.get(i).getComplete() == false){
                return true;
            }            
        }
        return false;
    }
}
        
        