package graphics.background;

import java.util.LinkedList;
import java.util.Random;


public class HorizontalDrawablePool extends LinkedList<HorizontalDrawableInterface> {
	
	private Random randomGenerator;
	private int lastUsedIndex = -1;
	
	public HorizontalDrawablePool(){
		randomGenerator = new Random();
	}
	
	public HorizontalDrawableInterface getRandom(){
		if(size() == 0){
			return null;
		}
		int index = randomGenerator.nextInt(size());
		while(index  == lastUsedIndex && size() > 1 ){
			index = randomGenerator.nextInt(size());
		}
		lastUsedIndex = index;
		return get(index);
	}
	
}
