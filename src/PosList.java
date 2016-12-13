import java.util.List;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;


public class PosList implements Iterable<Player>{
	List<Player> players = new ArrayList<Player>();
	List<int[]> positions = new ArrayList<int[]>();
	
	public PosList(){
		
	}
	
	public int[] update(Player player,int[] pos){
		int index = players.indexOf(player);
		int[] previousPos = positions.remove(index);
		positions.add(index,pos);
		return previousPos;
	}
	public void put(Player player,int[] pos){
		players.add(player);
		positions.add(pos);
	}
	public int[] get(Player player){
		int index = players.indexOf(player);
		return positions.get(index).clone();
	}
	
	public int[] get(int[] pos){
		for(int i  = 0; i < size(); ++i){
			if(equals(positions.get(i), pos)){
				return positions.get(i);
			}
		}
		return null;
	}
	public Player getFirstPlayer(int[] pos){
		for(int i  = 0; i < size(); ++i){
			if(equals(positions.get(i), pos)){
				return players.get(i);
			}
		}
		return null;
	}
	public Player getHumanAt(int[] pos){
		for(int i  = 0; i < size(); ++i){
			if(equals(positions.get(i), pos) && players.get(i) instanceof HumanPlayer){
				return players.get(i);
			}
		}
		return null;
	}
	public int[] remove(Player player){
		int index = players.indexOf(player);
		int[] pos = positions.remove(index);
		players.remove(player);
		return pos;
	}
	public Set<Player> keySet(){
		return new HashSet<Player>(players);
	}
	
	public int size(){
		return players.size();
	}
	
	public Player getMainPlayer(){
		for(Player player:players){
			if(player.isMainPlayer){
				return player;
			}
		}
		return null;
	}
	
	public int[] getNearestHuman(int[] source){
		int dist = Integer.MAX_VALUE;
		int[] pos = new int[]{0,0};
		for(Player player:players){
			if((player instanceof HumanPlayer) && player.lives > 0){
				int[] point = get(player);
				int newDist = (int) Point2D.distance(point[0],point[1], source[0], source[1]);
				if(newDist < dist){
					dist = newDist;
					pos = point;
				}
			}
		}
		return pos;
	}
	
	public boolean hasOverLap(int[] pos){
		int overlapCount = 0;
		for(int[] storedPos:positions){
			if(equals(pos,storedPos)){
				overlapCount++;
			}
		}
		return overlapCount>1?true:false;
	}
	
	public static boolean equals(int[] a,int[] b){
		return (a[0] == b[0] && a[1] == b[1]);
	}
	public boolean isReady(){
		if(players.size() ==  positions.size()){
			return true;
		}
		return false;
	}
	
	public static int[] subtract(int[] a,int[] b){
		return new int[]{a[0]-b[0],a[1]-b[1]};
	}
	
	@Override
	public Iterator<Player> iterator() {
		return players.iterator();
	}

	public List<Player> getPlayers(int [] pos) {
		List<Player> list = new ArrayList<Player>();
		for(int i  = 0; i < size(); ++i){
			if(equals(positions.get(i), pos)){
				list.add(players.get(i));
			}
		}
		return list;
	}
}
