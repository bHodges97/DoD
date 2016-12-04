import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Random;
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
	private List<int[]> posList = new ArrayList<int[]>();
	private List<int[]> emptyPosList;
	private PlayerPosList playerPosList = new PlayerPosList();
	
	
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
    	for(Player player:playerPosList.keySet()){
    		if(player instanceof HumanPlayer){
    			return playerPosList.get(player).clone();
    		}
    	}
    	return posList.get(0);
    }
    
    /**
     * @return : The position of the bot.
     */
    protected int[] getBotsPosition() {
    	for(Player player:playerPosList.keySet()){
    		if(player instanceof BotPlayer){
    			return playerPosList.get(player).clone();
    		}
    	}
    	return posList.get(0);
    }

    /**
     * Reads the map from file.
     *
     * @param : Name of the map's file.
     */
    protected void readMap(String fileName) {//TODO:safe
    	if(fileName.isEmpty()){
    		return;
    	}
		File file = new File(getClass().getResource(fileName).getPath());
		Scanner reader = null;
    	try{
    		reader = new Scanner(file);
			mapName = reader.nextLine().split("name ")[1];
			goldRequired = Integer.parseInt(reader.nextLine().split("win ")[1]);
			ArrayList<char[]> buffer = new ArrayList<char[]>();
			while(reader.hasNextLine()){//store map contents into a buffer.
				buffer.add(reader.nextLine().toCharArray());
			}
			char[][] bufferMap = new char[buffer.size()][];
			for(int i =0;i<buffer.size();++i){//transform buffer to char array.
				bufferMap[i] = buffer.get(i);
			}
			
			map = new char[bufferMap[0].length][bufferMap.length];
			for(int i = 0;i < bufferMap.length;++i){
				for(int j = 0; j< bufferMap[0].length;++j){
					map[j][i] = bufferMap[i][j];
				}
			}			
			
			for(int y = 0;y < getMapHeight();++y){
				for(int x = 0; x< getMapWidth();++x){
					posList.add(new int[]{x,y});
				}
			}
			emptyPosList = new ArrayList<int[]>(posList);
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
    protected void updatePlayerPosition(int[] pos) {
    	int[] oldPos = getPlayersPosition();
    	Player player = playerPosList.getPlayer(getListedTile(oldPos[0],oldPos[1]));
    	updatePosition(player, pos);
    }
    
    protected List<int[]> getAdjacentClearTiles(int[] current){
    	List<int[]> neighbors = new ArrayList<int[]>();
    	if(map[current[0]-1][current[1]] != '#'){
    		neighbors.add(getListedTile(current[0]-1,current[1]));
    	}
    	if(map[current[0]+1][current[1]] != '#'){
    		neighbors.add(getListedTile(current[0]+1,current[1]));
    	}
    	if(map[current[0]][current[1]-1] != '#'){
    		neighbors.add(getListedTile(current[0],current[1]-1));
    	}
    	if(map[current[0]][current[1]+1] != '#'){
    		neighbors.add(getListedTile(current[0],current[1]+1));
    	}
    	return neighbors;
    }       
    
    protected boolean placePlayer(Player player){
    	Random rand = new Random(System.currentTimeMillis());
    	if(emptyPosList.isEmpty()){
    		return false;
    	}    	
    	int[] tempPos = emptyPosList.get(rand.nextInt(emptyPosList.size()));
		while(getTile(tempPos)=='#'){
			emptyPosList.remove(tempPos);
	    	if(emptyPosList.isEmpty()){
	    		return false;
	    	}    
			tempPos = emptyPosList.get(rand.nextInt(emptyPosList.size()));
		}
		playerPosList.put(player,tempPos);
		emptyPosList.remove(tempPos);
		return true;
    }
    
    protected int[] getListedTile(int x,int y){
    	return posList.get(y*getMapWidth()+x);
    }
    
    protected void updatePosition(Player player,int[] pos){
    	int[] newPos = getListedTile(pos[0],pos[1]);
    	int[] oldPos = playerPosList.update(player,newPos);;
    	emptyPosList.add(oldPos);    	
		emptyPosList.remove(newPos);
    }
}
