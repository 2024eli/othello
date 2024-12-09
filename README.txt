=:=:=:=:=:=:=:=:=:=:=:=:=:=:=:=:=:=:=
CIS 1200 Game Project README
PennKey: eli22ß
=:=:=:=:=:=:=:=:=:=:=:=:=:=:=:=:=:=:=

===================
=: Core Concepts :=
===================

- List the four core concepts, the features they implement, and why each feature
  is an appropriate use of the concept. Incorporate the feedback you got after
  submitting your proposal.

  1. 2D Arrays - An Othello board is typically an 8x8 grid. Through a 2D array,
  I can represent this board and store the state of each square on the grid. "O"
  represents black, "X" represents white, and "." represents an empty square.
  When the game starts, all squares will hold value ".", symbolizing a blank board.

  2. Collections - I will store each of the moves during a game in a collection so
  that a user can undo a move. Specifically, I will store them in a Stack since order
  is important and I'll only need to push/pop from the top of the stack. When a user
  needs to undo, it will pop the last move off of the list and turn that square blank
  ("."). The Stack will be of type Stack<Coordinate> where Coordinate is an
  (x, y) pair.

  3. File I/O - My Othello implementation will use I/O to store game state so users
  can pause games and return to them. The state of the board (2D array) will be stored
  in a .txt file when a button is pressed. To load in a stored game state, the game
  will parse the .txt file and turn it back into the playable game board (2D array
  representation).

  4. JUnit testable component - I will create methods that take in coordinates and
  token type ("X" or "O") as a parameter, and update the game accordingly. I will
  design this functionality so it's testable via JUnit. I will test that the 2D array
  is updated correctly and that the list of moves is updated. I will test edge cases,
  such as when a user attempts to fill a non-empty square.

===============================
=: File Structure Screenshot :=
===============================
- Include a screenshot of your project's file structure. This should include
  all of the files in your project, and the folders they are in. You can
  upload this screenshot in your homework submission to gradescope, named 
  "file_structure.png".

=========================
=: Your Implementation :=
=========================

- Provide an overview of each of the classes in your code, and what their
  function is in the overall game.

I have a Coordinate, GameBoard, Othello, RunOthello, and Game class. In the Coordinate class, we represent positions on
an Othello game board. It stores the x and y coordinates for a grid. In the GameBoard class, we manage the Othello
board. It maintains the graphical representation and user interaction. It handles mouseclicks, paints the game board,
updates game status and move history, and provides some methods for game control. The Othello class implements core
game logic. It uses a 2D array representation of the board and implements turn taking and piece flipping. The RunOthello
class is the entry point for the game and UI setup. It displays the intro frame as well as the main game frame. It also
manages game mode selections.

- Were there any significant stumbling blocks while you were implementing your
  game (related to your design, or otherwise)?

  I had a few issues implementing the game logic to flip tokens. I also had a few problems covering the edge cases like
  different cases of ending the game, passing to the other player, etc. I also had some pretty big problems reaching my
  stretch goal of implementing AI. My alpha beta pruning doesn't work entirely, but most of the code works. However,
  edge cases like undo, reset, save, and load game aren't fully working for the AI game mode.


- Evaluate your design. Is there a good separation of functionality? How well is
  private state encapsulated? What would you refactor, if given the chance?

  The current design shows a good separation of functionality. The Othello class holds the game logic and state and can
  be tested without any involvement with the GUI. The GameBoard class handles the view and controller aspects. Private
  states are well encapsulated as the Othello class keeps the board state private, only providing getters and setters
  for interaction. I could potentially consider refactoring so that more methods in the Othello class could be used to
  modify the internal state of an Othello model, but also if I were to use it to selectly test parts of the class
  without changing the entire game state.


========================
=: External Resources :=
========================

- Cite any external resources (images, tutorials, etc.) that you may have used 
  while implementing your game.

  Official othello game rules:
  https://www.worldothello.org/about/about-othello/othello-rules/official-rules/english
