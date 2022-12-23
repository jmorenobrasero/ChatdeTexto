package sockets.ejemplo2.lanzador;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import sockets.ejemplo2.clases.Escribir;
import sockets.ejemplo2.clases.Leer;

public class Cliente {

	public static void main(String[] args) {
		try (Socket conexionRemota = new Socket("localhost", 3000)) {
			System.out.println(
					"Conectando a " + conexionRemota.getRemoteSocketAddress() + ":" + conexionRemota.getPort());
			System.out.println("Desde: " + conexionRemota.getLocalAddress() + ":" + conexionRemota.getLocalPort());
			Escribir escritor = new Escribir(conexionRemota.getOutputStream());
			Leer lector = new Leer(conexionRemota.getInputStream());
			lector.start();
			escritor.start();
			boolean escritorCorriendo = false;
			boolean lectorCorriendo = false;
			while ((escritorCorriendo = escritor.isAlive()) && (lectorCorriendo = lector.isAlive())) {
//				Espera activa
//				System.out.println("Espera activa.");
			}
			if (!escritorCorriendo) {
				lector.interrupt();
			} else if (!lectorCorriendo) {
				escritor.interrupt();
			}
		} catch (UnknownHostException e) {
			System.err.println("Error: " + e.getMessage());
		} catch (IOException e) {
			System.err.println("Error: " + e.getMessage());
		}
	}
}
