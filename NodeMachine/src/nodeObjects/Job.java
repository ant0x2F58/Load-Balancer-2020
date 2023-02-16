
package nodeObjects;
import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;  
/**
 * NODE VERSION
 * @author Antonio Marigliano
 */
public class Job {
    private int jobID;                 //Job ID number
    private boolean complete;            //Job completion status
    private int arrival;                 //load balancer arrival time
    private String message;             //this is the message content of the job

    
    /*
    Returns the Job ID number.
    */
    public int getJobID(){return this.jobID;}
    /*
    Returns the Job completion boolean.
    */
    public boolean getComplete(){return this.complete;}
    /*
    Returns the Job arrival time.
    */
    public int getArrival(){return this.arrival;}  
    /*
    Returns the Job message.
    */
    public String getMessage(){return this.message;}  

    
    /*
    Sets the completion boolean for the job.
    */
    public void setComplete(boolean flag){this.complete = flag;}
    /*
    Sets the message for the job.
    */
    public void setMessage(String message){this.message = message;}
    /*
    Sets the ID for the job as determined by the load balancer.
    */
    public void setID(int ID){this.jobID = ID;}
    
    /**
     * Much like the load balancer object of the same name this creates a job required for processing
     * it only deals with the completion, ID and message string. 
     * @param message
     * @param jobID 
     */
     
    public Job(String message, int jobID){
        DateTimeFormatter timeFormat = DateTimeFormatter.ofPattern("HHmmss");
        LocalDateTime currentTime = LocalDateTime.now();
        int i = Integer.parseInt(timeFormat.format(currentTime));
        this.jobID = jobID;
        this.message = message;
        this.arrival = i;
        this.complete = false;             
    }
    

    

    
    
}
