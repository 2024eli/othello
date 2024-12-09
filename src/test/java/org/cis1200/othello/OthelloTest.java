package org.cis1200.othello;

import org.junit.jupiter.api.*;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;

public class OthelloTest {

    Othello othello;

    @BeforeEach
    public void setUp() {
        othello = new Othello();
    }

    @Test
    public void noValidMovesPass() {
        // To test when one player cant move anymore and has to defer to the other
        // player
        String boardString = "OOOOOX..OOOOOX..OOXOOX..OXXOOX..XOXOOXXXXXOXOXX.OOOOOX..OOOOOX..";
        othello.setBoard(boardString);
        othello.setTurn(true);
        assertFalse(othello.playTurn(0, 6)); // X can't move
        assertEquals("O", othello.getToken());

        othello.setTurn(true);
        assertEquals(4, othello.checkWinner());
    }

    @Test
    public void testValidMove() {
        assertTrue(othello.playTurn(2, 3));
        assertEquals("O", othello.getCell(2, 3));
        assertEquals("O", othello.getCell(3, 3)); // Flipped token
    }

    @Test
    public void testInvalidMove() {
        assertFalse(othello.playTurn(0, 0));
        assertEquals(".", othello.getCell(0, 0));
    }

    @Test
    public void testGameOverCondition() {
        String fullBoard = "OOOOOOOXOOOOOOOXOOOOOOOXOOOOOOOXOOOOOOOXOOOOOOOXOOOOOOOXOOOOOOOX";
        othello.setBoard(fullBoard);
        othello.playTurn(2, 3);
        assertTrue(othello.isGameOver());
        assertEquals(1, othello.checkWinner());
    }

    @Test
    public void testUndoMove() {
        othello.playTurn(2, 3);
        Coordinate undoneMove = othello.undo();
        assertEquals(2, undoneMove.getRow());
        assertEquals(3, undoneMove.getCol());
        assertEquals(".", othello.getCell(2, 3));
    }

    @Test
    public void testFlip() {
        String boardString = "........" +
                "...XXX.." +
                "...XO..." +
                "...XXX.." +
                "........";
        othello.setBoard(boardString);

        othello.playTurn(3, 5);

        assertEquals("X", othello.getCell(3, 5));
        assertEquals("X", othello.getCell(3, 4));
    }

    @Test
    public void testTokenCount() {
        String boardString = "...O...." +
                "...OX..." +
                "...XO..." +
                "...XXX.." +
                "...O....";

        othello.setBoard(boardString);

        int countO = othello.getCount("O");
        int countX = othello.getCount("X");

        assertEquals(4, countO);
        assertEquals(5, countX);
    }

    @Test
    public void testUndoOnEmptyStack() {
        // Test undo operation when no moves have been made
        Coordinate undoneMove = othello.undo();
        undoneMove = othello.undo();
        assertNull(undoneMove);
        // Verify the board is reset to initial state
        assertEquals("X", othello.getCell(3, 3));
        assertEquals("O", othello.getCell(3, 4));
        assertEquals("O", othello.getCell(4, 3));
        assertEquals("X", othello.getCell(4, 4));
    }

    @Test
    public void testLoadInvalidGame() {
        // Test loading an invalid game state
        othello.loadGame("nonexistent_file.txt");
        // Verify that the board remains in its initial state
        assertEquals("X", othello.getCell(3, 3));
        assertEquals("O", othello.getCell(3, 4));
        assertEquals("O", othello.getCell(4, 3));
        assertEquals("X", othello.getCell(4, 4));
    }

    @Test
    public void testSaveAndLoadGame() {
        othello.playTurn(2, 3);
        othello.saveGame("test_save.txt");

        Othello newGame = new Othello();
        newGame.loadGame("test_save.txt");

        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                assertEquals(othello.getCell(i, j), newGame.getCell(i, j));
            }
        }

        new File("test_save.txt").delete();
    }
}
