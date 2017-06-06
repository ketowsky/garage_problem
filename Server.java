import java.rmi.*;
import java.rmi.server.*;
import java.net.*;
import java.util.*;
import java.lang.*;



/**
*
* Class Server represents implementation of the drive-in entry
* Class Server checks if there is still available stalls 
* and decides what the orders to give to the arriving cars.
*
*/
public class Server extends UnicastRemoteObject implements ServerInterface
{
	/** 
	*
	* Constructor of the Server class
	* No parameters required 
	*
	*/
	public Server() throws RemoteException
	{ 
			super(); 	/* call parent class constructor, it will create server skeleton */
	}
	
	/**
	*
	* Method GarageRun simulates behaviour of garage.
    * It implements method from interface @ServerImplementation
    * @param gar    is object contains all the measurements of instance of garage
    * @return       object conains measured parameters
	*
	*/
	public GarageData GarageRun( GarageData gar ) throws RemoteException
	{
		Stall[] Garage = new Stall[ gar.matrixSize ];
		
		for(int i = 0; i < gar.matrixSize; i++){
			Garage[i] = new Stall();
			Garage[i].avail = true;      //availabilit flag
			Garage[i].stallTimer = 0.0;  //timer counting down the time of car taking "this" stall
	    }
		gar.iterator = gar.requestIterations;  
		while (gar.iterator > 0){
			gar.iterator--;
			gar.turnedAwayFlag = true; 
			Random rand = new Random();
			double rand1 = ((double)(Math.abs(gar.expectedValueOfArrival*Math.log(rand.nextDouble()))));
			double rand2 = rand.nextGaussian()*gar.standardDeviationOfTheStoppage+gar.expectedValueOfStoppage;
			if(gar.arrivalRandTime == 0) {
				gar.allCarsCounter++;          //new car came
				gar.arrivalRandTime = (int)(rand1);//choose next random time of arriving
				gar.flag = true;               //toggles when car is parked
				for(int i = 0; i < gar.matrixSize; i++){
					if(Garage[i].avail && gar.flag) {
						Garage[i].stallTimer = rand2;
						Garage[i].avail = false;   //stall is now taken
						gar.availableStalls--;
						gar.flag = false;
						gar.turnedAwayFlag = false;
					}
					else if(Garage[i].avail && !gar.flag) { //this means that there is free stall in matrix, but the car has been already parked
						gar.availableStalls++;              //counting up the available stalls
					}
					else{ 
						--Garage[i].stallTimer;  //counting down time of car parked at this stall
						if(Garage[i].stallTimer == 0) { //if time is up we need to clean variables
							Garage[i].avail = true; //now it's free again
							gar.availableStalls++;
						}
					}
				}
				gar.flag = false; // this is needed to be sure, that car has been turned if there were no available stalls
				if(gar.turnedAwayFlag) {  //counting up turned away cars for stats
					gar.turnedAwayCars++;
				}
				for(int i = 0; i < gar.matrixSize; i++){
					if(Garage[i].avail) {
						gar.countOfAvailableStalls++;
					}
				}
			}
			else { //this means there is no new car at the gates
				for(int i = 0; i < gar.matrixSize; i++){
					if(Garage[i].avail == false) { //check if the stall is taken
						--Garage[i].stallTimer; //counting down time of car parked at this stall
						if(Garage[i].stallTimer == 0) { //if time is up we need to clean variables
							Garage[i].avail = true; //now it's free again
							gar.availableStalls++;
						}
					} //no new place is taken, because there is no new car   
				} 
				gar.arrivalRandTime--;
			}		
			gar.totalAllCarsCounter += gar.allCarsCounter;
			gar.totalTurnedAwayCars += gar.turnedAwayCars;
			gar.totalcountOfAvailableStalls += gar.countOfAvailableStalls;
		}
		gar.percentOfAvailableStalls = (gar.totalcountOfAvailableStalls/gar.matrixSize)/(gar.requestIterations*100.0);
		gar.probabilityOfTurningAway =  gar.totalTurnedAwayCars/gar.totalAllCarsCounter;
		return gar;
	}
	
	
	/**
	*
	* The main method for server
	* @param args   array of string arguments given with the compilation
	*
	*/
	public static void main( String args[] ) throws Exception {
		String hostName = InetAddress.getLocalHost().getHostName();
		if( System.getSecurityManager() == null )
			System.setSecurityManager( new SecurityManager() );
		Naming.rebind( "//" + hostName + (args.length>1?":" + args[1]:"") +"/" + args[0] +  "GarageRun", new Server() );
	}
}
