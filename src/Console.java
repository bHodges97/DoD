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
	
	private MyFrame gui ;
	private BufferedReader buffer=new BufferedReader(new InputStreamReader(System.in));
	private String input;
	private Set<Integer> arrowKeys = new HashSet<Integer>();
	
	public Console(){
		super();
		gui = new MyFrame("Dungeon of Doom",this);
		setFont(new Font("monospaced", Font.PLAIN, 12));
		//setRows(10);
		initialiseArrowKeys();
		addKeyListener(getTextAreaKeyListener());
	}
	
	protected void showWinEvent(){
		println("----****YOU_WIN****----");
	}
	protected void showFailEvent(){
		println("----****YOU_DEAD****----");
	}
	
	protected  String readln(){
		input = null;
		setEditable(true);
		boolean fromGui = true;
		while(input == null || input.equals("")){				
			input = gui.getInput();
			try {
				if(buffer.ready()){
					input=buffer.readLine();
					fromGui = false;
				}
			} catch (IOException e1) {
				e1.printStackTrace();
			}	
		}
		setEditable(false);
		if(fromGui){
			println(input);
		}
		return input;
	}
	
	protected void println(String line){
		line+="\n";
		print(line);
	}
	protected void print(String chars){
		System.out.print(chars);
		append(chars);
		setCaretPosition(getText().length());
	}
	
	protected void setMap(Map map){
		gui.drawMap(map);
	}

	
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
			private void restrictInput(KeyEvent e){
				int caretPos = getCaretPosition();
				try {
					if(caretPos < getLineStartOffset(getLineCount()-1) && !arrowKeys.contains(e.getKeyCode())){
						e.consume();//ignore input and move caret to end
						setCaretPosition(getText().length());
					}else if(e.getKeyCode() == KeyEvent.VK_ENTER){
						int start = getLineStartOffset(getLineCount()-1);
						int end = getLineEndOffset(getLineCount()-1);
						input = getText().substring(start,end);
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
