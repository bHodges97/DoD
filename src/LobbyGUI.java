import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.JToggleButton;
import javax.swing.border.Border;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import javax.swing.text.StyledDocument;

public class LobbyGUI extends JFrame{
	JPanel connectionsPanel = new JPanel(new BorderLayout());
	JPanel sidePanel = new JPanel(new BorderLayout());
	JPanel playerSettingsPanel = new JPanel();
	JScrollPane scrollPane = new JScrollPane();
	JPanel controls = new JPanel(new FlowLayout());
	JTextPane chatPane = new JTextPane();
	JTextField chatField = new JTextField(25);
	JButton nameButton = new JButton("NAME");
	JToggleButton readyButton = new JToggleButton("READY");
	JButton sendButton = new JButton("SEND");
	JPanel chatPanel = new JPanel(new BorderLayout());
	JButton startButton = new JButton("START GAME");
	JButton colorButton = new JButton("COLOR");
	Client client;
	
	public static void main(String[] args){
		new LobbyGUI(null);
	}
	
	public LobbyGUI(Client client){
		this.client = client;
		setTitle("Dungeon of Doom: Game Lobby");
		JPanel southPanel = new JPanel(new BorderLayout());
		add(connectionsPanel,BorderLayout.CENTER);
		add(southPanel,BorderLayout.SOUTH);
		southPanel.add(controls,BorderLayout.NORTH);
		southPanel.add(chatPanel,BorderLayout.CENTER);
		
		connectionsPanel.add(scrollPane);	
		
		//Set up chat room
		chatPanel.add(new JScrollPane(chatPane),BorderLayout.CENTER);
		JPanel sendmsgPanel = new JPanel();
		chatPanel.add(sendmsgPanel,BorderLayout.SOUTH);
		sendmsgPanel.add(chatField);
		sendmsgPanel.add(sendButton);
		sendButton.setPreferredSize(new Dimension(75,18));
		//Set up controls
		controls.add(new JLabel("OPTIONS",JLabel.LEFT));
		controls.add(nameButton);
		controls.add(colorButton);
		controls.add(readyButton);
		controls.add(startButton);

		startButton.setEnabled(false);
		colorButton.setPreferredSize(new Dimension(90, 18));
		nameButton.setPreferredSize(new Dimension(90, 18));
		readyButton.setPreferredSize(new Dimension(90, 18));
		startButton.setPreferredSize(new Dimension(120, 18));
		
		addActionListeners();
		chatPane.setText("Dungeon Of Doom Lobby Chat Room");
		chatPane.setEditable(false);
		chatPane.setPreferredSize(new Dimension(200,150));
		scrollPane.setPreferredSize(new Dimension(200, 300));
		
		setResizable(false);
		setVisible(true);
		pack();
		setDefaultCloseOperation(EXIT_ON_CLOSE);
	}
	
	public void addMessageToChat(String string,Color color)
    {	
		StyledDocument document = chatPane.getStyledDocument();
		StyleContext sc = StyleContext.getDefaultStyleContext();
	    AttributeSet aset = sc.addAttribute(SimpleAttributeSet.EMPTY,
	                                        StyleConstants.Foreground, color);
	   
		try {
			document.insertString(document.getLength(), "\n"+string, aset);
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
        
    }
	
	private void addActionListeners(){
		startButton.addActionListener(new ActionListener() {			
			@Override
			public void actionPerformed(ActionEvent e) {
				LobbyGUI.this.client.processUserInput("START");
				
			}
		});
		readyButton.addActionListener(new ActionListener() {			
			@Override
			public void actionPerformed(ActionEvent e) {
				LobbyGUI.this.client.processUserInput("READY");				
			}
		});
		nameButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String output = JOptionPane.showInputDialog(LobbyGUI.this,"Enter a new name");
				if(output != null && !output.isEmpty()){
					LobbyGUI.this.client.processUserInput("NAME "+ output);
				}
				
			}
		});
		colorButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Color newColor = JColorChooser.showDialog(
	                     LobbyGUI.this,
	                     "Choose your color",
	                   	 client.clientPlayer.color);
				if(newColor != null){
					LobbyGUI.this.client.send("<LOBBYPLAYER><COLOR>"+newColor.getRGB()+"</COLOR></LOBBYPLAYER>");
				}
			}
		});
		ActionListener sendMessageAction = new ActionListener() {			
			@Override
			public void actionPerformed(ActionEvent e) {
				String input = LobbyGUI.this.chatField.getText();
				LobbyGUI.this.client.processUserInput("SHOUT "+ input);
				chatField.setText("");
			}
		};
		chatField.setText("");
		sendButton.addActionListener(sendMessageAction);
		chatField.addActionListener(sendMessageAction);
	}
}
