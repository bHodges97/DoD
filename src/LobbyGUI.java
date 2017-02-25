import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.JToggleButton;
import javax.swing.text.StyleContext;

public class LobbyGUI extends JFrame{
	JPanel connectionsPanel = new JPanel(new BorderLayout());
	JPanel sidePanel = new JPanel(new BorderLayout());
	JPanel playerSettingsPanel = new JPanel();
	JScrollPane scrollPane = new JScrollPane();
	JPanel controls = new JPanel();
	JTextPane chatPane = new JTextPane();
	JTextField chatField = new JTextField(15);
	JTextField nameField = new JTextField(10);
	JToggleButton readyButton = new JToggleButton("READY");
	JButton sendButton = new JButton("SEND");
	JPanel chatPanel = new JPanel(new BorderLayout());
	JButton startGame = new JButton("START GAME");
	
	public static void main(String[] args){
		new LobbyGUI();
	}
	
	public LobbyGUI(){
		setTitle("Dungeon of Doom: Game Lobby");
		add(connectionsPanel,BorderLayout.CENTER);
		add(sidePanel,BorderLayout.EAST);
		connectionsPanel.add(scrollPane, BorderLayout.CENTER);
		
		sidePanel.add(chatPanel, BorderLayout.NORTH);
		sidePanel.add(controls,BorderLayout.SOUTH);

		chatPanel.add(new JScrollPane(chatPane),BorderLayout.CENTER);
		JPanel sendmsgPanel = new JPanel();
		chatPanel.add(sendmsgPanel,BorderLayout.SOUTH);
		sendmsgPanel.add(chatField);
		sendmsgPanel.add(sendButton);
		sendButton.setPreferredSize(new Dimension(80,chatField.getPreferredSize().height));
		
		controls.setBorder(BorderFactory.createLoweredBevelBorder());;
		controls.setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.anchor = GridBagConstraints.EAST;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.gridx = 0;
		gbc.gridy = 0;
		controls.add(new JLabel("Name:",JLabel.LEFT),gbc);
		gbc.gridx = 1;
		gbc.gridy = 0;
		controls.add(nameField,gbc);
		gbc.gridx = 1;
		gbc.gridy = 2;
		controls.add(readyButton,gbc);
		gbc.gridx = 1;
		gbc.gridy = 3;
		controls.add(startGame,gbc);
		
		chatPane.setText("Dungeon Of Doom Lobby Chat Room");
		chatPane.setEditable(false);
		chatPane.setPreferredSize(new Dimension(200,150));
		scrollPane.setPreferredSize(new Dimension(200, 300));
		
		setResizable(false);
		setVisible(true);
		pack();
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		
	}
	
	private void addMessageToChat(String msg)
    {
        StyleContext style = StyleContext.getDefaultStyleContext();
        
    }
}
