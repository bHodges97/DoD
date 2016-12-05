import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

public class PaintPanel extends JPanel{
	
	private Map map = null;
	private Image backGround,bot,player,wall,floor,gold,exit;
	private int tileSize = 64;
	private int frame = 0;
	private java.util.Map<Player,int[]> posMap = new HashMap<Player,int[]>();
	private Player current;
	private int[] offSet = new int[]{0,0};
	private boolean started = false;
	
	public PaintPanel() {
		super.repaint();
		setPreferredSize(new Dimension(350,350));
		loadSprites();
		
		Runnable runner = new Runnable(){
			@Override
			public void run() {
				while(map==null || !started){
					try {
						Thread.sleep(1);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}			
				}
				initialisePos();
				// TODO Auto-generated method stub
				while(true){
					update(current);
					repaint();
					try {
						Thread.sleep(10);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}			
				}
			}			
		};
		Thread repaintThread = new Thread(runner,"repainter");
		repaintThread.start();
	}
	
	protected void setMap(Map map){
		this.map = map;
	}
	protected void setPlayer(Player player){
		started = true;
		current = player;
	}
	
	@Override
	public void paintComponent(Graphics g){
		//super.paint(g); this line is VERY BAD
		Graphics2D g2d = (Graphics2D)g;
		int width =getWidth();
		int height = getHeight();	
		
		g2d.fillRect(0,0 , width, height);
		g2d.setColor(Color.WHITE);
		if(map==null){
			g2d.drawString("MAP LOAD FAILED", 0, 20);
			return;
		}
		if(!started){
			g2d.drawString("NO PLAYERS", 0, 20);
			return;
		}
		String title = map.getMapName();		
		g2d.setFont(new Font("monospaced", Font.BOLD, 20));
		int titleWidth = g2d.getFontMetrics().stringWidth(title);
		int titleHeight = g2d.getFontMetrics().getHeight();
		height+=titleHeight;
		
		int centerX = width/2-tileSize/2;
		int centerY = height/2-tileSize/2;
		
		if( backGround == null){
			backGround = drawMap();
		}
		if(current instanceof HumanPlayer){
			backGround = drawMap();
		}
		int bgWidth = backGround.getWidth(null);
		int bgHeight = backGround.getHeight(null);
		
		
		if(current instanceof HumanPlayer){
			g2d.drawImage(backGround, centerX-bgWidth/2+tileSize/2-offSet[0], centerY-bgHeight/2+tileSize/2-offSet[1],null);
			
		}else{
			g2d.drawImage(backGround, centerX-bgWidth/2+tileSize/2,centerY-bgHeight/2+tileSize/2,null);
		}
		g2d.drawImage(player, centerX, centerY, null);
		
		
		Player botPlayer = getBot();
		int[] botOffSet = posRelativeToCamera(botPlayer);
		int x,y;
		if(current instanceof BotPlayer && !finishedMove()){
			botOffSet = posDif(map.getPlayersPosition(),posMap.get(current));
			x = centerX-botOffSet[0]*tileSize+offSet[0];
			y = centerY-botOffSet[1]*tileSize+offSet[1];
		}else{
			botOffSet = posDif(map.getPlayersPosition(),map.getBotsPosition());
			x = centerX-botOffSet[0]*tileSize-offSet[0];
			y = centerY-botOffSet[1]*tileSize-offSet[1];
		}
		g2d.drawImage(bot,x ,y ,null);
		
		
		Image overlay = getOverlay(width,height);
		g2d.drawImage(overlay, 0, 0, null);
		
		g2d.drawString(title+frame+":"+offSet[0]+","+offSet[1], (width-titleWidth)/2, titleHeight) ;
	}
	
	protected void update(Player player){
		//if(player instanceof Player)return;

		int[] pos = map.getPosition(player);
		int[] oldPos = posMap.get(player);
		int[] posDif = posDif(pos,oldPos);
		if(posDif[0] == 0 && posDif[1] == 0){
			offSet = new int[]{0,0};
		}
		if(!finishedMove()){
			offSet[0]+=posDif[0]*4;
			offSet[1]+=posDif[1]*4;
		}else{
			offSet = new int[]{tileSize,tileSize};
			posMap.remove(player);
			posMap.put(player,pos);
		}		
		
	}
	private boolean finishedMove(){
		if(Math.abs(offSet[0])>=tileSize || Math.abs(offSet[1]) >= tileSize){
			return true;
		}
		return false;
	}
	
	protected void initialisePos(){
		PlayerPosList mapList = map.getPlayerPosList();
		for(Player player:mapList){
			if(!posMap.containsKey(player)){
				posMap.put(player,mapList.get(player).clone());
				if(player instanceof HumanPlayer)
				current = player;
			}
		}
	}

	protected Image getDefaultImg(){
		int halfTile = tileSize/2;
		BufferedImage defaultImg = new BufferedImage(tileSize,tileSize,BufferedImage.TYPE_INT_ARGB);
		for(int x = 0;x < tileSize;++x){
			for(int y = 0;y< tileSize;++y){
				if(( x < halfTile && y < halfTile) ||( x >= halfTile && y >= halfTile)){
					defaultImg.setRGB(x, y, new Color(255,0,220).getRGB());//purple
				}else{
					defaultImg.setRGB(x, y, Color.black.getRGB());
				}
			}
		}
		return defaultImg;
	}
	

	private void loadSprites(){
	try {
			bot = ImageIO.read(getClass().getResource("bot.png"));
			player = ImageIO.read(getClass().getResourceAsStream("player.png"));
			floor = ImageIO.read(getClass().getResource("floor.png"));
			wall =ImageIO.read(getClass().getResource("wall.png"));
			exit = ImageIO.read(getClass().getResource("exit.png"));
			gold = ImageIO.read(getClass().getResource("hello.png"));
		} catch (IOException e) {
			Image defaultImg = getDefaultImg();
			bot = defaultImg;
			player = defaultImg;
			floor= defaultImg;
			wall = defaultImg;
			exit = defaultImg;
			gold = defaultImg;
			e.printStackTrace();
		}
	}
	
	private Image drawMap(){
		int tilesWide = 11;//Must be odd
		int imageWidth = tileSize * tilesWide;
		int imageHeight = tileSize * tilesWide;
		BufferedImage image = new BufferedImage(imageWidth,imageHeight,BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2d = (Graphics2D)image.getGraphics();
		int[] playerPos = posMap.get(current);
		int offsety = playerPos[1]-(tilesWide-1)/2;
		int offsetx = playerPos[0]-(tilesWide-1)/2;
		for(int y = offsety;y < offsety + tilesWide;++y){
			for(int x = offsetx;x < offsetx + tilesWide;++x){
				Image tile;
				char c =map.getTile(new int[]{x,y});
				if(c == '#'){
					tile = wall;
				}else if(c == 'G'){
					tile = gold;
				}else if(c == 'E'){
					tile = exit;
				}else if(c == '.'){
					tile = floor;
				}else if(c == 'X'){
					continue;
				}else{
					tile = getDefaultImg();
				}
				g2d.drawImage(floor, (x-offsetx)*tileSize, (y-offsety)*tileSize, null);
				g2d.drawImage(tile, (x-offsetx)*tileSize, (y-offsety)*tileSize, null);
			}			
		}
		g2d.setColor(Color.red);
		//g2d.drawRect(0, 0, imageWidth-1, imageHeight-1);
		//g2d.drawLine(0, 0, imageWidth,imageHeight);
		//g2d.drawLine(0, imageHeight, imageWidth, 0);
		return image;
	}
	
	private int[] posRelativeToCamera(Player player){
		int[] current = map.getPosition(player);
		int[] center  = map.getPlayersPosition();
		return posDif(center,current);
	}
	
	private int[] posDif(int[] a,int[] b){
		return new int[]{a[0]-b[0],a[1]-b[1]};
	}
	
	private Player getBot(){
		for(Player player:posMap.keySet()){
			if(player instanceof BotPlayer){
				return player;
			}
		}
		return null;
	}
	
	private Image getOverlay(int width,int height){
		BufferedImage overlay = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2d = (Graphics2D)overlay.getGraphics();
		g2d.setColor(Color.DARK_GRAY);
		g2d.fillRect(0, 0, width, height);
		g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.CLEAR));
		g2d.fillOval(width/2-160, height/2-160, 320, 320);
		
		
		return overlay;
	}
}
