import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;

import javax.swing.BoxLayout;
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

public class ServerGUI extends JFrame{
	private static String ACTION_SAVE = "save", ACTION_ADD_GOLD = "gold", ACTION_MOVE_PLAYER = "move", ACTION_EDIT_TILE = "edit", ACTION_KILL_PLAYER = "kill";
	private PanelServer panelServer;
	private final DODServer server;
	private final JTextPane textPane = new JTextPane();;
	private final JRadioButton buttonConnectOn = new JRadioButton("Allow new connections");
	private final JRadioButton buttonConnectOff = new JRadioButton("Block new connections");
	private final JCheckBox checkBoxShowMap = new JCheckBox("Show map");
	private final JButton buttonMovePlayer = new JButton("Move Player");
	private final JButton buttonSpawnGold = new JButton("Spawn Gold");
	private final JButton buttonSave = new JButton("Save Log");
	private final JTextField fieldInput = new JTextField(30);
	private final JLabel labelServerDesc = new JLabel("IP: PORT:");
	private final JFileChooser fileChooser = new JFileChooser();
	
	
	public ServerGUI(DODServer server){
		this.server = server;
		panelServer = new PanelServer(server);
		addComponents();
		addListeners();
		textPane.setPreferredSize(new Dimension(400, 200));
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
		JScrollPane textPaneScroll = new JScrollPane(textPane,JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		add(panelNorth,BorderLayout.NORTH);
		add(panelSouth,BorderLayout.SOUTH);
		panelNorth.add(panelServer,BorderLayout.CENTER);
		panelSouth.add(textPaneScroll,BorderLayout.CENTER);
		JPanel panelSouthWest = new JPanel();
		JPanel panelSouthEast = new JPanel();
		panelSouthWest.setLayout(new BoxLayout(panelSouthWest, BoxLayout.Y_AXIS));
		panelSouthEast.setLayout(new GridLayout(2,0));
		panelSouth.add(panelSouthWest,BorderLayout.WEST);
		panelSouth.add(panelSouthEast,BorderLayout.EAST);
		panelSouth.add(fieldInput,BorderLayout.SOUTH);
		panelSouth.add(labelServerDesc,BorderLayout.NORTH);
		panelSouthEast.add(buttonMovePlayer);
		panelSouthEast.add(buttonSave);
		panelSouthEast.add(buttonSpawnGold);
		panelSouthEast.add(checkBoxShowMap);
		
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
					if(fileChooser.showOpenDialog(ServerGUI.this) == JFileChooser.APPROVE_OPTION){
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
						JOptionPane.showMessageDialog(ServerGUI.this, "No player selected!", "Error", JOptionPane.ERROR_MESSAGE);
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
						JOptionPane.showMessageDialog(ServerGUI.this, "Not a valid number", "Error", JOptionPane.ERROR_MESSAGE);
					}
				}else if(command.equals(ACTION_EDIT_TILE)){
					
				}else if(command.equals(ACTION_KILL_PLAYER)){
					
				}
				
			}
		};
		buttonMovePlayer.addActionListener(buttonActionListener);
		buttonMovePlayer.setActionCommand(ACTION_MOVE_PLAYER);
		buttonSave.addActionListener(buttonActionListener);
		buttonSave.setActionCommand(ACTION_SAVE);
		buttonSpawnGold.addActionListener(buttonActionListener);
		buttonSpawnGold.setActionCommand(ACTION_ADD_GOLD);
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
		int option = JOptionPane.showConfirmDialog(ServerGUI.this,message , "Input a position", JOptionPane.OK_OPTION);
		if(option == JOptionPane.OK_OPTION){
			try{
				int x = Integer.parseInt(fieldX.getText());
				int y = Integer.parseInt(fieldY.getText());
				return new Position(x,y);
			}catch (NumberFormatException e) {
				JOptionPane.showMessageDialog(ServerGUI.this, "Invalid input.","Error",JOptionPane.ERROR_MESSAGE);
				e.printStackTrace();
				return null;
			}
		}
		return null;
	}
}
