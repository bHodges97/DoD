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
	private GameState gameState = GameState.NOTSTARTED;
	
	public GameLogic(){
		map = new Map();
		if(!map.tryReadMap("example_map.txt")){
			console.println("map load failed: exiting");
			System.exit(1);
		}
	}
	
	/**
	 * Starts the DoD game;
	 */
	protected void startGame(){
		if(players.isEmpty()){
			return;
		}
		gameState = GameState.RUNNING;
		for(Player player:players){
			Thread thread = new Thread(new PlayerLogicThread(player,this));
			thread.start();
			threads.add(thread);
		}
		
		for(int y = 0; y< map.getMapHeight();++y){
			for(int x = 0; x < map.getMapWidth();++x){
				System.out.print(map.getTile(new Position(x,y)).getDisplayChar());
			}
			System.out.print("\n");
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
		
	}
	
	/**
	 * Add a player to the game
	 * @param player Player to add to the game.
	 */
	protected void addPlayer(Player player){ 
		player.setGameLogic(this);
		Random rand = new Random(System.currentTimeMillis());
		Set<Position> usedPositions = new HashSet<Position>();
		for(Player loadedPlayer:players){
			usedPositions.add(loadedPlayer.position);
		}
		List<Tile> emptyTiles = new ArrayList<Tile>(map.findEmptyTiles(usedPositions));
		int randomNum = rand.nextInt(emptyTiles.size());
		player.position  = new Position(emptyTiles.get(randomNum).pos);
		player.state = PlayerState.PLAYING;
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
    protected synchronized String hello() {
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
    	Position playerNewPos = playerPos.getAdjacentTile(direction);
    	if(playerNewPos == null || !map.getTile(playerNewPos).isPassable() ){
    		return "Fail";
    	}else if(currentPlayer.state != PlayerState.PLAYING){
    		return "Fail";
    	}
    	Player player = getPlayerAt(playerNewPos);
    	if(player != null){
    		if(player.isImmortal() || player.fightResolver != null){
    			return "FAIL";
    		}else if(currentPlayer.isImmortal()){
    			kill(player);
    		}else{
    			player.fightResolver = new FightResolver(currentPlayer, player);
    			currentPlayer.fightResolver = player.fightResolver;
    			currentPlayer.controller.sendOutput("You attacked "+player.name);
    			currentPlayer.controller.sendOutput(currentPlayer.name + " attacked you!");
    			String output = "Time to fight!\n Type ROCK, PAPER or SCISSORS";
    			player.controller.sendOutput(output);
    			return output;
    		}
    	}
    	
    	
		currentPlayer.position = playerNewPos;
		map.getTile(playerNewPos).onSteppedOn(currentPlayer);
		return "Success";
    }

    /**
     * Converts the map from a 2D char array to a single string.
     *
     * @return : A String representation of the game map.
     */
    protected synchronized String look(Player currentPlayer) {   	
    	String output = "";
    	Position playerPos = new Position(currentPlayer.position);
		for(int y = -2;y<=2;++y){
			for(int x = -2;x<=2;++x){
				Position drawingPos = new Position(playerPos.x+x,playerPos.y+y);
				Tile tile = map.getTile(drawingPos);
				char displayChar = (tile == null? '#':tile.getDisplayChar());
				if(!map.isTileEmpty(drawingPos)){
					displayChar = map.getItemCharAt(drawingPos);
				}
				Player tempPlayer = getPlayerAt(drawingPos);
				if(tempPlayer!=null){
					displayChar = tempPlayer.getDisplayChar();
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
    	Position playerPosition = currentPlayer.position;
    	if(!map.isTileEmpty(playerPosition)){
    		Inventory.transfer(map.removeItemsAt(playerPosition),currentPlayer.inventory);
    	}
    	
    	return ("You have "+currentPlayer.getGoldCount()+" gold!"); 
    }

    /**
     * Quits the game, shutting down the application.
     */
    protected synchronized void quitGame() {
    	System.exit(0);//todo: change it
    }
    
    protected GameState getGameState(){
    	return gameState;
    }
    
    protected Player getPlayerAt(Position position){
    	for(Player player:players){
			if(player.isInGame() && Position.equals(player.position, position)){
				return player;
			}
		}
    	return null;
    }
    
    private void kill(Player player){
    	player.state = PlayerState.DEAD;
    	if(player.inventory.isEmpty()){
    		DroppedItems dropped = new DroppedItems(player.inventory,player.position);
    	}
    	player.inventory.empty();
    	player.controller.sendOutput("You have died!");
    }

}
