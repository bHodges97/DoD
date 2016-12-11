import java.util.List;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Contains the main logic part of the game, as it processes.
 *
 * @author : The unsung tutor.
 */
public class GameLogic {

	private boolean running = true;
	private Map map;
	private List<Player> players = new ArrayList<Player>();
	private Player currentPlayer; 
	protected Console console;
	private MyFrame gui;
	private int turnCounter;
	private boolean hasMainPlayer = false;
	private String gameState = "NOTSTARTED";
	
	public GameLogic(MyFrame frame){
		this.gui = frame;
		console = frame.getConsole();
		map = new Map();
		map.readMap("example_map.txt");
		if(map.getMap()==null){
			console.println("map load failed: exiting");
		}else{
			gui.setMap(map);
		}
	}
	
	protected void startGame(){
		gameState = "RUNNING";
		turnCounter = 0;
		while(players.isEmpty()){
			
		}
		gui.update(players.get(0),gameState);
		while(running){
			console.println("Turn "+turnCounter);
			for(Player player: players){
				if(player.lives == 0){
					continue;
				}
				currentPlayer = player;
				player.selectNextAction();
				if(checkWin() || checkLost()){
					break;
				}
				gui.update(player,gameState);
			}				
			++turnCounter;
		}
		gui.update(currentPlayer,gameState);
		try {
			while(!console.readln().equals("QUIT")){
				console.println("GAME OVER(type QUIT to exit)");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		quitGame();
	}
	
	protected void addPlayer(Player player){ 
		if(!hasMainPlayer){
			hasMainPlayer = true;
			player.isMainPlayer = true;
		}else if(player.isMainPlayer){
			console.println("Only one main player can exist");
			return;
		}
		players.add(player);
		player.name+="("+players.size()+")";
		player.setGameLogic(this);
		map.placePlayer(player);
	}
	
	protected Map getMap(){
		return map;
	}
	
    /**
     * @return if the game is running.
     */
    protected boolean gameRunning() {
        return running;
    }

    /**
     * @return : Returns back gold player requires to exit the Dungeon.
     */
    protected String hello() {
		return ("GOLD: "+map.getGoldRequired());
    }

    /**
     * Checks if movement is legal and updates player's location on the map.
     *
     * @param direction : The direction of the movement.
     * @return : Protocol if success or not.
     */
    protected String move(char direction) {
    	int[] playerPos = map.getPosition(currentPlayer);
		switch(direction){
			case 'N':
				--playerPos[1];
				break;
			case 'S':
				++playerPos[1];
				break;
			case 'W':
				--playerPos[0];
				break;
			case 'E':
				++playerPos[0];
				break;
			default:
				return "Fail";
		}
		if(map.getTile(playerPos) != '#' ){ 
			if(map.getPlayer(playerPos)!= null){
				if( map.getPlayer(playerPos) instanceof HumanPlayer){
					Player target = map.getPlayer(playerPos);					
	    			--target.lives;				
	    			if(target.lives == 0){
	    				console.println(currentPlayer.name+" has eliminated " + target.name);	    				
	    				gui.update(currentPlayer, "RUNNING");
	    				map.updatePosition(currentPlayer,playerPos);
	    				gui.showFailEvent(currentPlayer);
	    				map.placeCoins(playerPos,target.getGoldCount());
	    				map.remove(target);
	    				return "Success";
	    			}	    			
				}
	    		else{
	    			return "Fail";
	    		}
			}
			map.updatePosition(currentPlayer,playerPos);
			return "Success";
		}else{
			return "Fail";
		}
    }

    /**
     * Converts the map from a 2D char array to a single string.
     *
     * @return : A String representation of the game map.
     */
    protected String look() {
    	String output = "";
    	int[] playerPos = map.getPosition(currentPlayer);
    	playerPos[0] -=2;
		playerPos[1] -=2;
		for(int y = 0;y<5;++y){
			for(int x = 0;x<5;++x){
				int[] currentPos = new int[]{playerPos[0]+x,playerPos[1]+y};
				Player player = map.getPlayer(currentPos);
				if(player instanceof HumanPlayer){
					output+='P';
				}else if (player instanceof BotPlayer){
					output+='B';
				}else{
					output+=map.getTile(currentPos);					
				}
			}
			output+="\n";
		}
        return output;
    }

    /**
     * Processes the player's pickup command, updating the map and the player's gold amount.
     *
     * @return If the player successfully picked-up gold or not.
     */
    protected String pickup() {
    	int[] playerPos = map.getPosition(currentPlayer);
		if(map.getTile(playerPos) == 'G'){
			map.updateMapLocation(playerPos,'.');
			currentPlayer.addGold(1);
		}
		return ("You have "+currentPlayer.getGoldCount()+" gold!");    	
    }

    /**
     * Quits the game, shutting down the application.
     */
    protected void quitGame() {
    	System.exit(0);
    }
    
    
    private boolean checkWin(){
    	int[] playerPos = map.getPosition(currentPlayer);
    	if(map.getTile(playerPos) =='E' 
    			&& currentPlayer.getGoldCount() >= map.getGoldRequired()){
			gameState = "WON";
			gui.showWinEvent(currentPlayer);
			running = false;
			return true;
		}
    	return false;
    }
    private boolean checkLost(){
		for (Player player : players) {
			if (player instanceof HumanPlayer && player.lives > 0) {
				return false;
			}
			if(player.isMainPlayer && player instanceof BotPlayer){
				return false;
			}
		}
		gameState = "END";
		running = false;
		return true;
    }
}
