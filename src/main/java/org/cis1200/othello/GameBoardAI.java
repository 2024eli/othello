package org.cis1200.othello;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.Array;
import java.util.*;
import java.util.stream.IntStream;

public class GameBoardAI extends GameBoard {

    private final Integer HOLES = 12;

    private JLabel status;
    private JTextArea moveBook;
    private JLabel counter;

    private boolean humanIsBlack;

    private LinkedList<Coordinate> corner_points;
    private int[] xSq;
    private int[] cSq;
    private HashMap<Coordinate, Coordinate> square;
    private HashMap<String, Coordinate> openingbook;

    // Game constants
    public static final int BOARD_WIDTH = 400;
    public static final int BOARD_HEIGHT = 400;

    public GameBoardAI(JLabel statusInit, JLabel counterInit, boolean humanIsBlack) {
        super(statusInit, counterInit);
        converter();
        // creates border around the court area, JComponent method
        setBorder(BorderFactory.createLineBorder(Color.BLACK));

        // Enable keyboard focus on the court area. When this component has the
        // keyboard focus, key events are handled by its key listener.
        setFocusable(true);

        status = statusInit; // initializes the status JLabel
        counter = counterInit;
        this.humanIsBlack = humanIsBlack;

        // Remove the existing mouse listener
        for (MouseListener listener : getMouseListeners()) {
            removeMouseListener(listener);
        }

        /*
         * Listens for mouseclicks. Updates the model, then updates the game
         * board based off of the updated model.
         */
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                Point p = e.getPoint();
                if (othello.getTurn() == !humanIsBlack) {
                    // updates the model given the coordinates of the mouseclick
                    boolean turnValid = othello.playTurn(p.y / 50, p.x / 50);
                    othello.printGameState();

                    updateStatus(); // updates the status JLabel
                    updateMoves();
                    repaint(); // repaints the game board

                    // AI makes a move after human
                    if (!othello.isGameOver() && turnValid) {
                        makeAIMove();
                        updateStatus();
                        updateMoves();
                        repaint();

                        requestFocusInWindow();
                    }
                }
            }
        });
    }

    private void makeAIMove() {
        // AI logic to make a move
        System.out.println("AI moving");
        Coordinate bestMove = getBestMove();
        if (bestMove != null) {
            othello.playTurn(bestMove.getRow(), bestMove.getCol());
            othello.printGameState();
        }
        System.out.println("AI done moving");
    }

    private void converter() {
        corner_points = intToCoordinateList(new int[] { 0, 7, 56, 63 });
        xSq = new int[] { 9, 14, 49, 54 };
        cSq = new int[] { 1, 8, 6, 15, 62, 48, 55, 57 };
        openingbook = new HashMap<>();

        openingbook.put(
                "...........................OX......XO...........................",
                new Coordinate(26)
        );
        openingbook.put(
                "..........................XXX......XO...........................",
                new Coordinate(20)
        );
        openingbook.put(
                "....................O.....XXO......XO...........................",
                new Coordinate(37)
        );
        openingbook.put(
                "..................O.......XOX......XO...........................",
                new Coordinate(19)
        );
        openingbook.put(
                "....................O.....XXO......XXX..........................",
                new Coordinate(42)
        );
        openingbook.put(
                "..................OX......XXX......XO...........................",
                new Coordinate(34)
        );
        openingbook.put(
                "....................O.....XXO......OXX....O.....................",
                new Coordinate(34)
        );
        openingbook.put(
                "..................OX......OXX.....OOO...........................",
                new Coordinate(33)
        );
        openingbook.put(
                "..................OX.....XXXX.....OOO...........................",
                new Coordinate(21)
        );

        square = new HashMap<>();
        square.put(new Coordinate(9), new Coordinate(0));
        square.put(new Coordinate(14), new Coordinate(7));
        square.put(new Coordinate(49), new Coordinate(56));
        square.put(new Coordinate(54), new Coordinate(63));
        square.put(new Coordinate(1), new Coordinate(0));
        square.put(new Coordinate(8), new Coordinate(0));
        square.put(new Coordinate(6), new Coordinate(7));
        square.put(new Coordinate(15), new Coordinate(7));
        square.put(new Coordinate(62), new Coordinate(63));
        square.put(new Coordinate(48), new Coordinate(56));
        square.put(new Coordinate(55), new Coordinate(56));
        square.put(new Coordinate(57), new Coordinate(63));
    }

    private LinkedList<Coordinate> intToCoordinateList(int[] intList) {
        LinkedList<Coordinate> coordinateList = new LinkedList<>();
        for (int j : intList) {
            coordinateList.add(new Coordinate(j));
        }
        return coordinateList;
    }

    /**
     * primary AI function
     * @return Coordinate of the best move
     */
    public Coordinate getBestMove() {
        Coordinate bestMove = null;
        boolean opp = !othello.getTurn();
        boolean token = othello.getTurn();

        String[][] board = othello.get2DBoard();
        String boardString = othello.getBoard(board);

        // INTRO: opening book
        if (openingbook.containsKey(boardString)) {
            return openingbook.get(boardString);
        }
        if (count(boardString, '.') < HOLES) {
            // ENDGAME: alpha beta pruning
            ArrayList<Integer> ab = alphabeta(board, token, -65, 65, true);
            return new Coordinate(ab.get(ab.size() - 1));
        }
        else {
            // MIDGAME: midgame heuristic
            ArrayList<Integer> mg = midgame(board, token,-99999, 99999, 4);
            return new Coordinate(mg.get(mg.size()-1));
        }
    }

    private ArrayList<Integer> midgame(String[][] board, boolean token, int beta, int alpha, int depth) {
        boolean opp = !token;
        LinkedList<Coordinate> listOfMoves = othello.getListofMoves();
        othello.setTurn(opp);
        int oppMoveLen = othello.getListofMoves().size();
        othello.setTurn(token);

        if (depth == 0) {
            ArrayList<Integer> result = new ArrayList<Integer>();
            result.add(scoreCalc(board, token, listOfMoves));
            System.out.println(result.get(0));
            return result;
        }
        if (listOfMoves.isEmpty()) {
            if (oppMoveLen == 0) {
                ArrayList<Integer> result = new ArrayList<Integer>();
                result.add(scoreCalc(board, token, listOfMoves));
                return result;
            }
            ArrayList<Integer> mg = midgame(board, opp, -alpha, -beta, depth-1);
            ArrayList<Integer> result = new ArrayList<Integer>();
            result.add(-mg.get(0));
            result.addAll(mg.subList(1, mg.size()));
            result.add(-1);
            return result;
        }

        ArrayList<Integer> best = new ArrayList<Integer>();
        best.add(beta-1);
        for (Coordinate c : listOfMoves) {
            ArrayList<Integer> mg = midgame(othello.possibleBoard(c.getRow(), c.getCol(), othello.getToken()), opp, -alpha, -beta, depth-1);
            int score = -mg.get(0);
            if (score < beta) {
                continue;
            }
            if (score > alpha) {
                ArrayList<Integer> result = new ArrayList<Integer>();
                result.add(score);
                result.add(c.getRow()*8+c.getCol());
                return result;
            }
            best = new ArrayList<>();
            best.add(score);
            best.addAll(mg.subList(1, mg.size()));
            best.add(c.getRow()*8+c.getCol());
            beta = score+1;
        }
        return best;
    }

    private int scoreCalc(String[][] board, boolean token, LinkedList<Coordinate> listOfMoves) {
        boolean opp = !token;
        int score = 0;

        othello.setTurn(opp);
        int mobility = othello.getListofMoves().size();
        othello.setTurn(token);

        char tokenString = token ? 'X' : 'O';
        char oppString = opp ? 'X' : 'O';

        String boardString = othello.getBoard(board);

        int cornerCount = cornerScore(boardString, tokenString) - cornerScore(boardString, oppString);
        score = score + 10*cornerCount - 50*mobility; // corners are prized!
        return score;
    }

    private int cornerScore(String boardString, char token) {
        ArrayList<Integer> corner = new ArrayList<Integer>();
        int score = 0;
        for (Coordinate c : corner_points) {
            int index = c.getRow()*8+c.getCol();
            if (boardString.charAt(index) == token) {
                score += 100;
                corner.add(index);
            }
        }
        for (Map.Entry<Coordinate, Coordinate> entry : square.entrySet()) {
            int key = entry.getKey().getRow()*8+entry.getKey().getCol();
            int value = entry.getValue().getRow()*8+entry.getValue().getCol();
            if (boardString.charAt(key) == token) {
                if (corner.contains(value)) {
                    score += 150;
                }
                else {
                    if (IntStream.of(cSq).anyMatch(x -> x == key)) {
                        score -= 15;
                    }
                    else if (IntStream.of(xSq).anyMatch(x -> x == key)) {
                        score -= 90;
                    }
                }
            }
        }
        return score;
    }

    private ArrayList<Integer> alphabeta(String[][] board, boolean token, int beta, int alpha, boolean topLvl) {
        boolean opp = !token;
        LinkedList<Coordinate> listOfMoves = othello.getListofMoves();
        String boardString = othello.getBoard(board);

        char tokenString = token ? 'X' : 'O';
        char oppString = opp ? 'X' : 'O';

        othello.setTurn(opp);
        int oppMoveLen = othello.getListofMoves().size();
        othello.setTurn(token);

        if (listOfMoves.isEmpty()) {
            if (oppMoveLen == 0) {
                ArrayList<Integer> result = new ArrayList<Integer>();
                result.add(count(boardString, tokenString) - count(boardString, oppString));
                return result;
            }
            ArrayList<Integer> ab = alphabeta(board, opp, -alpha, -beta, false);
            ArrayList<Integer> result = new ArrayList<Integer>();
            result.add(-ab.get(0));
            result.addAll(ab.subList(1, ab.size()));
            result.add(-1);
            return result;
        }
        ArrayList<Integer> best = new ArrayList<Integer>();
        best.add(beta-1);
        for (Coordinate c : listOfMoves) {
            ArrayList<Integer> ab = alphabeta(othello.possibleBoard(c.getRow(), c.getCol(), othello.getToken()), opp, -alpha, -beta, false);
            int score = -ab.get(0);
            if (score < beta) {
                continue;
            }
            if (score > alpha) {
                ArrayList<Integer> result = new ArrayList<Integer>();
                result.add(score);
                result.add(c.getRow()*8+c.getCol());
                return result;
            }
            best = new ArrayList<>();
            best.add(score);
            best.addAll(ab.subList(1, ab.size()));
            best.add(c.getRow()*8+c.getCol());
            beta = score+1;
        }
        return best;
    }

    public int count(String str, char c) {
        int count = 0;
        for (int i = 0; i < str.length(); i++) {
            if (str.charAt(i) == c) {
                count++;
            }
        }
        return count;
    }

    @Override
    public void reset() {
        super.reset();
        // If AI plays first (human is white), make its move
        if (!humanIsBlack) {
            makeAIMove();
        }
    }

    @Override
    public void undo() {
        othello.undo();
        if (othello.getTurn() == humanIsBlack) {
            makeAIMove();
        }
        updateStatus();
        updateMoves();
        repaint();
        requestFocusInWindow();
    }
}
