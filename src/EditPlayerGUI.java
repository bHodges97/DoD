import java.awt.Color;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class EditPlayerGUI extends JDialog{
	public boolean done = false;
	public boolean kill = false;
	public boolean disconnect = false;
	public Color color;
	public int goldCount = -1;
	public String name;
	private JLabel colorLabel = new JLabel();
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
	
	
	public EditPlayerGUI(Frame parent,LobbyPlayer player,boolean detailed){
		super(parent);
		this.player = player;
		this.detailed = detailed;
		name = player.name;
		color = player.color;
		colorLabel.setOpaque(true);
		initAndLayoutComponents();
		addListeners();
		colorLabel.setPreferredSize(nameField.getSize());
		//setPreferredSize(new Dimension(200, 200));
		setTitle("Player settings");
		setResizable(false);
        setModalityType(ModalityType.APPLICATION_MODAL);
        setModal(true);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(getParent());
		pack();
		setVisible(true);
	}
	
	/**
	 * 
	 */
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
				color = new Color(Integer.parseInt(red),Integer.parseInt(blue), Integer.parseInt(green));
				name = nameField.getText();
				done = true;
				dispose();
			}
		});
		disconnectButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int responce = JOptionPane.showConfirmDialog(EditPlayerGUI.this, "Are you sure about this", "Disconnect "+player.id, JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
				disconnect = responce == JOptionPane.YES_OPTION;
			}
		});
		setGoldButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String responce = JOptionPane.showInputDialog("Set gold count:");
				if(responce.matches("[0-9]?[0-9]")){
					goldCount = Integer.valueOf(responce);
				}
			}
		});
		killButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int responce = JOptionPane.showConfirmDialog(EditPlayerGUI.this, "Are you sure about this", "Kill "+player.id, JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
				kill = responce == JOptionPane.YES_OPTION;
			}
		});
		DocumentListener doclistener = new DocumentListener() {
			@Override
			public void changedUpdate(DocumentEvent e) {
				verify();
			}
			@Override
			public void insertUpdate(DocumentEvent e) {
				verify();
			}
			@Override
			public void removeUpdate(DocumentEvent e) {
				verify();
			}
		};
		colorBlueField.getDocument().addDocumentListener(doclistener);
		colorGreenField.getDocument().addDocumentListener(doclistener);
		colorRedField.getDocument().addDocumentListener(doclistener);
	}
	
	public void verify(){
		String red = colorRedField.getText();
		String blue = colorBlueField.getText();
		String green = colorGreenField.getText();
		if(isValidColor(red) && isValidColor(blue) && isValidColor(green)){
			colorLabel.setBackground(new Color(Integer.parseInt(red),Integer.parseInt(blue), Integer.parseInt(green)));
		}
	}

	public int getRGB() {
		return color.getRGB();
	}
}
