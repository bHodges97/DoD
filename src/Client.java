import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public abstract class Client {
	
	int port = -1;
	boolean clientReady = false;
	PrintWriter writer;
	Socket socket;
	int id;
	List<LobbyPlayer> lobbyPlayers = new ArrayList<LobbyPlayer>();
	LobbyPlayer clientPlayer = new LobbyPlayer(0);
	volatile boolean gameStarted = false;
	
	
	public Client(String[] args){
		if(!validateParams(args)){
			return;
		}
		if(!tryConnect(args[0],port)){
			System.out.println("Failed to connect to "+args[0]+" : "+args[1]);
			return;
		}else{
			System.out.println("Successfully connected!");
		}
		send("<GETID></GETID>");
		run();
	}
	
	public abstract void run();
	public abstract void print(Element message);
	public void updateLobbyInfo(){
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
	
	protected boolean validateParams(String[] args){ 
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
	
	
	boolean tryConnect(String hostName, int portNumber){
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
	

	public void send(String string){
		writer.println(string);
		writer.flush();
	}

	


	public synchronized void processInput(String line) {
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
	
	protected void updateLobby(Element e){
		int playerID = -1;
		
		for(Element child: e.children){
			if(child.tag.equals("ID") && child.isInt()){
				playerID = child.toInt();
			}
		}
		if(playerID == -1){
			System.out.println("Cannot update lobby with follwing element:");
			e.print(0);
			return;
		}
		
		
		for(LobbyPlayer known:lobbyPlayers){
			if(known.id == playerID){
				e.toLobbyPlayer(known);
				return;
			}
		}
		LobbyPlayer player = new LobbyPlayer(playerID);
		lobbyPlayers.add(player);
		e.toLobbyPlayer(player);
		print(Parser.makeMessage(-1,"Player "+player.id+" joined."));
	}
	
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
	
	protected abstract void startGameAction();
	
}
