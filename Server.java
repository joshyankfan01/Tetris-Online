package game;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Server {

	private static ServerSocket server;
	private static ArrayList<Socket> clients = new ArrayList<Socket>();
	private static ArrayList<PrintWriter> outputStreams = new ArrayList<PrintWriter>();
	private static ArrayList<BufferedReader> inputStreams = new ArrayList<BufferedReader>();

	public static void removeAllConnections() {
		for (Socket s : clients) {
			try {
				s.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		clients.clear();
		outputStreams.clear();
		inputStreams.clear();
	}

	private static boolean readInputAndWrite() {
		for (int i = 0; i < inputStreams.size(); i++) {
			String line;
			try {
				if (inputStreams.get(i).ready()) {
					line = inputStreams.get(i).readLine();
					if (line.equals("Game Over")) {
						/*Add outputstream write to be WIN or LOSE to end both games
						 * read these outputs in the gameOver() method in Controller.java
						 * and add the win or lose stuff to the drawGameOver() method in Controller.java
						 */
						return false;
					} else {
						int linesCleared = Integer.parseInt(line);
						for (int j = 0; j < outputStreams.size(); j++) {
							if (i != j) {
								if (linesCleared >= 2) {
									outputStreams.get(j).println(linesCleared);
								}
							}
						}
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return true;

	}

	private static void startGame() {
		for (PrintWriter out : outputStreams) {
			out.println("Start Game");
		}
	}

	public static void main(String[] args) {
		try {
			server = new ServerSocket();
			server.bind(new InetSocketAddress("localhost", 5000));
			System.out.println("Running at " + server.getLocalSocketAddress());

			while (clients.size() < 2) {
				Socket client = server.accept();
				clients.add(client);
				outputStreams.add(new PrintWriter(clients.get(clients.size() - 1).getOutputStream(), true));
				inputStreams.add(
						new BufferedReader(new InputStreamReader(clients.get(clients.size() - 1).getInputStream())));
				outputStreams.get(clients.size() - 1).println("Hello client");
			}

			startGame();

			while (readInputAndWrite()) {
			}

			server.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
