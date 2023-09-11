package main;

public class Deck {
	
	private Card[] deck;
	private int cardsUsed;
	private int numDecks;
	
	public Deck() {
		this(1);
	}
	
	public Deck(int _numDecks) {
		if (_numDecks <= 0) {
			throw new IllegalArgumentException("Illegal number of decks.");
		}
		
		numDecks = _numDecks;
		
		deck = new Card[52 * numDecks];
		
		// Assign each card a suit and value
		int count = 0;
		for (int i = 0; i < numDecks; i++) {
			
			// loop by suit
			for (int suit = 0; suit < 4; suit++) {
				
				// loop by value
				for (int value = 2; value < 15; value++) {
					deck[count] = new Card(value, suit);
					count++;
				}
			}
		}
		cardsUsed = 0;
	}
	
	// Return num of decks
	public int getNumDecks() {
		return numDecks;
	}
	
	// Return num of cards used
	public int getCardsUsed() {
		return cardsUsed;
	}
	
	// Return num of cards left
	public int getCardsLeft() {
		return deck.length - getCardsUsed();
	}
	
	// Shuffle cards
	public void shuffle() {
		for (int i = deck.length - 1; i > 0; i--) {
			int rand = (int) (Math.random() * (i + 1));
			Card temp = deck[i];
			deck[i] = deck[rand];
			deck[rand] = temp;
		}
		cardsUsed = 0;
	}
	
	// Deal card
	public Card dealCard() {
		int currentUsed = cardsUsed;
		cardsUsed++;
		return deck[currentUsed];
	}

}
