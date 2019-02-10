package com.romst.minesweeper;

import java.io.IOException;
import java.util.Scanner;

public class ConsoleUI implements GameUI {

    private Game game;
    private final Scanner sc;

    public ConsoleUI() {
        sc = new Scanner(System.in);
    }

    @Override
    public void initGame() throws IOException {
        int rows = 0;
        while (rows < 10 || rows > Game.MAX_ROWS) {
            System.out.print("\rHow many rows? Min: 10, Max: " + Game.MAX_ROWS + " ");
            rows = sc.nextInt();
        }
        int cols = 0;
        while (cols < 10 || cols > Game.MAX_COLS) {
            System.out.print("\rHow many cols? Min: 10, Max: " + Game.MAX_COLS + " ");
            cols = sc.nextInt();
        }
        this.game = new Game(rows, cols);

        int mines = 0;
        int maxMines = (int) (rows * cols * 0.5);
        while (mines < 10 || mines > maxMines) {
            System.out.print("\rHow many mines? Min: 10, Max: " + maxMines + " ");
            mines = sc.nextInt();
        }
        game.init(mines);
    }

    @Override
    public void start() {

        while (true) {
            printGame();
            int row = sc.nextInt();
            int col = sc.nextInt();
            try {
                game.step(row, col);
            } catch (GameOverException | WinException e) {
                System.out.println(e.getMessage());
                break;
            }
        }
    }

    public void printGame() {
        for (int row = 0; row < game.board.rows; row++) {
            for (int col = 0; col < game.board.cols; col++) {
                if (game.board.isOpen(row, col)) {
                    if (game.board.get(row, col) != MineField.MINE) {
                        System.out.print(game.board.get(row, col) + " ");
                    } else {
                        System.out.print("* ");
                    }
                } else {
                    System.out.print(". ");
                }
            }
            System.out.println();
        }
        System.out.println();
    }
}
