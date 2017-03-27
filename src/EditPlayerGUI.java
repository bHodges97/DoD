import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;

public class EditPlayerGUI extends JFrame{
	public boolean kill = false;
	public boolean disconnect = false;
	private JTextField colorLabel = new JTextField(2);
	private JTextField nameField = new JTextField(10);
	private JTextField colorRedField = new JTextField(10);
	private JTextField colorGreenField = new JTextField(10);
	private JTextField colorBlueField = new JTextField(10);
	private JButton confirmButton = new JButton("Done");
	private JButton setGoldButton = new JButton("Set Gold");
	private JButton setPositionButton = new JButton("Set Position");
	private JButton killButton = new JButton("Kill");
	private JButton disconnectButton = new JButton("Disconnect");
	private LobbyPlayer player;
	private final boolean detailed;
	
	public static void main(String[] args){
		new EditPlayerGUI(new LobbyPlayer(0),true);
	}
	
	public EditPlayerGUI(LobbyPlayer player,boolean detailed){
		this.player = player;
		this.detailed = detailed;
		initAndLayoutComponents();
		addListeners();
		setPreferredSize(new Dimension(400, 400));
		setTitle("Player settings");
		setResizable(false);
		setVisible(true);
		pack();
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
	}
	
	private void initAndLayoutComponents(){
		setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		c.anchor = GridBagConstraints.WEST;
		c.gridx = 0;
		c.gridy = 0;
		add(new JLabel("ID:"),c);
		c.gridx = 1;
		add(new JLabel(""+player.id),c);
		c.gridx = 0;
		c.gridy = 1;
		add(new JLabel("Name:"),c);
		c.gridx = 1;
		nameField.setText(player.name);
		add(nameField,c);
		c.gridx = 0;
		c.gridy = 2;
		add(new JLabel("Color:"),c);
		c.gridx = 1;
		colorLabel.setEditable(false);
		colorLabel.setBackground(player.color);
		add(colorLabel,c);
		c.gridx = 0;
		c.gridy = 3;
		add(new JLabel("Red:"),c);
		c.gridx = 1;
		colorRedField.setText(""+player.color.getRed());
		add(colorRedField,c);
		c.gridy = 4;
		c.gridx = 0;
		add(new JLabel("Green:"),c);
		c.gridx = 1;
		colorGreenField.setText(""+player.color.getGreen());
		add(colorGreenField,c);
		c.gridy = 5;
		c.gridx = 0;
		add(new JLabel("Blue:"),c);
		c.gridx = 1;
		colorBlueField.setText(""+player.color.getBlue());
		add(colorBlueField,c);
		
		if(detailed){
			c.gridx = 0;
			c.gridy++;
			add(setGoldButton,c);
			c.gridx = 1;
			add(setPositionButton, c);
			c.gridx = 0;
			c.gridy++;
			add(killButton,c);
			c.gridx = 1;
			add(disconnectButton,c);
		}
		
		
		c.gridx = 1;
		c.gridy += 2;
		add(confirmButton,c);
	}
	
	private boolean isValidColor(String color){
		try{
			int i = Integer.valueOf(color);
			if(i >= 0 && i <= 255){
				return true;
			}
		}catch(NumberFormatException e){
			
		}
		return false;
	}
	
	private void addListeners(){
		confirmButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String red = colorRedField.getText();
				String blue = colorBlueField.getText();
				String green = colorGreenField.getText();
				if(!isValidColor(red) || !isValidColor(blue) || !isValidColor(green)){
					JOptionPane.showMessageDialog(null, "Not a valid color");
					return;
				}
				player.color = new Color(Integer.parseInt(red),Integer.parseInt(blue), Integer.parseInt(green));
				player.name = nameField.getText();
			}
		});
		disconnectButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int responce = JOptionPane.showConfirmDialog(null, "Are you sure about this", "Disconnect "+player.id, JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
				disconnect = responce == JOptionPane.YES_OPTION;
			}
		});
		setGoldButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String responce = JOptionPane.showInputDialog("Set gold count:");
				
			}
		});
		killButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int responce = JOptionPane.showConfirmDialog(null, "Are you sure about this", "Kill "+player.id, JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
				kill = responce == JOptionPane.YES_OPTION;
			}
		});
	}
}
