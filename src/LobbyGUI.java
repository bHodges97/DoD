import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;

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
import javax.swing.text.DefaultCaret;
import javax.swing.text.StyledDocument;

/**
 * GUI window for lobby connections
 *
 */
public class LobbyGUI extends JFrame{
	private JPanel connectionsPanel = new JPanel(new GridLayout(0,1,0,0));
	private JScrollPane scrollPane = new JScrollPane(connectionsPanel,JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
	private JPanel controls = new JPanel(new FlowLayout());
	private JTextPane chatPane = new JTextPane();
	private JTextField chatField = new JTextField(25);
	private JButton nameButton = new JButton("NAME");
	private JToggleButton readyButton = new JToggleButton("READY");
	private JButton sendButton = new JButton("SEND");
	private JPanel chatPanel = new JPanel(new BorderLayout());
	private JButton colorButton = new JButton("COLOR");
	private Client client;
	private java.util.Map<LobbyPlayer,JLabel> playerToLabelMap = new HashMap<LobbyPlayer,JLabel>();
	protected JButton startButton = new JButton("START GAME");
	
	/**
	 * Construct a new gui for the given client
	 * @param client
	 */
	public LobbyGUI(Client client){
		this.client = client;
		setTitle("Dungeon of Doom: Game Lobby");
		JPanel southPanel = new JPanel(new BorderLayout());
		add(scrollPane,BorderLayout.CENTER);
		add(southPanel,BorderLayout.SOUTH);
		southPanel.add(controls,BorderLayout.NORTH);
		southPanel.add(chatPanel,BorderLayout.CENTER);
		chatPane.setAutoscrolls(true);
		((DefaultCaret)chatPane.getCaret()).setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);;
		
		
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
		chatPane.setPreferredSize(new Dimension(200,150));
		scrollPane.setPreferredSize(new Dimension(200, 300));
		
		setResizable(false);
		setVisible(true);
		pack();
		setDefaultCloseOperation(EXIT_ON_CLOSE);
	}
	
	/**
	 * Update display for currently connected players
	 */
	public void updateInfo() {
		for(LobbyPlayer player:client.lobbyPlayers){
			if(playerToLabelMap.containsKey(player)){
				playerToLabelMap.get(player).setText(player.toString());
			}else{
				JLabel label = new JLabel(player.toString());
				connectionsPanel.add(label);
				playerToLabelMap.put(player, label);
			}
		}
		
	}
	
	/**
	 * Add action listeners to the buttons
	 */
	private void addActionListeners(){
		//start
		startButton.addActionListener(new ActionListener() {			
			@Override
			public void actionPerformed(ActionEvent e) {
				LobbyGUI.this.client.processUserInput("START");
				
			}
		});
		//ready
		readyButton.addActionListener(new ActionListener() {			
			@Override
			public void actionPerformed(ActionEvent e) {
				LobbyGUI.this.client.processUserInput("READY");				
			}
		});
		//name
		nameButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String output = JOptionPane.showInputDialog(LobbyGUI.this,"Enter a new name");
				if(output != null && !output.isEmpty()){
					LobbyGUI.this.client.processUserInput("NAME "+ output);
				}
				
			}
		});
		//color
		colorButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				//Using JColorChooser
				Color newColor = JColorChooser.showDialog(
	                     LobbyGUI.this,
	                     "Choose your color",
	                   	 client.clientPlayer.color);
				if(newColor != null){
					LobbyGUI.this.client.send("<LOBBYPLAYER><COLOR>"+newColor.getRGB()+"</COLOR></LOBBYPLAYER>");
				}
			}
		});
		//send
		ActionListener sendMessageAction = new ActionListener() {			
			@Override
			public void actionPerformed(ActionEvent e) {
				String input = LobbyGUI.this.chatField.getText();
				LobbyGUI.this.client.processUserInput(input);
				chatField.setText("");
			}
		};
		chatField.setText("");
		sendButton.addActionListener(sendMessageAction);
		chatField.addActionListener(sendMessageAction);
	}

	/**
	 * @return StyledDocument for chatPane
	 */
	public StyledDocument getStyledDucment() {
		return chatPane.getStyledDocument();
	}
	
	/**
	 * Set styledDocument for chatPane
	 * @param doc the Document to use
	 */
	public void setStyledDocument(StyledDocument doc){
		chatPane.setStyledDocument(doc);
	}

}
