import java.rmi.*;

/**
* 
* Gives interface to be implemented by the server application
*
*/
public interface ServerInterface extends Remote
{
   /* declare RMI method */
   public GarageData GarageRun( GarageData gar ) throws RemoteException;
}
