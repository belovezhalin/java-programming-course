## Battleship Game

Create an application for playing Battleship over the network.

The application connects to another application and plays a game of Battleship.

### Command-Line Parameters
The application supports the following parameters:
* `-mode [server|client]` - specifies the mode of operation (as a server: accepts a connection, as a client: connects to a server)
* `-port N` - the port on which the application communicates.
* `-map map-file` - the path to the file containing the map with ship placements (format described in the Map section).

### Map
* The map is exactly the same as in task 3 from set 03-collections. You must use the previously written generator to create random maps.

### Communication Protocol
* Communication occurs using the TCP protocol, with UTF-8 encoding.
* The client and server alternately send each other a _message_, consisting of 2 parts: _command_ and _coordinates_, separated by the `;` character, and terminated by a newline `\n`.
  * Message format: `command;coordinates\n`
  * Example message: `miss;D6\n`
* Commands and their meanings:
  * _start_
    * Command to initiate the game.
    * Sent by the client only once, at the beginning.
    * Example: `start;A1\n`
  * _miss_
    * Response sent when no ship is located at the coordinates received from the other side.
    * Example: `miss;A1\n`
  * _hit_
    * Response sent when a ship is located at the coordinates received from the other side, and it is not the last remaining unhit segment of the ship.
    * Example: `hit;A1\n`
  * _hit sunk_
    * Response sent when a ship is located at the coordinates received from the other side, and the last remaining unhit segment of that ship has been hit.
    * Example: `hit sunk;A1\n`
  * _last sunk_
    * Response sent when a ship is located at the coordinates received from the other side, and the last remaining unhit segment of the entire fleet has been sunk.
    * This is the last command in the game. The side sending it loses.
    * No coordinates are provided with this command (there is no one left to shoot!).
    * Example: `last sunk\n`
* It is possible (though strategically unwise) to shoot multiple times at the same location. In this case, respond according to the current state of the board:
  * `miss` for a miss,
  * `hit` if the ship was already hit at this location but is not yet sunk,
  * `hit sunk` if the ship is already sunk.
* Error handling:
  * If an unrecognized command is received or if 1 second passes without a response, resend the last message.
  * After 3 failed attempts, display the message `Communication Error` and terminate the application.

### Application Behavior
* After launching (in any mode), the application should display its map.
* During the game, the application should display all sent and received messages.
* After the game ends, the application should display:
  * `Victory\n` in case of a win or `Loss\n` in case of a loss,
  * In case of victory: the opponent's full map,
  * In case of a loss: the opponent's map, replacing unknown fields with `?`. _Note_: fields adjacent to a sunk ship should be considered discovered (no other ship can occupy those fields).
  * An empty line.
  * Your own map, with additional markings: `~` for misses by the opponent, `@` for successful hits by the opponent.

Example of the opponent's map from a lost session:
```
..#..??.?.
#.????.#..
#....??...
..##....?.
?.....##..
??#??.....
..?......#
..##...#..
.##....#.#
.......#..
```

Example of your map after the game (won; not all ships sunk):
```
~~@~~.~~~.
@..~.~.@.~
#.~#..~.~.
..##..~..~
..~.~.@@..
.#@~..~...
.~.~.~.~.@
~.##.~.#~~
.##~..~~~~
..~.~.~~~.
```

### Grading Rules:
This task does not have automated tests or a predefined project structure (you need to create it yourself).

Grading will involve playing several rounds between participants during the next lab session.

