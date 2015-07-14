package def;
import java.util.Arrays;
import java.util.HashMap;

/**
 * Card - Playing card class representing all 52 cards of a French deck.
 * @author Todd W. Neller
 */
public class xCard {

	// Static definitions
	
	/**
	 * the number of ranks in the 52-card French deck
	 */
	public static final int NUM_RANKS = 13;
	/**
	 * the number of suits in the 52-card French deck
	 */
	public static final int NUM_SUITS = 4;
	/**
	 * the number of cards in the 52-card French deck
	 */
	public static final int NUM_CARDS = NUM_RANKS * NUM_SUITS;
	
	public static final int ACE = 0;
	
	public static final int TWO = 1;
	
	public static final int THREE = 2;
	
	public static final int FOUR = 3;
	
	public static final int FIVE = 4;
	
	public static final int SIX = 5;
	
	public static final int SEVEN = 6;
	
	public static final int EIGHT = 7;
	
	public static final int NINE = 8;
	
	public static final int TEN = 9;
	
	public static final int JACK = 10;
	
	public static final int QUEEN = 11;
	
	public static final int KING = 12;
	
	public static final int CLUB = 0;
	
	public static final int DIAMOND = 1;
	
	public static final int HEART = 2;
	
	public static final int SPADE = 3;
	
	public static final int TWO_KIND = 2;
	
	public static final int THREE_KIND = 3;
	
	public static final int FOUR_KIND = 4;

	private static xCard[] allCards; // an array of all cards in the deck
	private static String[] rankNames = {"A", "2", "3", "4", "5", "6", "7", "8", "9", "T", "J", "Q", "K"}; // all single-character rank names
	private static String[] suitNames = {"C", "D", "H", "S"}; // all single-character suit names
	private static HashMap<String, xCard> cardMap = new HashMap<String, xCard>(); // mapping from String representations of cards to Card objects

	static {
		// upon loading the Card class, initialize 52-card French deck cards
		
		// create all card objects
		allCards = new xCard[rankNames.length * suitNames.length];
		int i = 0;
		for (int suit = 0; suit < suitNames.length; suit++) 
			for (int rank = 0; rank < rankNames.length; rank++)
				allCards[i++] = new xCard(rank, suit);
		
		// create mapping from String representations to Card objects
		for (xCard card : allCards)
			cardMap.put(card.toString(), card);
	}
	
	/**
	 * Return an array of Strings with the single-character rank names.
	 * @return an array of Strings with the single-character rank names
	 */
	public static String[] getRankNames() {
		return rankNames.clone();
	}
	
	/**
	 * Return an array of Strings with the single-character suit names.
	 * @return an array of Strings with the single-character suit names
	 */
	public static String[] getSuitNames() {
		return suitNames.clone();
	}

	/**
	 * Given a Card String representation, return the associated Card.
	 * @param cardString card String representation
	 * @return Card associated with given Card String representation
	 */
	public static xCard getCard(String cardString) {
		return cardMap.get(cardString);
	}

	/**
	 * Get the Card object associated with the given card identification integer.
	 * @param cardId the unique integer identification number for the desired card
	 * @return the Card object associated with the given card identification integer
	 */
	public static xCard getCard(int cardId) {
		return allCards[cardId];
	}

	/**
	 * Get an array containing all cards.
	 * @return an array containing all cards
	 */
	public static xCard[] getAllCards() {
		return allCards.clone();
	}

	// Non-static definitions
	
	private int rank; // the index of the rank String in rankNames
	private int suit; // the index of the suit String in suitNames

	/**
	 * Create a card with the given rank and suit.
	 * @param rank Card rank. Should be in range [0, NUM_RANKS - 1].
	 * @param suit Card suit. Should be in range [0, NUM_SUITS - 1].
	 */
	public xCard(int rank, int suit) {
		this.rank = rank;
		this.suit = suit;
	}

	/**
	 * Get Card rank. Should be in range [0, NUM_RANKS - 1].
	 * @return Card rank. Should be in range [0, NUM_RANKS - 1].
	 */
	public int getRank() {
		return rank;
	}

	/**
	 * Get Card suit. Should be in range [0, NUM_SUITS - 1].
	 * @return Card suit. Should be in range [0, NUM_SUITS - 1].
	 */
	public int getSuit() {
		return suit;
	}
	
	/**
	 * Return whether or not the card is an ace.
	 * @return whether or not the card is an ace
	 */
	public boolean isAce() {
		return rank == 0;
	}
	
	/**
	 * Return unique integer card identification number according to "suit-major" ordering where cards are ordered
	 * A, 2, ..., K within suits that are ordered alphabetically. This identification number is the 0-based suit 
	 * integer times the number of ranks plus the 0-based rank number.
	 * @return the 0-based suit integer times the number of ranks plus the 0-based rank number
	 */
	public int getCardId() {
		return suit * NUM_RANKS + rank;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public java.lang.String toString() {
		return rankNames[rank] + suitNames[suit];
	}

	/**
	 * Return whether or not this card equals another Card object.
	 * @param other the other Card object being tested for equality.
	 * @return whether or not this card equals another Card object
	 */
	public boolean equals(xCard other) {
		return other != null && this.rank == other.rank && this.suit == other.suit;		
	}
	
	/**
	 * Print all card objects.
	 * @param args (not used)
	 */
	public static void main(String[] args) {
		System.out.println(Arrays.toString(allCards));
	}
}
