import java.awt.Font;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Set;

import javax.swing.JTextArea;
import javax.swing.text.BadLocationException;

public class Console extends JTextArea{
	
	private GameGUI gui ;
	private BufferedReader buffer=new BufferedReader(new InputStreamReader(System.in));
	private String input;
	private Set<Integer> arrowKeys = new HashSet<Integer>();
	
	/**
	 * Initialise a console using the given JFrame
	 * @param gui 
	 */
	public Console(GameGUI gui){
		super();
		this.gui = gui;
		setFont(new Font("monospaced", Font.PLAIN, 12));
		setRows(10);
		initialiseArrowKeys();
		setCaretPosition(0);
		addKeyListener(getTextAreaKeyListener());
	}
	
	/**
	 * @return A string that is the next line from System.out or gui
	 * @throws IOException  If an I/O error occurs
	 */
	protected  String readln() throws IOException{
		input = "";
		try {			 
			 while(input == null || input.isEmpty()){				
					input = gui.getInput();
					if(buffer.ready()){
						input=buffer.readLine();
						//fromGui = false
					}
					try {
						Thread.sleep(1);//wait for gui to update
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
			 }
			
		} catch (IOException e) {
			e.printStackTrace();
			input = "";
		}
		return input;
	}
	
	/**
	 * Print the line from parameter to console
	 * @param line: the line to print
	 */
	protected void println(String line){
		line+="\n";
		print(line);
	}
	
	/**
	 * Print a string to console
	 * @param chars: the chars to print
	 */
	protected void print(String chars){
		System.out.print(chars);
		append(chars);
		setCaretPosition(getText().length());
	}	
	
	/**
	 * @return The key listener for this class.
	 */
	private KeyListener getTextAreaKeyListener(){
		KeyListener textAreaListener = new KeyListener() {			
			@Override
			public void keyTyped(KeyEvent e) {
				restrictInput(e);
			}
			
			@Override
			public void keyReleased(KeyEvent e) {
			}
		
			@Override
			public void keyPressed(KeyEvent e) {	
				restrictInput(e);
			}
			private void restrictInput(KeyEvent e){//makes a textarea console like
				int caretPos = getCaretPosition();
				try {
					if(caretPos < getLineStartOffset(getLineCount()-1) && !arrowKeys.contains(e.getKeyCode())){
						e.consume();//ignore input and move caret to end
						setCaretPosition(getText().length());
					}else if(e.getKeyCode() == KeyEvent.VK_ENTER){
						int start = getLineStartOffset(getLineCount()-1);
						int end = getLineEndOffset(getLineCount()-1);
						input = getText().substring(start,end);
						setText(getText().substring(0,start));//remove the line inputed
						e.consume();
					}else if(e.isControlDown() && e.getKeyCode() == KeyEvent.VK_V){
						e.consume();//no pasting
					}else if(e.getKeyCode() == KeyEvent.VK_BACK_SPACE && caretPos <= getLineStartOffset(getLineCount()-1) ){
						e.consume();//control the backspace button
					}
				} catch (BadLocationException exception) {
					exception.printStackTrace();
				}	
			}
		};
		return textAreaListener;
	}
	
	/**
	 * Add all keys that could change a caret position to the set arrowKeys.
	 */
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
	
}
