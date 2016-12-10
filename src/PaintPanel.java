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
	private BufferedImage overlay,backGround,bot;
	private int frame = 0;
	private java.util.Map<Player,int[]> posMap = new HashMap<Player,int[]>();
	private Set<int[]> wallSet;
	private Player current;
	private int[] offSet = new int[]{0,0};
	private boolean playLoseAnimation = false;
	private int loseAnimeFrame = 0;
	private int[] animeOffSet = new int[]{0,0};
	private Font defaultFont;
	private int tileFrame = 0;
	private String gameState = "NOTSTARTED";
	
	
	public PaintPanel() {
		super.repaint();
		setPreferredSize(new Dimension(350,350));
		bot = Sprite.get("bot");
		
		Runnable runner = new Runnable(){
			@Override
			public void run() {
				while(map==null || gameState.equals("NOTSTARTED")){
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
					if(frame > TILESIZE){
						frame = 0;
						if(tileFrame > 10){
							tileFrame = 0;
						}
						if(System.currentTimeMillis() % 10 == 0)
						++tileFrame;
					}
					++frame;
					try{
						update(current);
					}catch(Exception e){
						e.printStackTrace();
					}
					repaint();
					try {
						Thread.sleep(4);
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
	protected void setPlayer(Player player, String gameState){
		this.gameState = gameState;
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
		if(gameState.equals("NOTSTARTED")){
			g2d.drawString("GAME NOT STARTED", 0, 20);
			return;
		}
		if(true){
			//check player exisit
		}
		String title = map.getMapName();		
		defaultFont = new Font("monospaced", Font.BOLD, 20);
		g2d.setFont(defaultFont);
		int titleWidth = g2d.getFontMetrics().stringWidth(title);
		int titleHeight = g2d.getFontMetrics().getHeight();
		height+=titleHeight;
		
		int centerX = width/2-TILESIZE/2;
		int centerY = height/2-TILESIZE/2;
		backGround = drawFullMap();
		
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
				break;
			}
		}
		
		
		for(Player player:posMap.keySet()){
			if(player.isMainPlayer){
				continue;
			}else{
				int[] posDif = PosList.subtract(cameraPos,posMap.get(player));
				x = centerX- posDif[0]*TILESIZE;
				y = centerY- posDif[1]*TILESIZE;
				if(!current.isMainPlayer){
					x+=offSet[0];
					y+=offSet[1];
				}else{
					x-=offSet[0];
					y-=offSet[1];
				}
				if(offSet[0] < 0 && offSet[0] > -64){
					bot = Sprite.get("bot"); 
				}else if(offSet[0] > 1 && offSet[0] < 64){
					bot = Sprite.get("bot1");
				}
				g2d.drawImage(bot,x ,y ,null);
			}
		}
		if(overlay==null || overlay.getWidth(null) != width || overlay.getHeight(null) != height){
			overlay = getOverlay(width,height);
		}
		g2d.drawImage(overlay, 0, 0, null);		
		if(loseAnimeFrame != 4){
			g2d.drawImage(Sprite.get("player"), centerX, centerY, null);
		}else{
			g2d.setColor(new Color(frame%255,0,0));
			g2d.setFont(defaultFont.deriveFont(32f));
			int twidth = g2d.getFontMetrics().stringWidth("GAME OVER");
			g2d.drawString("GAME OVER", (width - twidth )/2,centerY);
		}
		g2d.setFont(defaultFont);
		g2d.setColor(Color.LIGHT_GRAY);
		if(playLoseAnimation){
			BufferedImage[] animation = Sprite.getRow(8);
			int[] posDif = PosList.subtract(cameraPos,posMap.get(current));
			x = centerX- posDif[0]*TILESIZE;
			y = centerY- posDif[1]*TILESIZE;
			g2d.drawImage(animation[loseAnimeFrame], x + animeOffSet[0], y + animeOffSet[1], null);
		}
		g2d.drawString(title, (width-titleWidth)/2, titleHeight) ;
	}
	
	protected void update(Player player){
		if(!map.isReady())return;
		int[] pos = map.getPosition(player);
		int[] oldPos = posMap.get(player);
		int[] posDif = PosList.subtract(pos,oldPos);
		if(posDif[0] == 0 && posDif[1] == 0){
			offSet = new int[]{0,0};
		}
		if(gameState.equals("LOST") && playDefeat(player)){
			return;
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
	private boolean playDefeat(Player player){
		int[] pos = map.getPosition(player);
		int[] oldPos = posMap.get(player);
		int[] posDif = PosList.subtract(pos,oldPos);
		playLoseAnimation = true;
		if(loseAnimeFrame == 4){
			playLoseAnimation = false;
			return false;
		}else if(loseAnimeFrame == 0 && frame == TILESIZE){
			int x =  TILESIZE * posDif[0] / 4;
			int y =  TILESIZE * posDif[1] / 4;
			animeOffSet = new int[]{x,y};
			++loseAnimeFrame;
			return true;
		}
		if(frame == TILESIZE){
			animeOffSet[0]+= TILESIZE  / 4 * posDif[0];
			animeOffSet[1]+= TILESIZE  / 4 * posDif[1];
			++loseAnimeFrame;
		}
		return true;
	}
	
	private boolean finishedMove(){
		if(Math.abs(offSet[0]) >= TILESIZE || Math.abs(offSet[1]) >= TILESIZE){
			return true;
		}
		return false;
	}
	
	protected void initialisePos(){
		PosList mapList = map.getPlayerPosList();
		for(Player player:mapList){
			if(!posMap.containsKey(player)){
				posMap.put(player,mapList.get(player).clone());
				if(player instanceof HumanPlayer)
				current = player;
			}
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
					tile = Sprite.get("wall");
					wallSet.add(new int[]{x,y});
				}else if(c == 'G'){
					tile = Sprite.get("gold");
				}else if(c == 'E'){
					tile = Sprite.getRow(4)[tileFrame%4];
				}else if(c == '.'){
					tile = Sprite.get("floor");
				}else if(c == 'X'){
					continue;
				}else{
					tile = Sprite.getDefaultImg();
				}
				g2d.drawImage(Sprite.get("floor"), x*TILESIZE, y*TILESIZE, null);
				g2d.drawImage(tile,  x*TILESIZE, y*TILESIZE, null);
			}			
		}
		for(int[] wall : wallSet){
			drawWallEdge(image, wall);
		}
		return image;
	}
	
	private void drawWallEdge(BufferedImage image,int[] wallPos){
		Graphics2D g2d = (Graphics2D) image.getGraphics();
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