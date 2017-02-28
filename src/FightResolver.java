
public class FightResolver {
	Player[] players = new Player[2];
	String[] playerActions = new String[2];
	String ROCK = "ROCK",SCISSORS = "SCISSORS",PAPER = "PAPER";
	volatile boolean resolved = false;
	long startTime;
	int timer = 10;
	int winner = -1;
	
	public FightResolver(Player player1,Player player2){
		players[0] = player1;
		players[1] = player2;
		playerActions[0] = "SURRENDER";
		playerActions[1] = "SURRENDER";
		new Thread("Fight Resolver"){
			@Override
			public void run(){
				int i = timer;
				while(!resolved || i >= 0){
					try {
						
						Thread.sleep(1000);
						if(!resolved && i == timer/2){
							messageAllPlayers("Type "+ROCK+", "+PAPER+" OR "+SCISSORS+". You have "+i+" seconds remaining");
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
	
	public String handle(String input,Player player){
		int id = player == players[0]?0:1;
		if( !input.equals(ROCK) && !input.equals(PAPER) && !input.equals(SCISSORS) && !input.equals("SURRENDER")){
			return "Invalid command, Accepted are "+ROCK+", "+PAPER+" or "+SCISSORS;
		}
		playerActions[id] = input;
		
		if(!playerActions[1-id].equals("SURRENDER")){
			resolveCombat();
			return "";
		}
		
		return "Wait for the other player's decision";		
	}
	
	private void resolveCombat(){
		for(int i = 0;i < 2;++i){
			if(playerActions[i].equals(ROCK) && playerActions[1-i].equals(SCISSORS)){
				winner = i;
				break;
			}else if(playerActions[i].equals(PAPER) && playerActions[1-i].equals(ROCK)){
				winner = i;
				break;
			}else if(playerActions[i].equals(SCISSORS) && playerActions[1-i].equals(PAPER)){
				winner = i;
				break;
			}else if(!playerActions[i].equals("SURRENDER") && playerActions[1-i].equals("SURRENDER")){
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
	
	
	
	private void messageAllPlayers(String message){
		for(Player player:players){
			player.controller.sendOutput(message);
		}
	}
}
