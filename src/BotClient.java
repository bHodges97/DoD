
public class BotClient extends Client {
	
	
	
	public static void main(String[] args) {
		BotClient client = new BotClient(args);
	}
	
	public BotClient(String[] args){
		super(args);
	}

	@Override
	public void run(){
		send("<LOBBYPLAYER><BOT>true/BOT><READY>true</READY></LOBBYPLAYER>");
		
	}


	@Override
	protected void startGameAction() {
		
	}

	@Override
	public void print(Element message) {
		// TODO Auto-generated method stub
		
	}
	
}
