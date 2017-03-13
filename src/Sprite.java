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
	
	//whole class is static;
	private Sprite(){
	}
	
	static{
		loadSpriteSheet();
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
