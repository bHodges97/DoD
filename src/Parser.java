import java.util.Stack;

/**
 * For handling messageable objects
 *
 */
public class Parser {
	
	/**
	 * Make sure the input doesn't break the format
	 * @param input The input to clean
	 * @return Cleaned input
	 */
	public static String sanitise(String input){
		if(input == null || input.isEmpty()){
			return "";
		}
		input = input.replaceAll("/", "//");
		input = input.replaceAll(">", "/>");
		input = input.replaceAll("<", "/<");	
		return input;
	}
	
	/**
	 * Convert string back to a multiline format
	 * @param string the string to format
	 * @return converted string
	 */
	public static String convertToMultiLine(String string){
		return (string.replaceAll("\\\\n","\n"));
	}	
	/**
	 * Clean input so it doesn't break println
	 * @param input
	 * @return the cleaned input
	 */
	public static String convertFromMultiLine(String input){
		return  (input.replaceAll("\\n", "\\\\n"));	
	}	
	
	
	/**
	 * Basic parser for XML like structure used in my DOD for communication.
	 * May not safely parse erroneous inputs
	 * 
	 * @param in
	 * @return
	 */
	public static Element parse(String in){
		//validate
		if(in.length() <= 2 || in.charAt(0) != '<' || in.charAt(1) == '/'){
			System.out.println("Could not be parsed:"+in);
			return null;
		}
		//awful way of handling strings such as "<<<<<"
		char[] chars = (in+" ").toCharArray();
		Stack<Element> stack = new Stack<Element>();
    	String builder = "";
    	boolean buildTagEnd = false;
    	
    	for(int i = 0;i < chars.length;++i){
    		
    		if(chars[i] == '/' && (chars[i+1] == '<' || chars[i+1] == '>' || chars[i+1] == '/')){
    			++i;
    			builder += chars[i];
    		}else if(chars[i]== '<' && chars[i+1] != '/'){
    			//start of a tag, start building name
    			builder = "";
    		}else if(chars[i]== '<' && chars[i+1] == '/'){
    			//end of a tag stop building value and start building name
    			++i;
    			buildTagEnd = true;
    			stack.peek().value = builder;
    			builder = "";
    		}else if(chars[i] == '>' && buildTagEnd){
    			//if this is not root element move it from stack to its parent
				Element current = stack.pop();
				//Validation
				if(!current.tag.equals(builder)){
					System.out.println("Could not be parsed(Invalid closing tag!):"+in);
					return null;
				}
				
				//move element to parent or if no parent return element
				if(stack.size() > 0){
					stack.peek().children.add(current);
				}else{
					return current;
				}
				buildTagEnd = false;
				builder = "";	
    		}else if(chars[i] == '>' && !buildTagEnd){
    			//Add new element
    			stack.add(new Element(builder));	
    			builder = "";
    		}else{
    			builder+=chars[i];
    		}
    	}
    	//correctly formatted string should not reach this stage
		System.out.println("Could not be parsed(Tag is not closed!):"+in);
    	return null;    	
	}

	/**
	 * Make a message
	 * @param from The id of the sender
	 * @param message The message
	 * @return element representing message
	 */
	public static Element makeMessage(int from, String message) {
		return Parser.parse("<MESSAGE><FROM>"+from+"</FROM><CONTENT>"+Parser.sanitise(message)+"</CONTENT></MESSAGE>");
	}
	
	/**
	 * Make a message
	 * @param from The id of the sender
	 * @param to The recipient of the message
	 * @param message The message
	 * @return element representing message
	 */
	public static Element makeMessage(int from, int to,String message) {
		return Parser.parse("<MESSAGE><FROM>"+from+"</FROM><TO>"+to+"</TO><CONTENT>"+Parser.sanitise(message)+"</CONTENT></MESSAGE>");
	}
	
	/**
	 * Split message into type,content,target
	 * @param message the message to split
	 * @return splitted message
	 */
	public static String[] splitMessageToComponents(String message){
		String[] components;
		String[] splitter = message.split(" ",2);
		String type = splitter[0];
		String content = splitter[1];
		if(type.equals("WHISPER")){
			//split based on id or name in quotes
			splitter = content.split("((\\d+)|(\".*\")) ",2);
			components = new String[3];
			if(splitter.length == 2){
				components[1] = splitter[1];
				//find contents
				components[2] = content.replaceFirst(" "+components[1], "");
				if(components[2].endsWith("\"") && components[2].startsWith("\"")){
					components[2] = components[2].substring(1,components[2].length()-1);
				}
			}else{
				//whisper missing target info
				components[1] = content;
				components[2] = "-1";
			}
		}else{
			//shout has only two components
			components = new String[2];
			components[1] = content;
		}
		components[0] = type;
		components[1] = Parser.sanitise(components[1]);
		return components;
	}

	/**
	 * 
	 * @return A help message
	 */
	public static String makeHelpMessage() {
		String message = "Avaliable commands:\n";

		message+="At anytime:\n";
		message+="HELP\n   Display this menu\n";
		message+="QUIT\n   Exit the game\n";
		message+="SHOUT <MESSAGE>\n   Message other players\n";
		message+="WHISPER <ID or PlayerName in quotes> <MESSAGE>\n   Message specific player. Eg. WHISPER \"Player 1\" hello player 1\n";
		message+="While in lobby:\n";
		message+="READY\n   Toggle whether you are ready to start game\n";
		message+="START\n   Start the game if every player is ready\n";
		message+="NAME <Name>\n   Change your current name\n";	
		message+="While in game:\n";
		message+="LOOK \n   Display 5x5 Area around you\n";
		message+="HELLO \n   Display gold required to exit game\n";
		message+="PICKUP \n   Pick up items on floor\n";
		message+="MOVE <N or W or S or E> \n   Take a step in the chosen direction\n";
		message = Parser.sanitise(message);
		message = Parser.convertFromMultiLine(message);
		
		return ("<OUTPUT>"+message+"</OUTPUT>");
	}
	

}
