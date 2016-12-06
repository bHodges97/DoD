import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

public class PaintPanel extends JPanel{

	private static final int TILESIZE = 64;
	
	private Map map = null;
	private BufferedImage overlay,backGround;
	private Image bot,player,wall,floor,gold,exit;
	private int frame = 0;
	private java.util.Map<Player,int[]> posMap = new HashMap<Player,int[]>();
	private Set<int[]> wallSet;
	private Player current;
	private int[] offSet = new int[]{0,0};
	private boolean started = false;
	private boolean mapNeedsUpdate = true;
	private boolean playLoseAnimation = false;
	private int loseAnimFrame = 0;
	
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
					if(frame > 10){
						frame = 0;
					}
					try{
						update(current);
					}catch(Exception e){
						e.printStackTrace();
					}
					repaint();
					frame++;
					try {
						Thread.sleep(2);
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
		mapNeedsUpdate = true;
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
			g2d.drawString("GAME NOT STARTED", 0, 20);
			return;
		}
		if(true){
			//check player exisit
		}
		String title = map.getMapName();		
		g2d.setFont(new Font("monospaced", Font.BOLD, 20));
		int titleWidth = g2d.getFontMetrics().stringWidth(title);
		int titleHeight = g2d.getFontMetrics().getHeight();
		height+=titleHeight;
		
		int centerX = width/2-TILESIZE/2;
		int centerY = height/2-TILESIZE/2;
		
		if(backGround == null || mapNeedsUpdate){
			backGround = drawFullMap();
			mapNeedsUpdate = false;
		}
		
		
		
		
		int x,y;
		int[] cameraPos = new int[]{0,0};
		for(Player player:posMap.keySet()){
			if(player.isMainPlayer){
				cameraPos = posMap.get(player);
				x = centerX - cameraPos[0]*TILESIZE;
				y = centerY - cameraPos[1]*TILESIZE;
				if(player == current){
					x-=offSet[0];
					y-=offSet[1];
				}
				g2d.drawImage(backGround, x, y, null);

				g2d.drawString(""+(player instanceof HumanPlayer), 0, 30);
				break;
			}
		}
		
		
		for(Player player:posMap.keySet()){
			if(player.isMainPlayer){
				continue;
			}else{
				int[] posDif = posDif(cameraPos,posMap.get(player));
				x = centerX- posDif[0]*TILESIZE;
				y = centerY- posDif[1]*TILESIZE;
				if(!current.isMainPlayer){
					x+=offSet[0];
					y+=offSet[1];
				}else{
					x-=offSet[0];
					y-=offSet[1];
				}
				g2d.drawImage(bot,x ,y ,null);
			}
		}	
		if(overlay==null || overlay.getWidth(null) != width || overlay.getHeight(null) != height){
			overlay = getOverlay(width,height);
		}
		g2d.drawImage(player, centerX, centerY, null);		
		g2d.drawImage(overlay, 0, 0, null);
		g2d.drawString(title, (width-titleWidth)/2, titleHeight) ;
	}
	
	protected void update(Player player){
		if(!map.isReady())return;
		int[] pos = map.getPosition(player);
		int[] oldPos = posMap.get(player);
		int[] posDif = posDif(pos,oldPos);
		if(posDif[0] == 0 && posDif[1] == 0){
			offSet = new int[]{0,0};
			if(map.hasOverLap(pos)){
				//playDefeat(player);
				//return;
			}
		}
		if(!finishedMove()){
			offSet[0]+=posDif[0];
			offSet[1]+=posDif[1];
		}else{
			offSet = new int[]{TILESIZE,TILESIZE};
			posMap.remove(player);
			posMap.put(player,pos);
		}		
	}
	private void playDefeat(Player player){
		playLoseAnimation = true;
		if(loseAnimFrame == 10){
			playLoseAnimation = false;
		}else{
			return;
		}
		int[] pos = map.getPosition(player);
		int[] oldPos = posMap.get(player);
		int[] posDif = posDif(pos,oldPos);
		if(!finishedMove()){
			offSet[0]+=posDif[0];
			offSet[1]+=posDif[1];
		}else{
			offSet = new int[]{TILESIZE,TILESIZE};
			posMap.remove(player);
			posMap.put(player,pos);
		}
	}
	
	private boolean finishedMove(){
		if(Math.abs(offSet[0])>=TILESIZE || Math.abs(offSet[1]) >= TILESIZE){
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
		int halfTile = TILESIZE/2;
		BufferedImage defaultImg = new BufferedImage(TILESIZE,TILESIZE,BufferedImage.TYPE_INT_ARGB);
		for(int x = 0;x < TILESIZE;++x){
			for(int y = 0;y< TILESIZE;++y){
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
	
	private BufferedImage drawFullMap(){
		wallSet = new HashSet<int[]>();
		int imageWidth = TILESIZE * map.getMapWidth();
		int imageHeight = TILESIZE * map.getMapHeight();
		BufferedImage image = new BufferedImage(imageWidth,imageHeight,BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2d = (Graphics2D)image.getGraphics();
		for(int y = 0;y < map.getMapHeight();++y){
			for(int x = 0;x < map.getMapWidth();++x){
				Image tile;
				char c =map.getTile(new int[]{x,y});
				if(c == '#'){
					tile = wall;
					wallSet.add(new int[]{x,y});
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
				g2d.drawImage(floor, x*TILESIZE, y*TILESIZE, null);
				g2d.drawImage(tile,  x*TILESIZE, y*TILESIZE, null);
			}			
		}
		for(int[] wall : wallSet){
			drawWallEdge(image, wall);
		}
		return image;
	}
	public static int[] posDif(int[] a,int[] b){
		return new int[]{a[0]-b[0],a[1]-b[1]};
	}
	
	private void drawWallEdge(BufferedImage image,int[] wallPos){
		Graphics2D g2d = (Graphics2D) image.getGraphics();
		g2d.setColor(new Color(0,0,0,100));
		List<int[]> neighBours = map.getAdjacentClearTiles(wallPos);
		for(int[] pos:neighBours){
			int[] dif = posDif(wallPos,pos);
			int sx = wallPos[0]*TILESIZE;
			int sy = wallPos[1]*TILESIZE;
			int swidth,sheight;
			if(dif[0]<0){
				sx+=TILESIZE;
				swidth = TILESIZE / 8;
				sheight = TILESIZE;
			}else if(dif[0]>0){
				swidth = TILESIZE / 8;
				sheight = TILESIZE;
				sx-=swidth;
			}else if(dif[1]<0){
				sy+=TILESIZE;
				swidth = TILESIZE;
				sheight = TILESIZE / 8;
			}else{
				swidth = TILESIZE;
				sheight = TILESIZE/ 8;
				sy-=sheight;
			}
			g2d.fillRect(sx, sy, swidth, sheight);
		}		
	}
	
	
	private BufferedImage getOverlay(int width,int height){
		BufferedImage overlay = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);		
		int tileWidth = 5;
		int maskDiameter = tileWidth * TILESIZE;
		int maskRadius = maskDiameter/2;
		float ratio = ((float)255/(float)maskRadius) * 0.9f;
		for(int x = 0;x < width;++x){
			for(int y = 0;y < height;++y){
				int dist = (int) Point2D.distance(x, y, width/2,height/2);
				int alpha = dist*ratio > 255?255:(int)(dist*ratio);
				Color c = new Color(0,0,0,alpha);
				overlay.setRGB(x, y,c.getRGB());
			}
		}
		return overlay;
	}
}