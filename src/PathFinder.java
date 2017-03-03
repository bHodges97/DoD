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
	private Position start = new Position(0,0),goal = new Position(0,0);
	
    
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
		this.start = map.getTile(start).pos;
		this.goal = map.getTile(goal).pos;;
	    Set<Position> closedSet = new HashSet<Position>();
	    Set<Position> openSet = new HashSet<Position>();
	    openSet.add(start);
	    path = new HashMap<Position,Position> ();
	    java.util.Map<Position,Integer> gScore = new HashMap<Position,Integer>();
	    java.util.Map<Position,Integer> fScore = new HashMap<Position,Integer>();
	    gScore.put(start, 0);
	    fScore.put(start,estimateDistance(start,goal));
	    while(!openSet.isEmpty()){ 
	    	Position current = new Position(0,0);
	    	int score = Integer.MAX_VALUE;
	    	for(Position node:openSet){
	    		int newScore = fScore.get(node);
	    		if( newScore < score){
	    			score = newScore;
	    			current = node;
	    		}
	    	}
	    	if(current.equalsto(goal)){
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
	    		path.put(neighbor,current);
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
			if(path.get(step).equalsto(start)){
				return step;
			}
			step=path.remove(step);
		}
		throw new IllegalStateException("Pather path does not lead to goal");
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
