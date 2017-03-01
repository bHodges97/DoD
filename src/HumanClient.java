import java.awt.Color;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;

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
		lobbygui = new LobbyGUI(this);		
		console  = new Console(gamegui);
		gamegui = new GameGUI("DUNGEON OF DOOM",false,console);
		console.gui = gamegui;
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
		if(lobbygui != null){
			lobbygui.addMessageToChat(output, color);
		}
		if(console != null){
			gamegui.console.println(output);
		}
	}

	@Override
	public void updateLobbyInfo() {
		super.updateLobbyInfo();
		if(lobbygui == null){
			return;
		}
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
