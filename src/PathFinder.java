import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class PathFinder {
	private Map map;
    private java.util.Map<int[],int[]> path;
    private int[][][] posMap;
	private int[] start,goal;
    
	public PathFinder(Map map){
		this.map = map;
		this.posMap = map.getPosMap();
	}
	
	protected boolean  pathFind(int[] posA,int[] posB){	 	
	    start = posMap[posA[0]][posA[1]];
	    goal = posMap[posB[0]][posB[1]];
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
	
	protected int[] findNextStep(){
		int[] current = map.getPosMap()[goal[0]][goal[1]];
		while(!path.isEmpty()){				
			if(path.get(current)==start){
				System.out.println(current[0]+" "+current[1]);
				return current;
			}
			current=path.get(current);
			System.out.println(current[0]+current[1]-goal[0]-goal[1]);
		}
		return null;
	}
	
    /**
     * Manhattan block distance from a to b
     * @param a Position a
     * @param b Position b
     * @return Distance from a to b
     */
    private int estimateDistance(int[] a,int[] b){
    	return Math.abs(a[0]-b[0])+Math.abs(a[1]-b[1]);
    }
}
