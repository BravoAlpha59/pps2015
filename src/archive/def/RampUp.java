package def;

import java.util.ArrayList;

//A class designed to ramp up the memory allocation of the program, so it doesn't have to happen during the game. 
public class RampUp {

	public RampUp() {
		long allocationTime = System.currentTimeMillis() + 10000;
		ArrayList<Long> memoryHog = new ArrayList<Long>(); 
		long i = 10000000;
		while (System.currentTimeMillis() < allocationTime) {
			memoryHog.add(i);
			i += i;
		}
	}

}
