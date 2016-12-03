import java.util.List;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

/**
 * Contains the main logic part of the game, as it processes.
 *
 * @author : The unsung tutor.
 */
public class GameLogic {

	private boolean running = true;
	private Map map;
	private List<Player> players = new ArrayList<Player>();
	private Player humanPlayer; 
	private Console console;
	private int turnCounter;
	
	public GameLogic(Console console){
		this.console = console;
		map = new Map();
		map.readMap("example_map.txt");
		addPlayer(new HumanPlayer(this));
		addPlayer(new BotPlayer(this));
		
		turnCounter = 0;		
		console.drawMap(map);
		
		while(running){
			console.println("New turn");
			for(Player player: players){
				player.selectNextAction();
				if(checkWin()){
					console.showWinEvent();
					running = false;
				}else if(checkLost()){
					console.showFailEvent();
					running = false;
				}					
			}				
			++turnCounter;
		}
		if(console.readln().equals("QUIT")){
			quitGame();
		}else{
			console.println("GAME OVER(type QUIT to exit)");
		}
	}
	
	protected void addPlayer(Player player){
    	Random rand = new Random(System.currentTimeMillis());    	
		players.add(player);
		player.setGameLogic(this);
		int[] botPos = map.getBotsPosition();
		int[] playerPos = {rand.nextInt(map.getMapWidth()),rand.nextInt(map.getMapHeight())};
		while(map.getTile(playerPos) == '#' || map.getPlayerOrTile(playerPos) == 'P' || map.getPlayerOrTile(playerPos) == 'B'){
			playerPos[0] = rand.nextInt(map.getMapWidth());
			playerPos[1] = rand.nextInt(map.getMapHeight());
		}
		if(player instanceof HumanPlayer){
			map.updatePlayerPosition(playerPos);
			this.humanPlayer = player;
		}else{
			map.updateBotsPosition(playerPos);
		}
	}
	
	protected Console getConsole(){
		return console;
	}
	
	protected Map getMap(){
		return map;
	}
	
    /**
     * @return if the game is running.
     */
    protected boolean gameRunning() {
        return running;//??????
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
    	int[] playerPos = map.getPlayersPosition();
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
		if(map.getTile(playerPos) != 'X' && map.getTile(playerPos) !='#'){
			map.updatePlayerPosition(playerPos);
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
    	int[] playerPos = map.getPlayersPosition();
    	int[] botPos = map.getBotsPosition();
    	playerPos[0] -=2;
		playerPos[1] -=2;
		for(int y = 0;y<5;++y){
			for(int x = 0;x<5;++x){
				if(x == 2 && y == 2){
					output+='P';
				}else if(playerPos[0]+x == botPos[0] && playerPos[1]+y == botPos[1] ){
					output+='B';
				}else{
					output+=map.getTile(new int[] {playerPos[0]+x,playerPos[1]+y});
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
    	int[] playerPos = map.getPlayersPosition();
		if(map.getTile(playerPos) == 'G'){
			map.updateMapLocation(playerPos,'.');
			humanPlayer.addGold(1);
		}
		return ("You have "+humanPlayer.getGoldCount()+" gold!");    	
    }

    /**
     * Quits the game, shutting down the application.
     */
    protected void quitGame() {
    	System.exit(0);
    }
    
    
    private boolean checkWin(){
    	int[] playerPos = map.getPlayersPosition();
    	if(map.getTile(playerPos) =='E' && humanPlayer.getGoldCount() >= map.getGoldRequired()){
			//TODO: WIN;
			return true;
		}
    	return false;
    }
    private boolean checkLost(){
    	int[] playerPos = map.getPlayersPosition();
    	int[] botPos = map.getBotsPosition();
    	if(botPos[0]==playerPos[0]&&botPos[1]==playerPos[1]){
			return true;
		}
    	return false;
    }
}
