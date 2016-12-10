import java.awt.Color;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.RasterFormatException;
import java.io.IOException;
import java.util.HashMap;

import javax.imageio.ImageIO;

public class Sprite {	
	
	BufferedImage spriteSheet = null;
	public static final int TILESIZE = 64;
	public java.util.Map<String,BufferedImage> sprites = new HashMap<String,BufferedImage>();
	
	public Sprite(){
		loadSpriteSheet();
		loadSprites();
	}
	
	private void loadSprites(){
		sprites.put("bot", getSprite(0,0));
		sprites.put("bot1", getSprite(0,0));
		sprites.put("bot2", getSprite(0,1));
		sprites.put("player", getSprite(1,0));
		sprites.put("floor", getSprite(2,0));
		sprites.put("wall", getSprite(3,0));
		sprites.put("exit", getSprite(4,0));
		sprites.put("gold", getSprite(5,0));
		sprites.put("hello", getSprite(5,0));
		sprites.put("quit", getSprite(6,0));
		sprites.put("pickup", getSprite(6,1));
		sprites.put("look", getSprite(6,2));
		sprites.put("arrow", getSprite(7, 0));
		sprites.put("arrow1",getSprite(7,1));
		sprites.put("arrow2", getSprite(7,2));
		sprites.put("arrow3", getSprite(7,3));		
	}
	
	private void loadSpriteSheet(){
		try {
            spriteSheet = ImageIO.read(getClass().getResource("spritesheet.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
	}

	protected static BufferedImage getDefaultImg(){
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
	
	public BufferedImage getSprite(int x, int y){
		if(spriteSheet == null){
			return getDefaultImg();
		}
		try{
			return spriteSheet.getSubimage(y * TILESIZE, x * TILESIZE, TILESIZE, TILESIZE);
		}catch(RasterFormatException  e){
			e.printStackTrace();
		}
		return getDefaultImg();
	}	  
	
	public BufferedImage get(String name){
		try{
			return sprites.get(name);
		}catch(NullPointerException e){
			e.printStackTrace();
		}
		return getDefaultImg();
	}
}
