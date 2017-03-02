import java.awt.Color;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.swing.JTextPane;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import javax.swing.text.StyledDocument;

public class Console extends JTextPane{
	
	protected GameGUI gui ;
	private BufferedReader buffer=new BufferedReader(new InputStreamReader(System.in));
	private String input;
	private StyledDocument document;
	
	/**
	 * Initialise a console using the given JFrame
	 * @param gui 
	 */
	public Console(GameGUI gui){
		super();
		this.gui = gui;
		//setFont(new Font("monospaced", Font.PLAIN, 12));
		setEditable(false);
		setCaretPosition(0);
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
	protected void println(String line,Color color){
		line+="\n";
		print(line,color);
	}
	
	
	public void print(String string,Color color)
    {	
		document = getStyledDocument();
		StyleContext sc = StyleContext.getDefaultStyleContext();
	    AttributeSet aset = sc.addAttribute(SimpleAttributeSet.EMPTY,
	                                        StyleConstants.Foreground, color);
		try {
			document.insertString(document.getLength(), string, aset);
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
        
    }
	
	
}
