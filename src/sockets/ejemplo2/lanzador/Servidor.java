package sockets.ejemplo2.lanzador;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import sockets.ejemplo2.clases.SocketThread;

public class Servidor {

	public static void main(String[] args) {
		try (ServerSocket serverSocket = new ServerSocket(3000)) {
//			Podemos codificar la comunicación con una clave
			System.out.println(
					"Escuchando por el socket: " + serverSocket.getInetAddress() + ":" + serverSocket.getLocalPort());
			while (true) {
				Socket connection = serverSocket.accept();
				System.out.println(connection.getRemoteSocketAddress() + ":" + connection.getPort());

				// Creo un hilo que procesara el usuario y el destinatario, asi podre tener varias peticiones procesandose a la vez.
				SocketThread newThread = new SocketThread(connection);
				newThread.start();
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
