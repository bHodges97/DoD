import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.Scanner;

/**
 * Reads and contains in memory the map of the game.
 *
 * @author: The unnamed tutor.
 */
public class Map {
	
	private String mapName = "";
	private int goldRequired = 0;
	private char[][] map;
	private int[][][] posMap;
	private int[] playerPos = {0,0};
	private int[] botPos = {0,0};
	
	
	public Map(){
		
	}
	

    /**
     * @return : Gold required to exit the current map.
     */
    protected int getGoldRequired() {
        return goldRequired;
    }

    /**
     * @return : The map as stored in memory.
     */
    protected char[][] getMap() {
        return map;
    }
    
    protected int[][][] getPosMap(){
    	return posMap;
    }

    /**
     * @return : The height of the current map.
     */
    protected int getMapHeight() {
        return map[0].length;
    }

    /**
     * @return : The name of the current map.
     */
    protected String getMapName() {
        return mapName;
    }

    /**
     * @return : The width of the current map.
     */
    protected int getMapWidth() {
        return map.length;
    }

    /**
     * @return : The position of the player.
     */
    protected int[] getPlayersPosition() {
    	return playerPos.clone();
    }
    
    /**
     * @return : The position of the bot.
     */
    protected int[] getBotsPosition() {
    	return botPos.clone();
    }

    /**
     * Reads the map from file.
     *
     * @param : Name of the map's file.
     */
    protected void readMap(String fileName) {
    	if(fileName.isEmpty()){
    		return;
    	}
		File file = new File(Map.class.getResource(fileName).getPath());
		Scanner reader = null;
    	try{
    		reader = new Scanner(file);
			mapName = reader.nextLine().split("name ")[1];
			goldRequired = Integer.parseInt(reader.nextLine().split("win ")[1]);
			ArrayList<char[]> buffer = new ArrayList<char[]>();
			while(reader.hasNextLine()){//store map contents into a buffer.
				buffer.add(reader.nextLine().toCharArray());
			}
			map = new char[buffer.size()][];
			for(int i =0;i<buffer.size();++i){//transform buffer to char array.
				map[i] = buffer.get(i);
			}
			posMap = new int[map.length][map[0].length][2];
			for(int x = 0;x < map.length;++x){
				for(int y = 0; y< map[0].length;++y){
					posMap[x][y][0] = x;
					posMap[x][y][1] = y;
				}
			}
    	}catch(FileNotFoundException|NoSuchElementException e){
    		e.printStackTrace();
    		System.exit(1);
    	}finally{
    		if(reader!=null){
    			reader.close();
    		}
    	}
    }


    /**
     * Retrieves a tile on the map. If the location requested is outside bounds of the map, it returns 'X' wall.
     *
     * @param coordinates : Coordinates of the tile as a 2D array.
     * @return : What the tile at the location requested contains.
     */
    protected char getTile(int[] coordinates) {
		if(coordinates[0] >= getMapWidth() || coordinates[1] >= getMapHeight() || coordinates[0] < 0 || coordinates[1] <0 ){
	    	return 'X';
		}
        return map[coordinates[0]][coordinates[1]];
    }

    /**
     * Updates a floor tile in the map, as it is stored in the memory.
     *
     * @param coordinates : The coordinates of the tile to be updated.
     * @param updatedTile : The new tile.
     */
    protected void updateMapLocation(int[] coordinates, char updatedTile) {
    	if(getTile(coordinates)!='X'){
    		map[coordinates[0]][coordinates[1]] = updatedTile;
    	}
    }

    /**
     * Updates the stored in memory location of the player.
     *
     * @param location : New location of the player.
     */
    protected void updatePlayerPosition(int[] location) {
    	playerPos = location;
    }
    
    /**
     * Updates the stored in memory location of the bot.
     *
     * @param location : New location of the bot.
     */
    protected void updateBotsPosition(int[] location) {
    	botPos = location;
    }
}
