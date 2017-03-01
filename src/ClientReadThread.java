import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;

public class ClientReadThread extends Thread {
	private InputStream stream;
	private Client client;
	
	public ClientReadThread(InputStream stream,Client client){
		this.stream = stream;
		this.client = client;
	}
	
	@Override
	public void run(){
		BufferedReader in;
		try {
			in = new BufferedReader(new InputStreamReader(stream));
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