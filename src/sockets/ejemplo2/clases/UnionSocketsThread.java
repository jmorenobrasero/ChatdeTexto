package sockets.ejemplo2.clases;

import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class UnionSocketsThread extends Thread {

	private static HashMap<String, Socket> tablaUsers = new HashMap<>();
	private Socket emisor;
	private Socket receptor;

	public UnionSocketsThread(Socket emisor, Socket receptor) {
		super();
		this.emisor = emisor;
		this.receptor = receptor;
	}

	public static synchronized void imprimirUsers(Socket userSocket) {
		try (PrintStream psCliente = new PrintStream(userSocket.getOutputStream())) {
			// Tengo un HashMap con los usuarios y su socket y aqui imprimo los usuarios para que el cliente sepa quienes estan conectados.
			psCliente.println("Estos son los usuarios actualmente activos: ");
			for (Map.Entry<String, Socket> entry : tablaUsers.entrySet()) {
				String key = entry.getKey();
				psCliente.println(key);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public static synchronized void eliminarUser(String user) {
		tablaUsers.remove(user);
	}

	public static Socket getUser(String user) {
		return tablaUsers.get(user);
	}

	public static synchronized void setUser(String user, Socket userSocket) {
		tablaUsers.put(user, userSocket);
	}

	@Override
	public void run() {
		try {
			// Creo un hilo para conectar la lectura del emisor al receptor
			ConexionSocketsThread conexionEscrituraEmisor = new ConexionSocketsThread(emisor.getInputStream(),
					receptor.getOutputStream());
			// Aqui a la inversa que arriba. La entrada del receptor va a la salida del emisor
			ConexionSocketsThread conexionLecturaEmisor = new ConexionSocketsThread(receptor.getInputStream(),
					emisor.getOutputStream());
			conexionEscrituraEmisor.start();
			conexionLecturaEmisor.start();
			boolean escritorCorriendo = true;
			boolean lectorCorriendo = true;
			while ((escritorCorriendo = conexionEscrituraEmisor.isAlive())
					&& (lectorCorriendo = conexionLecturaEmisor.isAlive()) && !(Thread.interrupted())) {
//				Espera activa
//				System.out.println("Espera activa.");
			}
			if (!escritorCorriendo) {
				conexionLecturaEmisor.interrupt();
			} else if (!lectorCorriendo) {
				conexionEscrituraEmisor.interrupt();
			}
			System.out.println("La conexion entre " + emisor.getInetAddress() + " y " + receptor.getInetAddress()
					+ " se ha acabado.");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Override
	public int hashCode() {
		return Objects.hash(emisor, receptor);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		UnionSocketsThread other = (UnionSocketsThread) obj;
		return Objects.equals(emisor, other.emisor) && Objects.equals(receptor, other.receptor);
	}

	public void close() {
		this.interrupt();
	}
}
