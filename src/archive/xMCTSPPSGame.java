

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

import def.xMCTSGame;
import def.xMCTSGameState;
import def.xMCTSStringGameState;
import def.xMCTSGame.status;

public class xMCTSPPSGame implements xMCTSGame
{

   final int SIZE = 5;
  // protected int callsFromFullBoards = 0;
   
   @Override
   public ArrayList<xMCTSStringGameState> getPossibleMoves(xMCTSStringGameState gameState)
   {
	      ArrayList<xMCTSStringGameState> posMoves = new ArrayList<xMCTSStringGameState>();
	      if (gameStatus(gameState) == xMCTSGame.status.ONGOING) {
	    	 String activeCard = gameState.toString().substring(gameState.toString().length() - 2, gameState.toString().length());
	    	 int gridSize = SIZE*2;
	         for (int i = 0; i < gridSize; i += 2) {
	            for (int j = 0; j < gridSize; j += 2) {
	               int pos = i * SIZE + j;
	               if (gameState.toString().charAt(pos) == '_') {
	                  String temp = gameState.toString().substring(0, pos)
	                		  + activeCard
	                          + gameState.toString().substring(pos + 2,
	                          gameState.toString().length());
	                  posMoves.add(new xMCTSStringGameState(temp, 0.0, 0));
	               }
	            }

	         }
	      }
	      else {
	    	 // System.out.println("No moves can be made from this state");
	    	//  callsFromFullBoards++;
	    	//  System.out.println("There have been " + callsFromFullBoards + "attempts to get possible moves from a full board");
	      }
	      return posMoves;
   }
   
   public ArrayList<xMCTSStringGameState> getPossibleMoves(xMCTSStringGameState state, boolean[] canDraw) {
	   return getPossibleMoves(state);
   }

   @Override
   public status gameStatus(xMCTSGameState state)
   {
      String s = state.toString().substring(0, state.toString().length() - 2);
      if (!s.contains("_")) {
         return xMCTSGame.status.PLAYER1WIN;
      } else {
         return xMCTSGame.status.ONGOING;
      }
   }

   @Override
   public xMCTSStringGameState getStartingState()
   {
      return new xMCTSStringGameState("____________________________________________________", 0.0, 0);
   }

   @Override
   public void printState(xMCTSGameState state)
   {
      System.out.println(state.toString().substring(0, 10));
      System.out.println(state.toString().substring(10, 19));
      System.out.println(state.toString().substring(20, 29));
      System.out.println(state.toString().substring(30, 39));
      System.out.println(state.toString().substring(40, 49));
   }
}
