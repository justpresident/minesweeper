package com.romst.minesweeper;

class Game {

    public static final int MAX_ROWS = 50;
    public static final int MAX_COLS = 80;
    public static final String RULES = "This is a classic MineSweeper game.\n"+
        "Controls:\n" +
        "- Move caret with arrows\n" +
        "- Space - to open the point\n" +
        "- m - to mark point as a mine\n";


    public final MineField board;

    public Game(int rows, int cols) {
        this(new MineField(rows, cols));
    }

    private Game(MineField board) {
        this.board = board;
    }

    public void init(int mines) {
        board.putMines(mines);
        initNumbers();
    }

    public void step(int row, int col) throws GameOverException, WinException {
        if (board.get(row, col) == MineField.MINE) {
            throw new GameOverException("Game over\n");
        }
        board.open(row, col);
    }

    private void initNumbers() {
        for (int row = 0; row < board.rows; row++) {
            for (int col = 0; col < board.cols; col++) {
                if (board.isMine(row, col)) {
                    board.forNeighbors(row, col, pos -> {
                        if (!board.isMine(pos.row, pos.col)) {
                            board.inc(pos.row, pos.col);
                        }
                        return null;
                    });
                }
            }
        }
    }
}

