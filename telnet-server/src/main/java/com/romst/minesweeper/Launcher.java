package com.romst.minesweeper;

import java.io.IOException;

public class Launcher {

    public static void main(String[] argv) throws IOException {
        final Game game = new Game(10, 10);
        game.init(10);

        boolean telnet = true;
        if (telnet) {
            int port;
            if (argv.length == 0) {
                port = 9989;
            } else {
                port = Integer.valueOf(argv[0]);
            }
            TelnetServer server = new TelnetServer(port);
            server.start();
        } else {
            GameUI ui = new ConsoleUI();

            ui.initGame();
            ui.start();
        }
    }
}
