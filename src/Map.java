/**
 * Reads and contains in memory the map of the game.
 *
 * @author: The unnamed tutor.
 */
public class Map {

    /**
     * @return : Gold required to exit the current map.
     */
    protected int getGoldRequired() {
        return 0;
    }

    /**
     * @return : The map as stored in memory.
     */
    protected char[][] getMap() {
        return null;
    }

    /**
     * @return : The height of the current map.
     */
    protected int getMapHeight() {
        return 0;
    }

    /**
     * @return : The name of the current map.
     */
    protected String getMapName() {
        return null;
    }

    /**
     * @return : The width of the current map.
     */
    protected int getMapWidth() {
        return 0;
    }

    /**
     * @return : The position of the player.
     */
    protected int[] getPlayersPosition() {
        return null;
    }

    /**
     * Reads the map from file.
     *
     * @param : Name of the map's file.
     */
    protected void readMap(String fileName) {
    }

    /**
     * Retrieves a tile on the map. If the location requested is outside bounds of the map, it returns 'X' wall.
     *
     * @param coordinates : Coordinates of the tile as a 2D array.
     * @return : What the tile at the location requested contains.
     */
    protected char getTile(int[] coordinates) {
        return 'a';
    }

    /**
     * Updates a floor tile in the map, as it is stored in the memory.
     *
     * @param coordinates : The coordinates of the tile to be updated.
     * @param updatedTile : The new tile.
     */
    protected void updateMapLocation(int[] coordinates, char updatedTile) {
    }

    /**
     * Updates the stored in memory location of the player.
     *
     * @param location : New location of the player.
     */
    protected void updatePlayerPosition(int[] location) {
    }
}
