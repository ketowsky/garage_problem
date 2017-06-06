import java.rmi.*;	
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;			


/**
*
* Class represents instance of Clients application
* Class Client manages traffic in the roundabout
*
*/
public class Client
{
    
    /**
    *
    * main method for clients application
    *
    */
	public static void main( String arg[] ) throws Exception
	{
		if( arg.length < 3 )
		{					
			System.out.println( "Specify 3 x hostname and port." );
			return;
		}
		
		String	hostName0 = arg[ 0 ];			/* Server 0 */
		String	hostName1 = arg[ 1 ];			/* Server 1 */
		String	hostName2 = arg[ 2 ];			/* Server 2 */
		String  port = ( arg.length > 3 ? ":" + arg[3] : "" );
			
		try
		{
			/* start security manager */
			if( System.getSecurityManager() == null )
				System.setSecurityManager( new SecurityManager() );

			GarageData data0 = new GarageData();
			GarageData data1, data2;
			ClientData client = new ClientData();
			
			List<String> lines;
			//read input data from file
			try {
				lines = Files.readAllLines(Paths.get("params.txt"), StandardCharsets.UTF_8);
			} catch (IOException e) {
				e.printStackTrace();
				return;
			}
		
			data0.matrixSize = Integer.parseInt(lines.get(0));
			data0.requestIterations = Integer.parseInt(lines.get(1));
			data0.expectedValueOfArrival = Double.parseDouble(lines.get(2));
			data0.expectedValueOfStoppage = Double.parseDouble(lines.get(3));
			data1 = data0;
			data2 = data0;
			
			//System.out.print( data2.matrixSize + " " + data2.requestIterations + " " + data2.expectedValueOfArrival + " " + data2.expectedValueOfStoppage + " " +"\n");
		
			ServerThread thread0 = new ServerThread(data0,"s0",hostName1, port);
			ServerThread thread1 = new ServerThread(data1,"s1",hostName1, port);
			ServerThread thread2 = new ServerThread(data2,"s2",hostName2, port);
			
			thread0.start();
			thread1.start();
			thread2.start();
			
			thread0.join();
			thread1.join();
			thread2.join();
			
			data0 = thread0.getResult();
			data1 = thread1.getResult();
			data2 = thread2.getResult();
			
			client.sumOfPercentOfAvailableStalls = (data0.percentOfAvailableStalls + data1.percentOfAvailableStalls + data2.percentOfAvailableStalls)/3.0;
			client.sumOfProbabilityOfTurningAway = (data0.probabilityOfTurningAway + data1.probabilityOfTurningAway + data2.probabilityOfTurningAway)/3.0;
		
			/* print results from returned parameters object */
			
			// Wypisanie statystyk z serverow
			System.out.print( "\n" + " *** Server " + hostName0 + " s0:" + "\n");
			System.out.println( "\n\t" +" Probability of finding free stall: " + String.format( "%.4f", data0.percentOfAvailableStalls )+ "\n\t" +" Probability of turning away: " + String.format( "%.4f", data0.probabilityOfTurningAway ) );
			
			System.out.print( "\n" + " *** Server " + hostName1 + " s1:" + "\n");
			System.out.println( "\n\t" +" Probability of finding free stall: " + String.format( "%.4f", data1.percentOfAvailableStalls )+ "\n\t" +" Probability of turning away: " + String.format( "%.4f", data1.probabilityOfTurningAway ) );

			System.out.print( "\n" + " *** Server " + hostName2 + " s2:" + "\n");
			System.out.println( "\n\t" +" Probability of finding free stall: " + String.format( "%.4f", data2.percentOfAvailableStalls )+ "\n\t" +" Probability of turning away: " + String.format( "%.4f", data2.probabilityOfTurningAway )  );

			
			// Wypisanie statystyk clienta
			System.out.print( "\n" + " ________________________________ " + "\n\n" + " *** Client " + "\n");
			System.out.println( "\n\t" +" Probability of finding free stall: " + String.format( "%.4f", client.sumOfPercentOfAvailableStalls )+ "\n\t" +" Probability of turning away: " + String.format( "%.4f", client.sumOfProbabilityOfTurningAway )  );

			//System.out.print( " " + data1.matrixSize+ "\n\n");
		}
		catch( Exception e )
		{
			e.printStackTrace();
			return;
		}
	}
}


/**
*
* Represents servers threads 
* for clients
*
*/
class ServerThread extends Thread{
	private GarageData data1;
	private String name;
	private String hostName;
	private String port;

	/**
	*
	* Constructor for class ServerThread.
	* Allows to set up the new object.
	* @param data1 is
	* @param name is a name for given thread
	* @param hostName is a name for host contains the server
	* @param port is a port ID to call proper host which contains given server 
	*
	*/
	public ServerThread(GarageData data1,String name,String hostName, String port) {
		super();
		this.name = name;
		this.data1 = data1;
		this.hostName = hostName;
		this.port = port;
	}
	
	/**
	*
	* Method run allows to execute the application.
	* No parameters and returns.
	*
	*/
	public void run(){
		try{
			ServerInterface obj = ( ServerInterface ) Naming.lookup( "//" + hostName + port+"/"+ name +"GarageRun" );
			data1 = obj.GarageRun( data1 );
		} catch (Exception e){
			e.printStackTrace();
		}
	}
	
	/**
	*
	* getResult returns results of performed countings.
	* No parameters required.
	*
	*/
	public GarageData getResult(){
		return data1;
	}

}
