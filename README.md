# CS321 Final Project — Wordle

A client/server implementation of a Wordle-style word guessing game built for CS321 (Communications and Networking).

## Team

| Role | Author | File |
|------|--------|------|
| Server | Aidan Donohoe | `server.java` |
| Client | Michael Arculeo | `Client_v3.java` |
| Library | Josh Morris | `wordle_lib3.py` |

## Description

The project is a networked Wordle game. A player runs the client, connects to the server over TCP, and tries to guess the day's 5-letter word in six attempts or fewer. The server selects a word of the day that is the same for every player on a given date, and records the outcome of each session.

The system is split across three components:

- The **server** (Java) listens for incoming client connections on a specified port, spawns a handler thread for each client, manages the word of the day, and processes results.
- The **client** (Java) establishes a connection to the server, requests the word of the day, runs the guessing loop with the user, and reports the final outcome.
- The **library** (Python) defines the shared game logic, word list, word-of-the-day selection algorithm, input validation, and player session tracking that the project is built around.

## How to Compile and Run

### Server

​```
javac server.java
java server <port>
​```

Example: `java server 5000`

### Client

​```
javac Client_v3.java
java Client_v3 <hostname> <port>
​```

Example: `java Client_v3 localhost 5000`

The server must be running before the client is started.

### Library

No compilation step. The library is imported directly into any Python script that uses it:

​```python
import wordle_lib as wl
​```

Requires Python 3.6 or later. Uses only the standard library.

## Design Overview

The server is multithreaded. The main thread loops on `ServerSocket.accept()` and hands each new connection to a `Handler` thread, so multiple clients can play at the same time without blocking each other. Each handler owns its own socket, input reader, and output writer, and runs a receive-loop that dispatches on the first token of each incoming line.

The client is single-threaded and blocking. It opens a socket, walks through the protocol in order (connect, request word, guess loop, send result, disconnect), and reads from standard input between guesses.

The library provides shared game constants and data. It defines the word pool, the deterministic word-of-the-day algorithm (date-seeded random selection so every player on the same day gets the same word), input validation, and a session log that stores player ID, name, result, guesses used, and date played. Guess evaluation is intentionally not in the library — that logic lives in the Java client/server where it belongs.

## Library Type

Python does not distinguish between static and shared libraries the way C or C++ does. A `.py` module is loaded at runtime by the interpreter when another script calls `import`, so the closest equivalent is a **dynamically loaded module**, conceptually similar to a shared library.

`wordle_lib.py` is a standalone Python module that is imported at runtime. This approach was chosen because:

- Python is an interpreted language with no separate compilation or linking step, so a static-library equivalent (bundling compiled code into the caller) does not apply.
- Loading the module at runtime means any future Python client or test script can pull in the same game constants and tracking functions without duplicating code.
- The module has no external dependencies, so there is nothing to link against and no build configuration to maintain.

## Protocol

All messages are plain ASCII text terminated by a newline character (`\n`). The newline serves as the message delimiter — each side reads one line at a time, which is how it knows it has received a complete message. Messages are case-sensitive. The client initiates every exchange; the server only sends data in response to a client message.

### Client → Server Messages

| Message | Meaning |
|---------|---------|
| `CONNECT` | Request to open a game session |
| `REQUEST WOTD` | Ask the server for the current word of the day |
| `GUESS <word>` | Submit a 5-letter guess (e.g. `GUESS CRIMP`) |
| `RESULT WIN` | Report that the player won |
| `RESULT LOSE` | Report that the player lost |
| `DISCONNECT` | Close the session |

### Server → Client Messages

| Message | Meaning |
|---------|---------|
| `OK CONNECTED` | Connection acknowledged |
| `<WORD>` | The word of the day, returned as a bare 5-letter string |
| `PATTERN <pattern>` | 5-character feedback string (e.g. `PATTERN GYBBG`) |
| `ACK RESULT` | Result received and logged |
| `OK BYE` | Disconnect acknowledged |
| `ERROR UNKNOWN COMMAND` | The server did not recognize the message |

### Pattern Encoding

The 5-character feedback pattern uses one character per letter position:

- `G` — correct letter in the correct position (green)
- `Y` — correct letter in the wrong position (yellow)
- `B` — letter not in the word (black)

### Example Exchange

​```
Client: CONNECT
Server: OK CONNECTED
Client: REQUEST WOTD
Server: CRIMP
Client: GUESS PLUMB
Server: PATTERN BBBYB
Client: GUESS CRIMP
Server: PATTERN GGGGG
Client: RESULT WIN
Server: ACK RESULT
Client: DISCONNECT
Server: OK BYE
​```

### Message Termination

Because every message ends in `\n`, both sides use line-based reads to know when a message is complete. The Java code uses `BufferedReader.readLine()` and `PrintWriter.println()`, and any Python caller uses `socket.makefile()` line reads. There is no fixed-length prefix, no content-length header, and no terminator character other than the newline.

## Known Issues

- **Duplicate-letter handling in pattern evaluation.** The Java `checkGuess()` implementation marks every occurrence of a guessed letter as yellow if the target contains that letter anywhere. Standard Wordle behavior marks at most as many yellows as there are remaining instances in the target after greens are resolved.
- **Player log is not persisted.** The library's `player_log` is an in-memory list that resets each time the process exits. For persistent tracking across runs, entries would need to be written to a file.
- **No authentication or session IDs.** The protocol does not identify which client is sending which message beyond the socket itself. Player identity for tracking purposes must be passed in explicitly by the caller.
