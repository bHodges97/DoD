import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.EtchedBorder;
import javax.swing.border.LineBorder;
import javax.swing.text.DefaultCaret;
import javax.swing.text.StyledDocument;


/**
 * The main gui in which DOD is rendered on
 */
public class HumanClientGUI extends JFrame{
	private JTextPane console = new JTextPane();
	private JScrollPane scrollArea;
	private PanelPlayer paintPanel;
	private JPanel controlsPanel = new JPanel(new BorderLayout());
	private JPanel buttonsPanel = new JPanel(new GridBagLayout());
	private JTextField inputField = new JTextField(20);
	private JButton buttonLook = new JButton("LOOK");
	private JButton buttonPickup = new JButton("<html>PICK<br> UP</html>");
	private JButton buttonHello = new JButton("HELLO");
	private JButton buttonExit = new JButton("EXIT");
	private JButton buttonNorth = new JButton("MOVE N");
	private JButton buttonSouth = new JButton("MOVE S");
	private JButton buttonWest = new JButton("MOVE W");
	private JButton buttonEast = new JButton("MOVE E");
	private JButton buttonSettings = new JButton("Settings");
	private JButton buttonChat = new JButton("Chat");
	private String guiInput = null;
	private boolean allowInputs = false;
	private Client client;
	
	
	public static void main(String[] args) {
		HumanClient client = new HumanClient(args);		
	}
	
	/**
	 * Initialise a new frame
	 * @param title The title text of the frame
	 * @param visible true if the frame should be visible
	 */
	public HumanClientGUI(String title,boolean visible,Client client){
		super(title);
		this.client = client;
		paintPanel = new PanelPlayer(client);
		scrollArea = new JScrollPane(this.console);
		console.setFont(new Font("monospaced", Font.PLAIN, 12));
		loadIcons();
		initialiseLayout();		
		initialiseButtons();			
		allowInputs(false);
		setResizable(false);
		paintPanel.addMouseListener(getMouseListener());
		paintPanel.addKeyListener(getKeyListener());
		inputField.addActionListener(getActionListener());
		console.setAutoscrolls(true);
		console.setEditable(false);
		((DefaultCaret)console.getCaret()).setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
		pack();
		allowInputs(true);
		setVisible(false);
	}
	/**
	 * @param map The map to render;
	 */
	protected void setMap(Map map){
		//paintPanel.setMap(map);
	}
	
	/**
	 * Set if input is enabled or not
	 * @param mode true if enabled, false otherwise
	 */
	protected void allowInputs(boolean mode){
		allowInputs = mode;
		guiInput = null;
		for (Component comp : buttonsPanel.getComponents()) {
			try{//iterate through all buttons and disable/enable
				((JButton) comp).setEnabled(mode);
			}catch(ClassCastException e){
				
			}
		}
		
	}
	
	/**
	 * @return What a player inputted to gui
	 */
	protected synchronized String getInput(){
		String in = guiInput;
		guiInput = null;
		return in;
	}
	
	/**
	 * Load sprites for the button
	 */
	private void loadIcons(){	
		buttonLook.setIcon(		new ImageIcon(Sprite.getSprite(2,6)));
		buttonPickup.setIcon(	new ImageIcon(Sprite.getSprite(1, 6)));
		buttonHello.setIcon(	new ImageIcon(Sprite.getSprite(0, 5)));
		buttonExit.setIcon(		new ImageIcon(Sprite.getSprite(0, 6)));
		buttonNorth.setIcon(	new ImageIcon(Sprite.getSprite(1, 7)));
		buttonSouth.setIcon(	new ImageIcon(Sprite.getSprite(2, 7)));
		buttonWest.setIcon(		new ImageIcon(Sprite.getSprite(3, 7)));
		buttonEast.setIcon(		new ImageIcon(Sprite.getSprite(0, 7)));
		buttonChat.setIcon(		new ImageIcon(Sprite.getSprite(1, 5)));
		buttonSettings.setIcon( new ImageIcon(Sprite.getSprite(4, 6)));
	}
	
	/**
	 * initialise layout for components
	 */
	private void initialiseLayout(){
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException 
				| IllegalAccessException | UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}
		setLayout(new BorderLayout());
		add(paintPanel,BorderLayout.CENTER);
		add(controlsPanel,BorderLayout.SOUTH);
		JPanel textInput = new JPanel(new BorderLayout());
		textInput.add(scrollArea,BorderLayout.CENTER);
		textInput.add(inputField,BorderLayout.SOUTH);
		
		controlsPanel.add(textInput,BorderLayout.CENTER);
		controlsPanel.add(buttonsPanel, BorderLayout.EAST);
		buttonsPanel.setBorder(new LineBorder(Color.GRAY));
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		buttonsPanel.add(buttonSettings,c);
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 1;
		buttonsPanel.add(buttonChat, c);
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 2;
		buttonsPanel.add(buttonExit, c);
		c = new GridBagConstraints();
		c.gridx = 2;
		c.gridy = 1;
		buttonsPanel.add(buttonPickup, c);
		c = new GridBagConstraints();
		c.gridx = 2;
		c.gridy = 0;
		buttonsPanel.add(buttonNorth, c);
		c = new GridBagConstraints();
		c.gridx = 2;
		c.gridy = 2;
		buttonsPanel.add(buttonSouth, c);
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = 1;
		buttonsPanel.add(buttonWest, c);
		c = new GridBagConstraints();
		c.gridx = 3;
		c.gridy = 0;
		buttonsPanel.add(buttonLook, c);
		c = new GridBagConstraints();
		c.gridx = 3;
		c.gridy = 2;
		buttonsPanel.add(buttonHello, c);
		c = new GridBagConstraints();
		c.gridx = 3;
		c.gridy = 1;
		buttonsPanel.add(buttonEast, c);

		setMinimumSize(new Dimension(500, 600));
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
	/**
	 * Sets up buttons
	 */
	private void initialiseButtons(){
		ActionListener actionListener = getButtonListener();
		for(Component component : buttonsPanel.getComponents()){
			try{
				JButton button = (JButton)component;
				button.addActionListener(actionListener);
				button.setPreferredSize(new Dimension(64,64));
				button.setFont(button.getFont().deriveFont(8.4f));
				button.setBorder(new EtchedBorder());
				button.setHorizontalAlignment(JButton.CENTER);
				button.addKeyListener(getKeyListener());
				if(button.getIcon()!=null){
					button.setText("");//if icons loaded clear text
				}
			}catch(ClassCastException e){
				
			}
		}
	}
	
	/**
	 * @return Key listener that map arrow keys to MOVE and space bar to PICKUP
	 */
	private KeyListener getKeyListener(){
		KeyListener keyListener = new KeyListener() {
			@Override
			public void keyTyped(KeyEvent e) {
				action(e);
			}
			
			@Override
			public void keyReleased(KeyEvent e) {
				action(e);
			}
			
			@Override
			public void keyPressed(KeyEvent e) {
				action(e);
			}
			
			private void action(KeyEvent e){
				if(allowInputs == false){
					return;
				}
				if(e.getKeyCode() == KeyEvent.VK_UP){
					guiInput = "MOVE N";
				}else if(e.getKeyCode() == KeyEvent.VK_DOWN){
					guiInput = "MOVE S";
				}else if(e.getKeyCode() == KeyEvent.VK_LEFT){
					guiInput = "MOVE W";
				}else if(e.getKeyCode() == KeyEvent.VK_RIGHT){
					guiInput = "MOVE E";
				}else if(e.getKeyCode() == KeyEvent.VK_SPACE){
					guiInput = "PICKUP";
				}
			}
		};
		return keyListener;
	}
	
	/**
	 * @return Mouse Listener that grabs focus for paintpanel
	 */
	private MouseListener getMouseListener(){
		return new MouseListener() {			
			@Override
			public void mouseReleased(MouseEvent e) {
			}
			@Override
			public void mousePressed(MouseEvent e) {
			}			
			@Override
			public void mouseExited(MouseEvent e) {
			}			
			@Override
			public void mouseEntered(MouseEvent e) {
			}			
			@Override
			public void mouseClicked(MouseEvent e) {
				paintPanel.requestFocusInWindow();
				
			}
		};		
	}

	/**
	 * @return ActionListener that maps button presses to protocol commands
	 */
	private ActionListener getButtonListener(){
		ActionListener listener = new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				if(e.getSource()==buttonLook){
					guiInput = "LOOK";
				}else if(e.getSource()==buttonPickup){
					guiInput = "PICKUP";
				}else if(e.getSource()==buttonExit){
					guiInput = "QUIT";
				}else if(e.getSource()==buttonHello){
					guiInput = "HELLO";
				}else if(e.getSource()==buttonNorth){
					guiInput = "MOVE N";
				}else if(e.getSource()==buttonSouth){
					guiInput = "MOVE S";
				}else if(e.getSource()==buttonWest){
					guiInput = "MOVE W";
				}else if(e.getSource()==buttonEast){
					guiInput = "MOVE E";
				}else if(e.getSource()==buttonSettings){
					EditPlayerGUI gui = new EditPlayerGUI(HumanClientGUI.this,client.clientPlayer, false);
					client.send("<LOBBYPLAYER><COLOR>"+gui.getRGB()+"</COLOR></LOBBYPLAYER>");
					client.send("<LOBBYPLAYER><NAME>"+Parser.sanitise(gui.name)+"</NAME></LOBBYPLAYER>");
				}else if(e.getSource()==buttonChat){
					
				}
			}			
		};
		return listener;
	}
	
	private ActionListener getActionListener(){
		return new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				if(inputField.getText()!=null && !inputField.getText().isEmpty()){
					guiInput = inputField.getText();
					inputField.setText("");
				}				
			}
			
		};
	}
	protected void setStyledDocument(StyledDocument document) {
		 console.setStyledDocument(document);
		
	}

}
