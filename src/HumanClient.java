import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class HumanClient extends Client{
	
	List<LobbyPlayer> players = new ArrayList<LobbyPlayer>();
	
	public static void main(String[] args) {
		HumanClient client = new HumanClient(args);		
	}
	
	public HumanClient(String[] args){
		//super(args);
		//TODO: this method should just be super(args);
		super(new String[]{"localhost","38983"});
	}
	
	@Override
	public void run(){
		send("<LOBBYPLAYER><BOT>false</BOT><READY>false</READY></LOBBYPLAYER>");
		LobbyGUI gui = new LobbyGUI(this);
	}
	
}
