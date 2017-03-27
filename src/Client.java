import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.JOptionPane;


/**
 * Client used to connect to a server.
 */
public abstract class Client {
	
	
	protected List<LobbyPlayer> lobbyPlayers = new ArrayList<LobbyPlayer>();
	protected LobbyPlayer clientPlayer = new LobbyPlayer(0);	
	protected int port = -1;
	protected volatile GameLogic.GameState gameState = GameLogic.GameState.NOTSTARTED;
	protected Map gameMap = new Map();
	protected int id;
	private boolean clientReady = false;
	private PrintWriter writer;
	protected Socket socket;
	

	
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
	 * what to process when the lobby ends and the game begins.
	 */
	protected abstract void startGameAction();
	
	protected abstract boolean readyToStart();
	
	public void disconnect(){
		send("DISCONNECT");
	}
	
	protected synchronized void processInfo(Element message){
		for(LobbyPlayer player:lobbyPlayers){
			player.updated = false;
		}
		for(Element info:message.children){
			if(info.tag.equals("TILES")){
				//process map info
				for(Element child:info.children){
					Tile tile = child.toTile();
					if(gameMap.getTile(tile.pos) == null){
						gameMap.addTile(tile.getDisplayChar(),tile.pos.x,tile.pos.y);	
					}
				}
			}else if(info.tag.equals("HEIGHT")){
				gameMap.setHeight(info.toInt());
			}else if(info.tag.equals("WIDTH")){
				gameMap.setWidth(info.toInt());
			}else if(info.tag.equals("PLAYER")){
				//process player info
				Player player = info.toPlayer();
				LobbyPlayer lobbyPlayer = lobbyPlayers.get(player.id);
				lobbyPlayer.actualPos.set(Position.multiply(player.position, 64));
				lobbyPlayer.state = player.state;
				lobbyPlayer.inventory = player.inventory;
				if(!lobbyPlayer.visible){
					lobbyPlayer.screenPos.set(lobbyPlayer.actualPos);
				}
				lobbyPlayer.visible = true;
				lobbyPlayer.updated = true;
			}else if(info.tag.equals("GAMESTATE")){
				gameState = GameLogic.GameState.valueOf(info.value);
			}else if(info.tag.equals("DROPPED")){
				Set<DroppedItems> dropped = new HashSet<DroppedItems>();
				for(Element child:info.children){
					DroppedItems item = child.toDroppedItems();
					dropped.add(item);
				}
			gameMap.setDroppedItems(dropped);
			}else if(info.tag.equals("GOLD")){
				gameMap.setGoldRequired(info.toInt());
			}else if(info.tag.equals("DISCONNECT")){
				lobbyPlayers.get(info.toInt()).connected = false;
			}
		}
		for(LobbyPlayer player:lobbyPlayers){
			if(player.updated == false){
				player.visible = false;
			}
		}
	}
	
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
		if(line.equals("DISCONNECT")){
			JOptionPane.showMessageDialog(null, "CONNECTION REFUSED");
			System.exit(0);
		}
		Element element = Parser.parse(line);
		if(element == null){
			return;
		}
		String tag = element.tag;
		String value = element.value;
		if(tag.equals("ID")){
			if(element.isInt()){
				id = element.toInt();
				updateLobbyInfo();
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
			//waiting heavy operation so its on thread so other stuff is not blocked
			Thread startGameThread = new Thread(){
				@Override
				public void run(){
					if(clientPlayer.ready == false){
						JOptionPane.showMessageDialog(null, "Joining exisiting game!");
					}
					print(Parser.makeMessage(-1,"Dungeon of DOOM has started"));
					while(!readyToStart()){
						try {
							Thread.sleep(500);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
					startGameAction();					
				}
			};
			startGameThread.start();
		}else if(tag.equals("OUTPUT")){
			print(Parser.makeMessage(-1,Parser.convertToMultiLine(value)));
		}else if(tag.equals("INFO")){
			processInfo(element);
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
		if(lobbyPlayers.size() <= playerID){
			LobbyPlayer player = new LobbyPlayer(playerID);
			print(Parser.makeMessage(-1,"New player joined.("+lobbyPlayers.size()+")"));
			lobbyPlayers.add(player);
			lobbyPlayer.toLobbyPlayer(player);
		}else{
			lobbyPlayer.toLobbyPlayer(lobbyPlayers.get(playerID));
		}
		if(id < lobbyPlayers.size()){
			clientPlayer = lobbyPlayers.get(id);
		}
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
			send("DISCONNECT");
			System.exit(0);
		}else if(input.equals("HELP")){
			processInput(Parser.makeHelpMessage());
		}else{
			input = Parser.sanitise(input);
			send("<INPUT>"+input+"</INPUT>");
		}
	}

	/**
	 * Attempt to connect to the given host and port
	 * @param hostName The host name to connect to
	 * @param portNumber The port number to connect to
	 * @return <b>true</b> if connection is successful
	 */
	protected boolean tryConnect(String hostName, int portNumber){
		try {
			socket = new Socket(hostName,portNumber);
			if(!socket.isConnected()){
				return false;
			}
			ClientReadThread readThread = new ClientReadThread(socket.getInputStream(),this);
			readThread.start();
			writer = new PrintWriter(socket.getOutputStream());
		} catch (Exception e) {
			System.err.println(e.getMessage());
			return false;
		} 
		return true;
	}
	
}
