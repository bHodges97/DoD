import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Random;
import java.util.Scanner;
import java.util.Set;

/**
 * Reads and contains in memory the map of the game.
 *
 * @author: The unnamed tutor.
 */
public class Map {	
	private String mapName = "";
	private int goldRequired = 0;
	private char[][] map;
	private List<int[]> tileList;
	private List<int[]> emptyTileList;
	private PosList posList = new PosList();
	
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
    	return posList.getNearestHuman(new int[]{0,0});
    }
    
    protected int[] getPosition(Player player){
    	return posList.get(player).clone();
    }
    
    protected int[] getNearestHumanPos(int[] pos){
    	return posList.getNearestHuman(pos);
    }
    
    protected PosList getPlayerPosList(){
    	return posList;
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
			int width = 0;
			for(int i =0;i<buffer.size();++i){//transform buffer to char array.
				bufferMap[i] = buffer.get(i);
				width = bufferMap[i].length>width?bufferMap[i].length:width;
			}
			
			map = new char[width][bufferMap.length];
			for(int i = 0;i < bufferMap.length;++i){
				for(int j = 0; j< bufferMap[i].length;++j){
					map[j][i] = bufferMap[i][j];
				}
			}			
			
			generateTileList();
    	}catch(FileNotFoundException|NoSuchElementException e){
    		e.printStackTrace();
    		System.exit(1);
    	}finally{
    		if(reader!=null){
    			reader.close();
    		}
    	}
    }
    protected void generateTileList(){
    	tileList = new ArrayList<int[]>(getMapHeight()*getMapWidth());
    	for(int y = 0;y < getMapHeight();++y){
			for(int x = 0; x< getMapWidth();++x){
				tileList.add(new int[]{x,y});
			}
		}
		emptyTileList = new ArrayList<int[]>(tileList);    	
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
    	Player player = posList.getMainPlayer();
    	updatePosition(player, pos);
    }
    
    protected Set<int[]>getAdjacentTiles(int[] current){
    	Set<int[]> neighbors = new HashSet<int[]>();
    	if(current[0]-1>=0){
    		neighbors.add(getListedTile(current[0]-1,current[1]));
    	}
    	if(current[0]+1<getMapWidth()){
    		neighbors.add(getListedTile(current[0]+1,current[1]));
    	}
    	if(current[1]-1>=0){
    		neighbors.add(getListedTile(current[0],current[1]-1));
    	}
    	if(current[1]+1<getMapHeight()){
    		neighbors.add(getListedTile(current[0],current[1]+1));		
    	}
    	return neighbors;
    }
    
    protected List<int[]> getAdjacentClearTiles(int[] current){
    	List<int[]> neighbors = new ArrayList<int[]>(getAdjacentTiles(current));
    	List<int[]> tileList = new ArrayList<int[]>();
    	for(int[] neighbor:neighbors){
    		if(map[neighbor[0]][neighbor[1]] != '#'){
    			tileList.add(neighbor);
    		}
    	}
    	return tileList;
    }       
    
    protected boolean placePlayer(Player player){
    	Random rand = new Random(System.currentTimeMillis());
    	if(emptyTileList.isEmpty()){
    		return false;
    	}    	
    	int[] tempPos = emptyTileList.get(rand.nextInt(emptyTileList.size()));
		while(getTile(tempPos)=='#'){
			emptyTileList.remove(tempPos);
	    	if(emptyTileList.isEmpty()){
	    		return false;
	    	}    
			tempPos = emptyTileList.get(rand.nextInt(emptyTileList.size()));
		}
		posList.put(player,tempPos);
		emptyTileList.remove(tempPos);
		return true;
    }
    
    protected int[] getListedTile(int x,int y){
    	return tileList.get(y*getMapWidth()+x);
    }
    protected Player getPlayer(int[] pos){
    	return posList.getFirstIndexedPlayer(pos);
    }
    protected void updatePosition(Player player,int[] pos){
    	int[] newPos = getListedTile(pos[0],pos[1]);
    	int[] oldPos = posList.update(player,newPos);;
    	emptyTileList.add(oldPos);    	
		emptyTileList.remove(newPos);
    }
    protected boolean hasOverLap(int[] pos){
    	return posList.hasOverLap(pos);
    }
    //TODO: remove
    public static String toString(int[] param){
    	return("("+param[0]+","+param[1]+")");
    }
    
    protected boolean isReady(){
    	return posList.isReady();
    }
}
