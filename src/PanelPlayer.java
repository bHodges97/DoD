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

	private static final int TILESIZE = 64;
	private BufferedImage overlay,backGround;
	private Map map = null;
	private int tileFrame = 0;
	private int centerX,centerY;
	private Font defaultFont;	
	private List<LobbyPlayer> lobbyPlayers = new ArrayList<LobbyPlayer>();
	private LobbyPlayer clientPlayer = new LobbyPlayer(0);	
	private GameLogic.GameState gameState = GameLogic.GameState.NOTSTARTED;
	private Position cameraPos,center;
	private Client client;
	
	
	/**
	 * Constructor
	 */
	public PanelPlayer(Client client) {
		setPreferredSize(new Dimension(350,350));
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
		this.lobbyPlayers = client.lobbyPlayers;
		this.clientPlayer = client.clientPlayer;
		this.gameState = client.gameState;
		this.cameraPos = clientPlayer.currentPos;
		Thread repaintThread = new Thread(makeRunnable(),"repainter");
		repaintThread.start();
	}
	
	
	@Override
	public void paintComponent(Graphics g){
		super.paintComponent(g);
		Graphics2D g2d = (Graphics2D)g;
		int width =getWidth();
		int height = getHeight();		
		if(map==null || map.getMapWidth() == 0){
			//don't draw until game is properly loaded
			g2d.drawString("MAP LOAD FAILED", 0, 20);
			return;
		}else if(gameState == GameLogic.GameState.NOTSTARTED){
			g2d.drawString("GAME NOT STARTED", 0, 20);
			return;
		}else if(clientPlayer.visible == false){
			g2d.drawString("WAITING FOR PLAYER TO LOAD",0,20);
			return;
		}else{
			g2d.drawString("RENDERING "+cameraPos, 0, 20);
		}
		g2d.setFont(defaultFont);
		height+=g2d.getFontMetrics().getHeight();
		centerX = width/2-TILESIZE/2;
		centerY = height/2-TILESIZE/2;
		center = new Position(centerX,centerY);
		drawBackground(g2d);//draw background
		drawPlayers(g2d);//draw player sprites
		drawAnimations(g2d);//draw any animation
		
		if(overlay==null || overlay.getWidth(null) != width || overlay.getHeight(null) != height){
			overlay = getOverlay(width,height);
		}//drawing the fog 
		g2d.drawImage(overlay, 0, 0, null);	
		//draw the ui
		drawUI(g2d,width,height);
		g2d.drawString("RENDERING"+cameraPos, 0, 20);
	}
	
	/**
	 * Draw animations
	 * @param g2d The graphics object to draw onto.
	 */
	private void drawAnimations(Graphics2D g2d){
		//TODO:
		/*
		int[] posDif = PosList.subtract(posList.get(current),posMap.get(current));
		int[] camera = PosList.subtract(posList.get(current),cameraPos);
		int	x = centerX + camera[0] * TILESIZE - posDif[0]*TILESIZE;
		int y = centerY + camera[1] * TILESIZE - posDif[1]*TILESIZE;
		if(animation.equals("DEFEAT")){		
			g2d.drawImage(Sprite.getRow(8)[loseAnimeFrame], x + animeOffSet[0], y + animeOffSet[1], null);
		}
		*/
	}
	
	/**
	 * Draw the player sprites.
	 * @param g2d The graphics object to draw onto.
	 */
	private void drawPlayers(Graphics2D g2d){
		for(LobbyPlayer player:lobbyPlayers){
			if(player.visible){
				Position posDif = Position.subtract(cameraPos, player.currentPos);
				Position drawPos = Position.subtract(center, posDif);
				g2d.drawImage(player.toPlayer().getImages()[tileFrame%5],drawPos.x,drawPos.y,null);
			}
		}	
	}
	
	/**
	 * Draw the background
	 * @param g2d The graphics to draw onto.
	 */
	private void drawBackground(Graphics2D g2d){
		int x = centerX - cameraPos.x;
		int y = centerY - cameraPos.y;
		g2d.drawImage(map.getImages()[0], x, y, null);
	}
	
	/**
	 * Draw the ui
	 * @param g2d The graphics object to draw onto.
	 * @param width The width of the ui
	 * @param height The height of the ui
	 */
	private void drawUI(Graphics2D g2d,int width,int height){		
		if(gameState.equals("WON")){
			g2d.setColor(Color.orange);
			g2d.setFont(defaultFont.deriveFont(32f));
			int twidth = g2d.getFontMetrics().stringWidth("♪YOU WIN♪");
			g2d.drawString("♪YOU WIN♪", (width - twidth )/2,centerY);
		}else if(gameState.equals("END")){
			g2d.setColor(Color.red);
			g2d.setFont(defaultFont.deriveFont(32f));
			int twidth = g2d.getFontMetrics().stringWidth("GAME OVER");
			g2d.drawString("GAME OVER", (width - twidth )/2,centerY);
		}

		int x = 0;
		int y = height-TILESIZE -TILESIZE/2;
		g2d.setColor(Color.white);
		g2d.drawImage(new ItemGold().getImages()[0],x,y,TILESIZE/2,TILESIZE/2, null);//draw gold count
		g2d.drawString(" "+clientPlayer.inventory.getItemCount("GOLD") + "/" + map.getGoldRequired(), x + TILESIZE/2, y + TILESIZE/2-8);
		y+=TILESIZE/2 + 3;
		//draw lives count
		g2d.drawImage(Sprite.getSprite(3, 6),x,y,TILESIZE/2,TILESIZE/2,null);
		g2d.drawString(" "+1+"/1", x + TILESIZE/2, y + TILESIZE/2-8);//TODO:
		
		if(!clientPlayer.visible){//TODO:player dead temp solution
			g2d.setColor(Color.orange);
			g2d.setFont(defaultFont.deriveFont(25f));
			int twidth = g2d.getFontMetrics().stringWidth("YOU ARE DEAD");//player death
			g2d.drawString("YOU ARE DEAD", (width - twidth )/2,centerY+30);		
		}
		
		g2d.setFont(defaultFont);
		g2d.setColor(Color.LIGHT_GRAY);

		String title = map.getMapName();
		int titleHeight = g2d.getFontMetrics().getHeight();
		int titleWidth = g2d.getFontMetrics().stringWidth(title);
		g2d.drawString(title, (width-titleWidth)/2, titleHeight) ;
	}
	
	
	
	
	
	/**
	 * Render the fog
	 * @param width width of window
	 * @param height height of window
	 * @return The bufferedImage of the drawn overlay.
	 */
	private BufferedImage getOverlay(int width,int height){
		BufferedImage overlay = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);		
		int tileWidth = 5;
		int maskDiameter = tileWidth * TILESIZE+10;
		int maskRadius = maskDiameter/2;
		float ratio = ((float)255/(float)maskRadius) * 0.9f;
		for(int x = 0;x < width;++x){
			for(int y = 0;y < height;++y){
				int dist = (int) Point2D.distance(x, y, width/2,height/2);
				int alpha = dist*ratio > 255?255:(int)(dist*ratio);
				Color c = new Color(0,0,0,alpha);//becomes more solid black as further from center
				overlay.setRGB(x, y,c.getRGB());
			}
		}
		return overlay;
	}
	
	/**
	 * Play the animation 
	 * @param animation The animation to play
	 * @param player The player to play it on
	 * @return true if animation is playing
	 */
	private boolean playAnimation(String animation,Player player){
		if(animation.equals("DEFEAT")){
			return playDefeat(player);
		}
		//this.animation = "NONE";
		return true;		
	}
	
	/**
	 * The defeat animation of a player
	 * @param player
	 * @return true if the animation is playing
	 */
	private boolean playDefeat(Player player){
		/*
		int[] pos = posList.get(player);
		int[] oldPos = posMap.get(player);
		int[] posDif = PosList.subtract(pos,oldPos);

		if(loseAnimeFrame == 4){
			animation = "NONE";
			for(Player playerAtPos:posList.getPlayers(pos)){
				if(playerAtPos != player){
					deadPlayers.add(playerAtPos);
				}
			}			//animating finished
			loseAnimeFrame = 0;
			return false;
		}else if(loseAnimeFrame == 0 && frame >= TILESIZE){
			int x =  TILESIZE * posDif[0] / 4;
			int y =  TILESIZE * posDif[1] / 4;
			animeOffSet = new int[]{x,y};
			++loseAnimeFrame;
			return true;
		}
		if(frame >= TILESIZE){//move sprite across
			animeOffSet[0]+= TILESIZE  / 4 * posDif[0];
			animeOffSet[1]+= TILESIZE  / 4 * posDif[1];
			++loseAnimeFrame;
		}
		*/
		return true;
	}
	
	/**
	 * @return The runnable for the repaint thread
	 */
	private Runnable makeRunnable(){
		return new Runnable(){
			@Override
			public void run() {
				while(map==null || gameState.equals("NOTSTARTED")){
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}			
				}
				long lastChange = System.currentTimeMillis();
				while(true){
					if(tileFrame > 8){
						tileFrame = 0;
					}
					if(System.currentTimeMillis() - lastChange > 2000){
						++tileFrame;//for animating tiles
						lastChange = System.currentTimeMillis();
					}
					gameState = client.gameState;
					/*
					try{
						animate(current);
					}catch(Exception e){
						e.printStackTrace();
					}
					*/
					repaint();
					try {//delay for other threads
						Thread.sleep(4);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}			
				}
			}			
		};
	}
}