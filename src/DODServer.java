import java.io.BufferedWriter;
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

public class DODServer {
	
	//TODO todo list
	//TODO handle pvp note: immortal bot, janken boy for player
	//TODO bot handler/huuman handler interligent path
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
	private  PrintWriter chatlog;
	private Date date = new Date();
	DateFormat longDateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm");
	DateFormat shortDateFormat = new SimpleDateFormat("HH:mm");
	
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

		try {
			FileWriter fileWriter = new FileWriter("log.txt",true); 
		    chatlog = new PrintWriter(new BufferedWriter(fileWriter),true);	
		    chatlog.println("<"+longDateFormat.format(date)+">"+" NEW GAME");
		} catch (IOException e) {
			e.printStackTrace();
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
    

	public synchronized void send(String input,int id){
		if(id < 0 || sockets.isEmpty() || sockets.get(id) == null){
			return;
		}
		input = Parser.convertFromMultiLine(input);
	    PrintWriter writer = writers.get(id);
	    writer.println(input);
	    writer.flush();
	}
    
    public synchronized void sendToAll(String input){
    	for(int i = 0;i < connectionsCounter;++i){
    		send(input,i);
    	}
    }
    
    


    
    public synchronized void processInput(String input,int id){
    	Element message = Parser.parse(input);
    	if(message == null){
    		System.out.println("Invalid Message from" + id);
    		return;
    	}
    	if(message.tag.equals("INPUT")){
			chatlog.print("<"+shortDateFormat.format(date)+">");
			String log = "["+id+"]"+lobbyPlayers.get(id).name+":"+message.value;
			chatlog.println(log);
			System.out.println(log);
			if(message.value.startsWith("SHOUT ") || message.value.startsWith("WHISPER ")){
				String[] components = Parser.splitMessageToComponents(message.value);
				String output =  "<MESSAGE><FROM>"+id+"</FROM><CONTENT>"+components[1]+"</CONTENT>";
				if(components[0].equals("WHISPER")){
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
					if(target == id){
						send("<OUTPUT>Note to self:"+components[1]+"</OUTPUT>",id);
						return;
					}else if(target > -1 && target < connectionsCounter){
						output+="<TO>"+target+"</TO></MESSAGE>";
						send(output,id);
						send(output,target);					
					}else{
						send("<OUTPUT>Can not be found:"+components[2]+"</OUTPUT>",id);
					}						
				}else if(components[0].equals("SHOUT")){
					output+="</MESSAGE>";
					sendToAll(output);
				}
				return;
			}
    	}    	
    	if(game.getGameState() ==  GameLogic.GameState.NOTSTARTED){
    		handlePreGame(message,id);
    	}else{
    		handleDuringGame(message,id);
    	}
    }
    private void handleDuringGame(Element message,int id) {
    	String tag = message.tag;
    	String value = message.value;
    	if(tag.equals("INPUT")){
    		controllers.get(id).input = (value); 
    	}else if(tag.equals("OUTPUT")){
    		send("<OUTPUT>"+message.value+"</OUTPUT>", id);
    	}else{
    		System.out.println("Unrecognised game command");
			send("<OUTPUT>Unrecognised game command type \"HELP\" for avaliable commands</OUTPUT>",id);
    		message.print(0);
    	}
    }
    
	private void handlePreGame(Element message, int id) {
		String tag = message.tag;
		String value = message.value;
		LobbyPlayer player = lobbyPlayers.get(id);
		if(tag.equals("GAMESTART")){
			tryStartGame();
		}else if(tag.equals("LOBBYPLAYER")){
			message.toLobbyPlayer(player);
			//just making sure client doesn't get to change their id.
			player.id = id;
		}else if(tag.equals("GETID")){
			send("<ID>"+id+"</ID>",id);		
		}else{
			System.out.println("Unrecognised lobby command");
			send("<OUTPUT>Unrecognised game command type \"HELP\" for avaliable commands</OUTPUT>",id);
			message.print(0);
			return;
		}
		
		informClients();
	}
    private void informClients() {
    	System.out.println();
    	System.out.println(lobbyPlayers.size()+" players currently connected:");
    	String msg = "<LOBBY>";
    	for(LobbyPlayer player:lobbyPlayers){
    		System.out.println("---"+player.toString());
    		msg+=player.getInfo();
    	}   	
		sendToAll(msg+"</LOBBY>");
	}
    
    private boolean checkAllPlayersReady(){
		for(LobbyPlayer connectedPlayer: lobbyPlayers){
			if(!connectedPlayer.ready && connectedPlayer.connected){
				return false;
			}
		}
		return true;
    }

	private void tryStartGame(){
		if(!checkAllPlayersReady()){
			return;
		}
		for(LobbyPlayer lobbyPlayer : lobbyPlayers){
			if(lobbyPlayer == null || !lobbyPlayer.connected || !lobbyPlayer.ready){
				System.out.println("this is not good");
				continue;
			}
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
		}
		System.out.println("Game has begun, notifying all players.");
		sendToAll("<GAMESTART></GAMESTART>");
		new Thread("Game Logic Thread"){
			@Override
			public void run(){
				game.startGame();
			}
		}.start();
    }
	
    private void acceptConnections(){
    	System.out.println("Looking for connections");
    	while(game.getGameState() == GameLogic.GameState.NOTSTARTED){
			try{
				Socket clientSocket = serverSocket.accept();
				if(game.getGameState() != GameLogic.GameState.NOTSTARTED){
					System.out.println("Slow way of closing");
					clientSocket.close();
					break;
				}				
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
			}catch(Exception e){
				e.printStackTrace();
			}
		}
    	//TODO: can close blocking socket with close experiment later
    }
	
	void close(int id){
		try {
			sockets.get(id).close();
			sockets.set(id,null);
			System.out.println(id + "lost connection");
			lobbyPlayers.get(id).connected = false;
		} catch (IOException e) {
			e.printStackTrace();
		}
		for(Socket socket:sockets){
			if(socket != null){
				return;
			}
		}
		//no clients
		System.exit(0);
	}
}
