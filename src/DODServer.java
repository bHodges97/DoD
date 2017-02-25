import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
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
    List<OutputStream> outputStreams = new ArrayList<OutputStream>();
    List<Player> players = new ArrayList<Player>();
	
	
    public static void main(String[] args) {
    	int port = 0;
    	if(args.length != 1){
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
    		port = 0;
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
    	char[] chars = input.toCharArray();
    	Stack<String> types = new Stack<String>();
    	Stack<String> contents = new Stack<String>();
    	String nameBuilder = "";
    	String contentBuilder = "";
    	int counter = 0;
    	boolean buildName = false;
    	boolean buildEnd = false;
    	for(int i = 0;i < chars.length;++i){
    		if(chars[i]== '<' && chars[i] != '\\'){
    			buildName = true;
    			if(!nameBuilder.isEmpty()){
    				types.add(nameBuilder);
    			}
    			if(!contentBuilder.isEmpty()){
    				contents.add(contentBuilder);
    			}
    			contentBuilder = "";
    			nameBuilder = "";
    		}else if(chars[i] == '>'){
    			buildName = false;
    			if(buildEnd){
    				String name = types.pop();
    				if(name.equals(nameBuilder)){
    					
    				}
    			}
    			types.add(nameBuilder);
    		}else if(chars[i]== '<' && chars[i] == '\\'){
    			++i;
    			buildName = true;
    			buildEnd = true;
    		}else if(buildName){
    			nameBuilder+=chars[i];
    		}else{
    			contentBuilder+=chars[i];
    		}
    	}
    	
    }
    
    private synchronized void parse(String name,String contents){
    	
    }
    
    public synchronized void send(String input,int id){
    	OutputStream out = outputStreams.get(id);
    	try(PrintWriter writer = new PrintWriter(out)){
			writer.println(input);
		}
    }
    
    public synchronized void sendToAll(String input){
    	for(OutputStream out : outputStreams){
    		try(PrintWriter writer = new PrintWriter(out)){
    			writer.println(input);
    		}
    	}
    }
    
    
    private void acceptConnections(){
    	System.out.println("Looking for connections");
    	while(game.getGameState() == GameState.NOTSTARTED){
			try{
				Socket clientSocket = serverSocket.accept();
				if(game.getGameState() != GameState.NOTSTARTED){
					clientSocket.close();
					break;
				}
				new ServerReadThread(clientSocket, this, connectionsCounter).run();
				OutputStream out = clientSocket.getOutputStream();
				outputStreams.add(out);
				++connectionsCounter;
			}catch(Exception e){
				e.printStackTrace();
			}
		}
    	//TODO: can close blocking socket with close experiment later
    }
    
}
