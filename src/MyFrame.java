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
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.EtchedBorder;
import javax.swing.border.LineBorder;
import javax.swing.text.BadLocationException;

public class MyFrame extends JFrame{
	private Console console;
	private JScrollPane scrollArea;
	private PaintPanel paintPanel = new PaintPanel();
	private JPanel controlsPanel = new JPanel(new BorderLayout());
	private JPanel buttonsPanel = new JPanel(new GridBagLayout());
	private JButton buttonLook = new JButton("LOOK");
	private JButton buttonPickup = new JButton("<html>PICK<br> UP</html>");
	private JButton buttonHello = new JButton("HELLO");
	private JButton buttonExit = new JButton("EXIT");
	private JButton buttonNorth = new JButton("N");
	private JButton buttonSouth = new JButton("S");
	private JButton buttonWest = new JButton("W");
	private JButton buttonEast = new JButton("E");
	private String guiInput = null;
	
	
	public MyFrame(String title,Console console){
		super(title);
		this.console = console;
		scrollArea = new JScrollPane(this.console);
		loadIcons();
		initialiseLayout();		
		initialiseButtons();	
		setVisible(true);
		pack();
	}
	

	protected void drawMap(Map map){
		paintPanel.setMap(map);
	}
	protected String getInput(){
		String holder = guiInput;
		guiInput = null;
		return holder;
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
				if(button.getIcon()!=null){
					button.setText("");
				}
			}
		}
	}
}
