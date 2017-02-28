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
		gamegui = new GameGUI("DUNGEON OF DOOM",false);
		console  = new Console(gamegui);
		gamegui.console = console;
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
		//TODO: game gui was null here once havent reproduced it yet
		gamegui.setVisible(true);
	}

	@Override
	public void print(int id,String string) {
		Color color = Color.black;
		if(id >= 0){
			for(LobbyPlayer player:lobbyPlayers){
				if(id == player.id){
					color = player.color;
					string="["+id+"]"+player.name+":"+string;
					System.out.println(string);
				}
			}
		}
		if(lobbygui != null){
			lobbygui.addMessageToChat(string, color);
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
