package main;

public class Card {
	
	// ID's for card suits
	public final static int SPADES = 0;
	public final static int CLUBS = 1;
	public final static int HEARTS = 2;
	public final static int DIAMONDS = 3;
	
	private final int suit;
	private final int id;
	private final int countValue;
	private final int value;
	private final boolean ace;
	
	public Card(int _id, int _suit) {
		
		if (_suit != SPADES && _suit != CLUBS && _suit != HEARTS && _suit != DIAMONDS) {
			throw new IllegalArgumentException("Illegal value for playing card suit.");
		}
		if (_id < 2 || _id > 14) {
			throw new IllegalArgumentException("Illegal playing card value");
		}
		
		id = _id;
		suit = _suit;
		
		// Assign count value
		if (_id >= 10) {
			countValue = -1;
		} else if (_id <= 6) {
			countValue = 1;
		} else {
			countValue = 0;
		}
		
		// Assign card value
		if (_id <= 10) {
			value = _id;
			ace = false;
		} else if (_id > 10 && _id < 14) {
			value = 10;
			ace = false;
		} else {
			value = 11;
			ace = true;
		}
		
	}
	
	// Return suit ID
	public int getSuitID() {
		return suit;
	}
	
	// Return suit as a string
	public String getSuitString() {
		switch (suit) {
		case SPADES:
			return "Spades";
		case CLUBS:
			return "Clubs";
		case HEARTS:
			return "Hearts";
		default:
			return "Diamonds";
		}
	}
	
	// Return card id
	public int getId() {
		return id;
	}
	
	// Return card value
	public int getValue() {
		return value;
	}
	
	// Return card id as a string
	public String getIdString() {
		switch (id) {
		case 2:
			return "2";
		case 3:
			return "3";
		case 4:
			return "4";
		case 5:
			return "5";
		case 6:
			return "6";
		case 7:
			return "7";
		case 8:
			return "8";
		case 9:
			return "9";
		case 10:
			return "10";
		case 11:
			return "Jack";
		case 12:
			return "Queen";
		case 13:
			return "King";
		default:
			return "Ace";
		}
	}
	
	// Return count value
	public int getCountValue() {
		return countValue;
	}
	
	// Returns true if card is an ace
	public boolean isAce() {
		return ace;
	}
	
	public String toString() {
		return getIdString() + " of " + getSuitString();
	}

}
