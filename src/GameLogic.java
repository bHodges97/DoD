import java.awt.Color;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

/**
 * Contains the main logic part of the game, as it processes.
 *
 * @author : The unsung tutor.
 */
public class GameLogic {
	public enum GameState implements Messageable{
		RUNNING,STOPPED,NOTSTARTED;

		@Override
		public String getInfo() {
			return "<GAMESTATE>"+toString()+"</GAMESTATE>";
		}
	}

	
	private Map map;
	private List<Player> players = new ArrayList<Player>();
	private List<Thread> threads = new ArrayList<Thread>();
	private GameState gameState = GameState.NOTSTARTED;
	
	public GameLogic(){
		map = new Map();
		if(!map.tryReadMap("example_map.txt")){
			System.out.println("map load failed: exiting");
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
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		informPlayers();
		for(Player player:players){
			Thread thread = new Thread(new PlayerLogicThread(player,this));
			thread.start();
			threads.add(thread);
		}		
		informPlayers();		
		while(gameState != GameState.STOPPED){
			informPlayers();
			try {
				Thread.sleep(1000l);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			if(players.isEmpty()){
				break;
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
		
		if(gameState == GameLogic.GameState.RUNNING){
			Thread thread = new Thread(new PlayerLogicThread(player,this));
			thread.start();
			threads.add(thread);
		}		
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
    	if(playerNewPos == null || map.getTile(playerPos) == null || !map.getTile(playerNewPos).isPassable() ){
    		return "Fail";
    	}else if(currentPlayer.state != PlayerState.PLAYING){
    		return "Fail";
    	}
    	Player player = getPlayerAt(playerNewPos);
    	if(player != null){
    		if(player.isImmortal() || player.fightResolver != null){
    			return "FAIL";
    		}else if(currentPlayer.isImmortal()){
    			kill(player,"killed by "+currentPlayer.name);
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
				char displayChar = map.getCharAt(drawingPos);
				Player tempPlayer = getPlayerAt(drawingPos);
				if(tempPlayer!=null){
					displayChar = tempPlayer.getDisplayChar();
				}
				output+=displayChar;
			}
			output+="\n";
		}
		output = output.substring(0, output.length()-1);
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
    
    /**
     * @return current game state
     */
    protected GameState getGameState(){
    	return gameState;
    }
    
    /**
     * Get the first player at a position
     * @param position 
     * @return the player at the given position
     */
    protected Player getPlayerAt(Position position){
    	for(Player player:players){
			if(player.isInGame() && Position.equals(player.position, position)){
				return player;
			}
		}
    	return null;
    }
    
    protected List<Player> getPlayers(){
    	return players;
    }
    
    
    /**
     * Update players are game conditions
     */
	protected synchronized void informPlayers() {
		int width = 3;
		for(Player player:players){	
			String info = "";
			for(Player otherPlayer:players){
				if(Math.abs(player.position.x - otherPlayer.position.x) <= width && Math.abs(player.position.y - otherPlayer.position.y) <= width ){
					info+=otherPlayer.getInfo(); 
				}
			}
			info += "<TILES>";
			for(int y = -width ;y <= width;++y){
				for(int x = -width;x <= width;++x){
					Tile tile = map.getTile(new Position(player.position.x+x,player.position.y+y));
					if(tile != null){
						info+=tile.getInfo();
					}
				}
			}
			info+="</TILES>";
			info+="<GOLD>"+map.getGoldRequired()+"</GOLD>";
			for(DroppedItems dropped:map.getDroppedItems()){
				if(PathFinder.estimateDistance(player.position,dropped.position) <= 4){
					info+=dropped.getInfo();
				}
			}
			info+=gameState.getInfo();
			player.controller.sendInfo(info);		
		}	
	}
	/**
	 * Send message to all players
	 * @param message
	 */
	protected synchronized void informPlayers(String message) {
		for(Player player:players){
			player.controller.sendOutput(message);
		}
	}
	/**
	 * Make player dead
	 * @param player
	 */
    protected void kill(Player player, String reason){
    	player.state = PlayerState.DEAD;
    	if(!player.inventory.isEmpty()){
    		DroppedItems dropped = new DroppedItems(player.inventory,player.position);
    		map.addDroppedItems(dropped);
    	}
    	player.inventory.empty();
    	player.controller.sendOutput("You have died!"+"("+reason+")");
		informPlayers("Player "+player.id+ " has died");
		informPlayers();
    }

	public void movePlayer(Player player, Position position) {
		player.position = position;
		informPlayers();
	}

	public void addGold(int count, Position position) {
		map.addDroppedItems(new DroppedItems(new ItemGold(), count ,position.x,position.y));
	}
}
