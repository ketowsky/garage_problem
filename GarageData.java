import java.io.*;

/**
*
* Represents object which contains  
* statistics parameters of examined garage
*
*/
public class GarageData implements Serializable
{
	int requestIterations;
	int iterator;                       
	int matrixSize;
	int availableStalls = matrixSize;
	
	long allCarsCounter = 0; 
	long turnedAwayCars = 0;      
	long countOfAvailableStalls = 0;
	
	double totalAllCarsCounter = 0.0;
	double totalTurnedAwayCars = 0.0;
	double totalcountOfAvailableStalls = 0.0;
	
	int arrivalRandTime;
	double expectedValueOfArrival;
	double expectedValueOfStoppage;	
	double standardDeviationOfTheStoppage = expectedValueOfStoppage/4.0;
	
	boolean flag = false;        // flag useful to recognize if there were only one car which arrived at one iteration of while loop
	boolean turnedAwayFlag;      // flag useful to count all turned away cars
	
	double percentOfAvailableStalls = 0.0;
	double probabilityOfTurningAway = 0.0;
}
