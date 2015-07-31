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
import java.util.ArrayList;

/**
 * A class that implements this interface will encapsulate the rules of a given
 * game, including the calculation of available legal moves, and evaluating
 * gamestates for end game conditions. Not a necessary inclusion for the Poker Squares
 * contest, but kept anyway in case this code is ever revisited for a different game.
 *
 * @author Robert Arrington
 * @author Steven Bogaerts
 * @author Clay Langley
 */
public interface xMCTSGame
{

   enum status
   {
      GAMEEND,
      ONGOING;
   }

   
   
   /**
    * Generates all possible moves from the given state.
    *
    * @param state The state to be generated from.
    * @return The list of possible moves.
    */
   public ArrayList<xMCTSStringGameState> getPossibleMoves(xMCTSStringGameState state, boolean[] canDraw);

   /**
    * Gets the status of the game state.
    *
    * @param state The state to check the status of.
    * @return The status of the game.
    */
   public status gameStatus(xMCTSStringGameState state);

   /**
    * Gets the starting state of the game
    *
    * @return Starting game state.
    */
   public xMCTSStringGameState getStartingState();

}
