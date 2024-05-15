package org.project.server;

import java.io.IOException;
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
    public Game(UUID gameId, ClientSession client1, ClientSession client2) throws IOException {
        this.gameId = gameId;
        this.state = GameState.PARITY_SELECTION;
        this.number1 = -1;
        this.number2 = -1;

        Random random = new Random();
        ClientSession startingClient = random.nextBoolean() ? client1 : client2;
        ClientSession nonStartingClient = startingClient == client1 ? client2 : client1;

        this.startingClient = startingClient;
        this.nonStartingClient = nonStartingClient;

        startingClient.writer.write(
                "-----------------------------------------------\n" +
                "|                GAME STARTED                 |\n" +
                "|---------------------------------------------|\n" +
                "|  You'll be the player choosing the          |\n" +
                "|  parity for this game.                      |\n" +
                "|---------------------------------------------|\n" +
                "|  Please choose if you either want to        |\n" +
                "|  play as Even ['even'] or as Odd ['odd'].   |\n" +
                "-----------------------------------------------\n");
        startingClient.writer.flush();


        nonStartingClient.writer.write(
                    "-----------------------------------------------\n" +
                        "|                GAME STARTED                 |\n" +
                        "|---------------------------------------------|\n" +
                        "|  Please wait while the other player         |\n" +
                        "|  chooses its parity for this game.          |\n" +
                        "-----------------------------------------------\n"
        );
        nonStartingClient.writer.flush();
    }

    public void update(ClientSession client, String input) throws IOException {
        switch (state) {
            case PARITY_SELECTION:
                if(client == startingClient){
                    if (input.equals("odd") || input.equals("even")) {
                        parity = input;
                        nonStartingClient.writer.write(
                                "\n-----------------------------------------------\n" +
                                "   Opponent has chosen " + input + ".          \n" +
                                "|---------------------------------------------|\n" +
                                "|  Please type which number you want to use.  |\n" +
                                "-----------------------------------------------\n");
                        nonStartingClient.writer.flush();

                        state = GameState.GUESS_SELECTION;
                    } else {
                        client.writer.write(
                                "\n-----------------------------------------------\n" +
                                "| Invalid input. Please type 'even' or 'odd'. |\n" +
                                "-----------------------------------------------\n");
                        client.writer.flush();
                    }}
                break;
            case GUESS_SELECTION:
                //Each client guesses a number
                try {
                    int guess = Integer.parseInt(input);
                    if (client == startingClient) {
                        number1 = guess;
                        nonStartingClient.writer.write(
                                "\n-----------------------------------------------\n" +
                                "|   The other player made his guess.          |\n" +
                                "|   Please make yours.                        |\n" +
                                "-----------------------------------------------\n");
                        nonStartingClient.writer.flush();
                    } else {
                        number2 = guess;
                        startingClient.writer.write(
                                "\n-----------------------------------------------\n" +
                                "|   The other player made his guess.          |\n" +
                                "|   Please make yours.                        |\n" +
                                "-----------------------------------------------\n");
                        startingClient.writer.flush();
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

                        winningClient.writer.write(
                                "\n-----------------------------------------------\n" +
                                "|        Congratulations, you've Won!         |\n" +
                                "|---------------------------------------------|\n" +
                                "   The final result is " + sum + " which is " + winner + ".\n" +
                                "-----------------------------------------------\n");
                        winningClient.writer.flush();


                        losingClient.writer.write(
                                "\n-----------------------------------------------\n" +
                                "|          Bad Luck... you've Lost!           |\n" +
                                "|---------------------------------------------|\n" +
                                "   The final result is " + sum + " which is " + winner + ".\n" +
                                "-----------------------------------------------\n");
                        losingClient.writer.flush();

                        startingClient.changeState(ClientStateEnum.GAME_OVER);
                        nonStartingClient.changeState(ClientStateEnum.GAME_OVER);

                        ClientSession.games.remove(gameId);
                        startingClient.gameId = null;
                        nonStartingClient.gameId = null;

                    }
                } catch (NumberFormatException e) {
                    client.writer.write(
                            "\n-----------------------------------------------\n" +
                            "|  Invalid input. Please enter a number.      |\n" +
                            "-----------------------------------------------\n");
                    client.writer.flush();
                }
                break;
        }
    }
}

