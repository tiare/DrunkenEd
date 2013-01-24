package graphics.background;

import java.util.LinkedList;
import java.util.Random;


public class HorizontalDrawablePool extends LinkedList<HorizontalDrawableInterface> {
	
	private Random randomGenerator;
	
	public HorizontalDrawablePool(){
		randomGenerator = new Random();
	}
	
	public HorizontalDrawableInterface getRandom(){
		
		switch( size() ){
			case 0: return null;
			case 1: return get(0);
			default:
				return get(randomGenerator.nextInt(size()));
		}
	}
	
}
