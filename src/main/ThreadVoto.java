package main;

import java.io.IOException;

public class ThreadVoto extends Thread{

	Conexion conexion;
	Main main;
	String cedula;
	String departamento;
	String municipio;
	
	public ThreadVoto(String cedula, Conexion con, Main main,String municipio,String departamento) {
		this.conexion = con;
		this.main = main;
		this.cedula = cedula;
		this.municipio = municipio;
		this.departamento = departamento;
	}
	
	public void run() {
		try {
			String msg = conexion.getIn().readLine();
			if (msg.startsWith("VOTO:")) {
				msg = msg.replaceFirst("VOTO:", "");
				msg = main.desEncriptar(msg);
				msg = msg.replaceFirst(cedula, "");
				main.agregarVoto(msg,municipio,departamento);
				conexion.finVoto();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
