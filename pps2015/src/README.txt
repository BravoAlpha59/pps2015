This code is the result of research into a competitive AI player of a "parameterized" version of the single-player game Poker Squares.
While there are conventional scoring systems for Poker Squares, parameterized means that the player must be prepared to play the game
with widely different, and sometimes entirely random, scoring systems. 

First, to clarify the naming convention: the classes with "x" at the beginning of their name are classes created by us for our player. 
The "x" is to keep them separated from the other classes, which make up the tournament code provided to us and that we were not allowed
to alter. MCTS refers to Monte Carlo Tree Search, which makes up the core of our player's decision making strategy. PPS refers to
Parameterized Poker Squares, and indicate that that class is using domain knowledge specific to Poker Squares to make decisions. Pruning refers to 
reducing the size of the game tree created by MCTS, as this game has too large of a game space to search in any feasible amount of time,
much less the 30 seconds per game provided by the tournament code. 

As previously stated, the core of this player is MCTS. This core can be found in the xMCTSPPSPruningPlayer, particularly in the runTrial method,
which represents one full iteration of the MCTS process (further description of each step can be found in that method as well). GetPlay in the same
class is our player's main method of interacting with the tournament code, receiving a "drawn" card and time remaining in the game and returning a
location to place that card in the game grid.

The xMCTSPPSPruningPlayer is abstract, to allow for different specific iterations of the player, namely for the testing of different simulation strategies.
In our research we found that a "Best Move" strategy served us best, in which we utilize our pruning calculation to estimate a best potential move and selecting
that move. This improvement over standard MCTS (in which simulations are done purely randomly) showed the most significant improvement in player performance of
any of our changes.

The game tree created by MCTS is made up of ChanceNode and ChoiceNode objects, with chance nodes representing card draws and choice nodes representing
player decisions (so in a visual representation of the tree, levels of the tree alternate chance and choice). The selection equation, UCT, is found here.
Calls to expand happen in these nodes as well, but the specifics of the expand are left to the PPSGame class.

PPSGame holds the most domain-specific information, including analyzing the Poker Squares game grid for empty positions and checking for cards needed to 
complete certain poker hands. Additionally, the getPossibleMoves and getBestSimMove methods both utilize our pruning algorithm to determine which children
of a given node are worthwhile to add to the game tree. In this calculation, game states are givenan estimated value based on what hands can still be completed,
and how much those hands are worth. More information on this calculation can be found in those methods and the methods they call.