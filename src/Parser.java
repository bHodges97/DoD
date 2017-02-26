import java.util.Stack;

public class Parser {
	/**
	 * Delete me
	 * @param args
	 */
	public static void main(String[] args){
		String[] ffs = " ".split(" ",2);
		System.out.println(ffs[0]);
		System.out.println(ffs[1]);
		//parse("<player><name>hello</name><id>2</id></player>").print(0);;
		parse("<LOBBYPLAYER><BOT>false</BOT><READY>false<READY></LOBBYPLAYER>").print(0);
		//parse("<ID></ID>").print(0);
		parse("<><><<<");
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
    	String tagBuilder = "";
    	String valueBuilder = "";
    	boolean buildTag = false;
    	boolean buildTagEnd = false;
    	
    	for(int i = 0;i < chars.length;++i){
    		if(chars[i]== '<' && chars[i+1] != '/'){
    			//start of a tag, start building name
    			buildTag = true;
    			valueBuilder = "";
    			tagBuilder = "";
    		}else if(chars[i]== '<' && chars[i+1] == '/'){
    			//end of a tag stop building value and start building name
    			++i;
    			buildTag = true;
    			buildTagEnd = true;
    			stack.peek().value = valueBuilder;
    			valueBuilder = "";
    		}else if(chars[i] == '>' && buildTagEnd){
    			//if this is not root element move it from stack to its parent
				Element current = stack.pop();
				//Validation
				if(!current.tag.equals(tagBuilder)){
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
    			buildTag = false;  
				tagBuilder = "";	
    		}else if(chars[i] == '>' && !buildTagEnd){
    			//Add new element
    			stack.add(new Element(tagBuilder));	
    			tagBuilder = "";
    			buildTag = false;
    		}else if(buildTag){
    			//build name
    			tagBuilder+=chars[i];
    		}else{
    			//build value
    			valueBuilder+=chars[i];
    		}
    	}
    	//correctly formatted string should not reach this stage
		System.out.println("Could not be parsed(Tag is not closed!):"+in);
    	return null;    	
	}
	
	

}
