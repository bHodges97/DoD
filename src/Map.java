import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
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
	public List<Tile> tileList;//TODO: private
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
					if(c == 'G'){
						droppedItems.add(new DroppedItems(new ItemGold(),1,x,y));
					}					
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
    
    protected Tile getTile(Position tilePosition){
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
    	for(Tile current: tileList){
    		if(current != null && Position.equals(current.pos, tilePosition)){
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
		return getDroppedItemsAt(pos).inventory.getDisplayChar();
	}
	protected boolean isTileEmpty(Position pos){
		return getDroppedItemsAt(pos) == null;
	}
	
	protected DroppedItems getDroppedItemsAt(Position pos){
		for(DroppedItems droppedItem : droppedItems){
			if(droppedItem.position.equalsto(pos)){
				return droppedItem;
			}else{
				
			}
		}
		return null;
	}
	
    protected void addTile(char c,int x, int y){
    	Position pos = new Position(x,y);
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
    	if(c == '#'){
    		tileList.add(new TileWall(pos));
    	}else if( c == 'E'){
    		tileList.add(new TileExit(pos));
    	}else if( c == 'G'){
    		tileList.add(new TileFloor(pos));
    	}else if( c == '.'){
    		tileList.add(new TileFloor(pos));
    	}else{
    		throw new IllegalArgumentException("Unrecognised tile type:"+c);
    	}
    	validate();
    }
    
    protected void validate(){
    	List<Tile> holder = new ArrayList<Tile>();
    	for(Tile tile:tileList){
    		if(tile != null){
    			holder.add(tile);//TODO: optimise?
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


	public Inventory removeItemsAt(Position position) {
		DroppedItems toBeRemoved = getDroppedItemsAt(position);
		droppedItems.remove(toBeRemoved);
		return toBeRemoved.inventory;
	}


	public Set<DroppedItems> getDroppedItems() {
		return droppedItems;
	}

}
