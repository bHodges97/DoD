public class ClientBot extends Client {
	
	
	
	public static void main(String[] args) {
		ClientBot client = new ClientBot(args);
	}
	
	public ClientBot(String[] args){
		//super(args);
		//TODO: this method should just be super(args);
		super(new String[]{"localhost","38983"});
	}

	@Override
	public void run(){
		System.out.println("New bot started");
		send("<LOBBYPLAYER><BOT>true</BOT><READY>true</READY></LOBBYPLAYER>");

	}


	@Override
	protected void startGameAction() {
		
	}

	@Override
	public void print(Element message) {
		System.out.println(message.value);
	}
	
}
