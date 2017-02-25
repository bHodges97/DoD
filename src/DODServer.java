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

public class DODServer {
	
	//TODO todo list
	//TODO handle pvp note: immortal bot, janken boy for player
	//TODO bot handler/huuman handler interligent path
	//TODO tile triggered events
	//TODO rewrite ui intergration

	ServerSocket serverSocket;
	GameLogic game;
	private int connectionsCounter = 0;
	private boolean shouldGameStart = false;
    List<PrintWriter> writers = new ArrayList<PrintWriter>();
	List<Socket> sockets = new ArrayList<Socket>();
	List<ServerReadThread> readThreads = new ArrayList<ServerReadThread>();
    List<LobbyPlayer> lobbyPlayers = new ArrayList<LobbyPlayer>();
	
	
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
    	System.out.println("Received input from " + id);
    	if(message == null){
    		return;
    	}
    	message.print(0);
    	if(game.getGameState() ==  GameState.NOTSTARTED){
    		handlePreGame(message,id);
    	}else{
    		handleDuringGame(message,id);
    	}
    }
    

	public synchronized void send(String input,int id){
    	PrintWriter writer = writers.get(id);
    	writer.println(input);
    	writer.flush();
    	
	}
    
    public synchronized void sendToAll(String input){
    	for(PrintWriter writer : writers){
    		writer.println(input);
    		writer.flush();
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
    	String name = message.tag;
    	
	}

	private void handlePreGame(Element message, int id) {
		String tag = message.tag;
		String value = message.value;
		LobbyPlayer player = lobbyPlayers.get(id);
		if(tag.equals("START")){
			tryStartGame();
		}else if(tag.equals("LOBBYPLAYER")){
			message.toLobbyPlayer(player);
		}else if(tag.equals("GETID")){
			send("<ID>"+id+"</ID>",id);
		}
		informCLients();
	}
    private void informCLients() {
    	for(LobbyPlayer player:lobbyPlayers){
    		sendToAll(player.getFullInfo());
    	}
		
	}

	private void tryStartGame(){
    	boolean ready = true;
		for(LobbyPlayer connectedPlayer: lobbyPlayers){
			if(!connectedPlayer.ready){
				ready = false;
				break;
			}
		}
		if(!ready){
			return;
		}
		for(LobbyPlayer lobbyPlayer : lobbyPlayers){
			Player player = lobbyPlayer.toPlayer();
			game.addPlayer(player);
			//TODO: add controller and gameLogic to player;
		}
		
    }
}
