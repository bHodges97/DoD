import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class ServerReadThread extends Thread{
	
	private Socket socket;
	private DODServer server;
	private int id;
	
	public ServerReadThread(Socket socket,DODServer server,int id){
		this.socket = socket;
		this.server = server;
		this.id = id;
	}
	
	@Override
	public void run(){
		BufferedReader in;
		try {
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			String line;
			while((line = in.readLine()) != null){
				server.processInput(line,id);
			}
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
	}
	
	
}
