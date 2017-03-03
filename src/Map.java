import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
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
     * Reads the map from file.
     *
     * @param : Name of the map's file.
     */
    protected void readMap(String fileName) {
    	tryReadMap(fileName);
    }
    
    /**
     * Reads the map from file.
     *
     * @param : Name of the map's file.
     * @return true if map read successfully
     */
    protected boolean tryReadMap(String fileName){
    	if(fileName.isEmpty()){
    		return false;
    	}
		Scanner reader = null;
    	try{
    		reader = new Scanner(Map.class.getResourceAsStream(fileName));
			mapName = reader.nextLine().split("name ")[1];
			goldRequired = Integer.parseInt(reader.nextLine().split("win ")[1]);
			int x = 0, y = 0;
			do{
				x = 0;
				char[] line = reader.nextLine().toCharArray();
				for(char c:line){
					if(c == 'G'){
						droppedItems.add(new DroppedItems(new ItemGold(),1,x,y));
					}					
					addTile(c,x,y);
					++x;
				}
				++y;
			}while(reader.hasNextLine());
    	}catch(NoSuchElementException e){
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
     * @param current The current tile
     * @return A set of adjacent tiles to current
     */
    protected List<Tile> getAdjacentTiles(Position current){
    	List<Tile> neighbors = new ArrayList<Tile>();
    	char[] directions = {'N','S','W','E'};
    	for(int i = 0;i < directions.length;++i){
    		Tile tile = getTile(current.getAdjacentTile(directions[i]));
    		if(tile!=null){
    	    	neighbors.add(tile);
    		}
    	}
    	return neighbors;
    }
    
    /**
     * Get adjacent tile that can can be passed through by the player
     * @param current The centre tile
     * @return Set of walkable adjacent tiles
     */
    protected Set<Tile> getAdjacentWalkableTiles(Position current){
    	Set<Tile> neighbors = new HashSet<Tile>(getAdjacentTiles(current));
    	Set<Tile> passables = new HashSet<Tile>();
    	for(Tile tile:neighbors){
    		if(tile.isPassable()){
    			passables.add(tile);
    		}
    	}
    	return passables;
    }
    
    /**
     * Get tile at the given position
     * @param tilePosition The position to get tile from
     * @return Tile at position, null if none found
     */
    protected synchronized Tile getTile(Position tilePosition){
    	if(tilePosition == null){
    		return null;
    	}
    	Tile tile = null;
    	int index  = (tilePosition.x-minx)+(tilePosition.y-miny)*height;
    	
    	if(index < tileList.size() && index >= 0){
    		 tile = tileList.get(index);
    	}else{
    		return null;
    	}
    	if(tile != null){
    		if(tile.pos.equalsto(tilePosition)){
    			return tile;
    		}else{
    			validate();
    		}
    	}
    	//quick search failed, doing more thorough search
    	for(Tile current: tileList){
    		if(current != null && Position.equals(current.pos, tilePosition)){
    			return current;
    		}
    	}
    	return null;
    }
    
    /**
     * Find all empty tiles
     * @param excludedPositions Positions to exclude from final list
     * @return set of empty tiles
     */
	protected Set<Tile> findEmptyTiles(Set<Position> excludedPositions) {
		Set<Tile> emptyTiles = new HashSet<Tile>();
		for(Tile tile:tileList){
			if(tile == null){
				continue;
			}
			if(tile.isPassable()){
				boolean canPlaceHere = true;
				for(Position pos:excludedPositions){
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
	
	/**
	 * Get display char of item at position
	 * @param pos the position of items
	 * @return the display char of the item
	 */
	protected char getItemCharAt(Position pos){
		return getDroppedItemsAt(pos).inventory.getDisplayChar();
	}
	/**
	 * @param pos The position to test
	 * @return true if the tile is empty
	 */
	protected boolean isTileEmpty(Position pos){
		return getDroppedItemsAt(pos) == null;
	}
	/**
	 * Get items at the given position
	 * @param pos The position
	 * @return the items at the given position
	 */
	protected DroppedItems getDroppedItemsAt(Position pos){
		for(DroppedItems droppedItem : droppedItems){
			if(droppedItem.position.equalsto(pos)){
				return droppedItem;
			}
		}
		return null;
	}
	/**
	 * Add tile to map
	 * @param type the tile type
	 * @param x The position x
	 * @param y the position y
	 */
    protected void addTile(char type,int x, int y){
    	Position pos = new Position(x,y);
    	//set up bounds
    	if(x >= width-minx){
    		width = x-minx+1;
    	}
    	if(y >= height-miny){
    		height = y-miny+1;
    	}
    	if(x < minx){
    		width += x-minx;
    		minx = x;
    	}
    	if(y < miny){
    		height += y-miny;
    		miny = y;
    	}
    	/**
    	 * make tiles
    	 */
    	if(type == '#'){
    		tileList.add(new TileWall(pos));
    	}else if( type == 'E'){
    		tileList.add(new TileExit(pos));
    	}else if( type == 'G'){
    		tileList.add(new TileFloor(pos));
    	}else if( type == '.'){
    		tileList.add(new TileFloor(pos));
    	}else{
    		throw new IllegalArgumentException("Unrecognised tile type:"+type);
    	}
    	validate();
    }
    
    /**
     * Validate tile positions
     */
    protected void validate(){
    	List<Tile> holder = new ArrayList<Tile>();
    	for(Tile tile:tileList){
    		if(tile != null){
    			//TODO: optimise
    			holder.add(tile);
    		}
    		tile = null;
    	}
    	while(tileList.size()<=((width+1)*(height+1))){
    		tileList.add(null);
    	}
    	while(tileList.size()>((width+1)*(height+1))){
    		tileList.remove(tileList.get(tileList.size()-1));
    	}
    	for(Tile current:holder){
    		int index = (current.pos.x - minx) + (current.pos.y-miny) * width;
    		tileList.set(index,current);
    	}
    }

    /**
     * Remove items at position
     * @param position
     * @return the previous items the position
     */
	public Inventory removeItemsAt(Position position) {
		DroppedItems toBeRemoved = getDroppedItemsAt(position);
		droppedItems.remove(toBeRemoved);
		return toBeRemoved.inventory;
	}

	/**
	 * @return Dropped items
	 */
	public Set<DroppedItems> getDroppedItems() {
		return droppedItems;
	}

	/**
	 * @param dropped The item to add to droppedItems
	 */
	public void addDroppedItems(DroppedItems dropped) {
		DroppedItems existingItems = getDroppedItemsAt(dropped.position);
		if(existingItems == null){
			this.droppedItems.add(dropped);
			
		}else{
			Inventory.transfer(dropped.inventory, existingItems.inventory);
		}
		
	}

}
