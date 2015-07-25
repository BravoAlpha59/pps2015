package def;

/**
 * Copyright (c) 2012 Kyle Hughart
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 * Credit for algorithm goes to:
 *
 * Kocsis L. & Szepesvari C. (September 2006). Bandit based Monte-Carlo
 * Planning. Unpublished paper presented European Conference on Machine
 * Learning, Berlin, Germany.
 *
 * Chaslot, Guillaume et al. (October, 2008). Monte-Carlo Tree Search: A New
 * Framework for Game AI. Unpublished paper presented at the Fourth Artificial
 * Intelligence and Interactive Digital Entertainment Conference, Maastricht,
 * The Netherlands.
 */
/**
 * A Tic Tac Toe implementation of Game.
 *
 * @author Kyle
 */
import java.util.ArrayList;

public class xMCTSPruningPPSGame implements xMCTSGame
{

	final int SIZE = 5;
	//A-priori probabilities of...  hc	  2k	 2p	 	3k	   st	  fl	 fh	    4k	   sf	  rf
	final double[] aprioriProb = {.4254, .2127, .1064, .0608, .0327, .0709, .0387, .0250, .0137, .0137};
	protected PokerSquaresPointSystem pointSystem; // point system
	public long totalChildrenPruned = 0;
	protected final double ONE_CARD_WEIGHT = (1.0/10.0);
	protected final double TWO_CARD_WEIGHT = (2.5/10.0);
	protected final double THREE_CARD_WEIGHT = (8.5/10.0);
	protected final double FOUR_CARD_WEIGHT = 1.0;
	protected final double FIVE_CARD_WEIGHT = 1.0;
	protected final double ZERO_CARD;
	protected final double PRUNE_LIMIT = (3.0/10.0);
	protected final double DELTA = (1.3);
	protected final int COMPLETED = 2;
	protected final int POSSIBLE = 1;
	protected final int IMPOSSIBLE = 0;
	protected final double POSITIVE_ROUND_ADJ = 0.999999;
	protected final double NEGATIVE_ROUND_ADJ = 1.000001;
	Card[][] grid = new Card[SIZE][SIZE];
	public double totalPercentPruned;
	public long timesCalled;
	public long overAllMoves;


	public xMCTSPruningPPSGame(PokerSquaresPointSystem pointSystem) {
		this.pointSystem = pointSystem;
		double zeroCard = 0;
		for (int i = 0; i < aprioriProb.length - 1; i++) {
			zeroCard += (aprioriProb[i] * pointSystem.getHandScore(i));
		}
		ZERO_CARD = (zeroCard * (ONE_CARD_WEIGHT * 0.82));
	}

	public ArrayList<xMCTSStringGameState> getPossibleMoves(xMCTSStringGameState gameState, boolean[] canDraw)
	{
		int parentNumPlays = gameState.numPlays; // curNode or Parent node
		ArrayList<xMCTSStringGameState> posMoves = new ArrayList<xMCTSStringGameState>(25-parentNumPlays);
		ArrayList<xMCTSStringGameState> prunedMoves = new ArrayList<xMCTSStringGameState>(25-parentNumPlays);
		double boardExpectedValue; //The expected value of each board
		double scoreMean = 0;
		double percentPruned;
		int childNumPlays = gameState.numPlays + 1;
	

		if (gameStatus(gameState) == xMCTSGame.status.ONGOING) {
			String activeCard = gameState.toString().substring(gameState.toString().length() - 2, gameState.toString().length());

			int gridSize = SIZE*2; //Size is the size of the actual game grid... with 2 characters per card, size*2 is the grid size of string representations
			for (int row = 0; row < gridSize; row += 2) {//for each position in this state being checked
				for (int col = 0; col < gridSize; col += 2) {
					int pos = row * SIZE + col;
					if (gameState.toString().charAt(pos) == '_') {
						String possibleMove = gameState.toString().substring(0, pos)
								+ activeCard
								+ gameState.toString().substring(pos + 2,
										gameState.toString().length());

						if (parentNumPlays < 24 && parentNumPlays > 0) {//ignore triviality of first and last moves; do pruning for all others
							
							boardExpectedValue = getExpectedBoardScore(possibleMove, childNumPlays, canDraw, 5);
							scoreMean += boardExpectedValue;

							if (boardExpectedValue >= (gameState.expectedValue * DELTA)) { //If this play has a score below the parent's weighted with delta
								posMoves.add(new xMCTSStringGameState(possibleMove, boardExpectedValue, childNumPlays));
							}
							else {
								prunedMoves.add(new xMCTSStringGameState(possibleMove, boardExpectedValue, childNumPlays));
								totalChildrenPruned++;
							}
						}
						else { //numplays 0 or 24, so no pruning here
							posMoves.add(new xMCTSStringGameState(possibleMove, 0.0, childNumPlays));
						}
					}
				}
			}
			if (parentNumPlays < 24 && parentNumPlays > 0) {//should only run this when considering pruning
				double totalMoves = posMoves.size() + prunedMoves.size();
				if (posMoves.size() < PRUNE_LIMIT * (posMoves.size() + prunedMoves.size())) {//If posMoves' size is below a certain percent of the total number of moves, add all already-pruned moves above the average of the children to posMoves
					scoreMean /= (posMoves.size() + prunedMoves.size());
					double adjust_rounding = 0.0;
					for (xMCTSStringGameState move : prunedMoves) {
						if (scoreMean >= 0.0)
							adjust_rounding = POSITIVE_ROUND_ADJ;
						else
							adjust_rounding = NEGATIVE_ROUND_ADJ;
						if (move.expectedValue >= scoreMean * adjust_rounding) {
							totalChildrenPruned--;
							posMoves.add(move);
						}
					}
				}
			timesCalled++; 
			overAllMoves += totalMoves;
			percentPruned = (posMoves.size() / totalMoves);
			totalPercentPruned += percentPruned;
			}
		}
		return posMoves;
	}
	
	//A separate version of possibleMoves meant for returning only the best expected child of this node
	public ArrayList<xMCTSStringGameState> getBestSimMove(xMCTSStringGameState gameState, boolean[] canDraw)
	{
		int parentNumPlays = gameState.numPlays; // curNode or Parent node
		ArrayList<xMCTSStringGameState> posMoves = new ArrayList<xMCTSStringGameState>(1);
		ArrayList<xMCTSStringGameState> prunedMoves = new ArrayList<xMCTSStringGameState>(25-parentNumPlays);
		double boardExpectedValue; //The expected value of each board
		double percentPruned;
		int childNumPlays = gameState.numPlays + 1;


		if (gameStatus(gameState) == xMCTSGame.status.ONGOING) {
			String activeCard = gameState.toString().substring(gameState.toString().length() - 2, gameState.toString().length());
			double maxChildExpectedValue = Double.NEGATIVE_INFINITY;

			int gridSize = SIZE*2; //Size is the size of the actual game grid... with 2 characters per card, size*2 is the grid size of string representations
			for (int row = 0; row < gridSize; row += 2) {//for each position in this state being checked
				for (int col = 0; col < gridSize; col += 2) {
					int pos = row * SIZE + col;
					if (gameState.toString().charAt(pos) == '_') {
						String possibleMove = gameState.toString().substring(0, pos)
								+ activeCard
								+ gameState.toString().substring(pos + 2,
										gameState.toString().length());

						if (parentNumPlays < 24 && parentNumPlays > 0) {//ignore triviality of first and last moves; do pruning for all others
							boardExpectedValue = getExpectedBoardScore(possibleMove, childNumPlays, canDraw, 5);
								if (boardExpectedValue > maxChildExpectedValue) {
									maxChildExpectedValue = boardExpectedValue;
								}
								prunedMoves.add(new xMCTSStringGameState(possibleMove, boardExpectedValue, childNumPlays));
								totalChildrenPruned++;
						}
						else { //numplays 0 or 24, so no pruning here
							posMoves.add(new xMCTSStringGameState(possibleMove, 0.0, childNumPlays));
						}
					}
				}
			}
			if (parentNumPlays < 24 && parentNumPlays > 0) {//should only run this when considering pruning
					for (xMCTSStringGameState move : prunedMoves) {
						if (move.expectedValue == maxChildExpectedValue) {
							totalChildrenPruned--;
							posMoves.add(move);
							break;
						}
					}
			timesCalled++;
			overAllMoves += prunedMoves.size();
			percentPruned = (posMoves.size() / (double) prunedMoves.size());
			totalPercentPruned += percentPruned;
			}
		}
		return posMoves;
	}
	

	public double getExpectedBoardScore(String possibleMove, int numPlays, boolean[] canDraw, int numHands) {
		double handExpectedValue;
		double boardExpectedValue = 0;

		// SIZE is 5: the size of one dimension of the board grid (5x5)
		// when numHands is 5, you check all 5 hands in rows and columns. When numHands is 4, you only check the first 4 hands in rows and columns.
		for (int childRow = 0; childRow < numHands*SIZE*2; childRow += SIZE*2) {//for each horizontal hand in this potential child state
			handExpectedValue = getExpectedHandScore(new Card[] {Card.getCard(possibleMove.substring(childRow, childRow+2)), 
																 Card.getCard(possibleMove.substring(childRow+2, childRow+4)),
																 Card.getCard(possibleMove.substring(childRow+4, childRow+6)), 
																 Card.getCard(possibleMove.substring(childRow+6, childRow+8)),
																 Card.getCard(possibleMove.substring(childRow+8, childRow+10))}, 
													 numPlays, canDraw);
			boardExpectedValue += handExpectedValue;
		}


		for (int childCol = 0; childCol < numHands*2; childCol+= 2) {//for each vertical hand in this potential child state
			handExpectedValue = getExpectedHandScore(new Card[] {Card.getCard(possibleMove.substring(childCol, childCol+2)), 
																 Card.getCard(possibleMove.substring(childCol+10, childCol+12)),
																 Card.getCard(possibleMove.substring(childCol+20, childCol+22)), 
																 Card.getCard(possibleMove.substring(childCol+30, childCol+32)),
																 Card.getCard(possibleMove.substring(childCol+40, childCol+42))}, 
													 numPlays, canDraw);
			boardExpectedValue += handExpectedValue;
		}
		return boardExpectedValue;
	}

	public double getExpectedHandScore(Card[] hand, int numPlays, boolean[] canDraw) {
		int numCards = 0;
		int[] rankCounts = new int[Card.NUM_RANKS];
		int[] suitCounts = new int[Card.NUM_SUITS];
		double[] handProbabilities; //The probability of completing a hand in the next draw
		int[] isHandPossible;
		double handExpectedValue = 0.0;
		// Compute counts
		for (Card card : hand) {
			if (card != null) {
				rankCounts[card.getRank()]++;
				suitCounts[card.getSuit()]++;
				numCards++;
			}
		}
		if (numCards >= 4) {//if this hand is of length 4 or 5, calculate out probabilities
			handProbabilities = getPossibleHandsCardCounting(hand, numPlays, canDraw, rankCounts, suitCounts, numCards);
			for (int i = 0; i < handProbabilities.length; i++) { //add the expected value of each hand type to this hand's expected value. Hands that are impossible to make result in adding 0
				double cardCountWeight;
				if (handProbabilities[i] > 0) {
					if (numCards == 4)
						cardCountWeight = FOUR_CARD_WEIGHT;
					else //5 cards
						cardCountWeight = FIVE_CARD_WEIGHT;
					handExpectedValue += handProbabilities[i] * pointSystem.getHandScore(i) * cardCountWeight;
				}

			}

		}

		else {//if this hand has less than 4 cards, see what poker hand types are still possible, and use weighting and a-priori probabilities to give an estimated expected value
			    // not actually computing an expected value in handExpectedValue, since the probabilities we use won't add up to 1. (Impossible hands are 0-terms.)
			    // So handExpectedValue here is just a "score" of some kind.
			double cardCountWeight;
			if (numCards != 0) {
				if (numCards == 3)
					cardCountWeight = THREE_CARD_WEIGHT;
				else if (numCards == 2)
					cardCountWeight = TWO_CARD_WEIGHT;
				else //(numCards == 1)
					cardCountWeight = ONE_CARD_WEIGHT;
	
				isHandPossible = getPossibleHands(hand, rankCounts, suitCounts, numCards);
				for (int i = 0; i < isHandPossible.length; i++) {
					if (isHandPossible[i] == COMPLETED) { //if a hand type is possible, add its a-priori expected value to this hand's expected value including the card weights
						handExpectedValue += /*aprioriProb[i] * */pointSystem.getHandScore(i) * cardCountWeight;// do we need to weight by probability if it's already complete?
					}
					else if (isHandPossible[i] == POSSIBLE) {//if a hand type is possible, add its a-priori expected value to this hand's expected value including the card and moves remaining weights
						handExpectedValue += aprioriProb[i] * pointSystem.getHandScore(i) * cardCountWeight; 
					}
				}
			}
			else {
				handExpectedValue += ZERO_CARD;
			}
		}
		return handExpectedValue;
	}



	public ArrayList<xMCTSStringGameState> getPossibleMoves(xMCTSStringGameState state) {//this method should never be called from pruning code 
		System.out.println("Calling getPossibleMoves with only one parameter");
		ArrayList<xMCTSStringGameState> posMoves = new ArrayList<xMCTSStringGameState>();
		posMoves.add(new xMCTSStringGameState("____________________________________________________", 0.0, 0));
		return posMoves;
	}
   
   @Override
   public status gameStatus(xMCTSGameState state)
   {
      String s = state.toString().substring(0, state.toString().length() - 2);
      if (!s.contains("_")) {
         return xMCTSGame.status.GAMEEND;
      } else {
         return xMCTSGame.status.ONGOING;
      }
   }
 
   @Override
   public xMCTSStringGameState getStartingState()//Set the starting state equal to 10 times the minimum hand value for this board
   {
	  int minHandValue = Integer.MAX_VALUE;
	  for (int i = 0; i < 10; i++) {
		  if (minHandValue > pointSystem.getHandScore(i))
		  	minHandValue = pointSystem.getHandScore(i);
	  }
      return new xMCTSStringGameState("____________________________________________________", (minHandValue*10), 0);
   }
   
   //Reset certain variables without needing to recreate the game class every single time a new game starts
   public void resetTrackingValues() {
	   totalPercentPruned = 0;
	   timesCalled = 0;
	   overAllMoves = 0;
   } 
   
	/**
	 *  getPossibleHands - gets an array of cards representing a poker hand and evaluates the possible hands
	 *  which could yet be made
	 *  @return array of the int flags representing the 10 possible poker hands
	 */
	public int[] getPossibleHands(Card[] hand, int[] rankCounts, int[] suitCounts, int numCards){
		int[] posHands;
		
		// rankCounts[0] is how many Aces you have...
		// rankCounts[1] is how many 2's you have...
		// suitCounts[0] is how many Clubs you have...

		if (numCards == 5) {
			posHands = new int[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
			posHands[PokerHand.getPokerHandId(hand)] = COMPLETED;
			return posHands;
		}
		else if (numCards == 1) { 		// can possibly eliminate the royal flush
			if (!((rankCounts[0] == 0) && (rankCounts[9] == 0) && (rankCounts[10] == 0) && (rankCounts[11] == 0) && (rankCounts[12] == 0))) { //If a royal flush is possible
				return new int[] {COMPLETED, POSSIBLE, POSSIBLE, POSSIBLE, POSSIBLE, POSSIBLE, POSSIBLE, POSSIBLE, POSSIBLE, POSSIBLE};
			}
			
			else {
				return new int[] {COMPLETED, POSSIBLE, POSSIBLE, POSSIBLE, POSSIBLE, POSSIBLE, POSSIBLE, POSSIBLE, POSSIBLE, IMPOSSIBLE};
			}
		}
		else {
			posHands = new int[] {COMPLETED, POSSIBLE, POSSIBLE, POSSIBLE, POSSIBLE, POSSIBLE, POSSIBLE, POSSIBLE, POSSIBLE, POSSIBLE};
			
			// Compute count of rank counts
			int maxOfAKind = 0;
			int[] rankCountCounts = new int[hand.length + 1];
			for (int count : rankCounts) {
				rankCountCounts[count]++;
				if (count > maxOfAKind)
					maxOfAKind = count;
			}
			// rankCountCounts[i] is the number of times that you have i cards of the same rank.
			// For example: [11, 1, 0, 1, 0, ....] means you have 1 three of a kind, 1 "1 of a kind", and 11 ranks that don't appear at all.
			// maxOfAKind is the most of any one rank you have
			
			// process at least 2 cards
			// can possibly eliminate Straight, Flush, Straight flush and/or Royal flush
			if (numCards >= 2){
				//Pair check, once you have a pair you can no longer have a high card
				if (maxOfAKind == 2) {
					posHands[0] = IMPOSSIBLE;
					posHands[1] = COMPLETED;
				}
				
				boolean hasStraight = true;

				// Flush check
				boolean hasFlush = ((suitCounts[0] == numCards) || (suitCounts[1] == numCards) || (suitCounts[2] == numCards) || (suitCounts[3] == numCards));

				// Straight check
				// ace high straight
				boolean hasAceHighStraight = !(rankCounts[0] > 1 || rankCounts[1] > 0 || rankCounts[2] > 0 || rankCounts[3] > 0 || rankCounts[4] > 0 || rankCounts[5] > 0 || 
						rankCounts[6] > 0 || rankCounts[7] > 0 || rankCounts[8] > 0 || rankCounts[9] > 1 || rankCounts[10] > 1 || rankCounts[11] > 1 || rankCounts[12] > 1);

				// Don't have ace high straight. Do we have any other kind of straight?
				int rank = 0;
				while (rankCounts[rank] == 0)
					rank++;
				// Now, rank is the rank of the lowest card in the hand.

				// cards within 5 ranks and handling ace as low card	
				// face cards
				if (rank > Card.NUM_RANKS - 5) {
					for(int i = rank; i < Card.NUM_RANKS ; i++){
						if (rankCounts[i] > 1){
							hasStraight = false;
							break;
						}
					}
				}

				else { // check beginning 5 higher than rank for any card
					for(int i = rank + 5; i < Card.NUM_RANKS ; i++){
						if (rankCounts[i] > 0){
							hasStraight = false;
							break;
						}
					}

					if (hasStraight) { // if we *might* still have a straight
						// check for duplicates (pairs) within 5 ranks		
						for(int i = rank; i < rank + 5 ; i++){
							if (rankCounts[i] > 1){
								hasStraight = false;
								break;
							}
						}
					}
				}
				//				}

				// Royal Flush
				if (!(hasFlush && hasAceHighStraight))
					posHands[9] = IMPOSSIBLE;
				//Straight Flush, only on if its not a royal
				if (!(hasFlush && hasStraight))
					posHands[8] = IMPOSSIBLE;
				//Flush, only on if not a straight flush or royal
				if (!(hasFlush))
					posHands[5] = IMPOSSIBLE;
				//Straight, only on if not a straight flush or royal
				if (!(hasStraight || hasAceHighStraight))
					posHands[4] = IMPOSSIBLE;
				
			}

			
			if (numCards == 3) {
			
				if (maxOfAKind == 1) {
					posHands[6] = IMPOSSIBLE;
					posHands[7] = IMPOSSIBLE;
				}
				
				else if (maxOfAKind == 2) {
					posHands[0] = IMPOSSIBLE;
					posHands[1] = COMPLETED;
				}
				
				else if (maxOfAKind == 3) {
					posHands[0] = IMPOSSIBLE;
					posHands[1] = IMPOSSIBLE;
					posHands[2] = IMPOSSIBLE;
					posHands[3] = COMPLETED;
				}
				// no matter what, with 2 spaces open could still have 2 of a kind or 3 of a kind or two pairs
			}
			
			// process at least 4 cards
			if (numCards == 4){
				// with one card remaining to draw

				if (maxOfAKind == 3) { // three-of-a-kind
					posHands[0] = IMPOSSIBLE;
					posHands[1] = IMPOSSIBLE;
					posHands[2] = IMPOSSIBLE;
					posHands[3] = COMPLETED;
				}	
				else if (maxOfAKind == 4) {// four-of-a-kind
					posHands[0] = IMPOSSIBLE;
					posHands[1] = IMPOSSIBLE;
					posHands[2] = IMPOSSIBLE;
					posHands[3] = IMPOSSIBLE;
					posHands[7] = COMPLETED;
				}
				else if (rankCountCounts[2] == 2) { // two pair
					posHands[0] = IMPOSSIBLE;
					posHands[1] = IMPOSSIBLE;
					posHands[3] = IMPOSSIBLE;
					posHands[2] = COMPLETED;
				}
				else if (rankCountCounts[2] < 1) { // 0 pairs
					posHands[1] = IMPOSSIBLE;
					posHands[2] = IMPOSSIBLE;
				}
			}
			return posHands;
		}
	}
	
	/**
	 *  getPossibleHandsCardCount - Compiles an array of hand probabilities based upon possible hands
	 *  and cards already dealt
	 *  @param hand - array of cards in a hand
	 *  @param numPlays - the number of cards dealt/played thus far
	 *  @return array of doubles which are the probabilities of the 10 possible poker hands
	 */
	public double[] getPossibleHandsCardCounting(Card[] hand, int numPlays, boolean[] canDraw, int[] rankCounts, int[] suitCounts, int numCards) {
		double[] handProbs = {0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0};
		boolean[] possibleHands;
		int numPossibleCards;
		// rankCounts[0] is how many Aces you have...
		// rankCounts[1] is how many 2's you have...
		// suitCounts[0] is how many Clubs you have...

		int rank = 0;
		while (rankCounts[rank] == 0)
			rank++;
		// Now, rank is the rank of the lowest card in the hand.
		if (numCards == 5) {
			handProbs[PokerHand.getPokerHandId(hand)] = 1.0;
			return handProbs;
		}
		possibleHands = new boolean[] {true, true, true, true, true, true, true, true, true, true};

		// Compute count of rank counts
		int maxOfAKind = 0;
		int[] rankCountCounts = new int[hand.length + 1];
		for (int count : rankCounts) {
			rankCountCounts[count]++;
			if (count > maxOfAKind)
				maxOfAKind = count;
		}
		// rankCountCounts[i] is the number of times that you have i cards of the same rank.
		// For example: [11, 1, 0, 1, 0, ....] means you have 1 three of a kind, 1 "1 of a kind", and 11 ranks that don't appear at all.
		// maxOfAKind is the most of any one rank you have

		boolean hasHigh = true;
		boolean hasPair = true;

		// process at least 2 cards
		// can possibly eliminate Straight, Flush, Straight flush and/or Royal flush
		if (numCards >= 2){
			//Pair check, once you have a pair you can no longer have a high card
			if (maxOfAKind == xCard.TWO_KIND)
				hasHigh = false;

			// Flush check
			boolean hasStraight = true;

			// Flush check
			boolean hasFlush = ((suitCounts[xCard.CLUB] == numCards) || (suitCounts[xCard.DIAMOND] == numCards) || 
					(suitCounts[xCard.HEART] == numCards) || (suitCounts[xCard.SPADE] == numCards));

			// Straight check
			// ace high straight
			boolean hasAceHighStraight = !(rankCounts[xCard.ACE] > 1 || rankCounts[xCard.TWO] > 0 || rankCounts[xCard.THREE] > 0 || 
					rankCounts[xCard.FOUR] > 0 || rankCounts[xCard.FIVE] > 0 || rankCounts[xCard.SIX] > 0 || rankCounts[xCard.SEVEN] > 0 || 
					rankCounts[xCard.EIGHT] > 0 || rankCounts[xCard.NINE] > 0 || rankCounts[xCard.TEN] > 1 || rankCounts[xCard.JACK] > 1 || 
					rankCounts[xCard.QUEEN] > 1 || rankCounts[xCard.KING] > 1);

			//				if (!hasAceHighStraight) {
			// Don't have ace high straight. Do we have any other kind of straight?

			// cards within 5 ranks and handling ace as low card	
			// face cards
			if (rank > xCard.NUM_RANKS - 5) {
				for(int i = rank; i < xCard.NUM_RANKS ; i++){
					if (rankCounts[i] > 1){
						hasStraight = false;
						break;
					}
				}
			}

			else { // check beginning 5 higher than rank for any card
				for(int i = rank + 5; i < xCard.NUM_RANKS ; i++){
					if (rankCounts[i] > 0){
						hasStraight = false;
						break;
					}
				}

				if (hasStraight) { // if we *might* still have a straight
					// check for duplicates (pairs) within 5 ranks		
					for(int i = rank; i < rank + 5 ; i++){
						if (rankCounts[i] > 1){
							hasStraight = false;
							break;
						}
					}
				}
			}
			//				}

			// Royal Flush
			possibleHands[PokerHand.ROYAL_FLUSH.id] = hasFlush && hasAceHighStraight;
			//Straight Flush, only on if its not a royal
			possibleHands[PokerHand.STRAIGHT_FLUSH.id] = hasFlush && hasStraight;
			//Flush, only on if not a straight flush or royal
			possibleHands[PokerHand.FLUSH.id] = hasFlush;
			//Straight, only on if not a straight flush or royal
			possibleHands[PokerHand.STRAIGHT.id] = hasStraight || hasAceHighStraight;

		}


		if (numCards == 3) {
			boolean hasFour = false;
			boolean hasFull = false;
			boolean hasTwoPair = true;
			if (maxOfAKind == xCard.TWO_KIND) {
				hasFull = true;
				hasFour = true;
			}
			else if (maxOfAKind == xCard.THREE_KIND) {
				hasFull = true;
				hasFour = true;
				hasPair = false;
				hasHigh = false;
				hasTwoPair = false;
			}

			possibleHands[PokerHand.TWO_PAIR.id] = hasTwoPair;
			possibleHands[PokerHand.FULL_HOUSE.id] = hasFull;
			possibleHands[PokerHand.FOUR_OF_A_KIND.id] = hasFour;

			// no matter what, could still have 2 of a kind or 3 of a kind or two pairs
		}

		// process at least 4 cards
		if (numCards == 4){
			// with one card remaining to draw
			boolean hasTwoPair = true;
			boolean hasThree = true;
			boolean hasFour = false;
			boolean hasFull = false;

			if (maxOfAKind == xCard.THREE_KIND) { // three-of-a-kind
				hasFour = true;
				hasFull = true;
				hasTwoPair = false;
				hasPair = false;
				hasHigh = false;
			}	
			else if (maxOfAKind == xCard.FOUR_KIND) {// four-of-a-kind
				hasFour = true;
				hasThree = false;
				hasTwoPair = false;
				hasPair = false;
				hasHigh = false;
			}
			else if (rankCountCounts[PokerHand.TWO_PAIR.id] == 2) { // two pair
				hasFull = true;
				hasThree = false;
				hasPair = false;
				hasHigh = false;
			}
			else if (rankCountCounts[PokerHand.TWO_PAIR.id] < 1) { // 0 pairs
				hasThree = false;
				hasTwoPair = false;
			}
			possibleHands[PokerHand.TWO_PAIR.id] = hasTwoPair;
			possibleHands[PokerHand.THREE_OF_A_KIND.id] = hasThree;
			possibleHands[PokerHand.FULL_HOUSE.id] = hasFull;
			possibleHands[PokerHand.FOUR_OF_A_KIND.id] = hasFour;
		}

		possibleHands[PokerHand.HIGH_CARD.id] = hasHigh;
		possibleHands[PokerHand.ONE_PAIR.id] = hasPair;


		
		
		
		
		//For each hand still possible, check the probability of drawing the cards still needed
		if (numCards == 4) {
			//System.out.println(java.util.Arrays.toString(possibleHands));
			int suitIndex = 0;
			int rankIndex = 0;

			// Royal flush with or without already having ace but since the required
			// it limits the search space since only only one card is needed to make hand
			if (possibleHands[PokerHand.ROYAL_FLUSH.id]) {
				for (int i = 9; i < xCard.NUM_RANKS; i++) {
					if (rankCounts[i] == 0) { 
						rankIndex = i;
						break;
					}
				}
				suitIndex = findSuitIndex(suitCounts);
				if (canDraw[rankIndex + (suitIndex * xCard.NUM_RANKS)])
					handProbs[PokerHand.ROYAL_FLUSH.id] = calcProbability(numPlays, 1);

			}
			// Straight flush
			if (possibleHands[PokerHand.STRAIGHT_FLUSH.id]) {
					int maxRank = 0;
					suitIndex = findSuitIndex(suitCounts);
					numPossibleCards = 0;
					// find the maximum rank in the hand
					for (int i = 12; i >= rank ; i--) {
						if (rankCounts[i] > 0){
							maxRank = i;
							break;
						}
					}
					// Ace high
					if (rankCounts[xCard.ACE] > 0 && maxRank > xCard.NINE) { //have an ACE and maxRank is greater than 9: ace-high straight
						//Search through indexes 9-12. Search through all the suits of the rank of the card you're missing. Any that can still be drawn are added
						for (int i = xCard.TEN; i < xCard.NUM_RANKS; i++) {
							if (rankCounts[i] == 0) {
								if (canDraw[i + (suitIndex * xCard.NUM_RANKS)])
									numPossibleCards++;
								break;
							}
						}
					}
					// check for the kind of straight possible, inside(!=3) or outside (3) - ace high's are already handled
					else if ((maxRank - rank) != 3  ) { // inside straight - we need a card that's in the middle
						// calculate probability of the one missing card
						for (int i = rank; i < rank + 4; i++) { // not <= because we know we have the last card (this is an inside straight)
							if (rankCounts[i] == 0) { 
								if (canDraw[i + (suitIndex * xCard.NUM_RANKS)])
									numPossibleCards++;
								break;
							}
						}	
					} 
					
					else { // outside straight 
						// if have a King high straight look for Ace and 9
						if (maxRank == xCard.KING){
							if (canDraw[xCard.ACE + (suitIndex* xCard.NUM_RANKS)])
								numPossibleCards++;

							if (canDraw[xCard.NINE + (suitIndex * xCard.NUM_RANKS)])
								numPossibleCards++;
						}
						else if (rank == xCard.ACE) {
							if (canDraw[xCard.FIVE + (suitIndex * xCard.NUM_RANKS)])
								numPossibleCards++;
						}
						else {	// find the cards needed for outside straight
							if (canDraw[rank - 1 + (suitIndex * xCard.NUM_RANKS)])
								numPossibleCards++;

							if (canDraw[rank + 4 + (suitIndex * xCard.NUM_RANKS)])
								numPossibleCards++;
						}
					}

					handProbs[PokerHand.STRAIGHT_FLUSH.id] = calcProbability(numPlays, numPossibleCards);

					//Remove the chances of drawing a royal or straight flush
					handProbs[PokerHand.STRAIGHT_FLUSH.id] -= handProbs[PokerHand.ROYAL_FLUSH.id];
			}

			//Four-of-a-Kind
			if (possibleHands[PokerHand.FOUR_OF_A_KIND.id]) {
				boolean hasFour = false;
				for (int i = rank; i < rankCounts.length; i++) {
					//if you have a three of a kind, return that rank's index
					if (rankCounts[i] == xCard.THREE_KIND) {
						rankIndex = i;
						break;
					}

					//if you have a four of a kind, you're done
					else if (rankCounts[i] == xCard.FOUR_KIND) {
						hasFour = true; //Could have used maxOfAKind to check for already having 4, but that would not also give us the index
						break;
					}
				}

				if (!hasFour) {
					numPossibleCards = 0;

					//Search through the hand for the missing suit of your three of a kind
					for (Card toFind : hand) {
						if (toFind != null) {
							if (toFind.getRank() == rankIndex) {
								if (canDraw[rankIndex + (xCard.HEART * xCard.NUM_RANKS)]) {
									numPossibleCards++;
									break;
								}
								
								else if (canDraw[rankIndex + (xCard.SPADE * xCard.NUM_RANKS)]) {
									numPossibleCards++;
									break;
								}
								
								else if (canDraw[rankIndex + (xCard.CLUB * xCard.NUM_RANKS)]) {
									numPossibleCards++;
									break;
								}
								
								else if (canDraw[rankIndex + (xCard.DIAMOND * xCard.NUM_RANKS)]) {
									numPossibleCards++;
									break;
								}
							}
						}
					}
					handProbs[PokerHand.FOUR_OF_A_KIND.id] = calcProbability(numPlays, numPossibleCards);
				}

				//If you already have a four of a kind, that's all you can have
				else {
					handProbs[PokerHand.FOUR_OF_A_KIND.id] = 1.0;
				}
			}

			//Full House
			if (possibleHands[PokerHand.FULL_HOUSE.id]) {

				numPossibleCards = 0;
				//If we already have 3 of a kind
				if (maxOfAKind == xCard.THREE_KIND) {
					for (int i = rank; i < rankCounts.length; i++) {
						//find your single card and return its index
						if (rankCounts[i] == 1) {
							rankIndex = i;
							break;
						}
					}

					//Search through the hand for the suit of your single card. When you find it, check if you can draw any 
					//of them and if you can then add their probabilities, then break.
					if (canDraw[rankIndex + (xCard.DIAMOND * xCard.NUM_RANKS)])
						numPossibleCards++;

					if (canDraw[rankIndex + (xCard.HEART * xCard.NUM_RANKS)])
						numPossibleCards++;

					if (canDraw[rankIndex + (xCard.SPADE * xCard.NUM_RANKS)])
						numPossibleCards++;

					if (canDraw[rankIndex + (xCard.CLUB * xCard.NUM_RANKS)])
						numPossibleCards++;
				}
				// numPossibleCards will be as high as 3, if all the other cards of the needed rank are still available.

			
		

				//If we already have 2 pair
				else {
					numPossibleCards = 0;
					for (int i = rank; i < rankCounts.length; i++) {
						//find any pairs
						if (rankCounts[i] == xCard.TWO_KIND) {
							rankIndex = i;

										if (canDraw[rankIndex + (xCard.DIAMOND * xCard.NUM_RANKS)])
											numPossibleCards++;
										
										if (canDraw[rankIndex + (xCard.HEART * xCard.NUM_RANKS)])
											numPossibleCards++;
										
										if (canDraw[rankIndex + (xCard.SPADE * xCard.NUM_RANKS)])
											numPossibleCards++;
										
										if (canDraw[rankIndex + (xCard.CLUB * xCard.NUM_RANKS)])
											numPossibleCards++;
									}
								}
				}
					handProbs[PokerHand.FULL_HOUSE.id] = calcProbability(numPlays, numPossibleCards);
			}

			//Flush				
			if (possibleHands[PokerHand.FLUSH.id]) {
				suitIndex = findSuitIndex(suitCounts);
				numPossibleCards = 0;
				for (int i = 0; i < rankCounts.length; i++) {
					//find any undrawn cards
					if (rankCounts[i] == 0) {
						rankIndex = i;

						//If the card of this rank and suit is still in the deck, add the probability of drawing it
						if (canDraw[rankIndex + (suitIndex * xCard.NUM_RANKS)])
							numPossibleCards++;
					}
				}
				handProbs[PokerHand.FLUSH.id] = calcProbability(numPlays, numPossibleCards);

				//Don't double count hands that make royal or straight flushes
				handProbs[PokerHand.FLUSH.id] -= handProbs[PokerHand.STRAIGHT_FLUSH.id] + handProbs[PokerHand.ROYAL_FLUSH.id];
			}

			//Straight
			if (possibleHands[PokerHand.STRAIGHT.id]) {
				int maxRank = 0;
				numPossibleCards = 0;
				// find the maximum rank in the hand
				for (int i = 12; i >= rank ; i--) {
					if (rankCounts[i] > 0){
						maxRank = i;
						break;
					}
				}
				// Ace high
				if (rankCounts[xCard.ACE] > 0 && maxRank > xCard.NINE) { //have an ACE and maxRank is greater than 9: ace-high straight
					//Search through indexes 9-12. Search through all the suits of the rank of the card you're missing. Any that can still be drawn are added
					for (int i = xCard.TEN; i < xCard.NUM_RANKS; i++) {
						if (rankCounts[i] == 0) {
							if (canDraw[i + (xCard.DIAMOND * xCard.NUM_RANKS)])
								numPossibleCards++;

							if (canDraw[i + (xCard.HEART * xCard.NUM_RANKS)])
								numPossibleCards++;

							if (canDraw[i + (xCard.SPADE * xCard.NUM_RANKS)])
								numPossibleCards++;

							if (canDraw[i + (xCard.CLUB * xCard.NUM_RANKS)])
								numPossibleCards++;
							break;
						}
					}
				}
				// check for the kind of straight possible, inside(!=3) or outside (3) - ace high's are already handled
				else if ((maxRank - rank) != 3  ) { // inside straight - we need a card that's in the middle
					// calculate probability of the one missing card
					for (int i = rank; i < rank + 4; i++) { // not <= because we know we have the last card (this is an inside straight)
						if (rankCounts[i] == 0) { 
							if (canDraw[i + (xCard.DIAMOND * xCard.NUM_RANKS)])
								numPossibleCards++;

							if (canDraw[i + (xCard.HEART * xCard.NUM_RANKS)])
								numPossibleCards++;

							if (canDraw[i + (xCard.SPADE * xCard.NUM_RANKS)])
								numPossibleCards++;

							if (canDraw[i + (xCard.CLUB * xCard.NUM_RANKS)])
								numPossibleCards++;
							break;
						}
					}	
				} 
				else { // outside straight 
					// if have a King high straight look for Ace and 9
					if (maxRank == xCard.KING){
						if (canDraw[xCard.ACE + (xCard.DIAMOND * xCard.NUM_RANKS)])
							numPossibleCards++;

						if (canDraw[xCard.ACE + (xCard.HEART * xCard.NUM_RANKS)])
							numPossibleCards++;

						if (canDraw[xCard.ACE + (xCard.SPADE * xCard.NUM_RANKS)])
							numPossibleCards++;

						if (canDraw[xCard.ACE + (xCard.CLUB * xCard.NUM_RANKS)])
							numPossibleCards++;

						if (canDraw[xCard.NINE + (xCard.DIAMOND * xCard.NUM_RANKS)])
							numPossibleCards++;

						if (canDraw[xCard.NINE + (xCard.HEART * xCard.NUM_RANKS)])
							numPossibleCards++;

						if (canDraw[xCard.NINE + (xCard.SPADE * xCard.NUM_RANKS)])
							numPossibleCards++;

						if (canDraw[xCard.NINE + (xCard.CLUB * xCard.NUM_RANKS)])
							numPossibleCards++;
					}
					else if (rank == xCard.ACE) {
						if (canDraw[xCard.FIVE + (xCard.DIAMOND * xCard.NUM_RANKS)])
							numPossibleCards++;

						if (canDraw[xCard.FIVE + (xCard.HEART * xCard.NUM_RANKS)])
							numPossibleCards++;

						if (canDraw[xCard.FIVE + (xCard.SPADE * xCard.NUM_RANKS)])
							numPossibleCards++;

						if (canDraw[xCard.FIVE + (xCard.CLUB * xCard.NUM_RANKS)])
							numPossibleCards++;
					}
					else {	// find the cards needed for outside straight
						if (canDraw[rank - 1 + (xCard.DIAMOND * xCard.NUM_RANKS)])
							numPossibleCards++;

						if (canDraw[rank - 1 + (xCard.HEART * xCard.NUM_RANKS)])
							numPossibleCards++;

						if (canDraw[rank - 1 + (xCard.SPADE * xCard.NUM_RANKS)])
							numPossibleCards++;

						if (canDraw[rank - 1 + (xCard.CLUB * xCard.NUM_RANKS)])
							numPossibleCards++;

						if (canDraw[rank + 4 + (xCard.DIAMOND * xCard.NUM_RANKS)])
							numPossibleCards++;

						if (canDraw[rank + 4 + (xCard.HEART * xCard.NUM_RANKS)])
							numPossibleCards++;

						if (canDraw[rank + 4 + (xCard.SPADE * xCard.NUM_RANKS)])
							numPossibleCards++;

						if (canDraw[rank + 4 + (xCard.CLUB * xCard.NUM_RANKS)])
							numPossibleCards++;
					}
				}
				handProbs[PokerHand.STRAIGHT.id] = calcProbability(numPlays, numPossibleCards);

				//Remove the chances of drawing a royal or straight flush
				handProbs[PokerHand.STRAIGHT.id] -= handProbs[PokerHand.STRAIGHT_FLUSH.id] + handProbs[PokerHand.ROYAL_FLUSH.id];
			}

			//Three of a Kind
			if (possibleHands[PokerHand.THREE_OF_A_KIND.id]) {

				//if you have 3 of a kind already, you're guaranteed a 3 of a kind unless you draw a four of a kind or a full house
				if (maxOfAKind == xCard.THREE_KIND) {
					handProbs[PokerHand.THREE_OF_A_KIND.id] = 1.0 - handProbs[PokerHand.FOUR_OF_A_KIND.id] - handProbs[PokerHand.FULL_HOUSE.id];
				}

				//if you have a pair already
				else if (maxOfAKind == xCard.TWO_KIND) {
					numPossibleCards = 0;
					for (int i = rank; i < rankCounts.length; i++) {
						//find your pair, return that rank's index
						if (rankCounts[i] == xCard.TWO_KIND) {
							rankIndex = i;
							if (canDraw[rankIndex + (xCard.DIAMOND * xCard.NUM_RANKS)])
								numPossibleCards++;
							
							if (canDraw[rankIndex + (xCard.HEART * xCard.NUM_RANKS)])
								numPossibleCards++;
							
							if (canDraw[rankIndex + (xCard.SPADE * xCard.NUM_RANKS)])
								numPossibleCards++;
							
							if (canDraw[rankIndex + (xCard.CLUB * xCard.NUM_RANKS)])
								numPossibleCards++;
							break;
						}
					}
					handProbs[PokerHand.THREE_OF_A_KIND.id] = calcProbability(numPlays, numPossibleCards);
				}
			}

			//Two Pair
			if (possibleHands[PokerHand.TWO_PAIR.id]) {
				//Already have 2 pair
				if (rankCountCounts[PokerHand.TWO_PAIR.id] == 2) {
					handProbs[PokerHand.TWO_PAIR.id] = 1.0 - handProbs[PokerHand.FULL_HOUSE.id]; // subtract prob of full house
				}

				//1 pair and 2 unmatched
				else {
					numPossibleCards = 0;
					for (int i = rank; i < rankCounts.length; i++) {
						//find any single cards
						if (rankCounts[i] == 1) {
							rankIndex = i;
							//Search through the hand for the suit of your single card
							if (canDraw[rankIndex + (xCard.DIAMOND * xCard.NUM_RANKS)])
								numPossibleCards++;

							if (canDraw[rankIndex + (xCard.HEART * xCard.NUM_RANKS)])
								numPossibleCards++;

							if (canDraw[rankIndex + (xCard.SPADE * xCard.NUM_RANKS)])
								numPossibleCards++;

							if (canDraw[rankIndex + (xCard.CLUB * xCard.NUM_RANKS)])
								numPossibleCards++;
						}
					}
					handProbs[PokerHand.TWO_PAIR.id] = calcProbability(numPlays, numPossibleCards);
				}		
			}

			//Pair
			if (possibleHands[PokerHand.ONE_PAIR.id]) {
				//Already have a pair
				if (rankCountCounts[2] == 1) {
					handProbs[PokerHand.ONE_PAIR.id] = 1.0 - (handProbs[PokerHand.TWO_PAIR.id] + handProbs[PokerHand.THREE_OF_A_KIND.id]); // subtract prob of 2 pair and 3 of a kind
				}

				//No pair yet
				else {
					numPossibleCards = 0;
					for (int i = rank; i < rankCounts.length; i++) {
						//find any single cards
						if (rankCounts[i] == 1) {
							rankIndex = i;

							//Search through the hand for the suit of your single card. When you find it, check if you can draw any 
							//of the other three and if you can then add their probabilities, then break.
							if (canDraw[rankIndex + (xCard.DIAMOND * xCard.NUM_RANKS)])
								numPossibleCards++;

							if (canDraw[rankIndex + (xCard.HEART * xCard.NUM_RANKS)])
								numPossibleCards++;

							if (canDraw[rankIndex + (xCard.SPADE * xCard.NUM_RANKS)])
								numPossibleCards++;

							if (canDraw[rankIndex + (xCard.CLUB * xCard.NUM_RANKS)])
								numPossibleCards++;
						}
					}
					handProbs[PokerHand.ONE_PAIR.id] = calcProbability(numPlays, numPossibleCards);
				}
			}

			if (possibleHands[PokerHand.HIGH_CARD.id]) { // if this is true, then we know we can't have 2 (or more) of a kind
				handProbs[PokerHand.HIGH_CARD.id] = 1.0 - (handProbs[PokerHand.ONE_PAIR.id] + handProbs[PokerHand.STRAIGHT.id] + handProbs[PokerHand.FLUSH.id] + 
						handProbs[PokerHand.STRAIGHT_FLUSH.id] + handProbs[PokerHand.ROYAL_FLUSH.id]); // subtract pair, st, fl, sf, rf
				
			}
		}	 // end if (numCards == 4)	
		return handProbs;
	}

	/**
	 * calcProbability - gets the likelihood of drawing one of a specified number of cards given how many cards are remaining in the deck
	 * 
	 * @param numPlays - the number of plays so far, used to determine how many cards are left to be drawn
	 * @param numPossibleCards - the number of cards you need at least one of to complete the desired hand
	 * @return the likelihood of drawing one of the cards from numCardsNeeded
	 */	
	public double calcProbability(int numPlays, int numPossibleCards) {//Can be called multiple times to get the probability for each draw if multiple cards are missing
		int numAvailableCards = Card.NUM_CARDS - numPlays; //NOTE: numPlays here is the number of completed plays. If this is called 
			//in the middle of a play, it will account for a draw that has already occurred

		return ( (double) numPossibleCards/numAvailableCards);
	}
	
	/**
	 * findMissingCardIndex - find the rank index of the missing card
	 * 
	 * @param rankCounts - an array of integers that keeps track of the ranks in hand
	 * @param lowestRank - the rank to start the search for the missing card 
	 * @return the index of the card identified as missing
	 */	
	public int findMissingCardRankIndex(int[] rankCounts, int lowestRank) {
		System.out.println("rankCounts = " + java.util.Arrays.toString(rankCounts));
		for(int i = lowestRank; i < Card.NUM_RANKS; i++) {
			if (rankCounts[i] == 0) {
				System.out.println("Found card index: " + i);
				return i;
			}

		}
		throw new NullPointerException("findMissingCardIndex could not find the specified card");
	}
	
	/**
	 * findSuitIndex - find the suit index of this hand
	 * 
	 * @param suitCounts - an array of the number of cards of each suit in hand
	 * @return the index of the first suit identified - should be the only suit in any hand this is called on
	 */	
	public int findSuitIndex(int[] suitCounts) {
		for(int i = 0; i < Card.NUM_SUITS; i++) {
			if (suitCounts[i] > 0)
				return i;

		}
		 throw new NullPointerException("findSuitIndex could not find a valid suit");
	}
	
	/**
	 * isInDeck - returns true if the passed card is found, false if not
	 * 
	 * @param gameDeck - the player's current deck
	 * @param numPlays - number of completed plays in the game so far
	 * @param toFind - the card the method is searching through the deck for
	 * @return returns true if the passed card is found, false if not
	 */	
	public boolean isInDeck(boolean[] canDraw, Card toFind) {
		return canDraw[toFind.getRank() + (toFind.getSuit() * 13)];
	}
}
