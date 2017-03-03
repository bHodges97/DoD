
/**
 * Controller to communicate between player and gamelogic
 * 
 */
public abstract class Controller {
	Player player;
	DODServer server;
	int id;
	String input = "";
	
	
	/**
	 * Constructor
	 * @param dodServer The server to connect to
	 * @param id The id for this controller
	 * @param player The player does controller controls.
	 */
	public Controller(DODServer dodServer, int id, Player player) {
		this.server = dodServer;
		this.id = id;
		this.player = player;
	}
	
	/**
	 * get input to use on player  
	 * @return 
	 */
	public abstract String getInput();
	
	/**
	 * Send output to client
	 * @param output The output to send to client
	 */
	public abstract void sendOutput(String output);
	
	/**
	 * Process game info
	 * @param info process the info
	 */
	public abstract void processInfo(String info);

}
