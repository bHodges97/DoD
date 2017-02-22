import java.util.List;
import java.util.Random;
import java.util.Set;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

/**
 * Contains the main logic part of the game, as it processes.
 *
 * @author : The unsung tutor.
 */
public class GameLogic {

	
	private Map map;
	private List<Player> players = new ArrayList<Player>();
	private List<Thread> threads = new ArrayList<Thread>();
	protected Console console;
	private MyFrame gui;
	private int turnCounter;
	private int playerCount = 0;
	private GameState gameState = GameState.NOTSTARTED;
	
	/**
	 * Initialise the game with the given frame
	 * @param frame The gui frame to display the game with
	 */
	public GameLogic(MyFrame frame){
		this.gui = frame;
		console = frame.getConsole();
		map = new Map();
		if(!map.tryReadMap("example_map.txt")){
			console.println("map load failed: exiting");
			System.exit(1);
		}else{
			gui.setMap(map);
		}
	}
	
	/**
	 * Starts the DoD game;
	 */
	protected void startGame(){
		turnCounter = 0;
		if(players.isEmpty()){
			return;
		}
		for(Player player:players){
			Thread thread = new Thread(new PlayerLogicThread(player,this));
			threads.add(thread);
		}
		for(Thread thread:threads){
			thread.start();
		}
		while(gameState != GameState.STOPPED){
			if(players.isEmpty()){
				break;
			}
			try {
				Thread.sleep(1000l);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		System.out.println("Game ended. Server is shutting down");
		System.exit(0);
		
		/*
		currentPlayer = players.get(0);
		gui.update(currentPlayer,gameState);
		while(running){
			console.println("Turn "+turnCounter);
			for(Player player: players){
				if(player.lives == 0){
					continue;
				}
				currentPlayer.isMainPlayer = false;//uncomment these lines to have gui focus on current player
				player.isMainPlayer = true;
				gui.update(player,gameState);
				
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
		*/
		
	}
	
	/**
	 * Add a player to the game
	 * @param player Player to add to the game.
	 */
	protected void addPlayer(Player player){ 
		
		
		player.name+="("+player.id+")";
		player.setGameLogic(this);
		Random rand = new Random(System.currentTimeMillis());
		Set<Position> usedPositions = new HashSet<Position>();
		for(Player loadedPlayer:players){
			usedPositions.add(loadedPlayer.position);
		}
		List<Tile> emptyTiles = new ArrayList<Tile>(map.findEmptyTiles(usedPositions));
		int randomNum = rand.nextInt(emptyTiles.size());
		player.position  = new Position(emptyTiles.get(randomNum).pos);
		
		player.id = ++playerCount;
		players.add(player);
	}
	
	/**
	 * @return The map the game is running on
	 */
	protected Map getMap(){
		return map;
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
    protected synchronized String move(Player currentPlayer,char direction) {
    	Position playerPos = currentPlayer.position;
    	Position playerNewPos = new Position(playerPos);
		switch(direction){
			case 'N':
				--playerNewPos.y;
				break;
			case 'S':
				++playerNewPos.y;
				break;
			case 'W':
				--playerNewPos.x;
				break;
			case 'E':
				++playerNewPos.x;
				break;
			default:
				return "Fail";
		}

		if(map.getTile(playerNewPos).isPassable() ){ 
			playerPos = playerNewPos;
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
    protected synchronized String look(Player currentPlayer) {
    	String output = "";
    	Position playerPos = new Position(currentPlayer.position);
		for(int y = -2;y<2;++y){
			for(int x = -2;x<2;++x){
				Position drawingPos = new Position(playerPos.x+x,playerPos.y+y);
				char displayChar = map.getTile(drawingPos).getDisplayChar();
				for(Player player:players){
					if(Position.equals(player.position, drawingPos)){
						displayChar = player.getDisplayChar();
					}
				}
				if(!map.getIsTileEmpty(playerPos)){
					displayChar = map.getItemCharAt(playerPos);
				}
				output+=displayChar;
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
    protected synchronized String pickup(Player currentPlayer) {
    	return ("You have "+currentPlayer.getGoldCount()+" gold!"); 
    }

    /**
     * Quits the game, shutting down the application.
     */
    protected synchronized void quitGame() {
    	System.exit(0);//todo: change it
    }

}
