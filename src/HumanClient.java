import java.awt.Color;
import java.io.IOException;

public class HumanClient extends Client{
	
	LobbyGUI lobbygui;
	GameGUI gamegui;
	Console console;
	
	
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

		console  = new Console(gamegui);
		lobbygui = new LobbyGUI(this,console.getStyledDocument());	
		gamegui = new GameGUI("DUNGEON OF DOOM",false,console);
		console.gui = gamegui;
		console.println("Dungeon of Doom chat room", Color.gray);
		console.println("Type HELP for list of commands", Color.gray);
		lobbygui.updateInfo();
		while(true){
			String input = "";
			try {
				input = console.readln();
			} catch (IOException e) {
				e.printStackTrace();
			}
			processUserInput(Parser.sanitise(input));
		}
	}
	
	protected void startGameAction() {
		lobbygui.setEnabled(false);
		lobbygui.dispose();
		lobbygui = null;
		gamegui.setVisible(true);
	}

	@Override
	public void print(Element message) {
		Color color = Color.black;
		int from = -1,to = -1;
		String content = "",fromName = "",toName = "",output = "";
		
		for(Element part:message.children){
			if(part.tag.equals("FROM") && part.isInt()){
				from = part.toInt();
			}else if(part.tag.equals("TO") && part.isInt()){
				to = part.toInt();
			}else if(part.tag.equals("CONTENT")){
				content = part.value;
			}
		}
		if(from >= 0){
			for(LobbyPlayer player:lobbyPlayers){
				if(from == player.id){
					color = player.color;
					fromName = player.name;
					break;
				}
			}
		}
		if(to >= 0){
			for(LobbyPlayer player:lobbyPlayers){
				if(from == player.id){
					toName = player.name;
					output += "["+from+"]";
					if(from == id){
						output+= "You whispered to "+toName+":"+content;
					}else{
						output+= fromName + " whispered to you:"+content;
					}
					break;
				}
			}
		}else if(from >= 0){
			output = "["+id+"]"+fromName+":"+content;
		}else{
			output = content;
		}
		System.out.println(output);
		if(console != null){
			console.println(output,color);
		}
	}

	@Override
	public void updateLobbyInfo() {
		super.updateLobbyInfo();
		if(lobbygui == null){
			return;
		}		
		lobbygui.updateInfo();
		for(LobbyPlayer player:lobbyPlayers){
			if(!player.ready){
				lobbygui.startButton.setEnabled(false);
				return;
			}
		}
		lobbygui.startButton.setEnabled(true);
		
		//TODO; and then added panels
	}
	
}
