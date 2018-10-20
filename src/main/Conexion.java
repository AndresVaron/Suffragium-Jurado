package main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Conexion{

	private PrintWriter out;
	private BufferedReader in;
	private Main main;
	private Socket sock;
	private boolean votando;

	public Conexion(Socket sock, Main main) throws IOException {
		this.sock = sock;
		this.main = main;
		out = new PrintWriter(sock.getOutputStream(), true);
		in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
		votando = false;
	}

	public PrintWriter getOut() {
		return out;
	}

	public BufferedReader getIn() {
		return in;
	}

	public Socket getSock() {
		return sock;
	}

	public synchronized void iniciarVoto() {
		votando = true;
	}
	
	public synchronized void finVoto() {
		votando = false;
	}
	public synchronized boolean votando() {
		return votando;
	}
	
	public void votar(String cedula) {
		///Crea un thread que espera la respuesta;
		out.println("HABILITAR:"+main.encriptar(cedula));
		iniciarVoto();
		ThreadVoto t = new ThreadVoto(cedula,this,main);
		t.start();
	}
	
	
}
