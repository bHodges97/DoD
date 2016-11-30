import java.awt.Color;
import java.awt.Graphics;
import javax.swing.JPanel;

@SuppressWarnings("serial")
public class PaintPanel extends JPanel{
	
	public PaintPanel() {
		setBackground(Color.BLACK);
		super.repaint();
	}
	
	@Override
	public void paintComponent(Graphics g){
		
	}
}
