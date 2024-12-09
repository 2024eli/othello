package org.cis1200.othello;

import org.cis1200.othello.GameBoard;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.time.LocalDateTime;

public class RunOthello implements Runnable {

    private JFrame gameFrame;
    private GameBoard board;
    private JLabel status;
    private JLabel counter;

    public void run() {
        SwingUtilities.invokeLater(this::createAndShowIntroFrame);
    }

    private void createAndShowIntroFrame() {
        JFrame introFrame = new JFrame("Othello - Welcome");
        introFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        introFrame.setSize(400, 300);
        introFrame.setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JTextArea instruct = new JTextArea(
                "Welcome to Othello!\n\n" +
                        "Instructions:\n" +
                        "1. Black always moves first.\n" +
                        "2. Place a piece to flip your opponent's pieces.\n" +
                        "3. If you can't make a valid move, your turn is skipped.\n" +
                        "4. The game ends when the board is full or no valid moves remain.\n" +
                        "5. The player with the most pieces wins!"
        );
        instruct.setEditable(false);
        instruct.setWrapStyleWord(true);
        instruct.setLineWrap(true);
        instruct.setOpaque(false);
        instruct.setFocusable(false);
        panel.add(instruct);

        JButton oneVsOneButton = new JButton("1 vs 1");
        oneVsOneButton.addActionListener(e -> {
            introFrame.dispose();
            createAndShowGameFrame(false, false);
        });

        JButton oneVsAIButton = new JButton("1 vs AI");
        oneVsAIButton.addActionListener(e -> {
            int choice = JOptionPane.showOptionDialog(
                    introFrame,
                    "Choose your color:",
                    "Color Selection",
                    JOptionPane.DEFAULT_OPTION,
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    new String[] { "Black", "White" },
                    "Black"
            );
            boolean humanIsBlack = (choice == 0);
            introFrame.dispose();
            createAndShowGameFrame(true, humanIsBlack);
        });

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(oneVsOneButton);
        buttonPanel.add(oneVsAIButton);
        panel.add(buttonPanel);

        introFrame.add(panel);
        introFrame.setVisible(true);
    }

    private void createAndShowGameFrame(boolean isAI, boolean humanIsBlack) {
        gameFrame = new JFrame("Othello");
        gameFrame.setLocation(300, 300);

        JPanel status_panel = new JPanel();
        status_panel.setLayout(new BoxLayout(status_panel, BoxLayout.Y_AXIS));
        gameFrame.add(status_panel, BorderLayout.SOUTH);
        status = new JLabel("Setting up...");
        counter = new JLabel("Black: 2, White: 2");
        status_panel.add(status);
        status_panel.add(counter);

        board = isAI ? new GameBoardAI(status, counter, humanIsBlack)
                : new GameBoard(status, counter);
        gameFrame.add(board, BorderLayout.CENTER);

        // Reset and undo button
        final JPanel control_panel = new JPanel();
        gameFrame.add(control_panel, BorderLayout.NORTH);

        final JPanel moves_panel = new JPanel();
        gameFrame.add(moves_panel, BorderLayout.EAST);

        // Note here that when we add an action listener to the reset button, we
        // define it as an anonymous inner class that is an instance of
        // ActionListener with its actionPerformed() method overridden. When the
        // button is pressed, actionPerformed() will be called.
        final JButton reset = new JButton("Reset");
        reset.addActionListener(e -> board.reset());
        control_panel.add(reset);

        final JButton undo = new JButton("Undo");
        undo.addActionListener(e -> {
            board.undo();
        });
        control_panel.add(undo);

        final JButton saveButton = new JButton("Save");
        saveButton.addActionListener(e -> {
            String filename = "files/othello_save" + LocalDateTime.now().toString() + ".txt";
            board.saveGame(filename);
        });
        control_panel.add(saveButton);

        final JButton loadButton = new JButton("Load Game");
        loadButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser("/Users/evelynli/Downloads/othello/files");
            int result = fileChooser.showOpenDialog(null);
            if (result == JFileChooser.APPROVE_OPTION) {
                String filename = fileChooser.getSelectedFile().getAbsolutePath();
                board.loadGame(filename);
            }
        });
        control_panel.add(loadButton);

        final JTextArea moveBook = new JTextArea();
        moveBook.setEditable(false);
        moveBook.setLineWrap(true);
        moves_panel.add(new JScrollPane(moveBook));
        board.setMoveBook(moveBook);

        // Put the frame on the screen
        gameFrame.pack();
        gameFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        gameFrame.setVisible(true);

        board.reset();

    }
}
