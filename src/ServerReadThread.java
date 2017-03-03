import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.SocketException;

/**
 * Thread for reading client inputs to server
 */
public class ServerReadThread extends Thread{
	
	private Socket socket;
	private DODServer server;
	private int id;
	
	/**
	 * Constructor
	 * @param socket The socket to read from
	 * @param server the server this belongs to
	 * @param id The id of the socket
	 */
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
		} catch(SocketException e){
			server.close(id);
			return;
		}catch (IOException e) {
			e.printStackTrace();
			return;
		} 
	}
	
	
}
