/**
 * For resolving combat between two players with rock paper scissors
 *
 */
public class FightResolver {
	public enum MoveType{
		ROCK,PAPER,SCISSORS,SURRENDER
	}
	Player[] players = new Player[2];
	MoveType[] playerActions = new MoveType[2];
	volatile boolean resolved = false;
	long startTime;
	int timer = 10;
	int winner = -1;
	
	/**
	 * Create a new fight resolver for the given players
	 * @param player1 
	 * @param player2
	 */
	public FightResolver(Player player1,Player player2){
		players[0] = player1;
		players[1] = player2;
		//player default to surrender;
		playerActions[0] = MoveType.SURRENDER;
		playerActions[1] = MoveType.SURRENDER;
		
		//creating a timer so the fight will auto resolve
		new Thread("Fight Resolver"){
			@Override
			public void run(){
				int i = timer;
				while(!resolved || i >= 0){
					try {
						
						Thread.sleep(1000);
						if(!resolved && i == timer/2){
							messageAllPlayers("Type "+MoveType.ROCK+", "+MoveType.PAPER+" OR "+MoveType.SCISSORS+". You have "+i+" seconds remaining");
						}
						--i;
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				if(!resolved){
					messageAllPlayers("Times up");
					resolveCombat();
				}
			}
		}.start();
	}
	
	/**
	 * Process a player's chosen action
	 * @param input The player's input
	 * @param player The player
	 * @return output of action
	 */
	public String handle(String input,Player player){
		int id = player == players[0]?0:1;
		for(MoveType move:MoveType.values()){
			if(!move.name().equals(input)){
				return "Invalid command, Accepted are "+MoveType.ROCK+", "+MoveType.PAPER+" or "+MoveType.SCISSORS;				
			}
		}
		playerActions[id] = MoveType.valueOf(input);
		
		if(!playerActions[1-id].equals("SURRENDER")){
			resolveCombat();
			return "";
		}
		
		return "Wait for the other player's decision";		
	}
	
	/**
	 * Find a winner if one exists otherwise draw
	 */
	private void resolveCombat(){
		for(int i = 0;i < 2;++i){
			if(playerActions[i] == MoveType.ROCK && playerActions[1-i] == MoveType.SCISSORS){
				winner = i;
				break;
			}else if(playerActions[i] == MoveType.PAPER && playerActions[1-i] == MoveType.ROCK){
				winner = i;
				break;
			}else if(playerActions[i] == MoveType.SCISSORS && playerActions[1-i] == MoveType.PAPER){
				winner = i;
				break;
			}else if(playerActions[i] != MoveType.SURRENDER && playerActions[1-i] == MoveType.SURRENDER){
				winner = i;
				break;
			}
			System.out.println(i+" "+ winner+" "+playerActions[0] +" , "+playerActions[1]);
		}
		if(winner > -1){
			players[1-winner].lostCombat();
			players[winner].controller.sendOutput("You've beaten "+players[1-winner].name+"!");
		}else{
			messageAllPlayers("It was a tie!");
		}
		resolved = true;
		for(Player player:players){
			player.fightResolver = null;
		}
		return ;
	}
	
	
	/**
	 * Message all players
	 * @param message The message to send
	 */
	private void messageAllPlayers(String message){
		for(Player player:players){
			player.controller.sendOutput(message);
		}
	}
}
