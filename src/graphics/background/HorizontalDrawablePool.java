package graphics.background;

import java.util.LinkedList;
import java.util.Random;


public class HorizontalDrawablePool extends LinkedList<HorizontalDrawable> {
	
	private Random randomGenerator;
	
	
	public HorizontalDrawable getRandom(){
		if( randomGenerator == null){
			randomGenerator = new Random();
		}
		
		if( size() > 0 )
			return get(randomGenerator.nextInt(size()-1));
					
		return null;
	}
	
}
