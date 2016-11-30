import java.util.Random;

/**
 * Contains the main logic part of the game, as it processes.
 *
 * @author : The unsung tutor.
 */
public class GameLogic {

	private boolean running = true;
	private Map map;
	private HumanPlayer player;
	private BotPlayer bot;
	private Console console;
	
	public GameLogic(Console console){
		this.console = console;
		map = new Map();
		map.readMap("example_map.txt");
		player = new HumanPlayer(this);
		bot = new BotPlayer(this);		
		
		Random rand = new Random(System.currentTimeMillis());
		int[] playerPos = {rand.nextInt(map.getMapWidth()),rand.nextInt(map.getMapHeight())};
		int[] botPos = {rand.nextInt(map.getMapWidth()),rand.nextInt(map.getMapHeight())};
		while(map.getTile(playerPos) == '#'){
			playerPos[0] = rand.nextInt(map.getMapWidth());
			playerPos[1] = rand.nextInt(map.getMapHeight());
		}
		while(map.getTile(botPos) == '#' || (botPos[0]==playerPos[0]&&botPos[1]==playerPos[1])){
			botPos[0] = rand.nextInt(map.getMapWidth());
			botPos[1] = rand.nextInt(map.getMapHeight());
		}
		map.updatePlayerPosition(playerPos);
		map.updateBotsPosition(botPos);
		
		console.drawMap(map);
		
		while(true){
			if(running){
				console.println("New turn");
				
				player.selectNextAction();
				playerPos = map.getPlayersPosition();
				botPos = map.getBotsPosition();
				if(map.getTile(playerPos) =='E' && player.getGoldCount() >= map.getGoldRequired()){
					//TODO: WIN;
					console.println("----****YOU_WIN****----");
					running = false;
				}else{					
					bot.selectNextAction();
					botPos = map.getBotsPosition();
					if(botPos[0]==playerPos[0]&&botPos[1]==playerPos[1]){
						console.println("----****YOU_DEAD****----");
						running = false;
					}
				}			
			}else{
				if(console.readln().equals("QUIT")){
					quitGame();
				}else{
					console.println("GAME OVER(type QUIT to exit)");
				}
			}
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
			player.addGold(1);
		}
		return ("You have "+player.getGoldCount()+" gold!");    	
    }

    /**
     * Quits the game, shutting down the application.
     */
    protected void quitGame() {
    	System.exit(0);
    }
}
