
public abstract class Controller {
	Player player;
	DODServer server;
	int id;
	String input = "";
	
	
	
	public Controller(DODServer dodServer, int id, Player player) {
		this.server = dodServer;
		this.id = id;
		this.player = player;
	}
	public abstract String getInput();
	
	
	public abstract void sendOutput(String output);
	public synchronized void setInput(String string) {
		input = string;
	}

}
