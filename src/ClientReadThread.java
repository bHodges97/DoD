import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class ClientReadThread extends Thread {
	private Socket socket;
	private Client client;
	
	public ClientReadThread(Socket socket,Client client){
		this.socket = socket;
		this.client = client;
	}
	
	@Override
	public void run(){
		BufferedReader in;
		try {
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			String line;
			while((line = in.readLine()) != null){
				client.processInput(line);
			}
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
	}
}
