package sockets.ejemplo2.clases;

import java.io.InputStream;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class Leer extends Thread {

	private Scanner lector;

	public Leer(InputStream lector) {
		super();
		this.lector = new Scanner(lector);
	}

	@Override
	public void run() {
		String mensaje = null;
		try {
			while (true) {
				mensaje = lector.nextLine();
				if (Thread.interrupted()) {
					throw new InterruptedException();
				}
				System.out.println(mensaje);
			}
		} catch (NoSuchElementException e) {
			System.out.println("Se cortó la conexión desde el otro extremo. PRESS ENTER TO EXIT.");
		} catch (InterruptedException e) {
			System.out.println("Se finalizó la comunicación");
		}
	}
}
