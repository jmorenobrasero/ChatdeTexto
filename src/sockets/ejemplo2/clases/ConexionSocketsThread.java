package sockets.ejemplo2.clases;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class ConexionSocketsThread extends Thread {

	private DataInputStream lectorExterno;
	private DataOutputStream escritorExterno;

	public ConexionSocketsThread(InputStream lectorExterno, OutputStream escritorExterno) {
		super();
		this.lectorExterno = new DataInputStream(lectorExterno);
		this.escritorExterno = new DataOutputStream(escritorExterno);
	}

	@Override
	public void run() {
		String mensaje = null;
		try {
			// Lo que lee de uno se lo escribe al otro.
			while (true) {
				mensaje = lectorExterno.readUTF();
				if (Thread.interrupted()) {
					throw new InterruptedException();
				}
				escritorExterno.writeUTF(mensaje);
			}
		} catch (EOFException e) {
			System.out.println("Se cortó la conexión desde el otro extremo.");
		} catch (IOException e) {
			System.out.println("La comunicacion se ha cerrado.");
		} catch (InterruptedException e) {
			System.out.println("Se finalizó la comunicación");
		}
	}
}
