import java.awt.Color;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;

public class HumanClient extends Client{
	
	LobbyGUI gui;
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
		gui = new LobbyGUI(this);
		while(true){
			String input = readFromConsole();
			processUserInput(input);
		}
	}

	@Override
	public void print(int id,String string) {
		Color color = Color.black;
		if(id >= 0){
			for(LobbyPlayer player:lobbyPlayers){
				if(id == player.id){
					color = player.color;
					string="["+id+"]"+player.name+":"+string;
				}
			}
		}
		if(gui != null){
			gui.addMessageToChat(string, color);
		}
	}

	@Override
	public void updateLobbyInfo() {
		super.updateLobbyInfo();
		if(gui == null){
			return;
		}
		for(LobbyPlayer player:lobbyPlayers){
			if(!player.ready){
				gui.startButton.setEnabled(false);
				return;
			}
		}
		gui.startButton.setEnabled(true);
		//TODO; and then added panels
	}
	
}
