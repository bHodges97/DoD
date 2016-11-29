import java.util.Arrays;
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
	
	public GameLogic(){
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
			playerPos[0] = rand.nextInt(map.getMapWidth());
			playerPos[1] = rand.nextInt(map.getMapHeight());
		}
		map.updatePlayerPosition(playerPos);
		map.updateBotsPosition(botPos);
		while(true){
			if(running){
				Console.println("New turn");
				
				
				//DEBUG_MAP
				for(int x = 0; x < map.getMapWidth();++x){
					for(int y = 0; y < map.getMapHeight();++y){
						if(x == map.getPlayersPosition()[0] && y == map.getPlayersPosition()[1]){
							System.out.print('P');
							continue;
						}
						if(x == map.getBotsPosition()[0] && y == map.getBotsPosition()[1]){
							System.out.print('B');
							continue;
						}
						System.out.print(map.getTile(new int[]{x,y}));
					}
					System.out.println();
				}
				
				
				
				
				
				
				
				
				
				
				
				
				
				
				
				
				
				
				
				
				
				
				
				player.selectNextAction();
				playerPos = map.getPlayersPosition();
				botPos = map.getBotsPosition();
				if(map.getTile(playerPos) =='E' && player.getGoldCount() >= map.getGoldRequired()){
					//TODO: WIN;
					Console.println("----****YOU_WIN****----");
					running = false;
				}else{//TODO:botmove
					bot.selectNextAction();
					if(botPos[0]==playerPos[0]&&botPos[1]==playerPos[1]){
						Console.println("----****YOU_DEAD****----");
						running = false;
					}
				}			
			}
		}
	}
	
	public Map getMap(){
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
				return "fail";
		}
		if(map.getTile(playerPos) != 'X' || map.getTile(playerPos) !='#'){
			map.updatePlayerPosition(playerPos);
			return "success";
		}else{
			return "fail";
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
		}
		Console.println("You have "+player.getGoldCount()+" gold!");    	
        return null;
    }

    /**
     * Quits the game, shutting down the application.
     */
    protected void quitGame() {
    	System.exit(0);
    }
}
