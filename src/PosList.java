import java.util.List;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * Stores the positions of each player
 *
 */
public class PosList implements Iterable<Player>{
	List<Player> players = new ArrayList<Player>();
	List<int[]> positions = new ArrayList<int[]>();
	
	/**
	 * Change the position of a player
	 * @param player The player to update
	 * @param pos The new position
	 * @return Player's previous associated position.
	 */
	public int[] update(Player player,int[] pos){
		int index = players.indexOf(player);
		int[] previousPos = positions.remove(index);
		positions.add(index,pos);
		return previousPos;
	}
	
	/**
	 * Add a new player to the list
	 * @param player The new player
	 * @param pos Player position
	 */
	public void put(Player player,int[] pos){
		players.add(player);
		positions.add(pos);
	}
	/**
	 * Get the position of a player
	 * @param player The player
	 * @return The position of the player
	 */
	public int[] get(Player player){
		int index = players.indexOf(player);
		return positions.get(index).clone();
	}
	
	/**
	 * Get the first player stored with the position
	 * @param pos The position to search
	 * @return The first player found,null if none found
	 */
	public Player getFirstPlayer(int[] pos){
		for(int i  = 0; i < size(); ++i){
			if(equals(positions.get(i), pos)){
				return players.get(i);
			}
		}
		return null;
	}
	/**
	 * Get a human player a the position
	 * @param pos The position to search
	 * @return The first human player found,null if none found
	 */
	public Player getHumanAt(int[] pos){
		for(int i  = 0; i < size(); ++i){
			if(equals(positions.get(i), pos) && players.get(i) instanceof HumanPlayer){
				return players.get(i);
			}
		}
		return null;
	}
	/**
	 * Remove a player from this list
	 * @param player The player to remove
	 * @return previous postion assosicated with this player
	 */
	public int[] remove(Player player){
		int index = players.indexOf(player);
		int[] pos = positions.remove(index);
		players.remove(player);
		return pos;
	}
	/**
	 * @return the set of players
	 */
	public Set<Player> keySet(){
		return new HashSet<Player>(players);
	}
	/**
	 * @return size of this list
	 */
	public int size(){
		return players.size();
	}
	/**
	 * @return the player with isMainPlayer=true,null if none found
	 */
	public Player getMainPlayer(){
		for(Player player:players){
			if(player.isMainPlayer){
				return player;
			}
		}
		return null;
	}
	/**
	 * Get the position of the nearest human player
	 * @param source The tile that is the center of this search
	 * @return The closet player,{0,0} if none found
	 */
	public int[] getNearestHuman(int[] source){
		double dist = Double.MAX_VALUE;
		int[] pos = new int[]{0,0};
		for(Player player:players){
			if((player instanceof HumanPlayer) && player.lives > 0){
				int[] point = get(player);
				double newDist =  Point2D.distance(point[0],point[1], source[0], source[1]);
				if(newDist < dist){
					dist = newDist;
					pos = point;
				}
			}
		}
		return pos;
	}
	/**
	 * Check if a position has more than one player associated to it.
	 * @param pos The position to check
	 * @return true if more than one player on the same tile,false otherwise
	 */
	public boolean hasOverLap(int[] pos){
		return getPlayers(pos).size()>1?true:false;
	}
	
	/**
	 * Check if the parameters are mathematically equivalent
	 * @param a the first position
	 * @param b the second position
	 * @return true if they are equal,false otherwise
	 */
	public static boolean equals(int[] a,int[] b){
		return (a[0] == b[0] && a[1] == b[1]);
	}
	
	/**
	 * Check if this list has correct sizes
	 * @return true if ready,false otherwise
	 */
	public boolean isReady(){
		if(players.size() ==  positions.size()){
			return true;
		}
		return false;
	}
	/**
	 * Subtract b from a
	 * @param a The first position
	 * @param b The second position
	 * @return the result of the subtraction.
	 */
	public static int[] subtract(int[] a,int[] b){
		return new int[]{a[0]-b[0],a[1]-b[1]};
	}
	
	@Override
	public Iterator<Player> iterator() {
		return players.iterator();
	}
	/**
	 * Get every player associated with a tile
	 * @param pos The position to check
	 * @return A list of players on the given tile
	 */
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
