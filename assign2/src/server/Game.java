package server;

import java.io.PrintWriter;
import java.util.UUID;
import java.util.Random;
public class Game {
    private enum GameState {
        PARITY_SELECTION,
        GUESS_SELECTION
    }
    UUID gameId;
    private ClientSession startingClient;
    private ClientSession nonStartingClient;
    private String parity;
    private Integer number1, number2;
    GameState state;
    public Game(UUID gameId, ClientSession client1, ClientSession client2) {
        this.gameId = gameId;
        this.state = GameState.PARITY_SELECTION;
        this.number1 = -1;
        this.number2 = -1;

        Random random = new Random();
        ClientSession startingClient = random.nextBoolean() ? client1 : client2;
        ClientSession nonStartingClient = startingClient == client1 ? client2 : client1;

        this.startingClient = startingClient;
        this.nonStartingClient = nonStartingClient;

        startingClient.writer.println("You are the starting player. Choose odd or even.");
        nonStartingClient.writer.println("The other player is choosing. Please wait.");
    }

    public void update(ClientSession client, String input) {
        switch (state) {
            case PARITY_SELECTION:
                if(client == startingClient){
                if (input.equals("odd") || input.equals("even")) {
                        parity = input;
                        nonStartingClient.writer.println("Opponent has chosen " + input + ".");
                    state = GameState.GUESS_SELECTION;
                } else {
                    client.writer.println("Invalid input. Please choose odd or even.");
                }}
                break;
            case GUESS_SELECTION:
                //Each client guesses a number
                try {
                    int guess = Integer.parseInt(input);
                    if (client == startingClient) {
                        number1 = guess;
                        nonStartingClient.writer.println("The other player made is guess. Make yours.");
                    } else {
                        number2 = guess;
                        startingClient.writer.println("The other player made is guess. Make yours.");
                    }
                    if (number1 != -1 && number2 != -1) {
                        int sum = number1 + number2;
                        String winner = sum % 2 == 0 ? "even" : "odd";
                        ClientSession winningClient, losingClient;

                        if ((winner.equals("even") && parity.equals("even")) || (winner.equals("odd") && parity.equals("odd"))) {
                            winningClient = startingClient;
                            losingClient = nonStartingClient;
                        } else {
                            winningClient = nonStartingClient;
                            losingClient = startingClient;
                        }

                        winningClient.writer.println("You win! The sum is " + sum + " which is " + winner + ".");
                        losingClient.writer.println("You lose! The sum is " + sum + " which is " + winner + ".");

                        startingClient.changeState(ClientStateEnum.GAME_OVER);
                        nonStartingClient.changeState(ClientStateEnum.GAME_OVER);

                        ClientSession.games.remove(gameId);
                        startingClient.gameId = null;
                        nonStartingClient.gameId = null;

                    }
                } catch (NumberFormatException e) {
                    client.writer.println("Invalid input. Please enter a number.");
                }
                break;
        }
    }
}
