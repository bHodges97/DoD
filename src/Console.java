import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Set;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.EtchedBorder;
import javax.swing.border.LineBorder;
import javax.swing.text.BadLocationException;

public class Console {
	
	private JFrame frame = new JFrame("Dungeon of Doom");
	private PaintPanel paintPanel = new PaintPanel();
	private JPanel controlsPanel = new JPanel(new BorderLayout());
	private JTextArea textArea = new JTextArea();
	private JScrollPane scrollArea = new JScrollPane(textArea);
	private JPanel buttonsPanel = new JPanel(new GridBagLayout());
	private JButton buttonLook = new JButton("LOOK");
	private JButton buttonPickup = new JButton("<html>PICK<br> UP</html>");
	private JButton buttonHello = new JButton("HELLO");
	private JButton buttonExit = new JButton("EXIT");
	private JButton buttonNorth = new JButton("N");
	private JButton buttonSouth = new JButton("S");
	private JButton buttonWest = new JButton("W");
	private JButton buttonEast = new JButton("E");
	private BufferedReader buffer=new BufferedReader(new InputStreamReader(System.in));
	private String guiInput = null;
	private Set<Integer> arrowKeys = new HashSet<Integer>();
	
	
	public Console(){
		initialiseLayout();		
		initialiseArrowKeys();
		loadIcons();
		ActionListener actionListener = getButtonListener();
		textArea.addKeyListener(getTextAreaKeyListener());
		for(Component component : buttonsPanel.getComponents()){
			if(component instanceof JButton){
				JButton button = (JButton)component;
				button.addActionListener(actionListener);
				button.setPreferredSize(new Dimension(64,64));
				button.setFont(button.getFont().deriveFont(8.4f));
				button.setBorder(new EtchedBorder());
				button.setHorizontalAlignment(JButton.CENTER);
				if(button.getIcon()!=null){
					button.setText("");
				}
			}
		}
		
		frame.setVisible(true);
		frame.pack();
	}
	
	protected  String readln(){
		String consoleInput = "";
		guiInput = null;
		while(true){			
			if(guiInput != null){
				String holder = guiInput;
				guiInput = null;;
				return holder;
			}
			try {
				if(buffer.ready()){
					consoleInput=buffer.readLine();
				}
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			if(consoleInput!=null && consoleInput!=""){
				return consoleInput;
			}			
			try {
				Thread.sleep(1);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}		
	}
	
	protected void println(String line){
		System.out.println(line);
		textArea.append(line + "\n");
		textArea.setCaretPosition(textArea.getText().length());
	}
	protected void print(String chars){
		System.out.print(chars);
		textArea.append(chars);
		textArea.setCaretPosition(textArea.getText().length());
	}
	protected void drawMap(Map map){
		paintPanel.setMap(map);
	}
	
	private void loadIcons(){
		Image imgLOOK,imgPICKUP,imgEXIT,imgHELLO,imgArrowN,imgArrowE,imgArrowS,imgArrowW;
		try{
			imgLOOK = ImageIO.read(getClass().getResource("look.png"));
			imgPICKUP = ImageIO.read(getClass().getResource("pickup.png"));
			imgEXIT = ImageIO.read(getClass().getResource("quit.png"));
			imgHELLO = ImageIO.read(getClass().getResource("hello.png"));
			imgArrowN = ImageIO.read(getClass().getResource("arrowN.png"));
			imgArrowS = ImageIO.read(getClass().getResource("arrowS.png"));
			imgArrowE = ImageIO.read(getClass().getResource("arrowE.png"));
			imgArrowW = ImageIO.read(getClass().getResource("arrowW.png"));
		}catch(IOException e){
			e.printStackTrace();
			return;
		}
		
		buttonLook.setIcon(new ImageIcon(imgLOOK));
		buttonPickup.setIcon(new ImageIcon(imgPICKUP));
		buttonHello.setIcon(new ImageIcon(imgHELLO));
		buttonExit.setIcon(new ImageIcon(imgEXIT));
		buttonNorth.setIcon(new ImageIcon(imgArrowN));
		buttonSouth.setIcon(new ImageIcon(imgArrowS));
		buttonWest.setIcon(new ImageIcon(imgArrowW));
		buttonEast.setIcon(new ImageIcon(imgArrowE));
	}
	
	private void initialiseLayout(){
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException
				| UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}
		frame.setLayout(new BorderLayout());
		frame.add(paintPanel,BorderLayout.CENTER);
		frame.add(controlsPanel,BorderLayout.SOUTH);
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


		textArea.setFont(new Font("monospaced", Font.PLAIN, 12));
		frame.setMinimumSize(new Dimension(500, 600));
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
	private void initialiseArrowKeys(){
		arrowKeys.add(KeyEvent.VK_UP);
		arrowKeys.add(KeyEvent.VK_KP_UP);
		arrowKeys.add(KeyEvent.VK_DOWN);
		arrowKeys.add(KeyEvent.VK_KP_DOWN);
		arrowKeys.add(KeyEvent.VK_LEFT);
		arrowKeys.add(KeyEvent.VK_KP_LEFT);
		arrowKeys.add(KeyEvent.VK_RIGHT);
		arrowKeys.add(KeyEvent.VK_KP_RIGHT);		
	}
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
				println(guiInput);
			}			
		};
		return listener;
	}
	
	private KeyListener getTextAreaKeyListener(){
		KeyListener textAreaListener = new KeyListener() {			
			@Override
			public void keyTyped(KeyEvent e) {
				lockInput(e);
			}
			
			@Override
			public void keyReleased(KeyEvent e) {
			}
			
			@Override
			public void keyPressed(KeyEvent e) {	
				lockInput(e);
			}
			private void lockInput(KeyEvent e){
				int caretPos = textArea.getCaretPosition();
				try {
					if(caretPos < textArea.getLineStartOffset(textArea.getLineCount()-1) && !arrowKeys.contains(e.getKeyCode())){
						e.consume();//ignore input and move caret to end
						textArea.setCaretPosition(textArea.getText().length());
					}else if(e.getKeyCode() == KeyEvent.VK_ENTER){
						int start = textArea.getLineStartOffset(textArea.getLineCount()-1);
						int end = textArea.getLineEndOffset(textArea.getLineCount()-1);
						guiInput = textArea.getText().substring(start,end);
					}else if(e.isControlDown() && e.getKeyCode() == KeyEvent.VK_V){
						e.consume();//no pasting
					}
				} catch (BadLocationException exception) {
					exception.printStackTrace();
				}	
			}
		};
		return textAreaListener;
	}
}
