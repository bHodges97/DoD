
/**
 * Bot client. This allows a new Bot to join the server.
 *
 */
public class BotClient extends Client {	
	
	public static void main(String[] args) {
		args = new String[]{"localhost","41583"};//TODO; delete me
		BotClient client = new BotClient(args);
	}
	
	public BotClient(String[] args){
		if(validateArgs(args) && tryConnect(args[0],port)){
			System.out.println("Successfully connected!");
		}else{
			System.out.println("Connection failed, exisiting");
			return;
		}
		send("<GETID></GETID>");
		run();
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

	@Override
	protected boolean readyToStart() {
		return true;
	}
	
}
