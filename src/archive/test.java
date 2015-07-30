
import java.util.ArrayList;

import def.Card;
import def.PokerSquaresPointSystem;
import def.xMCTSPruningPPSGame;
import def.xMCTSStringGameState;
<<<<<<< HEAD
import def.xRandomRolloutPruningPlayer200rt2;


public class test {
	public static Card[][] hands ;
	public static int SIZE = 5;
	public static Card[] alreadyPlayed;
	static Card[][] grid;
	static xMCTSPruningPPSGame player;
	static int numPlays;
	static xMCTSStringGameState testState;
	static boolean[] canDraw;
	static ArrayList<xMCTSStringGameState> possibleMoves;
	static xRandomRolloutPruningPlayer200rt2 testPlayer = new xRandomRolloutPruningPlayer200rt2();
=======
import def.xRandomRolloutPruningPlayer;


public class test {
	public static Card[][] hands ;
	public static int SIZE = 5;
	public static Card[] alreadyPlayed;
	static Card[][] grid;
	static xMCTSPruningPPSGame player;
	static int numPlays;
	static xMCTSStringGameState testState;
	static boolean[] canDraw;
	static ArrayList<xMCTSStringGameState> possibleMoves;
	static xRandomRolloutPruningPlayer testPlayer = new xRandomRolloutPruningPlayer();
>>>>>>> refs/heads/ClayDevelop
	static int[] posHands;

	public static void main (String[] args) {
		player = new xMCTSPruningPPSGame(PokerSquaresPointSystem.getAmericanPointSystem());
		
		
		double[] probabilities;
		int numCards;
		int[] rankCounts;
		int[] suitCounts;
		String state;
		double handScore;
		double boardScore;
		Card[] gameDeck = Card.getAllCards();
					
// this begins a scoring test set of hands and board/state (order is important)
		// create a parent state
//		state = "6D3D8D9DAD4C4S8HQC5H6C7SJD9H5C7C2DQS2HQD7H__JCAHASJH";
//		numPlays = 24;
//		System.out.println("numPlays = " + numPlays);
//		System.out.println("Active card = " + state.substring(state.length() - 2, state.length()));
//
//		setAlreadyPlayed(state);
//		canDraw = setCanDraw(alreadyPlayed);
//		testState = new xMCTSStringGameState(state, player.getExpectedBoardScore(state, numPlays, canDraw), numPlays);
//				
//		setHands(state);
//  		player.pointSystem.printGrid(grid);
//		printHands();
//		printBoardScore(state);
//		printChildren();
		
		// this begins a scoring test set of hands and board/state (order is important)
	
		state = "6S3H______" +
				"__________" + 
				"__________" + 
				"__________" +
				"__________" +
				"4H";
numPlays = 1;
testPlayer.setPointSystem(PokerSquaresPointSystem.getAmericanPointSystem(), 1000);
testPlayer.init();
testPlayer.getPlay(Card.getCard("6S"), 30000);
testPlayer.getPlay(Card.getCard("3H"), 30000);
testPlayer.getPlay(Card.getCard("4H"), 29994);
		
		
//	state = "KS9H5CKHKD" +
//			"TSTDTC4C7D" + 
//			"9DAHAC6H6C" + 
//			"QD4S4H4DQS" +
//			"__8D3S8C3D" +
//			"9H";
//numPlays = 24;
//System.out.println("\n\nnumPlays = " + numPlays);
////System.out.println("Active card = " + state.substring(state.length() - 2, state.length()));
//setAlreadyPlayed(state);
//canDraw = setCanDraw(alreadyPlayed);
//testState = new xMCTSStringGameState(state, player.getExpectedBoardScore(state, numPlays, canDraw, 5), numPlays);
////	
//setHands(state);
//player.pointSystem.printGrid(grid);
//
//
//state = "KS__5CKHKD" +
//		"TSTDTC4C7D" + 
//		"9DAHAC6H6C" + 
//		"QD4S4H4DQS" +
//		"9H8D3S8C3D" +
//		"9H";
//numPlays = 24;
//System.out.println("\n\nnumPlays = " + numPlays);
////System.out.println("Active card = " + state.substring(state.length() - 2, state.length()));
//setAlreadyPlayed(state);
//canDraw = setCanDraw(alreadyPlayed);
//testState = new xMCTSStringGameState(state, player.getExpectedBoardScore(state, numPlays, canDraw, 5), numPlays);
////
//setHands(state);
//player.pointSystem.printGrid(grid);
//
//state = "KS__5CKHKD" +
//		"TSTDTC4C7D" + 
//		"9DAHAC6H6C" + 
//		"QD4S4H4DQS" +
//		"__8D3S8C3D" +
//		"9H";
//numPlays = 23;
//System.out.println("numPlays = " + numPlays);
////System.out.println("Active card = " + state.substring(state.length() - 2, state.length()));
//setAlreadyPlayed(state);
//canDraw = setCanDraw(alreadyPlayed);
//testState = new xMCTSStringGameState(state, player.getExpectedBoardScore(state, numPlays, canDraw, 5), numPlays);
////
//setHands(state);
//player.pointSystem.printGrid(grid);
//player.getPossibleMoves(testState, canDraw);
		
		// intuition 23
		
//		state = "7D7C7H____" +
//				"__________" + 
//				"__________" + 
//				"__________" +
//				"__________" +
//				"6H";
//		numPlays = 3;
//		System.out.println("numPlays = " + numPlays);
//		//System.out.println("Active card = " + state.substring(state.length() - 2, state.length()));
//		setAlreadyPlayed(state);
//		canDraw = setCanDraw(alreadyPlayed);
//		testState = new xMCTSStringGameState(state, player.getExpectedBoardScore(state, numPlays, canDraw, 5), numPlays);
////				
//		setHands(state);
//  		player.pointSystem.printGrid(grid);
//  		
//		state = "7D7C7H5C__" +
//				"__________" + 
//				"__________" + 
//				"__________" +
//				"__________" +
//				"6H";
//		numPlays = 4;
//		System.out.println("\n\nnumPlays = " + numPlays);
//		//System.out.println("Active card = " + state.substring(state.length() - 2, state.length()));
//		setAlreadyPlayed(state);
//		canDraw = setCanDraw(alreadyPlayed);
//		testState = new xMCTSStringGameState(state, player.getExpectedBoardScore(state, numPlays, canDraw, 5), numPlays);
////				
//		setHands(state);
//  		player.pointSystem.printGrid(grid);
//  		
//  		state = "7D7C7H____" +
//				"______5C__" + 
//				"__________" + 
//				"__________" +
//				"__________" +
//				"6H";
//		numPlays = 4;
//		System.out.println("\n\nnumPlays = " + numPlays);
//		//System.out.println("Active card = " + state.substring(state.length() - 2, state.length()));
//		setAlreadyPlayed(state);
//		canDraw = setCanDraw(alreadyPlayed);
//		testState = new xMCTSStringGameState(state, player.getExpectedBoardScore(state, numPlays, canDraw, 5), numPlays);
////				
//		setHands(state);
//  		player.pointSystem.printGrid(grid);
//		
//				state = "7D7C7H7S__" +
//						"__________" + 
//						"__________" + 
//						"__________" +
//						"__________" +
//						"6H";
//				numPlays = 4;
//				System.out.println("\n\nnumPlays = " + numPlays);
//				//System.out.println("Active card = " + state.substring(state.length() - 2, state.length()));
//				setAlreadyPlayed(state);
//				canDraw = setCanDraw(alreadyPlayed);
//				testState = new xMCTSStringGameState(state, player.getExpectedBoardScore(state, numPlays, canDraw, 5), numPlays);
////						
//				setHands(state);
//		  		player.pointSystem.printGrid(grid);
//		  		
//		  		state = "7D7C7H____" +
//						"______7S__" + 
//						"__________" + 
//						"__________" +
//						"__________" +
//						"6H";
//				numPlays = 4;
//				System.out.println("\n\nnumPlays = " + numPlays);
//				//System.out.println("Active card = " + state.substring(state.length() - 2, state.length()));
//				setAlreadyPlayed(state);
//				canDraw = setCanDraw(alreadyPlayed);
//				testState = new xMCTSStringGameState(state, player.getExpectedBoardScore(state, numPlays, canDraw, 5), numPlays);
////						
//				setHands(state);
//		  		player.pointSystem.printGrid(grid);
////		  		
////		  		
//				state = "7D7C7H____" +
//						"____7S____" + 
//						"__________" + 
//						"__________" +
//						"__________" +
//						"6H";
//				numPlays = 4;
//				System.out.println("\n\nnumPlays = " + numPlays);
//				//System.out.println("Active card = " + state.substring(state.length() - 2, state.length()));
//				setAlreadyPlayed(state);
//				canDraw = setCanDraw(alreadyPlayed);
//				testState = new xMCTSStringGameState(state, player.getExpectedBoardScore(state, numPlays, canDraw, 5), numPlays);
////						
//				setHands(state);
//		  		player.pointSystem.printGrid(grid);
//		  		
//				state = "7D7C7H____" +
//						"__7S______" + 
//						"__________" + 
//						"__________" +
//						"__________" +
//						"6H";
//				numPlays = 4;
//				System.out.println("\n\nnumPlays = " + numPlays);
//				//System.out.println("Active card = " + state.substring(state.length() - 2, state.length()));
//				setAlreadyPlayed(state);
//				canDraw = setCanDraw(alreadyPlayed);
//				testState = new xMCTSStringGameState(state, player.getExpectedBoardScore(state, numPlays, canDraw, 5), numPlays);
////						
//				setHands(state);
//		  		player.pointSystem.printGrid(grid);
//		  		
//				state = "7D7C7H____" +
//						"7S________" + 
//						"__________" + 
//						"__________" +
//						"__________" +
//						"6H";
//				numPlays = 4;
//				System.out.println("\n\nnumPlays = " + numPlays);
//				//System.out.println("Active card = " + state.substring(state.length() - 2, state.length()));
//				setAlreadyPlayed(state);
//				canDraw = setCanDraw(alreadyPlayed);
//				testState = new xMCTSStringGameState(state, player.getExpectedBoardScore(state, numPlays, canDraw, 5), numPlays);
////						
//				setHands(state);
//		  		player.pointSystem.printGrid(grid);
//		  		
//		  		//intuition 24
//				state = "4C7C5C6C__" +
//						"__________" + 
//						"__________" + 
//						"__________" +
//						"__________" +
//						"6H";
//				numPlays = 4;
//				System.out.println("\n\nnumPlays = " + numPlays);
//				//System.out.println("Active card = " + state.substring(state.length() - 2, state.length()));
//				setAlreadyPlayed(state);
//				canDraw = setCanDraw(alreadyPlayed);
//				testState = new xMCTSStringGameState(state, player.getExpectedBoardScore(state, numPlays, canDraw, 5), numPlays);
////						
//				setHands(state);
//		  		player.pointSystem.printGrid(grid);
//		  		
//				state = "4C7C5C____" +
//						"______6C__" + 
//						"__________" + 
//						"__________" +
//						"__________" +
//						"6H";
//				numPlays = 4;
//				System.out.println("\n\nnumPlays = " + numPlays);
//				//System.out.println("Active card = " + state.substring(state.length() - 2, state.length()));
//				setAlreadyPlayed(state);
//				canDraw = setCanDraw(alreadyPlayed);
//				testState = new xMCTSStringGameState(state, player.getExpectedBoardScore(state, numPlays, canDraw, 5), numPlays);
////						
//				setHands(state);
//		  		player.pointSystem.printGrid(grid);
//		  		
//				state = "4C7C5C____" +
//						"____6C____" + 
//						"__________" + 
//						"__________" +
//						"__________" +
//						"6H";
//				numPlays = 4;
//				System.out.println("\n\nnumPlays = " + numPlays);
//				//System.out.println("Active card = " + state.substring(state.length() - 2, state.length()));
//				setAlreadyPlayed(state);
//				canDraw = setCanDraw(alreadyPlayed);
//				testState = new xMCTSStringGameState(state, player.getExpectedBoardScore(state, numPlays, canDraw, 5), numPlays);
////						
//				setHands(state);
//		  		player.pointSystem.printGrid(grid);
//		  		
//				state = "4C7C5C____" +
//						"__6C______" + 
//						"__________" + 
//						"__________" +
//						"__________" +
//						"6H";
//				numPlays = 4;
//				System.out.println("\n\nnumPlays = " + numPlays);
//				//System.out.println("Active card = " + state.substring(state.length() - 2, state.length()));
//				setAlreadyPlayed(state);
//				canDraw = setCanDraw(alreadyPlayed);
//				testState = new xMCTSStringGameState(state, player.getExpectedBoardScore(state, numPlays, canDraw, 5), numPlays);
////						
//				setHands(state);
//		  		player.pointSystem.printGrid(grid);
//		  		
//				state = "4C7C5C____" +
//						"6C________" + 
//						"__________" + 
//						"__________" +
//						"__________" +
//						"6H";
//				numPlays = 4;
//				System.out.println("\n\nnumPlays = " + numPlays);
//				//System.out.println("Active card = " + state.substring(state.length() - 2, state.length()));
//				setAlreadyPlayed(state);
//				canDraw = setCanDraw(alreadyPlayed);
//				testState = new xMCTSStringGameState(state, player.getExpectedBoardScore(state, numPlays, canDraw, 5), numPlays);
////						
//				setHands(state);
//		  		player.pointSystem.printGrid(grid);
//		  		
//		  		//Intuition 25
//				state = "6D6CQSQC__" +
//						"__________" + 
//						"__________" + 
//						"__________" +
//						"__________" +
//						"6H";
//				numPlays = 4;
//				System.out.println("\n\nnumPlays = " + numPlays);
//				//System.out.println("Active card = " + state.substring(state.length() - 2, state.length()));
//				setAlreadyPlayed(state);
//				canDraw = setCanDraw(alreadyPlayed);
//				testState = new xMCTSStringGameState(state, player.getExpectedBoardScore(state, numPlays, canDraw, 5), numPlays);
////						
//				setHands(state);
//		  		player.pointSystem.printGrid(grid);
//		  		
//				state = "6D6CQS____" +
//						"______QC__" + 
//						"__________" + 
//						"__________" +
//						"__________" +
//						"6H";
//				numPlays = 4;
//				System.out.println("\n\nnumPlays = " + numPlays);
//				//System.out.println("Active card = " + state.substring(state.length() - 2, state.length()));
//				setAlreadyPlayed(state);
//				canDraw = setCanDraw(alreadyPlayed);
//				testState = new xMCTSStringGameState(state, player.getExpectedBoardScore(state, numPlays, canDraw, 5), numPlays);
////						
//				setHands(state);
//		  		player.pointSystem.printGrid(grid);
//		  		
//				state = "6D6CQS____" +
//						"____QC____" + 
//						"__________" + 
//						"__________" +
//						"__________" +
//						"6H";
//				numPlays = 4;
//				System.out.println("\n\nnumPlays = " + numPlays);
//				//System.out.println("Active card = " + state.substring(state.length() - 2, state.length()));
//				setAlreadyPlayed(state);
//				canDraw = setCanDraw(alreadyPlayed);
//				testState = new xMCTSStringGameState(state, player.getExpectedBoardScore(state, numPlays, canDraw, 5), numPlays);
////						
//				setHands(state);
//		  		player.pointSystem.printGrid(grid);
//		  		
//				state = "6D6CQS____" +
//						"__QC______" + 
//						"__________" + 
//						"__________" +
//						"__________" +
//						"6H";
//				numPlays = 4;
//				System.out.println("\n\nnumPlays = " + numPlays);
//				//System.out.println("Active card = " + state.substring(state.length() - 2, state.length()));
//				setAlreadyPlayed(state);
//				canDraw = setCanDraw(alreadyPlayed);
//				testState = new xMCTSStringGameState(state, player.getExpectedBoardScore(state, numPlays, canDraw, 5), numPlays);
////						
//				setHands(state);
//		  		player.pointSystem.printGrid(grid);
//
//				state = "6D6CQS____" +
//						"QC________" + 
//						"__________" + 
//						"__________" +
//						"__________" +
//						"6H";
//				numPlays = 4;
//				System.out.println("\n\nnumPlays = " + numPlays);
//				//System.out.println("Active card = " + state.substring(state.length() - 2, state.length()));
//				setAlreadyPlayed(state);
//				canDraw = setCanDraw(alreadyPlayed);
//				testState = new xMCTSStringGameState(state, player.getExpectedBoardScore(state, numPlays, canDraw, 5), numPlays);
////						
//				setHands(state);
//		  		player.pointSystem.printGrid(grid);
//		  		
//		  		//Intuition 26
//				state = "6DQDQSQC__" +
//						"______JC__" + 
//						"______KC__" + 
//						"______TC__" +
//						"__________" +
//						"6H";
//				numPlays = 7;
//				System.out.println("\n\nnumPlays = " + numPlays);
//				//System.out.println("Active card = " + state.substring(state.length() - 2, state.length()));
//				setAlreadyPlayed(state);
//				canDraw = setCanDraw(alreadyPlayed);
//				testState = new xMCTSStringGameState(state, player.getExpectedBoardScore(state, numPlays, canDraw, 5), numPlays);
////						
//				setHands(state);
//		  		player.pointSystem.printGrid(grid);
//		  		
//				state = "6DQDQS____" +
//						"____QCJC__" + 
//						"______KC__" + 
//						"______TC__" +
//						"__________" +
//						"6H";
//				numPlays = 7;
//				System.out.println("\n\nnumPlays = " + numPlays);
//				//System.out.println("Active card = " + state.substring(state.length() - 2, state.length()));
//				setAlreadyPlayed(state);
//				canDraw = setCanDraw(alreadyPlayed);
//				testState = new xMCTSStringGameState(state, player.getExpectedBoardScore(state, numPlays, canDraw, 5), numPlays);
////						
//				setHands(state);
//		  		player.pointSystem.printGrid(grid);
//		  		
//				state = "6DQDQS____" +
//						"______JCQC" + 
//						"______KC__" + 
//						"______TC__" +
//						"__________" +
//						"6H";
//				numPlays = 7;
//				System.out.println("\n\nnumPlays = " + numPlays);
//				//System.out.println("Active card = " + state.substring(state.length() - 2, state.length()));
//				setAlreadyPlayed(state);
//				canDraw = setCanDraw(alreadyPlayed);
//				testState = new xMCTSStringGameState(state, player.getExpectedBoardScore(state, numPlays, canDraw, 5), numPlays);
////						
//				setHands(state);
//		  		player.pointSystem.printGrid(grid);
//		  		
//				state = "6DQDQS____" +
//						"______JC__" + 
//						"__QC__KC__" + 
//						"______TC__" +
//						"__________" +
//						"6H";
//				numPlays = 7;
//				System.out.println("\n\nnumPlays = " + numPlays);
//				//System.out.println("Active card = " + state.substring(state.length() - 2, state.length()));
//				setAlreadyPlayed(state);
//				canDraw = setCanDraw(alreadyPlayed);
//				testState = new xMCTSStringGameState(state, player.getExpectedBoardScore(state, numPlays, canDraw, 5), numPlays);
////						
//				setHands(state);
//		  		player.pointSystem.printGrid(grid);
//		  		
//				state = "6DQDQS____" +
//						"______JC__" + 
//						"______KC__" + 
//						"____QCTC__" +
//						"__________" +
//						"6H";
//				numPlays = 7;
//				System.out.println("\n\nnumPlays = " + numPlays);
//				//System.out.println("Active card = " + state.substring(state.length() - 2, state.length()));
//				setAlreadyPlayed(state);
//				canDraw = setCanDraw(alreadyPlayed);
//				testState = new xMCTSStringGameState(state, player.getExpectedBoardScore(state, numPlays, canDraw, 5), numPlays);
////						
//				setHands(state);
//		  		player.pointSystem.printGrid(grid);
//		  		
//				state = "6DQDQS____" +
//						"______JC__" + 
//						"______KC__" + 
//						"______TC__" +
//						"______QC__" +
//						"6H";
//				numPlays = 7;
//				System.out.println("\n\nnumPlays = " + numPlays);
//				//System.out.println("Active card = " + state.substring(state.length() - 2, state.length()));
//				setAlreadyPlayed(state);
//				canDraw = setCanDraw(alreadyPlayed);
//				testState = new xMCTSStringGameState(state, player.getExpectedBoardScore(state, numPlays, canDraw, 5), numPlays);
////						
//				setHands(state);
//		  		player.pointSystem.printGrid(grid);
//		  		
//		  		//Intuition 27
//		  		state = "QHQDQS____" +
//						"______JC__" + 
//						"______KC__" + 
//						"______TC__" +
//						"______QC__" +
//						"6H";
//				numPlays = 7;
//				System.out.println("\n\nnumPlays = " + numPlays);
//				//System.out.println("Active card = " + state.substring(state.length() - 2, state.length()));
//				setAlreadyPlayed(state);
//				canDraw = setCanDraw(alreadyPlayed);
//				testState = new xMCTSStringGameState(state, player.getExpectedBoardScore(state, numPlays, canDraw, 5), numPlays);
////						
//				setHands(state);
//		  		player.pointSystem.printGrid(grid);
//		  		
//		  		state = "QHQDQS__QC" +
//						"______JC__" + 
//						"______KC__" + 
//						"______TC__" +
//						"__________" +
//						"6H";
//				numPlays = 7;
//				System.out.println("\n\nnumPlays = " + numPlays);
//				//System.out.println("Active card = " + state.substring(state.length() - 2, state.length()));
//				setAlreadyPlayed(state);
//				canDraw = setCanDraw(alreadyPlayed);
//				testState = new xMCTSStringGameState(state, player.getExpectedBoardScore(state, numPlays, canDraw, 5), numPlays);
////						
//				setHands(state);
//		  		player.pointSystem.printGrid(grid);
//		  		
//		  		state = "QHQDQSQC__" +
//						"______JC__" + 
//						"______KC__" + 
//						"______TC__" +
//						"__________" +
//						"6H";
//				numPlays = 7;
//				System.out.println("\n\nnumPlays = " + numPlays);
//				//System.out.println("Active card = " + state.substring(state.length() - 2, state.length()));
//				setAlreadyPlayed(state);
//				canDraw = setCanDraw(alreadyPlayed);
//				testState = new xMCTSStringGameState(state, player.getExpectedBoardScore(state, numPlays, canDraw, 5), numPlays);
////						
//				setHands(state);
//		  		player.pointSystem.printGrid(grid);
//		  		
//		  		//Intuition 28
//		  		state = "QHQDQS6D6H" +
//						"________6C" + 
//						"__________" + 
//						"__________" +
//						"__________" +
//						"8D";
//				numPlays = 7;
//				System.out.println("\n\nnumPlays = " + numPlays);
//				//System.out.println("Active card = " + state.substring(state.length() - 2, state.length()));
//				setAlreadyPlayed(state);
//				canDraw = setCanDraw(alreadyPlayed);
//				testState = new xMCTSStringGameState(state, player.getExpectedBoardScore(state, numPlays, canDraw, 5), numPlays);
////						
//				setHands(state);
//		  		player.pointSystem.printGrid(grid);
//		  		
//		  		state = "QHQDQS__6H" +
//						"________6C" + 
//						"________6D" + 
//						"__________" +
//						"__________" +
//						"8D";
//				numPlays = 7;
//				System.out.println("\n\nnumPlays = " + numPlays);
//				//System.out.println("Active card = " + state.substring(state.length() - 2, state.length()));
//				setAlreadyPlayed(state);
//				canDraw = setCanDraw(alreadyPlayed);
//				testState = new xMCTSStringGameState(state, player.getExpectedBoardScore(state, numPlays, canDraw, 5), numPlays);
////						
//				setHands(state);
//		  		player.pointSystem.printGrid(grid);
//		  		
//		  		//Intuition 28
//		  		state = "QHQD6D6C__" +
//						"__________" + 
//						"6S6H______" + 
//						"__________" +
//						"__________" +
//						"8D";
//				numPlays = 6;
//				System.out.println("\n\nnumPlays = " + numPlays);
//				//System.out.println("Active card = " + state.substring(state.length() - 2, state.length()));
//				setAlreadyPlayed(state);
//				canDraw = setCanDraw(alreadyPlayed);
//				testState = new xMCTSStringGameState(state, player.getExpectedBoardScore(state, numPlays, canDraw, 5), numPlays);
////						
//				setHands(state);
//		  		player.pointSystem.printGrid(grid);
//		  		
//		  		state = "QHQD6D____" +
//						"__________" + 
//						"6S6H6C____" + 
//						"__________" +
//						"__________" +
//						"8D";
//				numPlays = 6;
//				System.out.println("\n\nnumPlays = " + numPlays);
//				//System.out.println("Active card = " + state.substring(state.length() - 2, state.length()));
//				setAlreadyPlayed(state);
//				canDraw = setCanDraw(alreadyPlayed);
//				testState = new xMCTSStringGameState(state, player.getExpectedBoardScore(state, numPlays, canDraw, 5), numPlays);
////						
//				setHands(state);
//		  		player.pointSystem.printGrid(grid);
//		  		
//		  		
//		  		//Intuition 29
//		  		state = "QHQDQC____" +
//						"__________" + 
//						"__________" + 
//						"__________" +
//						"__________" +
//						"8D";
//				numPlays = 3;
//				System.out.println("numPlays = " + numPlays);
//				//System.out.println("Active card = " + state.substring(state.length() - 2, state.length()));
//				setAlreadyPlayed(state);
//				canDraw = setCanDraw(alreadyPlayed);
//				testState = new xMCTSStringGameState(state, player.getExpectedBoardScore(state, numPlays, canDraw, 5), numPlays);
////						
//				setHands(state);
//		  		player.pointSystem.printGrid(grid);
//		  		
//		  		state = "QHQDQC6D__" +
//						"__________" + 
//						"__________" + 
//						"__________" +
//						"__________" +
//						"8D";
//				numPlays = 4;
//				System.out.println("\n\nnumPlays = " + numPlays);
//				//System.out.println("Active card = " + state.substring(state.length() - 2, state.length()));
//				setAlreadyPlayed(state);
//				canDraw = setCanDraw(alreadyPlayed);
//				testState = new xMCTSStringGameState(state, player.getExpectedBoardScore(state, numPlays, canDraw, 5), numPlays);
////						
//				setHands(state);
//		  		player.pointSystem.printGrid(grid);
//		  		
//		  		state = "QHQDQC____" +
//						"______6D__" + 
//						"__________" + 
//						"__________" +
//						"__________" +
//						"8D";
//				numPlays = 4;
//				System.out.println("\n\nnumPlays = " + numPlays);
//				//System.out.println("Active card = " + state.substring(state.length() - 2, state.length()));
//				setAlreadyPlayed(state);
//				canDraw = setCanDraw(alreadyPlayed);
//				testState = new xMCTSStringGameState(state, player.getExpectedBoardScore(state, numPlays, canDraw, 5), numPlays);
////						
//				setHands(state);
//		  		player.pointSystem.printGrid(grid);
//		  		
//		  		state = "QHQDQC6D9D" +
//						"__________" + 
//						"__________" + 
//						"__________" +
//						"__________" +
//						"8D";
//				numPlays = 5;
//				System.out.println("\n\nnumPlays = " + numPlays);
//				//System.out.println("Active card = " + state.substring(state.length() - 2, state.length()));
//				setAlreadyPlayed(state);
//				canDraw = setCanDraw(alreadyPlayed);
//				testState = new xMCTSStringGameState(state, player.getExpectedBoardScore(state, numPlays, canDraw, 5), numPlays);
////						
//				setHands(state);
//		  		player.pointSystem.printGrid(grid);
//		  		
//		  		state = "QHQDQC____" +
//						"______6D9D" + 
//						"__________" + 
//						"__________" +
//						"__________" +
//						"8D";
//				numPlays = 5;
//				System.out.println("\n\nnumPlays = " + numPlays);
//				//System.out.println("Active card = " + state.substring(state.length() - 2, state.length()));
//				setAlreadyPlayed(state);
//				canDraw = setCanDraw(alreadyPlayed);
//				testState = new xMCTSStringGameState(state, player.getExpectedBoardScore(state, numPlays, canDraw, 5), numPlays);
////						
//				setHands(state);
//		  		player.pointSystem.printGrid(grid);
//		  		
//		  		state = "QHQDQC____" +
//						"______6D__" + 
//						"________9D" + 
//						"__________" +
//						"__________" +
//						"8D";
//				numPlays = 5;
//				System.out.println("\n\nnumPlays = " + numPlays);
//				//System.out.println("Active card = " + state.substring(state.length() - 2, state.length()));
//				setAlreadyPlayed(state);
//				canDraw = setCanDraw(alreadyPlayed);
//				testState = new xMCTSStringGameState(state, player.getExpectedBoardScore(state, numPlays, canDraw, 5), numPlays);
////						
//				setHands(state);
//		  		player.pointSystem.printGrid(grid);
//		  		
//		  		//Intuition 30
//		  		state = "QHQD______" +
//						"__________" + 
//						"__________" + 
//						"__________" +
//						"__________" +
//						"8D";
//				numPlays = 2;
//				System.out.println("\n\nnumPlays = " + numPlays);
//				//System.out.println("Active card = " + state.substring(state.length() - 2, state.length()));
//				setAlreadyPlayed(state);
//				canDraw = setCanDraw(alreadyPlayed);
//				testState = new xMCTSStringGameState(state, player.getExpectedBoardScore(state, numPlays, canDraw, 5), numPlays);
////						
//				setHands(state);
//		  		player.pointSystem.printGrid(grid);
//		  		
//		  		state = "QHQD2D____" +
//						"__________" + 
//						"__________" + 
//						"__________" +
//						"__________" +
//						"8D";
//				numPlays = 3;
//				System.out.println("\n\nnumPlays = " + numPlays);
//				//System.out.println("Active card = " + state.substring(state.length() - 2, state.length()));
//				setAlreadyPlayed(state);
//				canDraw = setCanDraw(alreadyPlayed);
//				testState = new xMCTSStringGameState(state, player.getExpectedBoardScore(state, numPlays, canDraw, 5), numPlays);
////						
//				setHands(state);
//		  		player.pointSystem.printGrid(grid);
//		  		
//		  		state = "QHQD2D6D__" +
//						"__________" + 
//						"__________" + 
//						"__________" +
//						"__________" +
//						"8D";
//				numPlays = 4;
//				System.out.println("\n\nnumPlays = " + numPlays);
//				//System.out.println("Active card = " + state.substring(state.length() - 2, state.length()));
//				setAlreadyPlayed(state);
//				canDraw = setCanDraw(alreadyPlayed);
//				testState = new xMCTSStringGameState(state, player.getExpectedBoardScore(state, numPlays, canDraw, 5), numPlays);
////						
//				setHands(state);
//		  		player.pointSystem.printGrid(grid);
//		  		
//		  		state = "QHQD2D6D8D" +
//						"__________" + 
//						"__________" + 
//						"__________" +
//						"__________" +
//						"8D";
//				numPlays = 5;
//				System.out.println("\n\nnumPlays = " + numPlays);
//				//System.out.println("Active card = " + state.substring(state.length() - 2, state.length()));
//				setAlreadyPlayed(state);
//				canDraw = setCanDraw(alreadyPlayed);
//				testState = new xMCTSStringGameState(state, player.getExpectedBoardScore(state, numPlays, canDraw, 5), numPlays);
////						
//				setHands(state);
//		  		player.pointSystem.printGrid(grid);
		  		
//		state = "KC3C2HKH6S" +
//				"KD3HAHAC4H" + 
//				"KS5C5DQS4D" + 
//				"7D7C9HTH__" +
//				"TS3D6DJS8S" +
//				"9C";
//		numPlays = 24;
//		testPlayer.setPointSystem(PokerSquaresPointSystem.getNegativePointSystem(), 1000);
//		testPlayer.init();
//		setAlreadyPlayed(state);
//		canDraw = setCanDraw(alreadyPlayed);
//		gameDeck = setGameDeck(gameDeck, alreadyPlayed);
//		testState = new xMCTSStringGameState(state, player.getExpectedBoardScore(state, numPlays, canDraw, 5), numPlays);
//		testPlayer.currentState = testState;
//		testPlayer.gameDeck = gameDeck;
//		testPlayer.gameCanDraw = canDraw;
//		testPlayer.numPlays = 24;
//		System.out.println("gameDeck -" + java.util.Arrays.toString(gameDeck));
//		System.out.println("canDraw  -" + java.util.Arrays.toString(canDraw));
//		testPlayer.curNode = new xMCTSPruningChanceNode(testPlayer.currentState, java.util.Arrays.copyOf(gameDeck, gameDeck.length), 0, 200, java.util.Arrays.copyOf(canDraw, canDraw.length));
//		testPlayer.getPlay(Card.getCard("9C"), 2000);
//		System.out.println("\n\nnumPlays = " + numPlays);
//		System.out.println("Active card = " + state.substring(state.length() - 2, state.length()));

//		testState = new xMCTSStringGameState(state, player.getExpectedBoardScore(state, numPlays, canDraw, 5), numPlays);
//				
//		setHands(state);
// 		player.pointSystem.printGrid(grid);
//				printHands();
//				printBoardScore(state);
//				printChildren();
				
//		
//		state = "AC________" +
//				"__________" + 
//				"__________" + 
//				"__________" +
//				"__________" +
//				"TC";
//		numPlays = 1;
//		testPlayer.setPointSystem(PokerSquaresPointSystem.getAmericanPointSystem(), 1000);
//		testPlayer.init();
//		testPlayer.getPlay(Card.getCard("AC"), 2000);
//		testPlayer.getPlay(Card.getCard("TC"), 2000);
//		
//		state = "2C________" +
//				"__________" + 
//				"__________" + 
//				"__________" +
//				"__________" +
//				"8S";
//		numPlays = 1;
//		testPlayer.setPointSystem(PokerSquaresPointSystem.getAmericanPointSystem(), 1000);
//		testPlayer.init();
//		testPlayer.getPlay(Card.getCard("2C"), 2000);
//		testPlayer.getPlay(Card.getCard("8S"), 2000);
//		
//		state = "2C________" +
//				"__________" + 
//				"__________" + 
//				"__________" +
//				"__________" +
//				"2S";
//		numPlays = 1;
//		testPlayer.setPointSystem(PokerSquaresPointSystem.getAmericanPointSystem(), 1000);
//		testPlayer.init();
//		testPlayer.getPlay(Card.getCard("2C"), 2000);
//		testPlayer.getPlay(Card.getCard("2S"), 2000);
//		
//		state = "TC________" +
//				"__________" + 
//				"__________" + 
//				"__________" +
//				"__________" +
//				"TS";
//		numPlays = 1;
//		testPlayer.setPointSystem(PokerSquaresPointSystem.getAmericanPointSystem(), 1000);
//		testPlayer.init();
//		testPlayer.getPlay(Card.getCard("TC"), 2000);
//		testPlayer.getPlay(Card.getCard("TS"), 2000);
//		
//		state = "AC________" +
//				"__________" + 
//				"__________" + 
//				"__________" +
//				"__________" +
//				"2S";
//		numPlays = 1;
//		testPlayer.setPointSystem(PokerSquaresPointSystem.getAmericanPointSystem(), 1000);
//		testPlayer.init();
//		testPlayer.getPlay(Card.getCard("AC"), 2000);
//		testPlayer.getPlay(Card.getCard("2S"), 2000);
//		
//		state = "AC________" +
//				"__________" + 
//				"__________" + 
//				"__________" +
//				"__________" +
//				"TS";
//		numPlays = 1;
//		testPlayer.setPointSystem(PokerSquaresPointSystem.getAmericanPointSystem(), 1000);
//		testPlayer.init();
//		testPlayer.getPlay(Card.getCard("AC"), 2000);
//		testPlayer.getPlay(Card.getCard("TS"), 2000);
//		System.out.println("numPlays = " + numPlays);
//		System.out.println("Active card = " + state.substring(state.length() - 2, state.length()));
		
//		state = "AC________" +
//				"__________" + 
//				"__________" + 
//				"__________" +
//				"__________" +
//				"TC";
//		numPlays = 1;
//	testPlayer.setPointSystem(PokerSquaresPointSystem.getAmericanPointSystem(), 1000);
//	testPlayer.init();
//	testPlayer.getPlay(Card.getCard("AC"), 2000);
//	testPlayer.getPlay(Card.getCard("TC"), 2000);
//
//		setAlreadyPlayed(state);
//		canDraw = setCanDraw(alreadyPlayed);
//		testState = new xMCTSStringGameState(state, player.getExpectedBoardScore(state, numPlays, canDraw), numPlays);
//				
//		setHands(state);
//  		player.pointSystem.printGrid(grid);
//		printHands();
//		printBoardScore(state);
//		printChildren();
		
// this is the end of scoring test set
		
				
	}
	
	private static boolean[] setCanDraw(Card[] alreadyPlayed) {
		boolean[] canDraw = {true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, 
				true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, 
				true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true}; 
		
		for (Card toFind : alreadyPlayed) {		
//			System.out.println(toFind.toString());
			if (toFind != null)
				canDraw[toFind.getRank() + (toFind.getSuit() * 13)] = false;
			
		}
		
		return canDraw;
	}
	
	private static boolean[] setCanDraw() {//CanDraw that can be manually set without being passed anything
		boolean[] canDraw = {true, false, false, false, false, false, false, false, true, false, false, false, false, false, false, false, false, 
				false, false, false, false, false, false, false, false, false, false, false, false, false, true, false, false, false, false, false, 
				false, false, false, false, false, false, true, true, false, false, true, false, false, false, false, false}; 

		return canDraw;
	}
	
	private static Card[] setGameDeck(Card[] gameDeck, Card[] alreadyPlayed) {
		int drawnxMCTSPPSCards = 0;
		int toFindIndex;
		Card temp;
		for (Card toFind : alreadyPlayed) {
			if (toFind != null) {
				toFindIndex = 0;
				while (!toFind.equals(gameDeck[toFindIndex])) {
					toFindIndex++;
				}
				temp = gameDeck[drawnxMCTSPPSCards]; 
				gameDeck[drawnxMCTSPPSCards] = toFind;
				gameDeck[toFindIndex] = temp;
				drawnxMCTSPPSCards++;
			}
		}
		
		return gameDeck;
	}
	
	private static double getTotalProbability(double[] probabilities){
		double totalProbability = 0.0;
		for (double p : probabilities){
			totalProbability += p;
		}		
		return totalProbability;
	}
	
	private static void setHands(String state){
		hands = new Card[10][5];
		grid = new Card[5][5];
		for (int i = 0; i < 5; i++) {//row
			for (int j = 0; j < 5; j++) {//column
				int position = i * 10 + j * 2;
				grid[i][j] = Card.getCard(state.substring(position, position + 2));//fill the grid with our cards
			}
		}
		
		for (int row = 0; row < 5; row++) {//row
			//hands[row] = grid[row]; // check this
			
			for (int col = 0; col < 5; col++) {//column
				hands[row][col] = grid[row][col];
			}
//			System.out.println(java.util.Arrays.toString(hands[row]));
		}
		
		for (int col = 0; col < 5; col++) {//col
			for (int row = 0; row < 5; row++) {//row
				hands[col + 5][row] = grid[row][col];
			}
//			System.out.println(java.util.Arrays.toString(hands[col + 5]));
		}
	}
	
	private static void printHands(){
		double handScore = 0;
		double totalScore = 0;
		System.out.println();
		for (Card[] hand : hands){
			handScore = player.getExpectedHandScore(hand, numPlays, setCanDraw(alreadyPlayed));
			totalScore += handScore;
			System.out.println("Score test for hand : \t" + java.util.Arrays.toString(hand) + "\t\t" + handScore);
		}
		System.out.println("Total of the hand scores : " + "\t\t\t\t" + totalScore);
		System.out.println();
	}
	
	private static void printChildren(){
	    possibleMoves = player.getPossibleMoves(testState, canDraw);
		//possibleMoves = player.getBestSimMove(testState, numPlays, canDraw);
	    for (xMCTSStringGameState child : possibleMoves){
	    	setHands(child.toString());
	  		player.pointSystem.printGrid(grid);
	    	//printHands();
	    	printBoardScore(child.toString());
	    	System.out.println();
	    }
	    
	}
	
	private static void printBoardScore(String state){
		double boardScore = 0.0;
		boardScore = player.getExpectedBoardScore(state, numPlays, canDraw, 5) ;
		System.out.println("Board score test " + boardScore);
		System.out.println();
	}
	
	private static void setAlreadyPlayed(String state){
		int cardCount=0;
		alreadyPlayed = new Card[25];
		for (int i=0;i<state.length() - 2;i+=2){
			alreadyPlayed[cardCount] = Card.getCard(state.substring(i, i+2)) ; 
			cardCount++;
		}
	}
}
