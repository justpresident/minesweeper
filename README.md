# minesweeper
Telnet server with Minesweeper game

# Build

To build game server you need to install maven to your machine.

```
git clone https://github.com/justpresident/minesweeper.git
cd minesweeper
mvn clean package
```
# Launching game server
To start game server on port 9989:

`
java -jar telnet-server/target/telnet-server-1.0-SNAPSHOT.jar 9989
`

To start it on default 23 port you need superuser permissions:

```
sudo java -jar telnet-server/target/telnet-server-1.0-SNAPSHOT.jar 23
```

# Playing
On Mac and Unix you can use telnet command
```
telnet localhost 9989
```
Of course you can launch it on a remote server and connect to it instead of running it locally
