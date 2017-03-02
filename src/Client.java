import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;


/**
 * Client used to connect to a server.
 */
public abstract class Client {
	
	private int port = -1;
	private boolean clientReady = false;
	private PrintWriter writer;
	private Socket socket;
	protected int id;
	protected List<LobbyPlayer> lobbyPlayers = new ArrayList<LobbyPlayer>();
	protected LobbyPlayer clientPlayer = new LobbyPlayer(0);
	private volatile boolean gameStarted = false;
	
	/**
	 * Constructor
	 * @param args Command line arguments to process
	 */
	public Client(String[] args){
		//check if parameters are valid
		if(!validateArgs(args)){
			return;
		}
		//check if connections can be made.
		if(!tryConnect(args[0],port)){
			System.out.println("Failed to connect to "+args[0]+" : "+args[1]);
			return;
		}else{
			System.out.println("Successfully connected!");
		}
		send("<GETID></GETID>");
		updateLobbyInfo();
		//start
		run();
	}
	
	/**
	 * Start the client processes.
	 */
	public abstract void run();
	
	/**
	 * Print the message to the client's display
	 * @param message The message to print
	 */
	public abstract void print(Element message);
	
	/**
	 * Update list of other connected players
	 */
	protected void updateLobbyInfo(){
    	System.out.println(lobbyPlayers.size()+" players currently connected:");
    	for(LobbyPlayer player:lobbyPlayers){
    		if(id == player.id){
    			clientPlayer = player;
    			System.out.print("you|");
    		}else{
    			System.out.print("---|");
    		}
    		System.out.println(player.toString());
    	}
	}
	
	/**
	 * <p>Check if the given arguments matches the expected format.</p>
	 * <p>Expects String array size 2 where <b>args[0]</b> is a String and <b>args[1]</b> is a valid integer port number
	 * @param args The argument given to the program
	 * @return
	 */
	protected boolean validateArgs(String[] args){ 
		if(args.length == 2){	
			try{
				port = Integer.valueOf(args[1]);
				if(port < 0 || port > 65535){
					throw new NumberFormatException();
				}
			}catch (NumberFormatException e) {
				System.out.println(args[1]+ " is not a valid port number.");
				return false;
			}
			
		}else{
			System.out.println("Expected parameters: hostname portnumber");
			return false;
		}
		return true;
	}
	
	/**
	 * Attempt to connect to the given host and port
	 * @param hostName The host name to connect to
	 * @param portNumber The port number to connect to
	 * @return <b>true</b> if connection is successful
	 */
	private boolean tryConnect(String hostName, int portNumber){
		try {
			socket = new Socket(hostName,portNumber);
			if(!socket.isConnected()){
				return false;
			}
			ClientReadThread readThread = new ClientReadThread(socket.getInputStream(),this);
			readThread.start();
			writer = new PrintWriter(socket.getOutputStream());
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} 
		return true;
	}
	
	/**
	 * Send string to server
	 * @param string The string to send
	 */
	protected void send(String string){
		writer.println(string);
		writer.flush();
	}

	/**
	 * Process input
	 * @param line The line to process
	 */
	protected synchronized void processInput(String line) {
		Element element = Parser.parse(line);
		if(element == null){
			return;
		}
		String tag = element.tag;
		String value = element.value;
		if(tag.equals("ID")){
			if(element.isInt()){
				id = element.toInt();
			}else{
				//try request id again
				send("<GETID></GETID>");
			}
		}else if(tag.equals("LOBBY")){
			//since children is type Set the updates are not in order
			//so lobbyplayers aren't displayed in order either
			for(Element child:element.children){
				updateLobby(child);
			}
			updateLobbyInfo();
		}else if(tag.equals("MESSAGE")){
			print(element);
		}else if(tag.equals("GAMESTART")){
			gameStarted = true;
			print(Parser.makeMessage(-1,"Dungeon of DOOM has started"));
			startGameAction();
		}else if(tag.equals("OUTPUT")){
			print(Parser.makeMessage(-1,Parser.convertToMultiLine(value)));
		}
	}
	
	/**
	 * Update the stored information about given player
	 * @param lobbyPlayer The player to update.
	 */
	protected void updateLobby(Element lobbyPlayer){
		int playerID = -1;
		
		for(Element child: lobbyPlayer.children){
			if(child.tag.equals("ID") && child.isInt()){
				playerID = child.toInt();
			}
		}
		//lobbyPlayer has not ID or a negative id.
		if(playerID == -1){
			System.out.println("Cannot update lobby with follwing element:");
			lobbyPlayer.print(0);
			return;
		}
		//check if player is already stored, otherwise add to list
		for(LobbyPlayer known:lobbyPlayers){
			if(known.id == playerID){
				lobbyPlayer.toLobbyPlayer(known);
				return;
			}
		}
		LobbyPlayer player = new LobbyPlayer(playerID);
		lobbyPlayers.add(player);
		lobbyPlayer.toLobbyPlayer(player);
		print(Parser.makeMessage(-1,"Player "+player.id+" joined."));
	}
	
	/**
	 * Read input from client
	 * @return String the client's input
	 */
	protected String readFromConsole(){
		BufferedReader in;
		try {
			in = new BufferedReader(new InputStreamReader(System.in));
			String line;
			while((line = in.readLine()) != null){
				return line;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "";
	}
	
	/**
	 * Process the client's input
	 * @param input The client's input
	 */
	protected synchronized void processUserInput(String input) {
		if(input.isEmpty()){
			return;
		}else if(input.equals("READY")){
			clientReady = !clientReady;
			send("<LOBBYPLAYER><READY>"+clientReady+"</READY></LOBBYPLAYER>");
		}else if(input.equals("START")){
			send("<GAMESTART></GAMESTART>");
		}else if(input.startsWith("NAME")){
			send("<LOBBYPLAYER><NAME>"+Parser.sanitise(input.split("NAME ")[1])+"</NAME></LOBBYPLAYER>");
		}else if(input.startsWith("QUIT")){
			send("<EXIT></EXIT>");//TODO:
			System.exit(0);
		}else if(input.equals("HELP")){
			processInput(Parser.makeHelpMessage());
		}else{
			input = Parser.sanitise(input);
			send("<INPUT>"+input+"</INPUT>");
		}
	}
	
	/**
	 * what to process when the lobby ends and the game begins.
	 */
	protected abstract void startGameAction();
	
}
