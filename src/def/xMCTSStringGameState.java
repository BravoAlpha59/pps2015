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
 * String game states can be used if you want to encode all gamestate
 * information into a single string. For example, a tic-tac-toe state
 * might be X_X_O____O where the first 9 chars indicate values for
 * spaces and the 10th indicates whose turn it is.
 * 
 * For Poker Squares, the string is 52 chars long. The first 50 represent
 * the 5x5 game board, with 2 chars per position. When a position is filled
 * with a card, those two chars can go from blank to a representation of card
 * (e.g. a King of hearts becomes KH, a two of clubs becomes 2C, etc). The 
 * final two chars are for the "active" card, or the card that the player
 * has to make a play with.
 * 
 * @author Robert Arrington
 * @author Steven Bogaerts
 * @author Clay Langley
 */
public class xMCTSStringGameState
{

   private String state;//String representation of the game state
   public double expectedValue;//The estimated value of this game state based on the pruning calculation
   public int numPlays;//Number of cards played on this board
   

   /**
    * Creates a new StringGameState
    *
    * @param state is the String to be wrapped in this gamestate.
    */
   public xMCTSStringGameState(String state, double expectedValue, int numPlays)
   {
      this.state = state;
      this.expectedValue = expectedValue;
      this.numPlays = numPlays;
   }
   
   @Override
   /**
    * Override the equals method to compare two strings
    * 
    * @param g the object being compared to this game state
    * @return true if the object is a StringGameState and matches this state's string, false if otherwise
    */
   public boolean equals(Object g)
   {
      if (g instanceof xMCTSStringGameState) {
         return state.equals(((xMCTSStringGameState) g).state);
      }
      return false;
   }
   
   /**
    * Returns the state as a string.
    *
    * @return The string that represents this GameState
    */
   @Override
   public String toString()
   {
      return state;
   }
}
