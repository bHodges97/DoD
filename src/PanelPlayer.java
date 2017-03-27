import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.swing.JPanel;


/**
 * The panel that is use to draw LOOK in terms of graphics
 *
 */
public class PanelPlayer extends JPanel{
	
	private static final int WIDTH = 502,HEIGHT = 369;
	private static final int TILESIZE = 64;
	private BufferedImage overlay;
	private Map map = null;
	private int tileFrame = 0;
	private int centerX,centerY;
	private Font defaultFont;	
	private Position center;
	private Client client;
	
	
	/**
	 * Constructor
	 */
	public PanelPlayer(Client client) {
		setPreferredSize(new Dimension(WIDTH,HEIGHT));
		//rubberbiscuit font http://dabnotu.tk free for non-commercial use
		InputStream is = getClass().getResourceAsStream("font.ttf");
		setFocusable(true);
		try {
			defaultFont = Font.createFont(Font.TRUETYPE_FONT, is).deriveFont(20f);
		} catch (FontFormatException |IOException e) {
			e.printStackTrace();
		}
		this.client = client;
		this.map = client.gameMap;
		//had an odd bug where drawn height is around 10px too short
		drawOverlay(WIDTH, HEIGHT+TILESIZE);
		Thread repaintThread = new Thread(makeRunnable(),"repainter");
		repaintThread.start();
	}
	
	
	@Override
	public void paintComponent(Graphics g){
		super.paintComponent(g);
		Graphics2D g2d = (Graphics2D)g;
		int width =getWidth();
		int height = getHeight();
		g2d.setColor(Color.BLACK);
		g2d.fillRect(0, 0, width, height);
		g2d.setColor(Color.WHITE);
		if(map==null || map.getMapWidth() == 0){
			//don't draw until game is properly loaded
			g2d.drawString("Game is loading. Please wait", 0, 20);
			return;
		}else if(client.gameState == GameLogic.GameState.NOTSTARTED){
			g2d.drawString("Waiting for game to start", 0, 20);
			return;
		}else if(client.clientPlayer.visible == false){
			g2d.drawString("WAITING FOR PLAYER TO LOAD",0,20);
			for(int i = 0;i < client.lobbyPlayers.size();++i){
				LobbyPlayer player = client.lobbyPlayers.get(i);
				g2d.drawString(player.id+"|"+player.visible+","+player.actualPos+","+player.screenPos+","+player.orientation,0,40 + i *20);			
			}
			return;
		}
		height+=g2d.getFontMetrics().getHeight();
		centerX = width/2-TILESIZE/2;
		centerY = height/2-TILESIZE/2;
		center = new Position(centerX,centerY);
		drawBackground(g2d);//draw background
		drawDecals(g2d);
		drawItems(g2d);
		drawPlayers(g2d);//draw player sprites
		drawUI(g2d);
		//drawDebug(g2d);		
	}
	

	/**
	 * Update the display positons for the player
	 */
	protected void updatePlayerPositions() {
		for(LobbyPlayer player:client.lobbyPlayers){
			if(player.visible){
				if(!player.actualPos.equalsto(player.screenPos)){
					Position dif = Position.subtract(player.actualPos,player.screenPos);
					if(dif.magnitude() > 3 * TILESIZE || (dif.magnitude() < 5)){
						//if the difference between actual location and drawn position is too large
						//teleport player to correct location
						player.screenPos.set(player.actualPos);
					}else{
						dif = dif.normalise();
						dif.multiply(16);
						player.orientation = dif;
						player.screenPos.add(dif);
					}
				}
			}
		}
		
	}
	
	/**
	 * Draw debug view
	 * @param g2d
	 */
	private void drawDebug(Graphics2D g2d){
		g2d.setFont(defaultFont);
		int i = 0;
		for(i = 0;i < client.lobbyPlayers.size();++i){
			LobbyPlayer player = client.lobbyPlayers.get(i);
			g2d.drawString(player.id+"|"+player.visible+","+player.actualPos+","+player.screenPos+","+player.orientation,0,20 + i *20);			
		}	
		g2d.drawString(map.getMapWidth() + " , "+map.getMapHeight(),0,40 + i *20);	
	}
	
	/**
	 * Draw decals
	 * @param g2d  graphics
	 */
	private void drawDecals(Graphics2D g2d){
		for(LobbyPlayer player:client.lobbyPlayers){
			if(player.state == PlayerState.DEAD){
				Image image = Sprite.getSprite(0, 9);
				if(player == client.clientPlayer){
					g2d.drawImage(image,center.x,center.y,null);
				}else if(player.toPlayer().state == PlayerState.DEAD){
					Position posDif = Position.subtract(client.clientPlayer.screenPos, player.screenPos);
					g2d.drawImage(image,center.x - posDif.x,center.y - posDif.y,null);
				}
			}			
		}
	}
	
	/**
	 * Draw the player sprites.
	 * @param g2d The graphics object to draw onto.
	 */
	private void drawPlayers(Graphics2D g2d){
		for(LobbyPlayer player:client.lobbyPlayers){
			int param = tileFrame%5;
			if(player.isBot){
				if(player.orientation.x > 0){
					param = 1;
				}else if(player.orientation.x < 0){
					param = 0;
				}else if(player.orientation.y < 0){
					param = 3;
				}else{
					param = 2;
				}
			}
			Image image = player.toPlayer().getImage(param);
			if(player == client.clientPlayer){
				g2d.drawImage(image,center.x,center.y,null);
			}else if(player.visible){
				Position posDif = Position.subtract(client.clientPlayer.screenPos, player.screenPos);
				g2d.drawImage(image,center.x - posDif.x,center.y - posDif.y,null);
			}
		}	
	}
	
	/**
	 * Draw the background
	 * @param g2d The graphics to draw onto.
	 */
	private void drawBackground(Graphics2D g2d){
		int x = centerX - client.clientPlayer.screenPos.x;
		int y = centerY - client.clientPlayer.screenPos.y;
		g2d.drawImage(map.getImage(0), x, y, null);
	}
	
	/**
	 * Draw the items
	 * @param g2d The graphics to draw onto.
	 */
	private void drawItems(Graphics2D g2d){
		int x = centerX - client.clientPlayer.screenPos.x;
		int y = centerY - client.clientPlayer.screenPos.y;
		g2d.drawImage(map.getImage(1), x, y, null);
	}
	
	/**
	 * Draw the ui
	 * @param g2d The graphics object to draw onto.
	 * @param width The width of the ui
	 * @param height The height of the ui
	 */
	private void drawUI(Graphics2D g2d){
		g2d.drawImage(overlay, 0, 0, null);
		if(client.clientPlayer.state == PlayerState.ESCAPED){
			Image confetti = Sprite.getSprite(1, 9);
			for (int y = 0; y < getHeight(); y+=TILESIZE){
			    for (int x = 0; x < getWidth(); x+=TILESIZE){
			    	g2d.drawImage(confetti , x, y,null);
			    }
			}
			g2d.setColor(Color.orange);
			g2d.setFont(defaultFont.deriveFont(32f));
			int textwidth = g2d.getFontMetrics().stringWidth("♪YOU WIN♪");
			g2d.drawString("♪YOU WIN♪", (WIDTH - textwidth )/2,centerY);
			g2d.setFont(defaultFont);
		}else if(client.clientPlayer.state == PlayerState.DEAD){
			g2d.setColor(Color.red);
			g2d.setFont(defaultFont.deriveFont(32f));
			int textwidth = g2d.getFontMetrics().stringWidth("GAME OVER");
			g2d.drawString("GAME OVER", (WIDTH - textwidth )/2,centerY);
			g2d.setFont(defaultFont);
		}
		int x = 0;
		int y = HEIGHT-TILESIZE -TILESIZE/2;
		g2d.setColor(Color.white);
		g2d.drawImage(new ItemGold().getImage(0),x,y,TILESIZE/2,TILESIZE/2, null);//draw gold count
		g2d.drawString(" "+client.clientPlayer.inventory.getItemCount("Gold") + "/" + map.getGoldRequired(), x + TILESIZE/2, y + TILESIZE/2-8);
		y+=TILESIZE/2 + 3;
		//draw lives count
		int lives = client.clientPlayer.state == PlayerState.DEAD? 0 : 1;
		g2d.drawImage(Sprite.getSprite(3, 6),x,y,TILESIZE/2,TILESIZE/2,null);
		g2d.drawString(" "+ lives+"/1", x + TILESIZE/2, y + TILESIZE/2-8);
		
		if(client.clientPlayer.state == PlayerState.DEAD){
			g2d.setColor(Color.orange);
			g2d.setFont(defaultFont.deriveFont(25f));
			int twidth = g2d.getFontMetrics().stringWidth("YOU ARE DEAD");//player death
			g2d.drawString("YOU ARE DEAD", (WIDTH - twidth )/2,centerY+30);		
		}
		
		g2d.setFont(defaultFont);
		g2d.setColor(Color.LIGHT_GRAY);

		String title = map.getMapName();
		int titleHeight = g2d.getFontMetrics().getHeight();
		int titleWidth = g2d.getFontMetrics().stringWidth(title);
		g2d.drawString(title, (WIDTH-titleWidth)/2, titleHeight) ;
	}
	
	
	/**
	 * Render the fog
	 * @param width width of window
	 * @param height height of window
	 * @return The bufferedImage of the drawn overlay.
	 */
	private void drawOverlay(int width,int height){
		overlay = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);		
		int tileWidth = 5;
		int maskDiameter = tileWidth * TILESIZE+10;
		int maskRadius = maskDiameter/2;
		float ratio = ((float)255/(float)maskRadius) * 0.8f;
		for(int x = 0;x < width;++x){
			for(int y = 0;y < height;++y){
				int dist = (int) Point2D.distance(x, y, width/2,height/2);
				int alpha = dist*ratio > 255?255:(int)(dist*ratio);
				Color c = new Color(0,0,0,alpha);//becomes more solid black as further from center
				overlay.setRGB(x, y,c.getRGB());
			}
		}
	}
	
	/**
	 * @return The runnable for the repaint thread
	 */
	private Runnable makeRunnable(){
		return new Runnable(){
			@Override
			public void run() {
				long lastChange = System.currentTimeMillis();
				while(true){
					if(tileFrame == 10){
						tileFrame = 0;
					}
					if(System.currentTimeMillis() - lastChange > 4000){
						++tileFrame;//for animating tiles
						lastChange = System.currentTimeMillis();
					}
					map.repaint(TILESIZE, tileFrame);
					updatePlayerPositions();
					repaint();
					try {//delay for other threads
						Thread.sleep(50);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}			
				}
			}		
		};
	}
}