import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

public class PaintPanel extends JPanel{
	
	private Map map = null;
	private Image bot,player,wall,floor,gold,exit;
	private int tileSize = 64;
	
	public PaintPanel() {
		super.repaint();
		setPreferredSize(new Dimension(350,350));
		loadSprites();
		
		Runnable runner = new Runnable() {
		      public void run() {
		    	  while(true){
			    	  repaint();
			    	  try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
		    	  }
		      }
		 };
		 Thread repaintThread = new Thread(runner, "repaint thread");
		 repaintThread.start();;
	}
	
	protected void setMap(Map map){
		this.map = map;
	}
	
	
	@Override
	public void paintComponent(Graphics g){
		Graphics2D g2d = (Graphics2D)g;
		int width =getWidth();
		int height = getHeight();	
		
		g2d.fillRect(0,0 , width, height);
		g2d.setColor(Color.WHITE);
		if(map==null){
			g2d.drawString("MAP LOAD FAILED", 0, 20);
			return;
		}
		String title = map.getMapName();		
		g2d.setFont(new Font("monospaced", Font.BOLD, 20));
		int titleWidth = g2d.getFontMetrics().stringWidth(title);
		int titleHeight = g2d.getFontMetrics().getHeight();
		g2d.drawString(title, (width-titleWidth)/2, titleHeight) ;
		height+=titleHeight;
		
		int centerX = width/2-tileSize/2;
		int centerY = height/2-tileSize/2;
		
		Image backGround = drawMap();
		int bgWidth = backGround.getWidth(null);
		int bgHeight = backGround.getHeight(null);
		g2d.drawImage(backGround, centerX-bgWidth/2+tileSize/2, centerY-bgHeight/2+tileSize/2,null);
		g2d.drawImage(player, centerX, centerY, null);
		
	//	g2d.drawImage(bot,0,0,null);
		
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
		int imageWidth = tileSize * 7;
		int imageHeight = tileSize * 7;
		BufferedImage image = new BufferedImage(imageWidth,imageHeight,BufferedImage.TYPE_INT_ARGB);
		
		Graphics2D g2d = (Graphics2D)image.getGraphics();
		int[] playerPos = map.getPlayersPosition();
		int offsety = playerPos[1]-3;
		int offsetx = playerPos[0]-3;
		for(int y = offsety;y <= offsety + 6;++y){
			for(int x = offsetx;x <= offsetx + 6;++x){
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
		g2d.drawRect(0, 0, imageWidth-1, imageHeight-1);
		g2d.drawLine(0, 0, imageWidth,imageHeight);
		g2d.drawLine(0, imageHeight, imageWidth, 0);
		return image;
	}
}
