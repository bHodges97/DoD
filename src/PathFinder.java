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
    private java.util.Map<int[],int[]> path;
	private int[] start,goal;
    
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
	protected boolean  pathFind(int[] posA,int[] posB){	 	
	    start = map.getListedTile(posA[0],posA[1]);
	    goal = map.getListedTile(posB[0],posB[1]);
	    Set<int[]> closedSet = new HashSet<int[]>();
	    Set<int[]> openSet = new HashSet<int[]>();
	    openSet.add(start);
	    java.util.Map<int[],int[]> cameFrom = new HashMap<int[],int[]> ();
	    java.util.Map<int[],Integer> gScore = new HashMap<int[],Integer>();
	    java.util.Map<int[],Integer> fScore = new HashMap<int[],Integer>();
	    gScore.put(start, 0);
	    fScore.put(start,estimateDistance(start,goal));
	    path = cameFrom;
	    while(!openSet.isEmpty()){ 
	    	int[] current = {0,0};
	    	int score = Integer.MAX_VALUE;
	    	for(int[] node:openSet){
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
	    	for(int[] neighbor:map.getAdjacentClearTiles(current)){
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
	protected int[] findNextStep(){
		int[] current = map.getListedTile(goal[0],goal[1]);
		while(!path.isEmpty()){				
			if(path.get(current)==start){
				return current;
			}
			current=path.get(current);
		}
		return null;
	}
	
	/**
	 * Find a random adjacent clear tile from starting position
	 * @return The next tile,null if no adjacent clear tiles exist
	 */
	protected int[] randomNextStep(){
		List<int[]> neighbours = map.getAdjacentClearTiles(start);
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
    public static char getRelativeDirection(int[] start,int[] end){
		if(end[1]>start[1]){
    		return 'S';
    	}else if(end[1]<start[1]){
    		return 'N';
    	}else if(end[0]>start[0]){
    		return 'E';
    	}else if(end[0]<start[0]){
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
    public static int estimateDistance(int[] a,int[] b){
    	return Math.abs(a[0]-b[0])+Math.abs(a[1]-b[1]);
    }
}
