package server;
import java.io.*;
import java.net.*;

public class MultiThreadChatServer {

	// Declaration section:
	// declare a server socket and a client socket for the server
	// declare an input and an output stream

	static Socket clientSocket = null;
	static ServerSocket serverSocket = null;

	// This chat server can accept up to 10 clients' connections

	static clientThread t[] = new clientThread[10];

	public static void main(String args[]) {

		// The default port

		int port_number = 8888;

		if (args.length < 1) {
			System.out.println("Usage: java MultiThreadChatServer "
					+ "Now using port number=" + port_number);
		} else {
			port_number = Integer.valueOf(args[0]).intValue();
		}

		// Initialization section:
		// Try to open a server socket on port port_number (default 8888)
		// Note that we can't choose a port less than 1023 if we are not
		// privileged users (root)

		try {
			serverSocket = new ServerSocket(port_number);
		}// try
		catch (IOException e) {
			System.out.println(e);
		}

		// Create a socket object from the ServerSocket to listen and accept
		// connections.
		// Open input and output streams for this socket will be created in
		// client's thread since every client is served by the server in
		// an individual thread

		// can use a for loop to control the number of clients
		// I have used the while so that we can have unlimited number of clients
		while (true) {
			try {
				clientSocket = serverSocket.accept();
				new clientThread(clientSocket, t).start();
				break;
			}// try

			catch (IOException e) {
				System.out.println(e);
			}
		}
	}
} // class

// This client thread opens the input and the output streams for a particular
// client,
// ask the client's name, informs all the clients currently connected to the
// server about the fact that a new client has joined the chat room,
// and as long as it receive data, echos that data back to all other clients.
// When the client leaves the chat room this thread informs also all the
// clients about that and terminates.

class clientThread extends Thread {

	DataInputStream is = null;
	PrintStream os = null;
	Socket clientSocket = null;
	clientThread t[];

	public clientThread(Socket clientSocket, clientThread[] t) {
		this.clientSocket = clientSocket;
		this.t = t;
	}

	public void run() {
		String line;
		String name;
		try {
			is = new DataInputStream(clientSocket.getInputStream());
			os = new PrintStream(clientSocket.getOutputStream());
			os.println("Enter your name.");
			name = is.readLine();
			os.println("Hello " + name
					+ " to our chat room.To leave enter /quit in a new line");
			for (int i = 0; i <= 9; i++)
				if (t[i] != null && t[i] != this)
					t[i].os.println("*** A new user " + name
							+ " entered the chat room !!!***");
			while (true) {
				line = is.readLine();
				if (line.startsWith("/quit"))
					break;
				for (int i = 0; i <= 9; i++)
					if (t[i] != null)
						t[i].os.println("<" + name + "> " + line);
			}
			for (int i = 0; i <= 9; i++)
				if (t[i] != null && t[i] != this)
					t[i].os.println("*** The user " + name
							+ " is leaving the chat room !!! ***");

			os.println("*** Bye " + name + " ***");

			// Clean up:
			// Set to null the current thread variable such that other client
			// could
			// be accepted by the server

			for (int i = 0; i <= 9; i++)
				if (t[i] == this)
					t[i] = null;

			// close the output stream
			// close the input stream
			// close the socket

			is.close();
			os.close();
			clientSocket.close();
		} catch (IOException e) {
		}
	}
}