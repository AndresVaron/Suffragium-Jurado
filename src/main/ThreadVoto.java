package main;

import java.io.IOException;

public class ThreadVoto extends Thread{

	Conexion conexion;
	Main main;
	String cedula;
	
	public ThreadVoto(String cedula, Conexion con, Main main) {
		this.conexion = con;
		this.main = main;
		this.cedula = cedula;
	}
	
	public void run() {
		try {
			String msg = conexion.getIn().readLine();
			if (msg.startsWith("VOTO:")) {
				msg.replaceFirst("VOTO:", "");
				msg = main.desEncriptar(msg);
				msg.replaceFirst(cedula, "");
				main.agregarVoto(msg);
				conexion.finVoto();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
