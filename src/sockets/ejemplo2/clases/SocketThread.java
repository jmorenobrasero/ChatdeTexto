package sockets.ejemplo2.clases;

import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

public class SocketThread extends Thread {

	private static Set<UnionSocketsThread> unionSockets = new HashSet<>();
	private Socket socketCliente;
	private String user;
	private String destination;

	public SocketThread(Socket socketCliente) {
		super();
		this.socketCliente = socketCliente;
		this.user = null;
		this.destination = "";
	}

	public Socket getSocketCliente() {
		return socketCliente;
	}

	public String getUser() {
		return user;
	}

	public String getDestination() {
		return destination;
	}

	@Override
	public void run() {
		try (Scanner lectorUsuario = new Scanner(socketCliente.getInputStream());
				PrintStream escritorUsuario = new PrintStream(socketCliente.getOutputStream())) {
			// Le pido al cliente su usuario.
			escritorUsuario.println("Escriba su usuario: ");

			// Lo leo
			this.user = lectorUsuario.nextLine();
			UnionSocketsThread.setUser(user, socketCliente);

			escritorUsuario.println(" ");

			// Imprimo en el server el usuario actual
			System.out.println("Current user: " + this.user);

			// Imprimo los usuarios activos.
			UnionSocketsThread.imprimirUsers(socketCliente);
			escritorUsuario.println(
					" \nEscriba el usuario con el que desea hablar(Escriba \"refrescar\" para refrescar la lista): ");
			while (this.destination.equalsIgnoreCase("refrescar") || this.destination.equals("")) {
				UnionSocketsThread.imprimirUsers(socketCliente);
				escritorUsuario.println(
						" \nEscriba el usuario con el que desea hablar(Escriba \"refrescar\" para refrescar la lista): ");

				// Me salta una NoSuchElementException y no se por que.
				this.destination = lectorUsuario.nextLine();
			}

			Socket destinationSocket = UnionSocketsThread.getUser(destination);

			// Imprimo en el server el destinatario.
			System.out.println("Destination user: " + this.destination);

			// Creo un hilo que unira los dos sockets.
			UnionSocketsThread unionSocket = new UnionSocketsThread(socketCliente, destinationSocket);

//			Aniado el hilo que une los dos en una lista para comprobar posteriormente si hay otro de union de destinatario a emisor
			unionSockets.add(unionSocket);

			// Compruebo que ambos usuarios quieran conectarse.
			while (!unionSockets.contains(new UnionSocketsThread(destinationSocket, socketCliente))) {
				wait();
			}
			notifyAll();

			// Notifico en el servidor que comienza la comunicacion entre los sockets
			System.out.println("Comienza la comunicacion entre los sockets.");
			unionSocket.start();

			unionSocket.join();
			// Cuando termine la union de los dos quiere decir que la comunicacion se ha
			// cerrado y elmimino de la lista el hilo de union de sockets y elimino del HashMap al usuario.
			unionSockets.remove(unionSocket);

			UnionSocketsThread.eliminarUser(user);
		} catch (IOException | InterruptedException e) {
			System.out.println("La comunicacion se ha cerrado.");
		}
	}

}
