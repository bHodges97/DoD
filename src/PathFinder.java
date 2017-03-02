import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

/**
 * PathFinder class, finds a path using the given map. Uses A* or random pathing.
 *
 */
public class PathFinder {
	private Map map;
    private java.util.Map<Position,Position> path;
	private Position start = new Position(0,0),goal;
	
	public static void main(String[] args){
		Position position = new Position(1,1);
		Position position2 = new Position(5,1);
		System.out.println(PathFinder.estimateDistance(position, position2));
	}
    
	/**
	 * Construct a new pathfinder
	 * @param map The map for which to pathfind on
	 */
	public PathFinder(Map map){
		this.map = map;
	}
	
	/**
	 * Find a path from the given positions using A*
	 * pseudocode from wikipedia. 
	 * @param posA Starting position
	 * @param posB Ending position
	 * @see <a href="https://en.wikipedia.org/wiki/A*_search_algorithm">wikipedia a* search</a>
	 * @return true if successful,false otherwise
	 */
	protected boolean  pathFind(Position start,Position goal){	 	
	    Set<Position> closedSet = new HashSet<Position>();
	    Set<Position> openSet = new HashSet<Position>();
	    openSet.add(start);
	    java.util.Map<Position,Position> cameFrom = new HashMap<Position,Position> ();
	    java.util.Map<Position,Integer> gScore = new HashMap<Position,Integer>();
	    java.util.Map<Position,Integer> fScore = new HashMap<Position,Integer>();
	    gScore.put(start, 0);
	    fScore.put(start,estimateDistance(start,goal));
	    path = cameFrom;
	    while(!openSet.isEmpty()){ 
	    	Position current = null;
	    	int score = Integer.MAX_VALUE;
	    	for(Position node:openSet){
	    		int newScore = fScore.get(node);
	    		if( newScore < score){
	    			score = newScore;
	    			current = node;
	    		}
	    	}
	    	if(current == goal){
	    		return true;
	    	}
	    	openSet.remove(current);
	    	closedSet.add(current);
	    	for(Tile neighborTile: map.getAdjacentWalkableTiles(current)){
	    		Position neighbor = neighborTile.pos;
	    		if(closedSet.contains(neighbor)){
	    			continue;
	    		}
	    		int tentative_gScore = gScore.get(current)+estimateDistance(current,neighbor);
	    		if(!openSet.contains(neighbor)){
	    			openSet.add(neighbor);
	    		}else if(tentative_gScore >= gScore.get(neighbor)){
	    			continue;
	    		}
	    		cameFrom.put(neighbor,current);
	    		gScore.put(neighbor,tentative_gScore);
	    		fScore.put(neighbor,gScore.get(neighbor)+estimateDistance(neighbor, goal));
	    	}
	    }
	   	return false;
	}
	/**
	 * Retrace the route from a successful pathfind
	 * @return The nexttile to move on to.
	 */
	protected Position findNextStep(){
		Position step = goal;
		while(!path.isEmpty()){				
			if(path.get(step)==start){
				return step;
			}
			step=path.get(step);
		}
		return null;
	}
	
	/**
	 * Find a random adjacent clear tile from starting position
	 * @param currentPosition 
	 * @return The next tile,null if no adjacent clear tiles exist
	 */
	protected Tile randomNextStep(Position currentPosition){
		List<Tile> neighbours = new ArrayList<Tile>(map.getAdjacentWalkableTiles(currentPosition));
    	if(neighbours.isEmpty()){
    		return null;//if bot has no where to go
    	}
    	Random rand = new Random(System.currentTimeMillis());
    	return neighbours.get(rand.nextInt(neighbours.size()));
	}
	
	
    /**
     * Manhattan block distance from a to b
     * @param position Position a
     * @param position2 Position b
     * @return Distance from a to b
     */
    public static int estimateDistance(Position position,Position position2){
    	return Math.abs(position.x-position2.x)+Math.abs(position.y-position2.y);
    }
}
