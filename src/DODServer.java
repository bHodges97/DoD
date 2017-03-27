import java.awt.Color;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import javax.swing.text.StyledDocument;


/**
 * Server that handles all the DOD client connections and gamelogic
 *
 */
public class DODServer {

	private ServerSocket serverSocket;
	private GameLogic game = null;
	private int connectionsCounter = 0;
	private boolean shouldGameStart = false;
   	private List<PrintWriter> writers = new ArrayList<PrintWriter>();
   	private List<Socket> sockets = new ArrayList<Socket>();
	private List<ServerReadThread> readThreads = new ArrayList<ServerReadThread>();
    private List<LobbyPlayer> lobbyPlayers = new ArrayList<LobbyPlayer>();
	private List<Controller> controllers = new ArrayList<Controller>();
	private PrintWriter chatlog;
	private String chatHistory;
	private Date date = new Date();
	private DateFormat longDateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm");
	private DateFormat shortDateFormat = new SimpleDateFormat("HH:mm");
	private int port = 0;
	private StyledDocument document = null;
	private volatile boolean allowConnections = true;
	private ServerGUI gui;
	
	/**
	 * Main method
	 * @param args port number
	 */
    public static void main(String[] args) {
    	
    	args = new String[]{"41583"};
    	int port = 0;
    	if(args.length == 1){
    		try{
    		port = Integer.valueOf(args[0]);
    		if(port > 65535 || port < 0){
    			throw new NumberFormatException();
    		}
    		}catch(NumberFormatException e){
    			System.out.println(args[0]+" is not a valid port number!");
    			System.exit(1);
    		}
    	}else{
    		System.out.println("Please specify a port number. Defaulting.");
    	}
    	DODServer server = new DODServer(port);	
    }
    
    /**
     * Constructor Make a new DODServer
     * @param port The port number to host it on
     */
    public DODServer(int port){
    	this.port = port;
    	game = new GameLogic();
    	gui = new ServerGUI(this);
    	document = gui.getDocument();
    	//try make game server
		try {
			serverSocket = new ServerSocket(port);
			System.out.println("Server hosted on port:"+serverSocket.getLocalPort());
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
		//try make log file
		try {
			FileWriter fileWriter = new FileWriter("log.txt",true); 
		    chatlog = new PrintWriter(new BufferedWriter(fileWriter),true);	
		    chatHistory = "<"+longDateFormat.format(date)+">"+" NEW GAME+\n";
		    chatlog.println(chatHistory);
		} catch (IOException e) {
			e.printStackTrace();
		} 
		//start the server
		new Thread(){
			@Override
			public void run() {
				acceptConnections();
			}
		}.run();
		
		//check if game started
		while(shouldGameStart){
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
    }
    
    public List<LobbyPlayer> getLobbyPlayers() {
		return lobbyPlayers;
	}
    
    /**
     * Send message to client
     * @param message The message to send
     * @param id The id of the client to send it to
     */
	public synchronized void send(String message,int id){
		if(id < 0 || sockets.isEmpty() || sockets.get(id) == null){
			return;
		}
		message = Parser.convertFromMultiLine(message);
	    PrintWriter writer = writers.get(id);
	    writer.println(message);
	    writer.flush();
	}
    
	/**
	 * Send a message to all cients.
	 * @param message The message to send
	 */
    public synchronized void sendToAll(String message){
    	for(int i = 0;i < connectionsCounter;++i){
    		send(message,i);
    	}
    }   
    
    /**
     * Process client input
     * @param input The client input
     * @param id The client id
     */
    public synchronized void processInput(String input,int id){
    	if(input.equals("DISCONNECT")){
    		close(id,"DISCONNECTED");
    		return;
    	}
    	Element message = Parser.parse(input);
    	if(message == null){
    		println("Invalid Message from" + id);
    		return;
    	}
    	if(message.tag.equals("INPUT")){
    		//log client input
			chatlog.print("<"+shortDateFormat.format(date)+">");
			String log = "["+id+"]"+lobbyPlayers.get(id).name+":"+message.value;
			chatlog.println(log);
			chatHistory+="<"+shortDateFormat.format(date)+">"+log;
			println(log);
			
			//if its a chat command
			if(message.value.startsWith("SHOUT ") || message.value.startsWith("WHISPER ")){
				handleShout(message.value, id);
				return;
			}				
    	}
		
    	handleInput(message,id);
    	
    }
    
    protected GameLogic getGameLogic(){
    	return game;
    }
    
    /**
     * Handle a players shout/whisper
     * @param message The players message
     * @param id The players id
     */
    private void handleShout(String message,int id){
    	//break message up into sender,text,target
    	String[] components = Parser.splitMessageToComponents(message);
		String output =  "<MESSAGE><FROM>"+id+"</FROM><CONTENT>"+components[1]+"</CONTENT>";
		if(components[0].equals("WHISPER")){
			//if its a whisper, find the target player
			int target = -1;
			if(components[2].matches("\\d+")){
				target = Integer.parseInt(components[2]);
			}else{
				for(LobbyPlayer player:lobbyPlayers){
					if(player.name.equals(components[2])){
						target = player.id;
						break;
					}
				}
			}
			//if target is send himself
			if(target == id){
				send("<OUTPUT>Note to self:"+components[1]+"</OUTPUT>",id);
				return;
			//otherwise send to both target and sender
			}else if(target > -1 && target < connectionsCounter){
				output+="<TO>"+target+"</TO></MESSAGE>";
				send(output,id);
				send(output,target);					
			}else{
				send("<OUTPUT>Can not be found:"+components[2]+"</OUTPUT>",id);
			}						
		//send shout message
		}else if(components[0].equals("SHOUT")){
			output+="</MESSAGE>";
			sendToAll(output);
		}
		return;
    }

    /**
     * Handle message=
     * @param message The message to process
     * @param id sender id
     */
	private void handleInput(Element message, int id) {
		String tag = message.tag;
		String value = message.value;
		LobbyPlayer player = lobbyPlayers.get(id);
		if(tag.equals("GAMESTART")){
			tryStartGame();
		}else if(tag.equals("LOBBYPLAYER")){
			message.toLobbyPlayer(player);
			//just making sure client doesn't get to change their id.
			player.id = id;
			informClients();
		}else if(tag.equals("GETID")){
			send("<ID>"+id+"</ID>",id);
			informClients();		
		}else if(tag.equals("INPUT")){
    		controllers.get(id).input = (value); 
    	}else if(tag.equals("OUTPUT") || tag.equals("INFO")){
    		send(message+"", id);
    	}else {
			System.out.println("Unrecognised game command");
			send("<OUTPUT>Unrecognised game command type \"HELP\" for avaliable commands</OUTPUT>",id);
			message.print(0);
			return;
		}
	}
	
	/**
	 * Update clients on connected player
	 */
    private void informClients() {
    	println("");
    	println(lobbyPlayers.size()+" players currently connected:");
    	String msg = "<LOBBY>";
    	for(LobbyPlayer player:lobbyPlayers){
    		println("---"+player.toString());
    		msg+=player.getInfo();
    	}   	
		sendToAll(msg+"</LOBBY>");
	}
    
    /**
     * Check if all players are ready to start game
     * @return false if a player is connected and not ready
     */
    private boolean checkAllPlayersReady(){
		for(LobbyPlayer connectedPlayer: lobbyPlayers){
			if(!connectedPlayer.ready && connectedPlayer.connected){
				return false;
			}
		}
		return true;
    }

    
    /**
     * Try to start a game of Dungeon of Doom
     */
	private void tryStartGame(){
		if(!checkAllPlayersReady()){
			return;
		}
		//convert each connected player to a gamelogic player
		for(LobbyPlayer lobbyPlayer : lobbyPlayers){
			addPlayerToGame(lobbyPlayer);
		}
		//starting game
		println("Game has begun, notifying all players.");
		sendToAll("<GAMESTART></GAMESTART>");
		gui.allowConnections(false);
		new Thread("Game Logic Thread"){
			@Override
			public void run(){
				game.startGame();
			}
		}.start();
    }
	
	/**
	 * Accept client connections
	 */
    private void acceptConnections(){
    	println("Looking for connections");
    	while(true){
			try{
				Socket clientSocket = serverSocket.accept();
				if(!allowConnections){
					println("A client tried to connect when not listening");
					clientSocket.getOutputStream().write("DISCONNECT".getBytes());
					clientSocket.close();
				}else{
					//set up lobbyplayer for client
					LobbyPlayer player = new LobbyPlayer(connectionsCounter);
					lobbyPlayers.add(player);
					player.id = connectionsCounter;
					System.out.println("Client "+connectionsCounter+" connected.");
					ServerReadThread thread = new ServerReadThread(clientSocket, this, connectionsCounter);
					thread.start();
					OutputStream out = clientSocket.getOutputStream();
					writers.add(new PrintWriter(out));
					sockets.add(clientSocket);
					readThreads.add(thread);
					++connectionsCounter;
					informClients();
					if(getGameLogic().getGameState() == GameLogic.GameState.RUNNING){
						addPlayerToGame(player);
						send("<GAMESTART></GAMESTART>", player.id);
					}
				}
			}catch(Exception e){
				e.printStackTrace();
			}
		}
    }
	/**
	 * Close a socket
	 * @param id The id to close
	 */
	void close(int id,String reason){
		String message = id +"("+lobbyPlayers.get(id).name+") disconnected: " + reason;
		try {
			if(sockets.get(id)==null){
				//already disconnected
				return;
			}
			sockets.get(id).close();
			sockets.set(id,null);
			println(message);
			lobbyPlayers.get(id).connected = false;
		} catch (IOException e) {
			e.printStackTrace();
		}
		sendToAll("<OUTPUT>"+message+"</OUTPUT>");
		sendToAll("<DISCONNECT>"+id+"</DISCONNECT>");
	}

	public void saveLogTo(File saveFile) {
		try{
			if(!saveFile.exists()){
				saveFile.createNewFile();
			}
			PrintWriter write = new PrintWriter(saveFile);
			write.print(chatHistory);
			write.flush();
			write.close();
		}catch(IOException e){
			JOptionPane.showMessageDialog(null, "File save failed");
		}
		
	}

	public int getPort() {
		return port; 
	}
	
	/**
	 * Print string to console
	 * @param string The string to print
	 * @param color The color to print as
	 */
	public void println(String string,Color color)    {	
		if(document == null){
			System.out.println("Styled Document is not loaded!");
			return;
		}
		StyleContext styleContext = StyleContext.getDefaultStyleContext();
	    AttributeSet attributeSet = styleContext.addAttribute(SimpleAttributeSet.EMPTY,StyleConstants.Foreground, color);
		try {
			document.insertString(document.getLength(), string+"\n", attributeSet);
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
		System.out.println(string);
        
    }
	
	public void println(String string){
		println(string,Color.BLACK);
	}

	public void allowConnections(boolean allowConnections) {
		this.allowConnections = allowConnections;
		if(allowConnections){
			println("Looking for connections...");
		}else{
			println("Stopped looking for connections.");
		}
	}
	
	private void addPlayerToGame(LobbyPlayer lobbyPlayer){
		if(lobbyPlayer == null || !lobbyPlayer.connected ){
			println("Player not connected? -tryStartGame() DODSERVER");
			return;
		}
		Player player = lobbyPlayer.toPlayer();
		player.gameLogic = game;
		int id = player.id;
		if(lobbyPlayer.isBot){
			player.controller = new ControllerBot(this,id,player);
		}else{
			player.controller = new ControllerHuman(this,id,player);
		}
		controllers.add(player.controller);	
		game.addPlayer(player);
	}
}
