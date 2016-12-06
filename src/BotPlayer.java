
/**
 * Starts the game with a Bot Player. Contains code for bot's decision making.
 *
 * @author : The unhelmed tutor.
 */
public class BotPlayer extends Player{
	private boolean	seenPlayer = false;
	private Map gameMap;
	private PathFinder finder;
	
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
    	String output = "Invalid";
    	if(command.length() == 6 && command.substring(0,5).equals("MOVE ")){
			output = gameLogic.move(command.charAt(5));
    	}
    	return output;
    }

    /**
     * Selects the next action the bot will perform. Outputs in gameLogic.getConsole() the final result.
     */
    public void selectNextAction() {
    	String command = "MOVE "+selectMoveDirection(gameLogic.getMap().getMap());
    	gameLogic.console.println(command);
    	gameLogic.console.println(processCommand(command));
    }	

    /**
     * @return :  The direction the agent will move.
     */
    protected char selectMoveDirection(char[][] map) {
     	int[] start = gameMap.getPosition(this);
    	int[] goal = gameMap.getNearestHumanPos(start);
    	int[] next;
    	PathFinder finder = new PathFinder(gameMap);
    	if(canSeePlayer()){
    		seenPlayer = true;
    	}    	
    	if(seenPlayer & finder.pathFind(start, goal)){//must evaluate both to generate a path 
    		System.out.println("Pather pathing"+Map.toString(start)+Map.toString(goal));
    		next = finder.findNextStep();			
    	}else{//random pathing
    		System.out.println("Random pathing"+Map.toString(start)+Map.toString(goal));
	    	next = finder.randomNextStep();
    	}
	    return PathFinder.getRelativeDirection(start, next);
    }
    /**
     * @return : Can the bot see a human player;
     */
    private boolean canSeePlayer(){
     	int[] start = gameMap.getPosition(this);
    	int[] goal = gameMap.getNearestHumanPos(start);
    	if(Math.abs(start[0]-goal[0]) <= 2 && Math.abs(start[1] - goal[1]) <= 2){
    		return true;
    	}
    	return false;	
    }      
    

    public static void main(String[] args) {
    	Console console = new Console();
    	GameLogic game = new GameLogic(console);
		//game.addPlayer(new HumanPlayer(game));
		game.addPlayer(new BotPlayer(game));
        // RUN FOREST RUN!
		//game.startGame();
    }
}
