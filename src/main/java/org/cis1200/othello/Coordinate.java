package org.cis1200.othello;

public class Coordinate {

    private int row;
    private int col;

    public Coordinate(int row, int col) {
        this.row = row;
        this.col = col;
    }

    public Coordinate(int index) {
        this.row = index / 8;
        this.col = index % 8;
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    public String toString() {
        return "(" + row + ", " + col + ")";
    }
}
