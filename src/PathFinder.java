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
    private java.util.Map<Tile,Tile> path;
	private Tile start,goal;
    
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
	protected boolean  pathFind(Tile start,Tile goal){	 	
	    Set<Tile> closedSet = new HashSet<Tile>();
	    Set<Tile> openSet = new HashSet<Tile>();
	    openSet.add(start);
	    java.util.Map<Tile,Tile> cameFrom = new HashMap<Tile,Tile> ();
	    java.util.Map<Tile,Integer> gScore = new HashMap<Tile,Integer>();
	    java.util.Map<Tile,Integer> fScore = new HashMap<Tile,Integer>();
	    gScore.put(start, 0);
	    fScore.put(start,estimateDistance(start,goal));
	    path = cameFrom;
	    while(!openSet.isEmpty()){ 
	    	Tile current = null;
	    	int score = Integer.MAX_VALUE;
	    	for(Tile node:openSet){
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
	    	for(Tile neighbor: map.getAdjacentWalkableTiles(current.pos)){
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
	protected Tile findNextStep(){
		Tile step = goal;
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
	 * @return The next tile,null if no adjacent clear tiles exist
	 */
	protected Tile randomNextStep(){
		List<Tile> neighbours = new ArrayList<Tile>(map.getAdjacentWalkableTiles(start.pos));
    	if(neighbours.isEmpty()){
    		return null;//if bot has no where to go
    	}
    	Random rand = new Random(System.currentTimeMillis());
    	return neighbours.get(rand.nextInt(neighbours.size()));
	}
	
	/**
	 * Get the relative direction from start till end
	 * @param start Starting position
	 * @param end Ending position
	 * @return 'N','W','S','E' 
	 */
    public static char getRelativeDirection(Position start,Position end){
		if(end.y>start.y){
    		return 'S';
    	}else if(end.y<start.y){
    		return 'N';
    	}else if(end.x>start.x){
    		return 'E';
    	}else if(end.x<start.x){
    		return 'W';
    	}else{
    		throw new IllegalArgumentException("start is same as end");
    	}
    }   
	
    /**
     * Manhattan block distance from a to b
     * @param a Position a
     * @param b Position b
     * @return Distance from a to b
     */
    public static int estimateDistance(Tile a,Tile b){
    	return Math.abs(a.pos.x-b.pos.x)+Math.abs(a.pos.y-b.pos.y);
    }
}
