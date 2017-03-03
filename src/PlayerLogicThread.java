/**
 * For handling player actions
 * 
 */
public class PlayerLogicThread implements Runnable{

	private Player player;
	private GameLogic gameLogic;
	
	public PlayerLogicThread(Player player, GameLogic gameLogic){
		this.player = player;
		this.gameLogic = gameLogic;
	}
	
	
	@Override
	public void run() {
		while(true){
			player.selectNextAction();
			//update other players on changes
			gameLogic.informPlayers();
			try {
				Thread.sleep(400);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
	}

}
