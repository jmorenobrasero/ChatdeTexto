package sockets.ejemplo2.clases;

import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Scanner;

public class Escribir extends Thread {

	private PrintStream escritor;

	public Escribir(OutputStream osEscritor) {
		this.escritor = new PrintStream(osEscritor);
	}

	@Override
	public void run() {
		try (Scanner sc = new Scanner(System.in)) {
			String mensaje = null;
			while (!(mensaje = sc.nextLine()).equals("")) {
				if (Thread.interrupted()) {
					throw new InterruptedException();
				}
				escritor.println(mensaje);
			}
		} catch (InterruptedException e) {
			System.out.println("Se ha terminado la comunicación");
		}
	}
}
