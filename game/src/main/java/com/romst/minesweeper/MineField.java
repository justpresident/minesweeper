package com.romst.minesweeper;

import java.util.concurrent.ThreadLocalRandom;

public class MineField extends Board {

    static final int MINE = 10;
    private static final int VAL_MASK = (1 << 5) - 1;
    private static final int OPENED_BIT = 1 << 6;
    private static final int BOMB_MARK = 1 << 7;
    private int mines;

    public MineField(int rows, int cols) {
        super(rows, cols);
    }

    public int getMinesCount() {
        return mines;
    }

    public void putMines(int mines) {
        this.mines = mines;

        for (int i = 0; i < mines; i++) {
            int row = ThreadLocalRandom.current().nextInt(rows);
            int col = ThreadLocalRandom.current().nextInt(cols);
            if (!isMine(row, col)) {
                set(row, col, MINE);
            } else {
                i--;
            }

        }
    }

    public boolean isMine(int row, int col) {
        return get(row, col) == MINE;
    }

    @Override
    public int get(int row, int col) {
        return super.get(row, col) & VAL_MASK;
    }

    public void reveal() {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                try {
                    open(i, j);
                } catch (WinException ignored) {

                }
            }
        }
    }

    public void open(int row, int col) throws WinException {
        if (isOpen(row, col)) {
            return;
        }

        set(row, col, super.get(row, col) | OPENED_BIT);
        removeBombMark(row, col);

        if (get(row, col) == 0) {
            forNeighbors(row, col, pos -> {
                try {
                    open(pos.row, pos.col);
                } catch (WinException ignored) {

                }
                return null;
            });
        }
        checkWin();
    }

    public boolean isOpen(int row, int col) {
        return (super.get(row, col) & OPENED_BIT) > 0;
    }

    public void triggerBombMark(int row, int col) throws WinException {
        if (isOpen(row, col)) {
            return;
        }
        if (isBombMark(row, col)) {
            removeBombMark(row, col);
        } else if (mines > 0) {
            set(row, col, super.get(row, col) | BOMB_MARK);
            mines--;
            checkWin();
        }
    }

    public void checkWin() throws WinException {
        if (mines == 0 && isEverythingOpen()) {
            throw new WinException("You Win!\n");
        }
    }

    public boolean isEverythingOpen() {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (!isOpen(i, j) && !isBombMark(i, j)) {
                    return false;
                }
            }
        }

        return true;
    }

    private void removeBombMark(int row, int col) {
        if (!isBombMark(row, col)) {
            return;
        }
        set(row, col, super.get(row, col) & ~BOMB_MARK);
        mines++;
    }

    public boolean isBombMark(int row, int col) {
        return (super.get(row, col) & BOMB_MARK) > 0;
    }
}
