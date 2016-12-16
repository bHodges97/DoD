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
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.swing.JPanel;

/**
 * The panel that is use to draw LOOK in terms of graphics
 *
 */
public class PaintPanel extends JPanel{

	protected String animation = "NONE";
	private static final int TILESIZE = 64;
	
	private Map map = null;
	private BufferedImage overlay,backGround,bot;
	private java.util.Map<Player,int[]> posMap = new ConcurrentHashMap<Player,int[]>();
	private Set<Player> deadPlayers = new HashSet<Player>();
	private int[] offSet = new int[]{0,0};
	private int[] animeOffSet = new int[]{0,0};
	private int[] cameraPos = new int[]{0,0};
	private int loseAnimeFrame = 0;	
	private int frame = 0;
	private int tileFrame = 0;
	private int centerX,centerY;
	private boolean showDeath = false;
	private Font defaultFont;
	private String gameState = "NOTSTARTED";
	private Player current,main;
	private PosList posList;
	
	/**
	 * Constructor
	 */
	public PaintPanel() {
		setPreferredSize(new Dimension(350,350));
		bot = Sprite.get("bot");
		InputStream is = getClass().getResourceAsStream("font.ttf");
		setFocusable(true);
		try {
			defaultFont = Font.createFont(Font.TRUETYPE_FONT, is).deriveFont(20f);
		} catch (FontFormatException |IOException e) {
			e.printStackTrace();
		}
		
		Thread repaintThread = new Thread(makeRunnable(),"repainter");
		repaintThread.start();
	}
	
	/**
	 * Set the map to render
	 * @param map The map tp render
	 */
	protected void setMap(Map map){
		this.map = map;
		this.posList = map.getPosList();
	}
	
	/**
	 * Set the current player
	 * @param player The current player
	 * @param gameState The current game state;
	 */
	protected void setPlayer(Player player, String gameState){
		this.gameState = gameState;
		current = player;
		if(current.isMainPlayer){
			main = player;
			initialisePos();
		}
	}
	
	@Override
	public void paintComponent(Graphics g){
		super.paintComponent(g);
		Graphics2D g2d = (Graphics2D)g;
		int width =getWidth();
		int height = getHeight();		
		
		g2d.fillRect(0,0 , width, height);//fill background 
		if(map==null){//don't draw until game is properly loaded
			g2d.drawString("MAP LOAD FAILED", 0, 20);
			return;
		}else if(gameState.equals("NOTSTARTED")){
			g2d.drawString("GAME NOT STARTED", 0, 20);
			return;
		}else if(main == null){
			return;
		}
		g2d.setFont(defaultFont);
		height+=g2d.getFontMetrics().getHeight();
		centerX = width/2-TILESIZE/2;
		centerY = height/2-TILESIZE/2;
		backGround = drawFullMap();
		
		cameraPos = posMap.get(main);
		if(cameraPos == null){//set camera pos
			cameraPos = new int[]{0,0};
		}
		drawBG(g2d);//draw background
		drawPlayers(g2d);//draw player sprites
		drawAnimations(g2d);//draw any animation
		
		if(overlay==null || overlay.getWidth(null) != width || overlay.getHeight(null) != height){
			overlay = getOverlay(width,height);
		}//drawing the fog 
		g2d.drawImage(overlay, 0, 0, null);	
		//draw the ui
		drawUI(g2d,width,height);
	}
	/**
	 * @return true If no current animation,false otherwise
	 */
	protected boolean finishedMove(){
		return animation.equals("NONE");
	}
	
	/**
	 * Initialise the posMap
	 */
	protected void initialisePos(){
		for(Player player:posList){
			if(!posMap.containsKey(player)){
				posMap.put(player,posList.get(player));
				if(player instanceof HumanPlayer)
				current = player;
			}
		}
	}
	
	/**
	 * Draw animations
	 * @param g2d The graphics object to draw onto.
	 */
	private void drawAnimations(Graphics2D g2d){
		int[] posDif = PosList.subtract(posList.get(current),posMap.get(current));
		int[] camera = PosList.subtract(posList.get(current),cameraPos);
		int	x = centerX + camera[0] * TILESIZE - posDif[0]*TILESIZE;
		int y = centerY + camera[1] * TILESIZE - posDif[1]*TILESIZE;
		if(animation.equals("DEFEAT")){		
			g2d.drawImage(Sprite.getRow(8)[loseAnimeFrame], x + animeOffSet[0], y + animeOffSet[1], null);
		}
	}
	
	/**
	 * Draw the player sprites.
	 * @param g2d The graphics object to draw onto.
	 */
	private void drawPlayers(Graphics2D g2d){
		for(Player player:posMap.keySet()){
			if(player.isMainPlayer){
				continue;
			}else{
				int[] posDif = PosList.subtract(cameraPos,posMap.get(player));
				if(!isVisible(posMap.get(player))){
					return;
				}
				int x = centerX- posDif[0]*TILESIZE;
				int y = centerY- posDif[1]*TILESIZE;
				if(current.isMainPlayer){
					x-=offSet[0];//shift map differently if camera is this player
					y-=offSet[1];
				}else if(player == current){
					x+=offSet[0];
					y+=offSet[1];
					
				}
				if(offSet[0] < 0 && offSet[0] > -64){
					bot = Sprite.get("bot"); //bot facing left
				}else if(offSet[0] > 1 && offSet[0] < 64){
					bot = Sprite.get("bot1");//bot facing right
				}
				if(!deadPlayers.contains(player)){
					if(player instanceof HumanPlayer){
						g2d.drawImage(Sprite.getRow(1)[tileFrame%5],x ,y ,null);
					}else{
						g2d.drawImage(bot,x ,y ,null);
					}
				}
				
			}
		}		
	}
	
	/**
	 * Draw the background
	 * @param g2d The graphics to draw onto.
	 */
	private void drawBG(Graphics2D g2d){
		int x = centerX - cameraPos[0] * TILESIZE;
		int y = centerY - cameraPos[1] * TILESIZE;
		if (main == current) {
			x -= offSet[0];
			y -= offSet[1];
			if(offSet[0] < 0 && offSet[0] > -64){
				bot = Sprite.get("bot"); //draw which way the bot is looking
			}else if(offSet[0] > 1 && offSet[0] < 64){
				bot = Sprite.get("bot1");
			}
		}
		g2d.drawImage(backGround, x, y, null);
		if (!deadPlayers.contains(main)) {
			if(main instanceof HumanPlayer){
				g2d.drawImage(Sprite.getRow(1)[tileFrame%5],centerX ,centerY ,null);
			}else{
				g2d.drawImage(bot, centerX ,centerY ,null);
			}
		} else {
			showDeath = true;
		}
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
			int twidth = g2d.getFontMetrics().stringWidth("♪YOU WIN♪");//WIN SCREEN
			g2d.drawString("♪YOU WIN♪", (width - twidth )/2,centerY);
		}else if(gameState.equals("END")){
			g2d.setColor(Color.red);
			g2d.setFont(defaultFont.deriveFont(32f));
			int twidth = g2d.getFontMetrics().stringWidth("GAME OVER");//LOSE SCREEN
			g2d.drawString("GAME OVER", (width - twidth )/2,centerY);
		}

		int x = 0;
		int y = height-TILESIZE -TILESIZE/2;
		g2d.setColor(Color.white);
		g2d.drawImage(Sprite.get("gold") ,x,y,TILESIZE/2,TILESIZE/2, null);//draw gold count
		g2d.drawString(" "+main.getGoldCount() + "/" + map.getGoldRequired(), x + TILESIZE/2, y + TILESIZE/2-8);
		y+=TILESIZE/2 + 3;
		g2d.drawImage(Sprite.get("heart"),x,y,TILESIZE/2,TILESIZE/2,null);//draw lives count
		g2d.drawString(" "+main.lives+"/1", x + TILESIZE/2, y + TILESIZE/2-8);
		
		if(showDeath){
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
	 * Draw the map onto a buffered image
	 * @return The map as a buffered image
	 */
	private BufferedImage drawFullMap(){
		Set<int[]> wallSet = new HashSet<int[]>();
		int imageWidth = TILESIZE * map.getMapWidth();
		int imageHeight = TILESIZE * map.getMapHeight();
		BufferedImage image = new BufferedImage(imageWidth,imageHeight,BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2d = (Graphics2D)image.getGraphics();
		for(int y = 0;y < map.getMapHeight();++y){
			for(int x = 0;x < map.getMapWidth();++x){
				Image tile;
				char c =map.getTile(new int[]{x,y});
				if(c == '#'){
					tile = Sprite.get("wall");
					wallSet.add(new int[]{x,y});
				}else if(c == 'G'){
					tile = Sprite.get("gold");
				}else if(c == 'E'){
					tile = Sprite.getRow(4)[tileFrame%4];//exit is an animated tile
				}else if(c == '.'){
					tile = Sprite.get("floor");//get the relevant sprites
				}else if(c == 'X'){
					continue;
				}else{
					tile = Sprite.getDefaultImg();
				}
				g2d.drawImage(Sprite.get("floor"), x*TILESIZE, y*TILESIZE, null);//always draw floor first
				g2d.drawImage(tile,  x*TILESIZE, y*TILESIZE, null);//then tile other sprites on top
				
				for(Player deadPlayer : deadPlayers){
					int[] pos = posMap.get(deadPlayer);
					if(pos[0] == x && pos[1] == y){
						g2d.drawImage(Sprite.get("blood"),  x*TILESIZE, y*TILESIZE, null);
					}
				}
				
				if(gameState.equals("WON")){
					g2d.drawImage(Sprite.get("confetti"),x*TILESIZE, y*TILESIZE, null);
				}
			}			
		}
		for(int[] wall : wallSet){
			drawWallEdge(g2d, wall);
		}
		return image;
	}
	
	/**
	 * Draw the wall shadows
	 * @param g2d The graphics object to draw to
	 * @param wallPos the position of the wall tile
	 */
	private void drawWallEdge(Graphics2D g2d,int[] wallPos){
		g2d.setColor(new Color(0,0,0,100));
		List<int[]> neighBours = map.getAdjacentClearTiles(wallPos);
		for(int[] pos:neighBours){
			int[] dif = PosList.subtract(wallPos,pos);
			int sx = wallPos[0]*TILESIZE;
			int sy = wallPos[1]*TILESIZE;
			int swidth,sheight;
			if(dif[0]<0){
				sx+=TILESIZE;
				swidth = TILESIZE / 4;
				sheight = TILESIZE;
			}else if(dif[0]>0){
				swidth = TILESIZE / 4;
				sheight = TILESIZE;
				sx-=swidth;
			}else if(dif[1]<0){
				sy+=TILESIZE;
				swidth = TILESIZE;
				sheight = TILESIZE / 4;
			}else{
				swidth = TILESIZE;
				sheight = TILESIZE/ 4;
				sy-=sheight;
			}
			g2d.fillRect(sx, sy, swidth, sheight);//paint a transparent rectangle as the shadow
		}		
	}
	
	/**
	 * Render the fog
	 * @param width width of window
	 * @param height height of window
	 * @return The bufferedImage of the drawn overlay.
	 */
	private BufferedImage getOverlay(int width,int height){
		BufferedImage overlay = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);		
		int tileWidth = 6;
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
	 * Animate a player moving
	 * @param player the player to animate 
	 */
	private void animate(Player player){
		if(!posList.isReady())return;
		int[] pos = posList.get(player);
		int[] oldPos = posMap.get(player);
		int[] posDif = PosList.subtract(pos,oldPos);
		if(!animation.equals("NONE") && !animation.equals("MOVING") && playAnimation(animation,player)){
			return;
		}else if(posDif[0] == 0 && posDif[1] == 0){
			return;
		}
		if(Math.abs(offSet[0]) >= TILESIZE || Math.abs(offSet[1]) >= TILESIZE
				|| !isVisible(pos)){
			posMap.remove(player);
			posMap.put(player,pos);
			offSet = new int[]{0,0};
			animation = "NONE";//animation is completed
		}else{
			if(animation.equals("NONE")){
				animation = "MOVING";
			}
			offSet[0]+=posDif[0];
			offSet[1]+=posDif[1];
		}		
	}
	
	/**
	 * Check if the given tile is visible from cameraPos
	 * @param pos The tile to test
	 * @return true if tile is visible, false otherwise
	 */
	private boolean isVisible(int[] pos){
		int[] posDif = PosList.subtract(cameraPos,pos);
		if(Math.abs(posDif[0]) > 5|| Math.abs(posDif[1]) > 5){
			return false;
		}
		return true;
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
		this.animation = "NONE";
		return true;		
	}
	
	/**
	 * The defeat animation of a player
	 * @param player
	 * @return true if the animation is playing
	 */
	private boolean playDefeat(Player player){
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
				initialisePos();
				long lastChange = System.currentTimeMillis();
				while(true){
					if(frame > TILESIZE){
						frame = 0;
					}
					++frame;
					if(tileFrame > 8){
						tileFrame = 0;
					}
					if(System.currentTimeMillis() - lastChange > 2000){
						++tileFrame;//for animating tiles
						lastChange = System.currentTimeMillis();
					}
					try{
						animate(current);
					}catch(Exception e){
						e.printStackTrace();
					}
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