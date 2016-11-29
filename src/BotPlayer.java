import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Random;
import java.util.Set;

/**
 * Starts the game with a Bot Player. Contains code for bot's decision making.
 *
 * @author : The unhelmed tutor.
 */
public class BotPlayer {
	
	GameLogic gameLogic = null;
	
	public BotPlayer(GameLogic logic){
		this.gameLogic = logic;
	}

    /**
     * Processes the command. It should return a reply in form of a String, as the protocol dictates.
     * Otherwise it should return the string "Invalid".
     *
     * @param command : Input entered by the user.
     * @return : Processed output or Invalid if the @param command is wrong.
     */
    public String processCommand(String command) {
    	//command = command.split("MOVE")[1];
    	Map map = gameLogic.getMap();
    	int[] botPos = map.getBotsPosition();
    	switch(command.charAt(4)){
		case 'N':
			--botPos[1];
			break;
		case 'S':
			++botPos[1];
			break;
		case 'W':
			--botPos[0];
			break;
		case 'E':
			++botPos[0];
			break;
		default:
			return "fail";
    	}
    	if(map.getTile(botPos) != 'X' || map.getTile(botPos) !='#'){
			map.updateBotsPosition(botPos);
			return "success";
		}else{
			return "fail";
		}
    }

    /**
     * Selects the next action the bot will perform. Outputs in console the final result.
     */
    public void selectNextAction() {
    	String command = "MOVE"+selectMoveDirection(gameLogic.getMap().getMap());
    	Console.println("Bot: "+command);
    	Console.println(processCommand(command));
    }	

    /**
     * @return :  The direction the agent will move.
     */
    protected char selectMoveDirection(char[][] map) { 	
    	int[][][] posMap = gameLogic.getMap().getPosMap();
     	int[] start = gameLogic.getMap().getBotsPosition();
    	int[] goal = gameLogic.getMap().getPlayersPosition();
    	
    	start = posMap[start[0]][start[1]];
    	goal = posMap[goal[0]][goal[1]];
    	
    	Set<int[]> closedSet = new HashSet<int[]>();
    	Set<int[]> openSet = new HashSet<int[]>();
    	openSet.add(start);
    	java.util.Map<int[],int[]> cameFrom = new LinkedHashMap<int[],int[]> ();
    	java.util.Map<int[],Integer> gScore = new HashMap<int[],Integer>();
    	java.util.Map<int[],Integer> fScore = new HashMap<int[],Integer>();
    	gScore.put(start, 0);
    	fScore.put(start,estimateDistance(start,goal));
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
    			while(true){
    				if(cameFrom.get(current)==start){
    					return getDirectionTo(start,current);
    				}
    				current=cameFrom.get(current);
    			}
    		}
    		openSet.remove(current);
    		closedSet.add(current);
    		for(int[] neighbor:getNeighbors(current,map,posMap)){
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
    	System.out.println("Where are you?");
    	//at this point the path finding has failed ;(
    	//generating random direction to try
    	List<int[]> neighbours= getNeighbors(start,map,posMap);
    	if(neighbours.isEmpty()){//can this even happen?
    		return 'N';
    	}
    	Random rand = new Random(System.currentTimeMillis());
    	int[] next = neighbours.get(rand.nextInt(neighbours.size()));
    	return getDirectionTo(start, next);
    }
    
    private char getDirectionTo(int[] start,int[] end){
		if(end[0]>start[0]){
    		return 'S';
    	}else if(end[0]<start[0]){
    		return 'N';
    	}else if(end[1]>start[1]){
    		return 'E';
    	}else if(end[1]<start[0]){
    		return 'W';
    	}else{
    		throw new IllegalArgumentException("start is same as end");
    	}
    }
    
    private List<int[]> getNeighbors(int[] current,char[][] map,int[][][] posmap){
    	List<int[]> neighbors = new ArrayList<int[]>();
    	if(map[current[0]-1][current[1]] != '#'){
    		neighbors.add(posmap[current[0]-1][current[1]]);
    	}
    	if(map[current[0]+1][current[1]] != '#'){
    		neighbors.add(posmap[current[0]+1][current[1]]);
    	}
    	if(map[current[0]][current[1]-1] != '#'){
    		neighbors.add(posmap[current[0]][current[1]-1]);
    	}
    	if(map[current[0]][current[1]+1] != '#'){
    		neighbors.add(posmap[current[0]][current[1]+1]);
    	}
    	return neighbors;
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

    public static void main(String[] args) {
    	GameLogic main = new GameLogic();
        // RUN FOREST RUN!
    }
}