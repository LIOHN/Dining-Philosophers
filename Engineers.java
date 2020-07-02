/**
* Implementation of a variation of the Dining Philosophers problem. EIGHT COPYISTS are employed by
* to make diﬀerent scale accurate reproductions of various technical drawings, e.g. architectural
* blueprints. After completing a task, the copyist checks it against the original, and if satisfied
* with the work done, the copyist moves onto the next task. Since the firm is rather impoverished,
* it has only been able to provide FOUR EIDOGRAPHS and FOUR PENCILS. As a result these must be 
* shared between the eight. A copyist, however, cannot carry out any task unless he/she is able to
* use both simultaneously. 
* Class Engineers contains the main method and initialises the 8 copyists, their respective threads
* and the tools.
* Shared resources are pencils and eidographs. The use of keyword "synchronised" makes this program
* thread-safe. Callbacks with lock held may occur but a thread is asked to wait AND sleep if 
* a tool is unavailable.
*
* @author  Daniel Ene
* @version 2.0
* @since   15-03-2017 (COMP104)
*/
import java.util.Random;

public class Engineers {
    public static void main(String args[]) {
        int num = 4;      //number of tools      

		//initialise num pencils and eidographs
        Eidograph[] eidograph = new Eidograph[num];  
		for (int i = 0; i < num; i++) {
			eidograph[i] = new Eidograph(i);
		}
		
		Pencil[] pencil = new Pencil[num];  
		for (int i = 0; i < num; i++) {
			pencil[i] = new Pencil(i);
        }
		
		/*
		* initialise the 8 copyists and give each one a set of instructions using a for 
		* loop in such way that eidographs and pencils from arrays are placed in the
		* correct positions (...on an imaginary desk)
		*/
        Copyist[] copyist = new Copyist[8];
		for (int i = 0; i < 8; i++) {
			if (i % 2 == 0) {
				copyist[i]= new Copyist(i, pencil[((i + 1) / 4) % 4], eidograph[(i - 1) / 2], 30, 60, 40, 100);
			} else {
				copyist[i]= new Copyist(i, pencil[((i + 1) / 4) % 4], eidograph[(i - 1) / 2], 30, 80, 40, 60);
			}
		}
		
		//start a thread for each copyist
		for (int i = 0; i < 8; i++) { 
			Thread e = new Thread(copyist[i]);
			e.start();
		}
        
    }
}


//****************************************************************************************************************************************//
/**
* By extending Thread, an instance of class Copyist creates a copyist thread. In this class, allowed 
* behaviour is also defined (i.e. copy, check)
*
* @author  Daniel Ene
* @version 2.0
* @since   15-03-2017 (COMP104)
*/
class Copyist extends Thread {
	private int copyistID;
	private Pencil pencil;
	private Eidograph eidograph;
	private int minCopy;
	private int maxCopy;
	private int minCheck;
	private int maxCheck;
	
	/**
	* Constructor sets parameters for an instance of Copyist.
	*
	* @param int copyistID, identifier used to determine whether copyist is odd or even
	* @param Pencil pencil that this copyist can reach
	* @param Eidograph eidograph that this copyist can reach
	* @param minCopy minimum time spent copying
	* @param maxCopy maximum time spent copying
	* @param minCheck minimum time spent checking own work
	* @param maxCheck maximum time spent checking own work
	*/	
	public Copyist(int copyistID, Pencil pencil, Eidograph eidograph, int minCopy, int maxCopy, int minCheck, int maxCheck) {
		this.copyistID = copyistID;
		this.pencil = pencil;
		this.eidograph = eidograph;
		this.minCopy = minCopy;
		this.maxCopy = maxCopy;
		this.minCheck = minCheck;
		this.maxCheck = maxCheck;
	}
	
	/**
	* Thread RUN method (override run())
	* Each copyist has to do 5 copies before he/she can leave.
	*/
	public void run() {
		for(int i = 0 ; i < 5 ; i++) {
			try{
				copy();
			} catch (InterruptedException e) {}
			
			System.out.println("Copyist " + copyistID + " has finished copy " + (i + 1));
		}
		System.out.println("Copyist " + copyistID + " has finished work and gone to the pub.");
	}
	
	/**
	* COPY Method
	* A copyist drawing copies is implemented as making the thread sleep for a random duration 
	* within set time limits. Once the copyist is done (i.e. wakes up), he puts his 
	* tools down and starts checking.
	*
	* @throws InterruptedException
	*/
	public void copy() throws InterruptedException {
		this.pencil.pickToolUp(this.copyistID);
		System.out.println("Copyist " + this.copyistID + " has picked pencil " + pencil.toolID + " up.");
		this.eidograph.pickToolUp(this.copyistID);
		System.out.println("Copyist " + this.copyistID + " has picked eidograph " + eidograph.toolID + " up.");
		System.out.println("Copyist " + this.copyistID + " is now drawing.");
		
		try {
			sleep(new Random().nextInt((maxCopy - minCopy + 1) + minCopy));
		} catch (InterruptedException e) {}
		
		this.pencil.putToolDown(this.copyistID);
		this.eidograph.putToolDown(this.copyistID);
		
		check();
	}
	
	/**
	* CHECK Method
	* A copyist checking his work is implemented as making the thread sleep for a random duration 
	* within set time limits.
	*
	* @throws InterruptedException
	*/
	public void check() throws InterruptedException {
		System.out.println("Copyist " + this.copyistID + " is now checking. ");
		sleep(new Random().nextInt((maxCopy - minCopy + 1) + minCopy));
		System.out.println("Copyist " + this.copyistID + " has stopped checking. ");
	}
}


//****************************************************************************************************************************************//
/**
* Class Tool defines tool related actions (i.e. pick it up, put it down).
*
* @author  Daniel Ene
* @version 2.0
* @since   15-03-2017 (COMP104)
*/
class Tool {
	public int toolID;
	private String toolName;
	private boolean isAvailable; 
	
    /**
	* Constructor sets a toolID for an instance of Tool and initialises its isAvailable value to 
	* true, as a tool that's just been created will be available for use. Calling the constructor
	* also sets the name for a tool, for logging purposes.
	*
	* @param toolID
	*/
	public Tool(int toolID, String toolName){
		this.toolID = toolID;
		this.toolName = toolName;
		this.isAvailable = true;
	}
	
	/**
	* When a copyist picks the tool up, it becomes unavailable. If another copyist tries to pick it
	* up but it's unavailable, he'll just have to wait and be notified. The code that gets triggered
	* first is the code that checks if a tool that's in use is being picked up. If that happens, the
	* copyist (thread) should wait (wait();)
	*
 	* @param int copyistID
	*/
	public synchronized void pickToolUp(int copyistID) throws InterruptedException {
		int counter = 0; //keeps track of no. of times the copyist tries to pickToolUp
		//int waitUntil = new Random().nextInt(10) + 5;
		
		while(!isAvailable){
			try {
				wait();
				System.out.println("Copyist " + copyistID + " is waiting for a/an " + this.toolName + " to use.");
			} catch (InterruptedException e) {}
			
			Thread.sleep(new Random().nextInt(100) + 50);			
			counter++;
			//if the copyist fails; goes over the waitUntil limit, he has to putPencilDown by returning false
		}
		
		//if isAvailable is true, then the copyist picks the tool up, and makes the tool unavailable
		isAvailable = false;
	}
	
	/**
	* When a copyist puts the tool down, it becomes available (isAvailable becomes true) and 
	* notifies the copyist waiting for access to this tool (wakes up the thread waiting for access
	* to resource) that it is available.
	*
	* @param int copyistID
	* @throws InterruptedException
	*/
	public synchronized void putToolDown(int copyistID) throws InterruptedException {
		isAvailable = true;
		System.out.println("Copyist " + copyistID + " has put " + this.toolName + " " + this.toolID + " down.");
		notify();
	}
}


//****************************************************************************************************************************************//
/**
* Class Pencil 
*
* @author  Daniel Ene
* @version 2.0
* @since   15-03-2017 (COMP104)
*/
class Pencil extends Tool {
	/**
	* Constructor for a pencil calls the constructor in the super class and defines the tool's ID
	* and the name of the tool. The availability of a new tool is set to true by default in super 
	* constructor.
	*
	* @param int eidographID
	*/
	public Pencil(int pencilID) {
		super(pencilID, "pencil");
	}
}


//****************************************************************************************************************************************//
/**
* Class Eidograph
*
* @author  Daniel Ene
* @version 2.0
* @since   15-03-2017 (COMP104)
*/
class Eidograph extends Tool {
    /**
	* Constructor for an eidograph calls the constructor in the super class and defines the tool's ID
	* and the name of the tool. The availability of a new tool is set to true by default in super 
	* constructor.
	*
	* @param int pencilID
	*/
	public Eidograph(int eidographID) {
		super(eidographID, "eidograph");
	}
}