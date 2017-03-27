import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.text.DefaultCaret;
import javax.swing.text.StyledDocument;



public class DODServerGUI extends JFrame{
	private static String ACTION_SAVE = "save", ACTION_ADD_GOLD = "gold", ACTION_MOVE_PLAYER = "move", ACTION_EDIT_TILE = "edit", ACTION_EDIT_PLAYER = "play";
	private PanelServer panelServer;
	private final DODServer server;
	private final JTextPane textPane = new JTextPane();;
	private final JRadioButton buttonConnectOn = new JRadioButton("Allow new connections");
	private final JRadioButton buttonConnectOff = new JRadioButton("Block new connections");
	private final JCheckBox checkBoxShowMap = new JCheckBox("<html>Show map<br>(you can drag the map)</html>");
	private final JButton buttonMovePlayer = new JButton("Move Player");
	private final JButton buttonSpawnGold = new JButton("Spawn Gold");
	private final JButton buttonSettings = new JButton("Edit Player");
	private final JButton buttonSave = new JButton("Save Log");
	private final JTextField fieldInput = new JTextField(30);
	private final JFileChooser fileChooser = new JFileChooser();
	
	public static void main(String[] args) {
		int port = -1;
		if(args.length == 1){
			try{
			port = Integer.valueOf(args[0]);
			if(port > 65535 || port < 0){
				throw new NumberFormatException();
			}
			}catch(NumberFormatException e){
				System.out.println(args[0]+" is not a valid port number!");
				System.exit(1);
			}
		}
		DODServer server = new DODServer(port); 
	}
	
	public DODServerGUI(DODServer server){
		this.server = server;
		panelServer = new PanelServer(server);
		addComponents();
		addListeners();
		textPane.setPreferredSize(new Dimension(400, 200));
		textPane.setAutoscrolls(true);
		setTitle("DODServer");
		//setPreferredSize(new Dimension(576,700));
		setResizable(false);
		setVisible(true);
		pack();
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		
	}
	
	private void addComponents(){
		JPanel panelNorth = new JPanel(new BorderLayout());
		JPanel panelSouth = new JPanel(new BorderLayout());
		JPanel consolePanel = new JPanel(new BorderLayout());
		JScrollPane textPaneScroll = new JScrollPane(textPane,JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		((DefaultCaret)textPane.getCaret()).setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
		add(panelNorth,BorderLayout.NORTH);
		add(panelSouth,BorderLayout.SOUTH);
		panelNorth.add(panelServer,BorderLayout.CENTER);
		panelSouth.add(consolePanel,BorderLayout.CENTER);
		JPanel panelSouthWest = new JPanel();
		JPanel panelSouthEast = new JPanel();
		panelSouthWest.setLayout(new BoxLayout(panelSouthWest, BoxLayout.Y_AXIS));
		panelSouthEast.setLayout(new GridLayout(0,1));
		panelSouth.add(panelSouthWest,BorderLayout.WEST);
		panelSouth.add(panelSouthEast,BorderLayout.EAST);
		consolePanel.add(textPaneScroll,BorderLayout.CENTER);
		consolePanel.add(fieldInput,BorderLayout.SOUTH);
		//panelSouth.add(labelServerDesc,BorderLayout.NORTH);
		JPanel radioPanel = new JPanel(new GridLayout(2,1));
		radioPanel.setBorder(BorderFactory.createEtchedBorder());
		radioPanel.add(buttonConnectOff);
		radioPanel.add(buttonConnectOn);
		panelSouthEast.setBorder(BorderFactory.createEtchedBorder());
		panelSouthEast.add(checkBoxShowMap);
		panelSouthEast.add(radioPanel);
		panelSouthEast.add(buttonMovePlayer);
		panelSouthEast.add(buttonSave);
		panelSouthEast.add(buttonSpawnGold);
		panelSouthEast.add(buttonSettings);
		
		ButtonGroup group = new ButtonGroup();
		group.add(buttonConnectOff);
		group.add(buttonConnectOn);
	}
	
	private void addListeners(){
		checkBoxShowMap.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				showMap();
			}
		});
		ActionListener buttonActionListener = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String command = e.getActionCommand();
				if(command.equals(ACTION_SAVE)){
					if(fileChooser.showSaveDialog(DODServerGUI.this) == JFileChooser.APPROVE_OPTION){
						File saveFile = fileChooser.getSelectedFile();
						server.saveLogTo(saveFile);
					}
					return;
				}
				Position selectedTile = panelServer.getSelected();
				Player selectedPlayer = panelServer.getSelectedPlayer();
				if(command.equals(ACTION_MOVE_PLAYER)){
					if(selectedPlayer != null){
						selectedTile = showPositionInput();
						if(selectedTile != null){
							server.getGameLogic().movePlayer(selectedPlayer,selectedTile);
						}
					}else{
						JOptionPane.showMessageDialog(DODServerGUI.this, "No player selected!", "Error", JOptionPane.ERROR_MESSAGE);
					}					
				}else if(command.equals(ACTION_ADD_GOLD)){
					String count  = JOptionPane.showInputDialog("Input gold count to add to " + selectedTile);
					try{
						int c = Integer.parseInt(count);
						if(c < 0 || c > 10){
							throw new NumberFormatException();
						}
						server.getGameLogic().addGold(c,selectedTile);
					}catch(NumberFormatException e2){
						JOptionPane.showMessageDialog(DODServerGUI.this, "Not a valid number", "Error", JOptionPane.ERROR_MESSAGE);
					}
				}else if(command.equals(ACTION_EDIT_TILE)){
					
				}else if(command.equals(ACTION_EDIT_PLAYER)){
					if(selectedPlayer != null){
						LobbyPlayer player = server.getLobbyPlayers().get(selectedPlayer.id);
						EditPlayerGUI dialog = new EditPlayerGUI(DODServerGUI.this, player, true);
						player.name = Parser.sanitise(dialog.name);
						player.color = dialog.color;
						server.informClients();
						if(dialog.kill){
							server.getGameLogic().kill(selectedPlayer, "Server command");
						}
						if(dialog.goldCount != -1){
							selectedPlayer.inventory.getItemStack("Gold").count = dialog.goldCount;
						}
						if(dialog.disconnect){
							server.close(selectedPlayer.id, "Kicked");
						}
					}
				}
				
			}
		};
		ItemListener connectionListener = new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				server.allowConnections(!buttonConnectOff.isSelected());
			}
		};
		buttonConnectOn.setSelected(true);
		buttonConnectOff.addItemListener(connectionListener);
		buttonMovePlayer.addActionListener(buttonActionListener);
		buttonMovePlayer.setActionCommand(ACTION_MOVE_PLAYER);
		buttonSave.addActionListener(buttonActionListener);
		buttonSave.setActionCommand(ACTION_SAVE);
		buttonSpawnGold.addActionListener(buttonActionListener);
		buttonSpawnGold.setActionCommand(ACTION_ADD_GOLD);
		buttonSettings.addActionListener(buttonActionListener);
		buttonSettings.setActionCommand(ACTION_EDIT_PLAYER);
		fieldInput.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				server.println(fieldInput.getText());
				server.sendToAll("<OUTPUT>"+fieldInput.getText()+"</OUTPUT>");
				fieldInput.setText("");
			}
		});
	}
	
	protected void showMap(){
		panelServer.showMap(checkBoxShowMap.isSelected());
	}
	
	private Position showPositionInput(){
		JTextField fieldX = new JTextField("0"), fieldY = new JTextField("0");
		Object[] message = {
		    "x coordinate:", fieldX,
		    "y coordinate:", fieldY
		};
		int option = JOptionPane.showConfirmDialog(DODServerGUI.this,message , "Input a position", JOptionPane.OK_OPTION);
		if(option == JOptionPane.OK_OPTION){
			try{
				int x = Integer.parseInt(fieldX.getText());
				int y = Integer.parseInt(fieldY.getText());
				return new Position(x,y);
			}catch (NumberFormatException e) {
				JOptionPane.showMessageDialog(DODServerGUI.this, "Invalid input.","Error",JOptionPane.ERROR_MESSAGE);
				//.printStackTrace();
				return null;
			}
		}
		return null;
	}

	public StyledDocument getDocument() {
		return textPane.getStyledDocument();
	}

	public void allowConnections(boolean allowConnections) {
		if(buttonConnectOff.isSelected() == allowConnections){
			buttonConnectOff.setSelected(!allowConnections);
		}
	}
}
