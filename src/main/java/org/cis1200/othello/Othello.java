package org.cis1200.othello;

import java.util.*;
import java.io.*;

public class Othello {

    private static final int[] dy = new int[] { 0, 0, 1, -1, -1, 1, -1, 1 };
    private static final int[] dx = new int[] { 1, -1, 0, 0, 1, -1, -1, 1 };

    private String[][] board;
    private boolean turn; // false is O true is X
    private boolean gameOver;
    private Stack<Coordinate> moves; // using stacks so I can use FILO
    private Stack<String> boardState; // using stacks so i can use FILO

    public Othello() {
        reset();
    }

    public void reset() {
        board = new String[8][8];
        turn = false;
        gameOver = false;
        moves = new Stack<>();
        boardState = new Stack<>();

        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                board[i][j] = ".";
            }
        }

        board[3][3] = "X";
        board[3][4] = "O";
        board[4][3] = "O";
        board[4][4] = "X";

        boardState.push(getBoard(board));
    }

    /**
     * playTurn is for each player to place a token down at the respective
     * spot (row, col). It also flips all the tokens on the board according to the
     * move.
     * 
     * @return if playTurn is successful
     */
    public boolean playTurn(int row, int col) {
        if (!isValidMove(row, col, turn) || gameOver) {
            if (checkWinner() == 4) {
                turn = !turn; // passed
            }
            if (checkWinner() != 0) {
                gameOver = true;
            }
            return false;
        }
        board[row][col] = this.getToken();
        for (int i = 0; i < 8; i++) {
            board = flip(row, col, dy[i], dx[i], board[row][col], board);
        }
        if (checkWinner() == 0) {
            turn = !turn;
        } else {
            gameOver = true;
        }
        moves.push(new Coordinate(row, col));
        boardState.push(getBoard(board));
        return true;
    }

    /**
     * checkWinner used to determine what status the game is at
     * 
     * @return integer 0 if still valid moves left, 1 if "O" wins, 2 if "X"
     *         wins, and "3" if it's a stalemate, "4" if its a pass
     */
    public int checkWinner() {
        boolean done = true;
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (board[i][j].equals(".")) {
                    if (isValidMove(i, j, turn)) {
                        done = false;
                    }
                }
            }
        }

        if (!done) {
            return 0;
        }

        int countO = 0;
        int countX = 0;
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (board[i][j].equals("O")) {
                    countO++;
                } else if (board[i][j].equals("X")) {
                    countX++;
                }
            }
        }
        if (countO + countX != 64) {
            System.out.println("\nPass to next!");
            return 4; // keep playing by passing
        }
        if (countO == countX) {
            return 3;
        } else if (countO > countX) {
            return 1;
        } else {
            return 2;
        }
    }

    /**
     * isValidMove to determine if the player's move is valid
     * if a player has no valid moves, they have to defer to the other player
     * if both players have no valid moves, the game ends there.
     * 
     * @param row
     * @param col
     * @return
     */
    public boolean isValidMove(int row, int col, boolean player) {

        if (!board[row][col].equals(".")) {
            return false;
        }

        String opp = player ? "O" : "X";
        String current = !player ? "O" : "X";

        for (int i = 0; i < 8; i++) {
            int newRow = row + dy[i];
            int newCol = col + dx[i];

            if (newRow >= 0 && newRow < 8 && newCol >= 0 && newCol < 8
                    && board[newRow][newCol].equals(opp)) {
                while (newRow >= 0 && newRow < 8 && newCol >= 0 && newCol < 8) {
                    newRow += dy[i];
                    newCol += dx[i];

                    if (newRow < 0 || newRow >= 8 || newCol < 0 || newCol >= 8) {
                        break;
                    }

                    if (board[newRow][newCol].equals(".")) {
                        break;
                    }

                    if (board[newRow][newCol].equals(current)) {
                        return true;
                    }
                }
            }
        }

        return false;

    }

    /**
     * flip to change the tokens between
     * 
     * @param row
     * @param col
     * @param dy
     * @param dx
     * @param token
     */
    public String[][] flip(int row, int col, int dy, int dx, String token, String[][] board) {
        int newR = row + dy;
        int newC = col + dx;
        if (newR < 0 || newR >= 8 || newC < 0 || newC >= 8) {
            return board;
        }
        while (!board[newR][newC].equals(".")) {
            if (board[newR][newC].equals(token)) { // see same token
                while (!(row == newR && col == newC)) { // while not reaching original
                    board[newR][newC] = token; // flip
                    newR -= dy; // increment down
                    newC -= dx;
                }
                break;
            } else { // increment
                newR += dy;
                newC += dx;
            }
            if (newR < 0 || newR >= 8 || newC < 0 || newC >= 8) {
                break;
            }
        }
        return board;
    }

    /**
     * undoes the most recent move
     * uses a try catch if tries to undo final stack
     * 
     * @return the Coordinate popped
     */
    public Coordinate undo() {
        try {
            turn = !turn;
            boardState.pop();
            setBoard(boardState.peek());
            System.out.print("UNDO (new board below)");
            return moves.pop();
        } catch (EmptyStackException e) {
            System.out.println("Empty stack -- reset game");
            reset();
            return null;
        }
    }

    // getters and setters

    public Stack<Coordinate> getMoves() {
        return moves;
    }

    /**
     * show valid moves at any point in the game for player of turn
     * @return a list of valid moves at that point
     */
    public LinkedList<Coordinate> getListofMoves() {
        LinkedList<Coordinate> returnList = new LinkedList<>();
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (isValidMove(i, j, turn)) {
                    returnList.add(new Coordinate(i, j));
                }
            }
        }
        return returnList;
    }

    public boolean getTurn() {
        return turn;
    }

    public String getToken() {
        return !turn ? "O" : "X";
    }

    public String getBoard(String[][] board) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                sb.append(board[i][j]);
            }
        }
        return sb.toString();
    }

    public boolean isGameOver() {
        return gameOver;
    }

    public String getCell(int row, int col) {
        return board[row][col];
    }

    public int getCount(String token) {
        int count = 0;
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (board[i][j].equals(token)) {
                    count += 1;
                }
            }
        }
        return count;
    }

    public String[][] get2DBoard() {
        String[][] result = new String[8][8];
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                result[i][j] = board[i][j];
            }
        }
        return result;
    }

    public void setCell(int row, int col, String token) {
        board[row][col] = token;
    }

    public void setTurn(boolean turn) {
        this.turn = turn;
    }

    public void setBoard(String board) {
        for (int i = 0; i < board.length(); i++) {
            int row = i / 8;
            int col = i % 8;
            this.board[row][col] = board.substring(i, i + 1);
        }
    }

    public void printGameState() {
        System.out.println("\nIt is " + getToken() + "'s turn!");
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                System.out.print(board[i][j]);
            }
            System.out.println();
        }
        System.out.println();
    }

    public void saveGame(String filename) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            for (int i = 0; i < board.length; i++) {
                for (int j = 0; j < board[i].length; j++) {
                    writer.write(board[i][j] + " ");
                }
                writer.newLine();
            }
            System.out.println("Game saved successfully!");
        } catch (IOException e) {
            System.out.println("Error saving game: " + e.getMessage());
        }
    }

    public void loadGame(String filename) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            for (int i = 0; i < board.length; i++) {
                String[] line = reader.readLine().trim().split(" "); // Read and split each line
                for (int j = 0; j < line.length; j++) {
                    board[i][j] = line[j];
                }
            }
            System.out.println("Game loaded successfully!");
        } catch (IOException e) {
            System.out.println("Error loading game: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        Othello o = new Othello();
        String boardString = "OOOOOX..OOOOOX..OOXOOX..OXXOOX..XOXOOXXXXXOXOXX.OOOOOX..OOOOOX..";
        o.setBoard(boardString);
        o.setTurn(true);
    }

    public String[][] possibleBoard(int row, int col, String token) {
        String[][] possibleBoard = get2DBoard();
        if (!isValidMove(row, col, turn) || gameOver) {
            return possibleBoard;
        }
        possibleBoard[row][col] = token;
        for (int i = 0; i < 8; i++) {
            possibleBoard = flip(row, col, dy[i], dx[i], token, possibleBoard);
        }
        return possibleBoard;
    }
}
