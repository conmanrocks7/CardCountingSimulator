package main;

import java.util.Scanner;
import java.util.ArrayList;

public class Simulator {
	
	static int limit = 15; // Number of cards left in deck before needing to shuffle
	static int totalWin = 0;
	static int totalBet = 0;
	static int runningCount = 0;
	static Deck deck;
	
	static Scanner input = new Scanner(System.in);
	
	public static void main(String[] args) {
		
		System.out.println("+----- Blackjack -----+");
		System.out.println("\nGet as close to 21 without exceeding 21.");
		System.out.println("You will play against the dealer.");
		System.out.println("\nDealer must draw to 16 and stand on 17.");
		System.out.println("Blackjack pays 3:2.");
		
		System.out.println("\nTo reveal the true count, enter 'C' at any time.\n");
		
		play();

	}
	
	private static void play() {
		
		// Retrieve number of decks
		int numDecks = 0;
		do {
			try {
				System.out.print("How many decks would you like to play with: ");
				int decks = input.nextInt();
				if (decks > 0) {
					numDecks = decks;
				}
			} catch (Exception e) {
				// Exception if not a number
				System.out.println("Please enter a number");
			}
			input.nextLine();
		} while (numDecks == 0);
		
		deck = new Deck(numDecks);
		deck.shuffle();
		
		limit = limit * numDecks;
		int count = 0;
		
		do {
			// Receive bet amount from player
			int currentBet = getPlayerBet();
			
			// Assign player and dealer their starting cards
			ArrayList<Card> playerCards = new ArrayList<Card>();
			ArrayList<Card> dealerCards = new ArrayList<Card>();
			
			playerCards.add(deck.dealCard());
			dealerCards.add(deck.dealCard());
			playerCards.add(deck.dealCard());
			dealerCards.add(deck.dealCard());
			
			// Update count
			runningCount += playerCards.get(0).getCountValue();
			runningCount += playerCards.get(1).getCountValue();
			runningCount += dealerCards.get(0).getCountValue();
			
			// Check if dealer & player have blackjack before playing
			boolean dealerBJ = false;
			boolean playerBJ = false;
			
			int dealerTotal = getDealerTotal(dealerCards, false);
			if (dealerTotal == 21) {
				dealerBJ = true;
				getDealerTotal(dealerCards, true);
				getPlayerTotal(playerCards, true);
			}
			
			int playerTotal = getPlayerTotal(playerCards, false);
			if (playerTotal == 21) {
				playerBJ = true;
				getDealerTotal(dealerCards, true);
				getPlayerTotal(playerCards, true);
			}
			
			if (playerBJ && dealerBJ) {
				push(currentBet);
			} else if (playerBJ) {
				blackjack(currentBet);
			} else if (dealerBJ) {
				dealerBlackjack(currentBet);
			} else {
				// Player plays first
				int[] doesDealerPlay = playerPlay(dealerCards, playerCards, currentBet, true);
				
				currentBet = doesDealerPlay[1];
				
				if (doesDealerPlay[0] == 1) {
					dealerPlay(dealerCards, playerCards, currentBet, false);	
				}
			}
			
			count++;
			totalBet += currentBet;
			
		} while (deck.getCardsLeft() > limit);
		
		System.out.println("+---Summary---+");
		System.out.println("Total bet amount: £" + totalBet);
		System.out.println("Total winnings: £" + totalWin);
		System.out.println("Profit: £" + (totalWin - totalBet));
	}
	
	public static int[] playerPlay(ArrayList<Card> dealerCards, ArrayList<Card> playerCards, int currentBet, boolean allowSplit) {
		
		boolean doDouble = false;
		boolean doStand = false;
		
		do {
			System.out.println("\n+---------------------+");
			System.out.println("Dealer has: " + dealerCards.get(0).toString() + " (" + dealerCards.get(0).getValue() + ")");
			System.out.println();
			
			// Calculate player total
			int playerTotal = getPlayerTotal(playerCards, true);
			
			// Check for bust
			if (playerTotal > 21) {
				playerBust(currentBet);
				int[] returnValue = new int[2];
				returnValue[0] = 0;
				returnValue[1] = currentBet;
				return returnValue;
			}
			
			// Determine options for player
			if (!doDouble) {
				String message = "Enter (H) to Hit, (S) to Stand";
				boolean validChoice = false;
				String choice;
				do {
					boolean split = false;
					if (playerCards.get(0).getValue() == playerCards.get(1).getValue() && playerCards.size() == 2 && allowSplit) {
						System.out.print(message + ", (D) to Double or (P) to Split: ");
						split = true;
					} else if (playerCards.size() == 2) {
						System.out.print(message + " or (D) to Double: ");	
					} else {
						System.out.print(message + ": ");
					}
					
					choice = input.nextLine();
					
					validChoice = validateChoice(choice, true, split);
					
					if (!validChoice && !choice.equalsIgnoreCase("c")) {
						System.out.println("Enter a valid option.");
					}
				} while (!validChoice);
				
				// Carry out action
				if (choice.equalsIgnoreCase("h")) {
					Card newCard = deck.dealCard();
					playerCards.add(newCard);
					runningCount += newCard.getCountValue();
				} else if (choice.equalsIgnoreCase("d")) {
					Card newCard = deck.dealCard();
					playerCards.add(newCard);
					runningCount += newCard.getCountValue();
					currentBet *= 2; // Double the bet amount
					doDouble = true;
					System.out.println("Bet amount now: £" + currentBet);
				} else if (choice.equalsIgnoreCase("p")) {
					// Split cards
					doStand = true;
					
					// Split cards into two array lists
					ArrayList<Card> playerFirstHand = new ArrayList<Card>();
					ArrayList<Card> playerSecondHand = new ArrayList<Card>();
					playerFirstHand.add(playerCards.get(0));
					playerSecondHand.add(playerCards.get(1));
					
					playerSplit(dealerCards, playerFirstHand, playerSecondHand, currentBet);
					
					int[] returnValue = new int[2];
					returnValue[0] = 0;
					returnValue[1] = currentBet;
					return returnValue;
				} else {
					doStand = true;
				}
				
			} else {
				// Cannot hit again if doubled
				System.out.println("Press enter to continue.");
				input.nextLine();
				doStand = true;
			}
			
			
		} while (!doStand);
		
		// Returns two values:
		// 1. Whether dealer should play or not
		// 2. The new bet amount if doubled
		int[] returnValue = new int[2];
		returnValue[0] = 1;
		returnValue[1] = currentBet;
		return returnValue;
		
	}
		
	public static int dealerPlay(ArrayList<Card> dealerCards, ArrayList<Card> playerCards, int currentBet, boolean didSplit) {
		
		boolean dealerStand = false;
		int dealerTotal = 0;
		
		// Update running count
		runningCount += dealerCards.get(1).getCountValue();
		
		do {
			System.out.println("\n+---------------------+");
			dealerTotal = getDealerTotal(dealerCards, true);
			System.out.println();
			boolean shouldDisplayMessage = true;
			if (didSplit) {
				shouldDisplayMessage = false;
			}
			int playerTotal = getPlayerTotal(playerCards, shouldDisplayMessage);
			System.out.println();
			
			if (dealerTotal >= 17 && dealerTotal <= 21) {
				
				// If cards were split
				if (didSplit) {
					return dealerTotal;
				}
				
				dealerStand = true;
				if (!didSplit) {
					if (dealerTotal > playerTotal) {
						dealerWin(currentBet); // Dealer wins
					} else if (dealerTotal == playerTotal) {
						push(currentBet); // Draw - Dealer and Player have same value
					} else {
						playerWin(currentBet); // Player wins
					}	
				}
				
				return dealerTotal;
			} else if (dealerTotal > 21) {
				dealerStand = true; // Player wins
				if (!didSplit) {
					System.out.println("Dealer busts!");
					playerWin(currentBet);	
				}
				return dealerTotal;
			} else {
				Card newCard = deck.dealCard();
				dealerCards.add(newCard); // Dealer draws another card
				runningCount += newCard.getCountValue();
				System.out.println("Press enter to continue.");
				input.nextLine();
			}
			
		} while (!dealerStand);
		
		return dealerTotal;
		
	}
	
	public static int getPlayerBet() {
		int currentBet = 0;
		boolean validBet = false;
		do {
			try {
				System.out.print("Please enter a bet amount: £");
				currentBet = input.nextInt();
				if (currentBet < 1) {
					System.out.println("Enter a valid number.");
				} else {
					validBet = true;	
				}
			} catch (Exception e) {
				System.out.println("Enter a valid number.");
			}	
			input.nextLine();
		} while (!validBet);
		
		return currentBet;
	}
	
	public static void playerSplit(ArrayList<Card> dealerCards, ArrayList<Card> playerFirstHand, ArrayList<Card> playerSecondHand, int currentBet) {
		
		System.out.println("Splitting Cards");
		
		// Add another card to both ArrayLists
		playerFirstHand.add(deck.dealCard());
		playerSecondHand.add(deck.dealCard());
		
		// Update running count
		runningCount += playerFirstHand.get(1).getCountValue();
		runningCount += playerSecondHand.get(1).getCountValue();
		
		System.out.print("\nPlay first hand:");
		int[] firstHand = playerPlay(dealerCards, playerFirstHand, currentBet, false);
		System.out.println("\nPlay second hand:");
		int[] secondHand = playerPlay(dealerCards, playerSecondHand, currentBet, false);
		
		currentBet = firstHand[1] + secondHand[1];
		
		if (firstHand[0] == 1 || secondHand[0] == 1) {
			int dealerTotal = dealerPlay(dealerCards, playerFirstHand, currentBet, true);
			
			// Ensure dealer did not bust
			if (dealerTotal > 21) {
				if (firstHand[0] == 1 && secondHand[0] == 1) {
					playerWin(currentBet);
				} else if (firstHand[0] == 1) {
					playerWin(firstHand[1]);
				} else {
					playerWin(secondHand[1]);
				}
				return;
			}
			
			// Check if dealerTotal is greater than player's hands
			int firstHandTotal = getPlayerTotal(playerFirstHand, false);
			int secondHandTotal = getPlayerTotal(playerSecondHand, false);
			
			String firstHandStatus = "";
			String secondHandStatus = "";
			
			if (dealerTotal > firstHandTotal || firstHandTotal > 21) {
				System.out.println("First Hand: You lose");
				firstHandStatus = "Lose";
			} else if (dealerTotal < firstHandTotal) {
				System.out.println("First Hand: You win!");
				firstHandStatus = "Win";
			} else {
				System.out.println("First Hand: Push");
				firstHandStatus = "Push";
			}
			
			if (dealerTotal > secondHandTotal || secondHandTotal > 21) {
				System.out.println("Second Hand: You lose");
				secondHandStatus = "Lose";
			} else if (dealerTotal < secondHandTotal) {
				System.out.println("Second Hand: You win!");
				secondHandStatus = "Win";
			} else {
				System.out.println("Second Hand: Push");
				secondHandStatus = "Push";
			}
			
			// Calculate winnings/losses
			if (firstHandStatus == "Win" && secondHandStatus == "Win") {
				playerWin(currentBet);
				return;
			} else if (firstHandStatus == "Win") {
				playerWin(firstHand[1]);
			} else if (secondHandStatus == "Win") {
				playerWin(secondHand[1]);
			}
			
			if (firstHandStatus == "Push" || secondHandStatus == "Push") {
				if (firstHandStatus == "Push" && secondHandStatus == "Push") {
					push(currentBet);
					return;
				} else if (firstHandStatus == "Push") {
					push(firstHand[1]);
				} else if (secondHandStatus == "Push") {
					push(secondHand[1]);
				}	
				return;
			}
			
			// If no wins or pushes, both hands lose
			System.out.println("Both hands lose.");
			dealerWin(currentBet);
			
		} else {
			dealerWin(currentBet);
		}
		
	}
	
	public static void playerWin(int currentBet) {
		
		System.out.println("You win £" + (currentBet * 2) + "!");
		System.out.println("Press enter to continue.");
		
		totalWin += currentBet * 2;
		
		input.nextLine();
	}
	
	public static void dealerWin(int currentBet) {
		
		System.out.println("Dealer wins.");
		System.out.println("Press enter to continue.");
		
		//totalWin -= currentBet;
		
		input.nextLine();
	}
	
	public static void playerBust(int currentBet) {
		
		System.out.println("Bust!");
		System.out.println("Dealer wins.");
		System.out.println("Press enter to continue.");
		
		//totalWin -= currentBet;
		
		input.nextLine();
	}
	
	public static void push(int currentBet) {
		
		System.out.println("Push!");
		System.out.println("You win £" + currentBet + "!");
		System.out.println("Press enter to continue.");
		
		totalWin += currentBet;
		
		input.nextLine();
	}
	
	public static void blackjack(int currentBet) {
		
		System.out.println("Blackjack!");
		System.out.println("You win £" + (currentBet * 2.5) + "!");
		System.out.println("Press enter to continue.");
		
		totalWin += currentBet * 2.5;
		
		input.nextLine();
	}
	
	public static void dealerBlackjack(int currentBet) {
		
		System.out.println("Dealer has blackjack.");
		dealerWin(currentBet);
	}
	
	public static int getPlayerTotal(ArrayList<Card> playerCards, boolean displayMessage) {
		int playerTotal = 0;
		String cardMessage = "You have: ";
		int aceCount = 0;
		for (int i=0; i < playerCards.size(); i++) {
			// Calculate player's total
			if (playerCards.get(i).isAce()) {
				aceCount++;
			} else {
				playerTotal += playerCards.get(i).getValue();	
			}
			cardMessage += playerCards.get(i).toString();
			if (i != playerCards.size() - 1) {
				cardMessage += ", ";
			}
		}
		
		// Add aces to total
		for (int i=0; i < aceCount; i++) {
			if (playerTotal + (11 + (aceCount - 1)) > 21) {
				playerTotal += 1;
			} else {
				playerTotal += 11;
			}
		}
		
		if (displayMessage) {
			System.out.println(cardMessage + " (" + playerTotal + ")");				
		}
		
		return playerTotal;
	}
	
	public static int getDealerTotal(ArrayList<Card> dealerCards, boolean displayMessage) {
		int dealerTotal = 0;
		String cardMessage = "Dealer has: ";
		int aceCount = 0;
		for (int i=0; i < dealerCards.size(); i++) {
			// Calculate player's total
			if (dealerCards.get(i).isAce()) {
				aceCount++;
			} else {
				dealerTotal += dealerCards.get(i).getValue();	
			}
			cardMessage += dealerCards.get(i).toString();
			if (i != dealerCards.size() - 1) {
				cardMessage += ", ";
			}
		}
		
		// Add aces to total
		for (int i=0; i < aceCount; i++) {
			if (dealerTotal + (11 + (aceCount - 1)) > 21) {
				dealerTotal += 1;
			} else {
				dealerTotal += 11;
			}
		}
		
		if (displayMessage) {
			System.out.println(cardMessage + " (" + dealerTotal + ")");				
		}
		
		return dealerTotal;
	}
	
	public static boolean validateChoice(String choice, boolean _double, boolean split) {
		
		if (choice.equalsIgnoreCase("h")) {
			return true;
		} else if (choice.equalsIgnoreCase("s")) {
			return true;
		} else if (choice.equalsIgnoreCase("d") && _double) {
			return true;
		} else if (choice.equalsIgnoreCase("p") && split) {
			return true;
		} else if (choice.equalsIgnoreCase("c")) {
			System.out.println("\n+--- Count Reveal ---+");
			System.out.println("Running count: " + runningCount);
			System.out.println("True count: " + runningCount / (deck.getCardsLeft() / 52));
			System.out.println("Number of decks: " + deck.getNumDecks());
			System.out.println("Number of cards left in deck: " + deck.getCardsLeft());
			System.out.println("+--------------------+\n");
			return false;
		} else {
			return false;
		}
	}

}
