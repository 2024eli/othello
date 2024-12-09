package org.cis1200.othello;

import org.cis1200.othello.Othello;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;

/**
 * As the user clicks the game board, the model is updated. Whenever the model
 * is updated, the game board repaints itself and updates its status JLabel to
 * reflect the current state of the model.
 *
 * This game adheres to a Model-View-Controller design framework. This
 * framework is very effective for turn-based games. We STRONGLY
 * recommend you review these lecture slides, starting at slide 8,
 * for more details on Model-View-Controller:
 * https://www.seas.upenn.edu/~cis120/current/files/slides/lec37.pdf
 *
 * In a Model-View-Controller framework, GameBoard stores the model as a field
 * and acts as both the controller (with a MouseListener) and the view (with
 * its paintComponent method and the status JLabel).
 */
@SuppressWarnings("serial")
public class GameBoard extends JPanel {

    public Othello othello; // model
    private JLabel status;
    private JTextArea moveBook;
    private JLabel counter;

    // Game constants
    public static final int BOARD_WIDTH = 400;
    public static final int BOARD_HEIGHT = 400;

    public GameBoard(JLabel statusInit, JLabel counterInit) {
        // creates border around the court area, JComponent method
        setBorder(BorderFactory.createLineBorder(Color.BLACK));

        // Enable keyboard focus on the court area. When this component has the
        // keyboard focus, key events are handled by its key listener.
        setFocusable(true);

        othello = new Othello(); // initializes model for the game
        status = statusInit; // initializes the status JLabel
        counter = counterInit;

        /*
         * Listens for mouseclicks. Updates the model, then updates the game
         * board based off of the updated model.
         */
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                Point p = e.getPoint();

                // updates the model given the coordinates of the mouseclick
                othello.playTurn(p.y / 50, p.x / 50);
                othello.printGameState();

                updateStatus(); // updates the status JLabel
                updateMoves();
                repaint(); // repaints the game board
            }
        });
    }

    /**
     * (Re-)sets the game to its initial state.
     */
    public void reset() {
        othello.reset();
        updateStatus();
        updateMoves();
        repaint();

        // Makes sure this component has keyboard/mouse focus
        requestFocusInWindow();
    }

    public void undo() {
        othello.undo();
        updateStatus();
        updateMoves();
        repaint();
        requestFocusInWindow();
    }

    public void saveGame(String filename) {
        othello.saveGame(filename);
    }

    public void loadGame(String filename) {
        othello.loadGame(filename);
        updateStatus();
        updateMoves();
        repaint();
        requestFocusInWindow();
    }

    /**
     * Updates the JLabel to reflect the current state of the game.
     */
    public void updateStatus() {
        String color = othello.getTurn() ? "White" : "Black";
        status.setText(color + "'s Turn");
        counter.setText(
                "Black: " + othello.getCount("O") + ", White: " + othello.getCount("X")
        );

        int winner = othello.checkWinner();
        if (winner == 1) {
            status.setText("Black wins!!!");
        } else if (winner == 2) {
            status.setText("White wins!!!");
        } else if (winner == 3) {
            status.setText("Stalemate.");
        }
    }

    public void setMoveBook(JTextArea moveBook) {
        this.moveBook = moveBook;
    }

    public void updateMoves() {
        Stack<Coordinate> moves = othello.getMoves();
        StringBuilder stringOfMoves = new StringBuilder();
        for (Coordinate c : moves) {
            stringOfMoves.append(c.toString()).append(" ");
        }
        moveBook.setText(stringOfMoves.toString());
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        int unitWidth = BOARD_WIDTH / 8;
        int unitHeight = BOARD_HEIGHT / 8;

        for (int i = 0; i <= 8; i++) {
            // Draw vertical lines
            g.drawLine(unitWidth * i, 0, unitWidth * i, BOARD_HEIGHT);
            // Draw horizontal lines
            g.drawLine(0, unitHeight * i, BOARD_WIDTH, unitHeight * i);
        }

        // draw white and blacks
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                String state = othello.getCell(i, j);
                if (state.equals("O")) {
                    g.setColor(Color.BLACK);
                    g.fillOval(5 + 50 * j, 5 + 50 * i, 40, 40);
                } else if (state.equals("X")) {
                    g.setColor(Color.WHITE);
                    g.fillOval(5 + 50 * j, 5 + 50 * i, 40, 40);
                }
            }
        }
    }

    /**
     * Returns the size of the game board.
     */
    @Override
    public Dimension getPreferredSize() {
        return new Dimension(BOARD_WIDTH, BOARD_HEIGHT);
    }

}
