
public class PlayerLogicThread implements Runnable{

	private Player player;
	private GameLogic gameLogic;
	
	private PlayerLogicThread(){
		
	}
	
	public PlayerLogicThread(Player player, GameLogic gameLogic){
		this.player = player;
		this.gameLogic = gameLogic;
	}
	
	
	@Override
	public void run() {
		while(true){
			player.selectNextAction();
			gameLogic.informPlayers();
			try {
				Thread.sleep(400);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
	}

}
