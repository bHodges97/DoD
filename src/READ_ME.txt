---------------------------------------------------------
Acknowledgments
Rubber Biscuit Font by http://dabnotu.tk Free for non-commercial use

---------------------------------------------------------
New Mechanics
The game is now real time as compared to previous version, humans have 0.4 seconds delay between each action and bots have slightly longer.
Player can attack other players by moving on to their tile. This will initiate a rock paper scizzors game between players. There is 10 seconds for both players to make a decision, the losing party is killed.
Bot have the trait immortal which makes them unkillable.

---------------------------------------------------------
Installation
-Make sure Java 7+ is installed;
-Copy files to an empty directory.
-run “javac *.java” from the directory.

---------------------------------------------------------
Running DOD
-run DODServer to launch a server on default
-run HumanClient or BotClient with the matching hostname to join the game
-Type READY to be ready to start game (bots are always ready)
-Type START when all players are ready to start game 

---------------------------------------------------------
Game Objective
Navigate around the dungeon and collect enough gold to exit through the E tile. Avoid the bot to stay alive.

---------------------------------------------------------
Protocol Commands:
HELLO - Displays gold needed to exit map.
PICKUP -  Picks up gold at player location and displays gold count.
LOOK - Shows a 5x5 area around the player.
QUIT - Exits game.
MOVE N - Move player north.
MOVE W - Move player west.
MOVE S - Move player south.
MOVE E - Move player east.
SHOUT message - Broadcast a message to all players.
WHISPER id/"name" message - Send message to specific player

---------------------------------------------------------
Map Tiles Reference:
'#' - Wall tile.
'G' - Gold tile.
'B' - Bot player.
'P' - Human player.
'E' - Exit tile.
'.' - Floor tile.

---------------------------------------------------------
New Maps
Edit the default_map.txt file to change the map.
The first line should always start with name followed by map name.
The second line should always start with win followed by a number to indicate gold needed to win.
The map should only use tiles given in tile reference.

---------------------------------------------------------
Sprite Sheet
The tilesheet can be changed with a chosen image editor.

---------------------------------------------------------
How the program is run.
GameLogic.startGame() loops continously until the game is won or lost.
Player.selectNextAction() is called continously in a thread for each player.
The bot player moves randomly until it spots the player, upon which it will use a* path finding to keep tracking the player until the end.
Players can be killed if any bot moves onto the same tile.
Moving on to a tile will trigger the relevant tile action. Moving on to Exit Tile will make player try to leave the dungeon.
LOOK and HELLO do not use up an action.
The Map class uses a list of Tiles to store the map and each player stores their own position.


---------------------------------------------------------
GUI logic
All input and text output is handled by the Console class.
A players controls are disable while the gui is being animated.
When each player completes their action, the gui is updated to reflect the change with animation.
The JTextArea emulates a console by ignoring all key presses that are not on the last line, and enter submits the last line as an input.
PaintPanel is currently removed for this version, the following text is legacy from previous version.
PainPanel handles the rendering of LOOK. The entire map is drawn but an overlay hides it so the player only sees 5x5.
The sprites are loaded with the Sprite class, if a sprite fails to load a default image is hard-coded in to the program.

---------------------------------------------------------
Key Mapping
The arrow keys are mapped to the command MOVE N,W,S,E
The spacebar is mapped to the command PICKUP
