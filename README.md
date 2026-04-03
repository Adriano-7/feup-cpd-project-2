
# Online Game Server 

> **Project**
> <br />
> Course Unit: [Computação Paralela e Distribuída](https://sigarra.up.pt/feup/pt/ucurr_geral.ficha_uc_view?pv_ocorrencia_id=520333) (CPD), 3rd year
> <br />
> Course: Informatics and Computing Engineering
> <br />
> Faculty: **FEUP** (Faculty of Engineering of the University of Porto)
> <br />
> Project evaluation: **18**/20

---

## Project Goals

Development of a robust, concurrent client-server system for an online text-based "Odds and Evens" game. The system focuses on handling user authentication, secure communication via SSL/TLS, and matchmaking strategies in a distributed environment, ensuring thread safety and scalability.

**Core Requirements:**
- **User Authentication:** Secure registration and login using hashed passwords (BCrypt).
- **Matchmaking:** Implementation of both "Simple" (FCFS) and "Ranked" (score-based) matching.
- **Concurrency:** Management of client sessions using Java Virtual Threads.
- **Data Persistence:** Thread-safe access to user data and match history.

## Technical Approach

### 1. Networking and Security
To ensure secure communication, we implemented an **SSL/TLS layer** using `SSLSocket` and `SSLServerSocket`. Certificates were managed via a local Java KeyStore (`JKS`), providing encrypted data transmission between clients and the server.

### 2. Concurrency Model
The server leverages **Java Virtual Threads** (`java.lang.Thread.ofVirtual()`) to handle a high volume of concurrent client connections without the memory overhead associated with platform threads. This allowed for:
* **Non-blocking IO:** Independent threads for input/output loops per client.
* **Scalable Matchmaking:** Dedicated virtual threads for each matchmaking strategy, continuously processing pools in the background.

### 3. Thread-Safe Data Management
Given the distributed nature of the game, maintaining consistent states across concurrent sessions was critical:
* **Database Access:** We implemented a `ReentrantReadWriteLock` in the `DatabaseManager` to allow multiple concurrent reads while ensuring exclusive access during write operations (e.g., registration or score updates).
* **Matchmaking Pools:** The matchmaking strategies utilized synchronized blocks and thread-safe collections to manage client hand-offs effectively, preventing race conditions when two users are matched simultaneously.

### 4. Matchmaking Strategies
We developed a modular strategy pattern to handle different matchmaking needs:
* **Simple Matchmaking:** Operates on a First-Come, First-Served (FCFS) basis, grouping the first two available users in the pool.
* **Ranked Matchmaking:** Uses a score-based queue. To ensure players eventually find a match, the algorithm dynamically increases the `maxDifference` threshold as wait time increases, allowing players with larger skill gaps to match if no closer opponent is found within a specific timeout.

## Implementation Details

* **Authentication Handler:** A state-machine-based handler that guides users through the login/registration process, preventing unauthorized access to the game room.
* **Game Logic:** A stateful `Game` class manages individual match progression, ensuring parity choices and guesses are processed sequentially within a single game instance.
* **Fault Tolerance:** Robust handling of connection drops during the matchmaking phase or in-game, ensuring that players are removed from pools and authentication states are cleaned up correctly.

## Running the Project

**Prerequisites:**
- Java 21+
- Gradle

**Build and Run:**
```bash
# Clone the repository
git clone git@git.fe.up.pt:cpd/2324/t10/g11.git
cd g11/assign2

# Start the application
./gradlew run
```

## Tech Stack

* **Language:** Java 21
* **Build System:** Gradle
* **Networking:** Java SSL/TLS Sockets
* **Security:** BCrypt for password hashing
* **Concurrency:** Java Virtual Threads, `java.util.concurrent` locks
* **Data Persistence:** CSV-based file system with atomic read/write locks

## Team

- Adriano Machado (**up202105352@up.pt**)
- Daniel Dória (**up202108808@up.pt**)
- André Rodrigues (**up202108721@up.pt**)
