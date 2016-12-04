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
	private Player currentPlayer; 
	protected Console console;
	private int turnCounter;
	
	public GameLogic(Console console){
		this.console = console;
		map = new Map();
		map.readMap("example_map.txt");
		if(map.getMap()==null){
			console.println("map load failed: exiting");
		}else{
			console.setMap(map);
		}
	}
	
	protected void startGame(){
		turnCounter = 0;		
		while(running){
			if(players.isEmpty()){
				continue;
			}
			console.println("Turn "+turnCounter);
			for(Player player: players){
				currentPlayer = player;
				player.selectNextAction();
				if(checkWin()){
					console.showWinEvent();
					running = false;
				}else if(checkLost()){
					console.showFailEvent();
					running = false;
				}
				wait(500);
			}				
			++turnCounter;
		}
		while(!console.readln().equals("QUIT")){
			console.println("GAME OVER(type QUIT to exit)");
		}
		quitGame();
	}
	
	protected void addPlayer(Player player){    	   	
		players.add(player);
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
    	int[] playerPos = currentPlayer instanceof HumanPlayer?map.getPlayersPosition():map.getBotsPosition();
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
    	int[] playerPos = map.getPlayersPosition();
    	if(map.getTile(playerPos) =='E' && currentPlayer.getGoldCount() >= map.getGoldRequired()){
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
    private void wait(int time){
    	try {
			Thread.sleep(time);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
}
