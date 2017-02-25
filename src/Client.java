import java.io.OutputStream;
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
	List<LobbyPlayer> players = new ArrayList<LobbyPlayer>();
	
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
		send("<ID></ID>");
		run();
	}
	
	public abstract void run();
	
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
			ClientReadThread readThread = new ClientReadThread(socket,this);
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
		Element e = Parser.parse(line);
		if(e.tag.equals("ID")){
			if(e.isInt()){
				id = e.toInt();
			}else{
				//try request id again
				send("<ID></ID>");
			}
		}else if(e.tag.equals("LOBBYPLAYER")){
			updateLobby(e);
		}
		
	}
	
	private void updateLobby(Element e){
		int id = -1;
		for(Element child: e.children){
			if(child.tag.equals("ID") && child.isInt()){
				id = child.toInt();
			}
		}
		if(id == -1){
			System.out.println("Cannot update lobby with follwing element:");
			e.print(0);
			return;
		}
		LobbyPlayer player;
		
		for(LobbyPlayer iterator:players){
			if(iterator.id == id){
				player = iterator;
				break;
			}
		}
		player = new LobbyPlayer();
		players.add(player);
		e.toLobbyPlayer(player);
	}
}
