package graphics.background;

import java.util.LinkedList;
import java.util.Random;


public class HorizontalDrawablePool extends LinkedList<HorizontalDrawable> {
	
	private Random randomGenerator;
	
	public HorizontalDrawablePool(){
		randomGenerator = new Random();
	}
	
	public HorizontalDrawable getRandom(){
		switch( size() ){
			case 0: return null;
			case 1: return get(0);
			default: return get(randomGenerator.nextInt(size()-1)); 
		}
	}
	
}
