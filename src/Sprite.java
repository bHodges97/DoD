import java.awt.Color;
import java.awt.image.BufferedImage;
import java.awt.image.RasterFormatException;
import java.io.IOException;
import java.util.HashMap;

import javax.imageio.ImageIO;

/**
 * Sprite sheet class
 *
 */
public class Sprite {	
	
	private static BufferedImage spriteSheet = null;
	private static final int TILESIZE = 64;
	private static java.util.Map<String,BufferedImage> sprites = new HashMap<String,BufferedImage>();
	
	
	private Sprite(){//whole class is static;
	}
	
	static{
		loadSpriteSheet();
		loadSprites();
	}
	
	/**
	 * Add sprites to the map
	 */
	private static void loadSprites(){
		sprites.put("bot", getSprite(0,0));
		sprites.put("bot1", getSprite(1,0));
		sprites.put("player", getSprite(0,1));
		sprites.put("floor", getSprite(0,2));
		sprites.put("wall", getSprite(0,3));
		sprites.put("exit", getSprite(0,4));
		sprites.put("gold", getSprite(0,5));
		sprites.put("hello", getSprite(0,5));
		sprites.put("quit", getSprite(0,6));
		sprites.put("pickup", getSprite(1,6));
		sprites.put("look", getSprite(2,6));
		sprites.put("heart", getSprite(3,6));
		sprites.put("arrow", getSprite(0,7));
		sprites.put("arrow1",getSprite(1,7));
		sprites.put("arrow2", getSprite(2,7));
		sprites.put("arrow3", getSprite(3,7));		
		sprites.put("confetti", getSprite(4,7));	
		sprites.put("blood", getSprite(0,9));		
	}
	
	/**
	 * Load a sprite sheet
	 */
	private static void loadSpriteSheet(){
		try {
            spriteSheet = ImageIO.read(Sprite.class.getResource("spritesheet.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
	}
	
	/**
	 * @return The default image if spritesheet didn't load
	 */
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
	
	/**
	 * Return the sprite at the location in the sprite sheet.
	 * @param x The x position of the sprite
	 * @param y The y position of the sprite
	 * @return The sprite at the given location.
	 */
	protected static BufferedImage getSprite(int x, int y){
		if(spriteSheet == null){
			return getDefaultImg();
		}
		try{//x and  y inverse is intentional
			return spriteSheet.getSubimage(x * TILESIZE, y * TILESIZE, TILESIZE, TILESIZE);
		}catch(RasterFormatException  e){
			e.printStackTrace();
		}
		return getDefaultImg();
	}	  
	
	/**	
	 * @param name Name of the sprite
	 * @return The sprite with the corresponding name
	 */
	protected static BufferedImage get(String name){
		try{
			return sprites.get(name);
		}catch(NullPointerException e){
			e.printStackTrace();
		}
		return getDefaultImg();
	}
	
	/**
	 * @param row The row of sprites to retrieve
	 * @return every sprite on the given row in an array.
	 */
	protected static BufferedImage[] getRow(int row){
		int sheetWidth = 5;
		BufferedImage[] sprites = new BufferedImage[sheetWidth];
		for(int i = 0 ; i < sheetWidth; ++i){
			sprites[i] = getSprite(i,row);
		}
		return sprites;
	}
}
