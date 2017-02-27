import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.text.html.HTML.Tag;

public class DODServer {
	
	//TODO todo list
	//TODO handle pvp note: immortal bot, janken boy for player
	//TODO bot handler/huuman handler interligent path
	//TODO tile triggered events
	//TODO rewrite ui intergration
	//TODO minute timer if all players ready.

	private ServerSocket serverSocket;
	private GameLogic game;
	private int connectionsCounter = 0;
	private boolean shouldGameStart = false;
   	private List<PrintWriter> writers = new ArrayList<PrintWriter>();
   	private List<Socket> sockets = new ArrayList<Socket>();
	private List<ServerReadThread> readThreads = new ArrayList<ServerReadThread>();
    private List<LobbyPlayer> lobbyPlayers = new ArrayList<LobbyPlayer>();
	private List<Controller> controllers = new ArrayList<Controller>();
	
    public static void main(String[] args) {
    	int port = 38983;//TODO:set this back to zero after testing. Java will generate a random valid port number if this is 0.
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
    
    public DODServer(int port){
    	game = new GameLogic();
		try {
			serverSocket = new ServerSocket(port);
			System.out.println("Server hosted on port:"+serverSocket.getLocalPort());
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
		new Thread(){
			@Override
			public void run() {
				acceptConnections();
			}
		}.run();
		while(shouldGameStart){
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
    }
    
    public synchronized void processInput(String input,int id){
    	Element message = Parser.parse(input);
    	if(message == null){
    		System.out.println("Invalid Message from" + id);
    		return;
    	}
    	if(game.getGameState() ==  GameState.NOTSTARTED){
    		handlePreGame(message,id);
    	}else{
    		handleDuringGame(message,id);
    	}
    }
    

	public synchronized void send(String input,int id){
		if(id < 0 || sockets.get(id) == null){
			return;
		}
	    PrintWriter writer = writers.get(id);
	    writer.println(input);
	    writer.flush();
	}
    
    public synchronized void sendToAll(String input){
    	for(int i = 0;i < connectionsCounter;++i){
    		send(input,i);
    	}
    }
    
    
    private void acceptConnections(){
    	System.out.println("Looking for connections");
    	while(game.getGameState() == GameState.NOTSTARTED){
			try{
				Socket clientSocket = serverSocket.accept();
				if(game.getGameState() != GameState.NOTSTARTED){
					System.out.println("Slow way of closing");
					clientSocket.close();
					break;
				}				
				
				LobbyPlayer player = new LobbyPlayer();
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
			}catch(Exception e){
				e.printStackTrace();
			}
		}
    	//TODO: can close blocking socket with close experiment later
    }
    
    private void handleDuringGame(Element message,int id) {
    	String tag = message.tag;
    	String value = message.value;
    	if(tag.equals("INPUT")){
    		controllers.get(id).setInput(value); 
    	}else if(tag.equals("OUTPUT")){
    		send("<OUTPUT>"+message.value+"</OUTPUT>", id);
    	}
	}

	private void handlePreGame(Element message, int id) {
		String tag = message.tag;
		String value = message.value;
		LobbyPlayer player = lobbyPlayers.get(id);
		if(tag.equals("START")){
			tryStartGame();
		}else if(tag.equals("LOBBYPLAYER")){
			message.toLobbyPlayer(player);
			//just making sure client doesn't get to change their id.
			player.id = id;
		}else if(tag.equals("GETID")){
			send("<ID>"+id+"</ID>",id);
		}else if(tag.equals("INPUT")){
			for(Element child:message.children){
				if(child.tag.equals("SHOUT")){
					sendToAll("<SHOUT><ID>"+id+"</ID><MESSAGE>"+child.value+"</MESSAGE></SHOUT>");
				}
			}
		}
		informCLients();
	}
    private void informCLients() {
    	System.out.println();
    	System.out.println(lobbyPlayers.size()+" players currently connected:");
    	String msg = "<LOBBY>";
    	for(LobbyPlayer player:lobbyPlayers){
    		System.out.println("---"+player.toString());
    		msg+=player.getFullInfo();
    	}   	
		sendToAll(msg+"</LOBBY>");
	}
    
    private boolean checkAllPlayersReady(){
		for(LobbyPlayer connectedPlayer: lobbyPlayers){
			if(!connectedPlayer.ready){
				return false;
			}
		}
		return true;
    }

	private void tryStartGame(){
		if(checkAllPlayersReady()){
			return;
		}
		for(LobbyPlayer lobbyPlayer : lobbyPlayers){
			Player player = lobbyPlayer.toPlayer();
			game.addPlayer(player);
			player.gameLogic = game;
			int id = player.id;
			if(lobbyPlayer.isBot){
				player.controller = new ControllerBot(this,id,player);
			}else{
				player.controller = new ControllerHuman(this,id,player);
			}
			controllers.add(player.controller);
			//TODO: add controller and gameLogic to player;
		}
		System.out.println("Game has begun, notifying all players.");
		sendToAll("<GAMESTART></GAMESTART>");
		game.startGame();
    }
	
	void close(int id){
		try {
			sockets.get(id).close();
			sockets.set(id,null);
			System.out.println(id + "lost connection");
			lobbyPlayers.set(id,null);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
