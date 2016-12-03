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
public class BotPlayer extends Player{
	private boolean	seenPlayer = false;
	private Map gameMap;
	PathFinder finder;
	
	public BotPlayer(GameLogic logic){
		this.gameLogic = logic;
		gameMap = gameLogic.getMap();
		finder = new PathFinder(gameMap);
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
    	switch(command.charAt(5)){
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
			return "Fail";
    	}
    	if(map.getTile(botPos) != 'X' || map.getTile(botPos) !='#'){
			map.updateBotsPosition(botPos);
			return "Success";
		}else{
			return "Fail";
		}
    }

    /**
     * Selects the next action the bot will perform. Outputs in gameLogic.getConsole() the final result.
     */
    public void selectNextAction() {
    	String command = "MOVE "+selectMoveDirection(gameLogic.getMap().getMap());
    	gameLogic.getConsole().println("Bot: "+command);
    	gameLogic.getConsole().println(processCommand(command));//TODO: can bots fail?
    }	

    /**
     * @return :  The direction the agent will move.
     */
    protected char selectMoveDirection(char[][] map) {
     	int[] start = gameMap.getBotsPosition();
    	int[] goal = gameMap.getPlayersPosition();
    	PathFinder finder = new PathFinder(gameMap);
    	if(canSeePlayer()){
    		seenPlayer = true;
    	}    	
    	if(seenPlayer && finder.pathFind(start, goal)){    		
    		int[] next = finder.findNextStep();
    		return getRelativeDirection(start,next);
			
    	}else{
	    	System.out.println("Where are you?");
	    	List<int[]> neighbours= gameMap.getAdjacentClearTiles(start);
	    	if(neighbours.isEmpty()){
	    		return 'N';
	    	}
	    	Random rand = new Random(System.currentTimeMillis());
	    	int[] next = neighbours.get(rand.nextInt(neighbours.size()));
	    	return getRelativeDirection(start, next);
    	}
    }
    
    private char getRelativeDirection(int[] start,int[] end){
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
    
  
    
    private boolean canSeePlayer(){
     	int[] start = gameLogic.getMap().getBotsPosition();
    	int[] goal = gameLogic.getMap().getPlayersPosition();
    	if(Math.abs(start[0]-goal[0]) <= 2 && Math.abs(start[1] - goal[1]) <= 2){
    		return true;
    	}
    	return false;	
    }   
    
    

    public static void main(String[] args) {
    	//GameLogic main = new GameLogic();
        // RUN FOREST RUN!
    }
}
