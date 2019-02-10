package com.romst.minesweeper;

import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TextCharacter;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.screen.TerminalScreen;
import com.googlecode.lanterna.terminal.Terminal;
import com.googlecode.lanterna.terminal.ansi.TelnetTerminal;
import com.googlecode.lanterna.terminal.ansi.TelnetTerminalServer;
import java.io.IOException;

class TelnetUI implements Runnable, GameUI {

    private final TerminalScreen screen;
    private Game game;
    private final Pos pos;

    public static char LEFT_TOP_CORNER = '\u2554';
    public static char RIGHT_TOP_CORNER = '\u2557';
    public static char LEFT_BOTTOM_CORNER = '\u255A';
    public static char RIGHT_BOTTOM_CORNER = '\u255D';
    public static char HORIZONTAL_BAR = '\u2550';
    public static char VERTICAL_BAR = '\u2551';
    public static char DEATH = '\u2620';
    public static char BOMB_MARK = '\u2622';
    public static char HIDDEN_SPACE = '░';

    public TelnetUI(Terminal term) throws IOException {
        this.screen = new TerminalScreen(term);
        this.screen.startScreen();
        this.pos = new Pos(0, 0);
    }

    private void print(String s) throws IOException {
        for (char c : s.toCharArray()) {
            if (c == '\n') {
                newLine();
            } else if (c == '\r') {
                screen.newTextGraphics()
                    .drawLine(screen.getCursorPosition().withColumn(0), screen.getCursorPosition(), ' ');
                screen.setCursorPosition(screen.getCursorPosition().withColumn(0));
            } else {
                print(c);
            }
        }
    }

    private void print(char c) {
        screen.setCharacter(screen.getCursorPosition(), new TextCharacter(c));
        screen.setCursorPosition(screen.getCursorPosition().withRelativeColumn(1));
    }

    private int readInt() throws IOException {
        int result = 0;
        Character c;
        while ((c = screen.readInput().getCharacter()) != null && c != '\n') {
            if (c >= '0' && c <= '9') {
                print(c);
                screen.refresh();
                result = result * 10 + Integer.valueOf(c.toString());
            }
        }
        return result;
    }

    private void newLine() throws IOException {
        screen.setCursorPosition(screen.getCursorPosition().withRelativeRow(1).withColumn(0));
    }

    private int boardRow;

    private void redraw() throws IOException {
        screen.clear();
        screen.setCursorPosition(new TerminalPosition(0, 0));

        print(String.format("Pos: %d, %d", pos.row, pos.col));
        newLine();
        print(String.format("Mines: %d", game.board.getMinesCount()));
        newLine();

        horizontalBar(LEFT_TOP_CORNER, RIGHT_TOP_CORNER);
        boardRow = screen.getCursorPosition().getRow();

        for (int row = 0; row < game.board.rows; row++) {
            print(VERTICAL_BAR);
            for (int col = 0; col < game.board.cols; col++) {
                if (game.board.isOpen(row, col)) {
                    switch (game.board.get(row, col)) {
                        case MineField.MINE:
                            print(DEATH);
                            break;
                        case 0:
                            print(' ');
                            break;
                        default:
                            print(Integer.toString(game.board.get(row, col)).charAt(0));
                            break;
                    }
                } else if (game.board.isBombMark(row, col)) {
                    print(BOMB_MARK);
                } else {
                    print(HIDDEN_SPACE);
                }
            }
            print(VERTICAL_BAR);
            newLine();
        }
        horizontalBar(LEFT_BOTTOM_CORNER, RIGHT_BOTTOM_CORNER);

//        screen.refresh();
    }

    private void setCursor() {
        screen.setCursorPosition(new TerminalPosition(1 + pos.col, boardRow + pos.row));
    }

    private void horizontalBar(char leftCorner, char rightCorner) throws IOException {
        print(leftCorner);
        screen.newTextGraphics().drawLine(
            screen.getCursorPosition(),
            screen.getCursorPosition().withRelativeColumn(game.board.cols - 1),
            HORIZONTAL_BAR
        );
        screen.setCursorPosition(screen.getCursorPosition().withRelativeColumn(game.board.cols));
        print(rightCorner);
        newLine();
    }

    @Override
    public void run() {
        try {
            initGame();
        } catch (IOException e) {
            try {
                screen.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            System.out.println(e.getMessage());
        }
        start();
    }

    @Override
    public void initGame() throws IOException {
        screen.clear();
        screen.setCursorPosition(new TerminalPosition(0, 0));

        print(Game.RULES);
        newLine();
        screen.refresh();
        int rows = 0;
        while (rows < 10 || rows > Game.MAX_ROWS) {
            print("\rHow many rows? Min: 10, Max: " + Game.MAX_ROWS + " ");
            screen.refresh();
            rows = readInt();
        }
        int cols = 0;
        while (cols < 10 || cols > Game.MAX_COLS) {
            print("\rHow many cols? Min: 10, Max: " + Game.MAX_COLS + " ");
            screen.refresh();
            cols = readInt();
        }
        this.game = new Game(rows, cols);

        int mines = 0;
        int maxMines = (int) (rows * cols * 0.5);
        while (mines < 10 || mines > maxMines) {
            print("\rHow many mines? Min: 10, Max: " + maxMines + " ");
            screen.refresh();
            mines = readInt();
        }
        game.init(mines);
    }

    @Override
    public void start() {
        try {
            redraw();
            setCursor();
            screen.refresh();
            LOOP:
            while (true) {
                Thread.sleep(100);
                boolean setCursor = false;
                boolean redraw = false;
                KeyStroke key = screen.readInput();
                switch (key.getKeyType()) {
                    case ArrowLeft:
                        if (game.board.isValid(pos.row, pos.col - 1)) {
                            pos.col--;
                            setCursor = true;
                        }
                        break;
                    case ArrowRight:
                        if (game.board.isValid(pos.row, pos.col + 1)) {
                            pos.col++;
                            setCursor = true;
                        }
                        break;
                    case ArrowUp:
                        if (game.board.isValid(pos.row - 1, pos.col)) {
                            pos.row--;
                            setCursor = true;
                        }
                        break;
                    case ArrowDown:
                        if (game.board.isValid(pos.row + 1, pos.col)) {
                            pos.row++;
                            setCursor = true;
                        }
                        break;
                    case Escape:
                        break LOOP;
                    default:
                        switch (key.getCharacter()) {
                            case ' ':
                                game.step(pos.row, pos.col);
                                redraw = true;
                                break;
                            case 'm':
                                game.board.triggerBombMark(pos.row, pos.col);
                                redraw = true;
                                break;
                            case 'q':
                                break LOOP;
                        }
                }
                if (redraw) {
                    redraw();
                    setCursor();
                } else if (setCursor) {
                    setCursor();
                }
                screen.refresh();
            }
        } catch (GameOverException | WinException e) {
            try {
                game.board.reveal();
                redraw();
                print(e.getMessage());
                screen.refresh();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
            try {
                redraw();
                print(e.getMessage());
                screen.refresh();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        } finally {
            try {
                screen.readInput();
                screen.close();
                screen.getTerminal().close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

class TelnetServer {

    private final int port;

    public TelnetServer(int port) {
        this.port = port;
    }

    public void start() throws IOException {
        TelnetTerminalServer server = new TelnetTerminalServer(port);
        TelnetTerminal term;
        while ((term = server.acceptConnection()) != null) {
            new Thread(new TelnetUI(term)).start();
        }
    }
}
