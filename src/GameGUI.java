import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
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
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.EtchedBorder;
import javax.swing.border.LineBorder;


/**
 * The main gui in which DOD is rendered on
 */
public class GameGUI extends JFrame{
	Console console;
	private JScrollPane scrollArea;
	private JPanel paintPanel = new JPanel();//TODO:
	private JPanel controlsPanel = new JPanel(new BorderLayout());
	private JPanel buttonsPanel = new JPanel(new GridBagLayout());
	private JButton buttonLook = new JButton("LOOK");
	private JButton buttonPickup = new JButton("<html>PICK<br> UP</html>");
	private JButton buttonHello = new JButton("HELLO");
	private JButton buttonExit = new JButton("EXIT");
	private JButton buttonNorth = new JButton("MOVE N");
	private JButton buttonSouth = new JButton("MOVE S");
	private JButton buttonWest = new JButton("MOVE W");
	private JButton buttonEast = new JButton("MOVE E");
	private String guiInput = null;
	private boolean allowInputs = false;
	
	
	/**
	 * Initialise a new frame
	 * @param title The title text of the frame
	 * @param visible true if the frame should be visible
	 */
	public GameGUI(String title,boolean visible){
		super(title);
		this.console = new Console(this);
		scrollArea = new JScrollPane(this.console);
		loadIcons();
		initialiseLayout();		
		initialiseButtons();			
		allowInputs(false);
		setResizable(false);
		paintPanel.addMouseListener(getMouseListener());
		paintPanel.addKeyListener(getKeyListener());
		pack();
		allowInputs(true);
		setVisible(false);
	}
	
	/**
	 * Update gui with gamestate and current player
	 * @param player The new current player
	 * @param gameState The new gamestate
	 */
	protected void update(Player player, String gameState){
		//paintPanel.setPlayer(player,gameState);
		waitForAnimation();
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
			if (comp instanceof JButton) {//iterate through all buttons and disable/enable
				((JButton) comp).setEnabled(mode);
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
	 * @return The gui console
	 */
	protected Console getConsole(){
		return console;
	}

	/**
	 * Wait for paint panel to
	 */
	protected void waitForAnimation(){
		if(!isVisible()){
			return;//if window is not visible, don't wait
		}
		/*
		do{
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}while(!paintPanel.finishedMove());
		*/
	}
	
	/**
	 * Shows when a player wins
	 * @param player The player that won
	 */
	protected void showWinEvent(Player player){
		console.println("----****"+player.name+"_WON****----");
	}
	
	/**
	 * Play when a player is killed
	 * @param player The player that attacks
	 */
	protected void showFailEvent(Player player){
		//paintPanel.animation = "DEFEAT";
		waitForAnimation();
		//console.println("----****"+player.name+"_DIED****----");
	}
	
	/**
	 * Load sprites for the button
	 */
	private void loadIcons(){	
		buttonLook.setIcon(new ImageIcon(Sprite.get("look")));
		buttonPickup.setIcon(new ImageIcon(Sprite.get("pickup")));
		buttonHello.setIcon(new ImageIcon(Sprite.get("hello")));
		buttonExit.setIcon(new ImageIcon(Sprite.get("quit")));
		buttonNorth.setIcon(new ImageIcon(Sprite.get("arrow1")));
		buttonSouth.setIcon(new ImageIcon(Sprite.get("arrow2")));
		buttonWest.setIcon(new ImageIcon(Sprite.get("arrow3")));
		buttonEast.setIcon(new ImageIcon(Sprite.get("arrow")));
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
		controlsPanel.add(scrollArea,BorderLayout.CENTER);
		controlsPanel.add(buttonsPanel, BorderLayout.EAST);
		buttonsPanel.setBorder(new LineBorder(Color.GRAY));
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		buttonsPanel.add(buttonLook,c);
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 1;
		buttonsPanel.add(buttonHello, c);
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
			if(component instanceof JButton){
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
				}
			}			
		};
		return listener;
	}
	
}
