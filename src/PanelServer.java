import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.util.List;

import javax.swing.JPanel;

public class PanelServer extends JPanel {
	
	protected boolean showMap = false;
	private DODServer server;
	private static final int TILESIZE = 64;
	private int offsetX = 0, offsetY = 0;
	private int clickX = 0, clickY = 0;
	private int cursorX = 0, cursorY = 0;
	private Position selected = new Position(0,0);
	private Player selectedPlayer =  null;
	private static int WIDTH = 576;
	private static int HEIGHT = 448;
	private int portNum = 0;
	private int imageWidth = 0, imageHeight =0;
	private Font standardFont,largeFont;
	
	public PanelServer(DODServer server){
		this.server = server;
		portNum = server.getPort();
		setPreferredSize(new Dimension(WIDTH,HEIGHT));
		this.setFocusable(true);
		addMotionListeners();
		standardFont = getFont();
		largeFont = standardFont.deriveFont(30f);

		Runnable runnable = new Runnable(){		
			@Override
			public void run() {
				while(true){
					repaint();
					try {//delay for other threads
						PanelServer.this.server.getGameLogic().getMap().repaint(64, 0);
						Thread.sleep(100);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}			
				}
			}		
		};
		Thread paintThread = new Thread(runnable);
		paintThread.start();
	}
	
	
	@Override
	public void paintComponent(Graphics g){
		super.paintComponent(g);
		Graphics2D g2d = (Graphics2D)g;
		GameLogic gameLogic = server.getGameLogic();
		if(!showMap){
			g2d.setFont(largeFont);
			g2d.drawString("Map hidden", 0, 40);
			g2d.drawString("Server hosted on port: "+portNum, 0, 80);
			g2d.drawString("Connected players: ", 0, 120);
			for(LobbyPlayer player:server.getLobbyPlayers()){
				g2d.drawString("id:"+player.id+" name:"+player.name+" connected:"+player.connected+" bot:"+player.isBot+" color:"+player.color, 0, 160 + player.id * 40);
			}
			return;
		}
		Map map = gameLogic.getMap();
		if(map==null || map.getMapWidth() == 0){
			//don't draw until game is properly loaded
			g2d.drawString("Game is loading. Please wait", 0, 20);
			return;
		}
		Image image = map.getImage(0);
		imageWidth = image.getWidth(null);
		imageHeight = image.getHeight(null);
		g2d.drawImage(image, offsetX, offsetY, null);
		for(Player player:gameLogic.getPlayers()){
			Position pos = Position.multiply(player.position, TILESIZE);
			g2d.drawImage(player.getImage(2), pos.x + offsetX,  pos.y + offsetY, null);
		}
		g2d.setFont(standardFont);
		g2d.setColor(new Color(255, 255, 255, 100));
		g2d.fillRect(0, HEIGHT-90, 200, 60);
		g2d.setColor(Color.black);
		Position highLightedPos = new Position((cursorX-offsetX)/64,(cursorY-offsetY)/64);
		Tile highlightedTile = map.getTile(highLightedPos);
		Player highLightedPlayer = gameLogic.getPlayerAt(highLightedPos);
		DroppedItems highLightedItems = map.getDroppedItemsAt(highLightedPos);
		String text = highLightedPos+"";
		text+= highlightedTile == null? "": " - "+highlightedTile.getTileType();
		text+= highLightedPlayer == null? "": " - "+highLightedPlayer;
		text+= highLightedItems == null? "": " - "+highLightedItems.inventory;
		g2d.drawString(text, 0, HEIGHT - 80);
		g2d.drawString("Selected tile: "+ selected, 0, HEIGHT - 60);
		g2d.drawString("Selected player: " + selectedPlayer, 0 , HEIGHT - 40);
	}
	
	public synchronized void showMap(boolean state){
		offsetX = 0;
		offsetY = 0;
		
		this.showMap = state;
		System.out.println("Showing map");
	}
	
	public Position getSelected(){
		return selected;
	}
	
	public Player getSelectedPlayer(){
		return selectedPlayer;
	}
	
	private synchronized void selectTile(int x, int y){
		selected = new Position((x - offsetX) / TILESIZE,(y - offsetY) / TILESIZE);
		selectedPlayer = server.getGameLogic().getPlayerAt(selected);
	}
	
	private synchronized void setOrigin(int x, int y){
		this.offsetX = x - clickX;
		this.offsetY = y - clickY ;
	}
	
	private void addMotionListeners(){
		this.addMouseListener(new MouseListener() {
			@Override
			public void mouseReleased(MouseEvent e) {				
			}
			
			@Override
			public void mousePressed(MouseEvent e) {
				clickX = e.getX()  - offsetX;
				clickY = e.getY() - offsetY;
			}
			
			@Override
			public void mouseExited(MouseEvent e) {
			}
			
			@Override
			public void mouseEntered(MouseEvent e) {
			}
			
			@Override
			public void mouseClicked(MouseEvent e) {
				if(showMap){
					selectTile(e.getX(),e.getY());		
				}
			}
		});
		this.addMouseMotionListener(new MouseMotionListener() {
			@Override
			public void mouseMoved(MouseEvent e) {
				cursorX = e.getX();
				cursorY = e.getY();
			}
			
			@Override
			public void mouseDragged(MouseEvent e) {
				if(showMap && e.getX() < getWidth() && e.getY() < getHeight() && e.getX() > 0 && e.getY() > 0){
					setOrigin(e.getX(),e.getY());
				}
			}
		});
	}
}
