import java.awt.Color;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import javax.swing.text.StyledDocument;

public class HumanClient extends Client{
	
	private LobbyGUI lobbygui;
	private GameGUI gamegui;
	private StyledDocument document;
	
	public static void main(String[] args) {
		HumanClient client = new HumanClient(args);		
	}
	
	public HumanClient(String[] args){
		super(args);
	}
	
	@Override
	public void run(){
		//Initialise the gui
		lobbygui = new LobbyGUI(this);
		document = lobbygui.getStyledDucment();
		lobbygui.setStyledDocument(document);
		gamegui = new GameGUI("DUNGEON OF DOOM",false,this);
		gamegui.setStyledDocument(document);
		println("Dungeon of Doom chat room", Color.gray);
		println("Type HELP for list of commands", Color.gray);
		lobbygui.updateInfo();
		
		//Read inputs from console and gui
		BufferedReader buffer= new BufferedReader(new InputStreamReader(System.in));
		while(true){		
			String input = "";
			try {	
				 while(input == null || input.isEmpty()){				
					input = gamegui.getInput();
					if(buffer.ready()){
						input=buffer.readLine();
						//fromGui = false
					}
					try {
						Thread.sleep(1);//wait for gui to update
						} catch (InterruptedException e) {
						e.printStackTrace();
					}
				 }				
			} catch (IOException e) {
				e.printStackTrace();
				input = "";
			}
			processUserInput(Parser.sanitise(input));			
			
		}
	}
	
	@Override
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
		//Convert message into SHOUT/WHISPER/general style
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
			//load names and colors
			for(LobbyPlayer player:lobbyPlayers){
				if(from == player.id){
					color = player.color;
					fromName = player.name;
					break;
				}
			}
		}
		if(to >= 0){
			//output different messages based on player id
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
			//make shout message
			output = "["+id+"]"+fromName+":"+content;
		}else{
			output = content;
		}
		System.out.println(output);
		//print to console
		println(output,color);
	}

	@Override
	public void updateLobbyInfo() {
		super.updateLobbyInfo();
		if(lobbygui == null){
			return;
		}		
		lobbygui.updateInfo();
		//Enable start button if all players are ready
		for(LobbyPlayer player:lobbyPlayers){
			if(!player.ready){
				lobbygui.startButton.setEnabled(false);
				return;
			}
		}
		lobbygui.startButton.setEnabled(true);
	}
	

	/**
	 * Print string to console
	 * @param string The string to print
	 * @param color The color to print as
	 */
	public void println(String string,Color color)    {	
		if(document == null){
			System.out.println("Styled Document is not loaded!");
			return;
		}
		StyleContext styleContext = StyleContext.getDefaultStyleContext();
	    AttributeSet attributeSet = styleContext.addAttribute(SimpleAttributeSet.EMPTY,StyleConstants.Foreground, color);
		try {
			document.insertString(document.getLength(), string+"\n", attributeSet);
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
        
    }
	
}
