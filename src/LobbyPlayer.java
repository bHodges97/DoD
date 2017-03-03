import java.awt.Color;

/**
 * Class for storing client connection info
 *
 */
public class LobbyPlayer implements Messageable{
	public boolean ready = false;
	public boolean isBot = false;
	public String name = "New Player";
	public Color color = new Color(0, 0, 0, 0);//transparent i think havent tested it yet
	public int id;
	public boolean connected = true;
	
	public LobbyPlayer(int connectionsCounter) {
		name = "Player "+connectionsCounter;
	}
	
	/**
	 * Convert to gamelogic player
	 * @return new Player
	 */
	public Player toPlayer(){
		Player player;
		if(isBot){
			player = new BotPlayer();
		}else{
			player = new HumanPlayer();
		}
		if(!name.isEmpty()){
			player.name = name;
		}else{
			player.name = "Player "+id;
		}
		if(color != null){
			player.color = color;
		}
		player.id = id;
		return player;
	}

	@Override
	public String getInfo() {
		return  "<LOBBYPLAYER>"
					+ "<NAME>"+name+"</NAME>"
					+ "<ID>"+id+"</ID>"
					+ "<READY>"+ready+"</READY>"
					+ "<BOT>"+isBot+"</BOT>"
					+ "<COLOR>"+color.getRGB()+"</COLOR>"
					+ "<CONNECTED>"+connected+"</CONNECTED>"
				+"</LOBBYPLAYER>";
	}
	
	@Override
	public String toString(){
		return "ID: "+id+", Name: "+name+", Bot: "+isBot+", Ready: "+ready+", Connected: "+connected+".";
		
	}

}
