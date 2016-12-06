import java.util.List;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;


public class PlayerPosList implements Iterable<Player>{
	List<Player> players = new ArrayList<Player>();
	List<int[]> positions = new ArrayList<int[]>();
	
	public PlayerPosList(){
		
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
		return positions.get(index);
	}
	public Player getFirstIndexedPlayer(int[] pos){
		int index = positions.indexOf(pos);
		return players.get(index);
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
			if((player instanceof HumanPlayer)){
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
		return overlapCount>2?true:false;
	}
	
	public static boolean equals(int[] a,int[] b){
		return (a[0] == a[0] && b[1] == b[1]);
	}
	
	@Override
	public Iterator<Player> iterator() {
		return players.iterator();
	}
}
