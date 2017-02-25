import java.awt.Color;

public class LobbyPlayer implements Messageable{
	public boolean ready = false;
	public boolean isBot = false;
	public String name = "";
	public Color color;
	public int id;
	
	
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
			player.name = "PLAYER "+id;
		}
		if(color != null){
			player.color = color;
		}
		player.id = id;
		return player;
	}


	@Override
	public String getSummary() {
		return "<LOBBYPLAYER><NAME>"+name+"</NAME><ID>"+id+"</ID></LOBBYPLAYER>";
	}


	@Override
	public String getFullInfo() {
		return  "<LOBBYPLAYER>"
					+ "<NAME>"+name+"</NAME>"
					+ "<ID>"+id+"</ID>"
					+ "<READY>"+ready+"</READY>"
					+ "<ISBOT>"+isBot+"</ISBOT>"
					+ "<COLOR>"+color.getRGB()+"</COLOR>"
				+"</LOBBYPLAYER>";
	}

}
