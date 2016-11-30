import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.text.BadLocationException;

public class Console {
	
	private PaintPanel paintPanel = new PaintPanel();
	private JPanel controlsPanel = new JPanel(new BorderLayout());
	private  JTextArea textArea = new JTextArea();
	private JPanel buttonsPanel = new JPanel(new GridBagLayout());
	private JButton buttonLook = new JButton("LOOK");
	private JButton buttonPickup = new JButton("PICK UP");
	private JButton buttonHello = new JButton("HELLO");
	private  Scanner scanner = new Scanner(System.in);
	private  String guiInput = null;
	
	
	
	public Console(){
		JFrame frame = new JFrame("Dungeon of Doom");
		frame.setLayout(new BorderLayout());
		frame.add(paintPanel,BorderLayout.CENTER);
		frame.add(controlsPanel,BorderLayout.SOUTH);
		controlsPanel.add(textArea,BorderLayout.CENTER);
		controlsPanel.add(buttonsPanel, BorderLayout.EAST);
		
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		buttonsPanel.add(buttonLook,c);
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 1;
		buttonsPanel.add(buttonPickup, c);
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 2;
		buttonsPanel.add(buttonHello, c);
		
		Set<Integer> arrowKeys = new HashSet<Integer>();
		arrowKeys.add(KeyEvent.VK_UP);
		arrowKeys.add(KeyEvent.VK_KP_UP);
		arrowKeys.add(KeyEvent.VK_DOWN);
		arrowKeys.add(KeyEvent.VK_KP_DOWN);
		arrowKeys.add(KeyEvent.VK_LEFT);
		arrowKeys.add(KeyEvent.VK_KP_LEFT);
		arrowKeys.add(KeyEvent.VK_RIGHT);
		arrowKeys.add(KeyEvent.VK_KP_RIGHT);
		
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
		textArea.addKeyListener(textAreaListener);
		
		frame.setMinimumSize(new Dimension(800, 600));
		frame.setVisible(true);
		System.out.println("initialised");
	}
	
	protected  String readln(){
		String consoleInput = "";
		while(true){			
			if(guiInput != null){
				String holder = guiInput;
				guiInput = null;;
				return holder;
			}
			if(scanner.hasNextLine()){
				consoleInput = scanner.nextLine();
				if(!consoleInput.isEmpty()){
					return consoleInput;
				}
			}
			try {
				Thread.sleep(1);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}		
	}
	
	protected void println(String line){//TODO:?
		System.out.println(line);
		textArea.append(line + "\n");
	}
	protected void print(String chars){
		System.out.print(chars);
		textArea.append(chars);
	}
}
