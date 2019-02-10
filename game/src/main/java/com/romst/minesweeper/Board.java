package com.romst.minesweeper;

import java.util.function.Function;

public class Board {

    private final int[][] board;
    final int rows;
    final int cols;

    Board(int rows, int cols) {
        this(rows, cols, new int[rows][cols]);
    }

    private Board(int rows, int cols, int[][] board) {
        this.rows = rows;
        this.cols = cols;
        this.board = board;
    }

    public boolean isValid(int row, int col) {
        return row >= 0 && col >= 0 & row < rows && col < cols;
    }

    protected void inc(int row, int col) {
        if (!isValid(row, col)) {
            throw new Error("Illegal access: row = " + row + " col = " + col);
        }

        set(row, col, get(row, col) + 1);
    }

    protected void set(int row, int col, int val) {
        if (!isValid(row, col)) {
            throw new Error("Illegal access: row = " + row + " col = " + col);
        }

        board[row][col] = val;
    }

    public int get(int row, int col) {
        if (!isValid(row, col)) {
            throw new Error("Illegal access: row = " + row + " col = " + col);
        }

        return board[row][col];
    }

    protected void forNeighbors(int row, int col, Function<Pos, Void> func) {
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                if (i == 0 && j == 0) {
                    continue;
                }
                if (isValid(row + i, col + j)) {
                    func.apply(new Pos(row + i, col + j));
                }
            }
        }
    }
}
