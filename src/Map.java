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
public class Map{	
	private String mapName = "";
	private int goldRequired = 0;
	private List<Tile> tileList;
	private Set<DroppedItems> droppedItems = new  HashSet<DroppedItems>();
	private int width = 0,height = 0;
	private int minx = 0,miny = 0;
	
	public Map(){
		tileList = new ArrayList<Tile>();
		tileList.add(null);
	}
	
	
    /**
     * @return : Gold required to exit the current map.
     */
    protected int getGoldRequired() {
        return goldRequired;
    }
    
    /**
     * @return : The map as stored in memory.
     * @deprecated Use getTileList()
     */
    protected char[][] getMap() {
        return null;
    }


    /**
     * @return : The height of the current map.
     */
    protected int getMapHeight() {
        return height;
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
        return width;
    }

    /**
     * @return : The position of the player.
     * @deprecated 
     */
    protected int[] getPlayersPosition() {
    	//return posList.getNearestHuman(new int[]{0,0});
    	return null;
    }
    
    /**
     * Reads the map from file.
     *
     * @param : Name of the map's file.
     */
    protected void readMap(String fileName) {
    	tryReadMap(fileName);
    }
    
    protected boolean tryReadMap(String fileName){
    	if(fileName.isEmpty()){
    		return false;
    	}
		File file = new File(getClass().getResource(fileName).getPath());
		Scanner reader = null;
    	try{
    		reader = new Scanner(file);
			mapName = reader.nextLine().split("name ")[1];
			goldRequired = Integer.parseInt(reader.nextLine().split("win ")[1]);
			int x = 0, y = 0;
			do{
				x = 0;
				char[] line = reader.nextLine().toCharArray();
				for(char c:line){					
					addTile(c,x,y);
					++x;
				}
				++y;
			}while(reader.hasNextLine());
    	}catch(FileNotFoundException|NoSuchElementException e){
    		System.out.println("Map load failed");
    		e.printStackTrace();
    		return false;
    	}finally{
    		if(reader!=null){
    			reader.close();
    		}
    	}
    	return true;
    }

    /**
     * Retrieves a tile on the map. If the location requested is outside bounds of the map, it returns 'X' wall.
     *
     * @param coordinates : Coordinates of the tile as a 2D array.
     * @return : What the tile at the location requested contains.
     * @deprecated
     */
    protected char getTile(int[] coordinates) {
		if(coordinates[0] >= getMapWidth() || coordinates[1] >= getMapHeight() || coordinates[0] < 0 || coordinates[1] <0 ){
	    	return 'X';
		}
		return '.';
        //return map[coordinates[0]][coordinates[1]];
    }

    /**
     * Updates a floor tile in the map, as it is stored in the memory.
     *
     * @param coordinates : The coordinates of the tile to be updated.
     * @param updatedTile : The new tile.
     * @deprecated
     */
    protected void updateMapLocation(int[] coordinates, char updatedTile) {
    	if(getTile(coordinates)!='X'){
    		//map[coordinates[0]][coordinates[1]] = updatedTile;
    	}
    }

    /**
     * Updates the stored in memory location of the player.
     *
     * @param location : New location of the player.
     * @deprecated
     */
    protected void updatePlayerPosition(int[] pos) {
    	//Player player = posList.getMainPlayer();
    	//updatePosition(player, pos);
    }
    
    /**
     * @param current The current tile
     * @return A set of adjacent tiles to current
     */
    protected Set<Tile> getAdjacentTiles(Position current){
    	Set<Tile> neighbors = new HashSet<Tile>();
    	neighbors.add(getTile(current.getAdjacentTile('N')));
    	neighbors.add(getTile(current.getAdjacentTile('S')));
    	neighbors.add(getTile(current.getAdjacentTile('W')));
    	neighbors.add(getTile(current.getAdjacentTile('E')));
    	for(Tile tile:neighbors){
    		if(tile == null){
    			neighbors.remove(tile);
    		}
    	}
    	return neighbors;
    }
    
    protected Set<Tile> getAdjacentWalkableTiles(Position current){
    	Set<Tile> neighbors = getAdjacentTiles(current);
    	for(Tile tile:neighbors){
    		if(tile.isPassable() == false){
    			neighbors.remove(tile);
    		}
    	}
    	return neighbors;
    }
    
    protected Tile getTile(Position tilePosition){
    	Tile tile = null;
    	int index  = (tilePosition.x-minx)+(tilePosition.y-miny)*height;
    	if(index <= tileList.size()){
    		 tile = tileList.get(index);
    	}
    	if(tile != null){
    		if(tile.pos.equalsto(tilePosition)){
    			return tile;
    		}else{
    			validate();
    		}
    	}
    	for(Tile current: tileList){
    		if(Position.equals(current.pos, tilePosition)){
    			return current;
    		}
    	}
    	
    	return null;
    }

	protected Set<Tile> findEmptyTiles(Set<Position> usedPositions) {
		Set<Tile> emptyTiles = new HashSet<Tile>();
		for(Tile tile:tileList){
			if(tile == null){
				continue;
			}
			if(tile.isPassable()){
				boolean canPlaceHere = true;
				for(Position pos:usedPositions){
					if(Position.equals(pos, tile.pos)){
						canPlaceHere = false;
						break;
					}
				}
				if(canPlaceHere){
					emptyTiles.add(tile);
				}	
			}
		}
		return emptyTiles;
		
	}
	protected char getItemCharAt(Position pos){
		for(DroppedItems droppedItem : droppedItems){
			if(droppedItem.position.equals(pos)){
				return droppedItem.inventory.getDisplayChar();
			}
		}
		throw new IllegalArgumentException("No items at "+pos.toString());
	}
	protected boolean getIsTileEmpty(Position pos){
		for(DroppedItems droppedItem : droppedItems){
			if(droppedItem.position.equals(pos)){
				return false;
			}
		}
		return true;
	}
	
    private void addTile(char c,int x, int y){
    	Position pos = new Position(x,y);
    	int index;
    	if(x > width-minx){
    		width = x-minx;
    	}
    	if(y > height-miny){
    		height = y-miny;
    	}
    	if(x < minx){
    		width += x-minx;
    		minx = x;
    	}
    	if(y < miny){
    		height += y-miny;
    		miny = y;
    	}
    	index = (x-minx)+(y-miny)*height;
    	if(c == '#'){
    		tileList.add(new TileWall(pos));
    	}else if( c == 'E'){
    		tileList.add(new TileExit(pos));
    	}else{
    		tileList.add(new TileFloor(pos));
    	}
    	validate();
    }
    
    private void validate(){
    	List<Tile> holder = new ArrayList<Tile>();
    	for(Tile tile:tileList){
    		if(tile != null){
    			holder.add(tile);//TODO: optimise?
    		}
    		tile = null;
    	}
    	while(tileList.size()<=(width+1)*(height+1)){
    		tileList.add(null);
    	}
    	for(Tile current:holder){
    		int index = (current.pos.x - minx) + (current.pos.y-miny) * width;
    		tileList.set(index,current);
    		System.out.println("holder adding"+current.toString()+"----"+index);
    	}
    }

}
