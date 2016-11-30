import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

@SuppressWarnings("serial")
public class PaintPanel extends JPanel{
	
	Map map = null;
	Image bot,player,wall,floor,gold,exit;
	
	
	public PaintPanel() {
		super.repaint();
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
		g2d.drawString(title, width/2-title.length()*5, 20);
		
		int centerX = width/2-32;
		int centerY = height/2-32;
		int[] playerPos = map.getPlayersPosition();
		int[] botPos = map.getBotsPosition();
		for(int y = playerPos[1]-2;y < playerPos[1] + 3;++y){
			for(int x = playerPos[0]-2;x < playerPos[0] + 3;++x){
				Image tile;
				char c =map.getTile(new int[]{x,y});
				if(x == botPos[0] && y == botPos[1]){
					tile = bot;
				}else if(x == playerPos[0] && y == playerPos[1]){
					tile = player;
				}else if(c == '#'){
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
				g2d.drawImage(floor, (x-playerPos[0])*64+centerX, (y-playerPos[1])*64+centerY, null);
				g2d.drawImage(tile, (x-playerPos[0])*64+centerX, (y-playerPos[1])*64+centerY, null);
			}			
		}
		
	//	g2d.drawImage(bot,0,0,null);
		
	}
	
	protected Image getDefaultImg(){
		BufferedImage defaultImg = new BufferedImage(64,64,BufferedImage.TYPE_INT_ARGB);
		for(int x = 0;x < 64;++x){
			for(int y = 0;y<64;++y){
				if((x<32&&y<32) ||( x>=32&&y>=32)){
					defaultImg.setRGB(x, y, new Color(255,0,220).getRGB());//purple
				}else{
					defaultImg.setRGB(x, y, Color.black.getRGB());
				}
			}
		}
		return defaultImg;
	}
}
