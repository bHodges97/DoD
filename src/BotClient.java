
/**
 * Bot client. This allows a new Bot to join the server.
 *
 */
public class BotClient extends Client {	
	
	public static void main(String[] args) {
		BotClient client = new BotClient(args);
	}
	
	public BotClient(String[] args){
		super(args);
	}

	@Override
	public void run(){
		System.out.println("New bot started");
		send("<LOBBYPLAYER><BOT>true</BOT><READY>true</READY></LOBBYPLAYER>");
	}


	@Override
	protected void startGameAction() {
		//Do nothing. Bot logic is done server side.
	}

	@Override
	public void print(Element message) {
		//message.print(0);
	}
	
}
