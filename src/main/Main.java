package main;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

import interfaz.Principal;

public class Main {

	private static final String VALORES = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

	private ServerSocket receptor;

	private String ip;
	private int port;
	private boolean start;
	private ArrayList<Conexion> conexiones;
	private int nConexiones;
	private Principal interfaz;
	private byte[] llave;
	private Bag<String> votos;

	public Main(int nConexiones, Principal interfaz) { // Entra por parametro la interfaz.
		this.interfaz = interfaz;
		this.nConexiones = nConexiones;
		try {
			ip = InetAddress.getLocalHost().getHostAddress();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		String key = generarLlave(16);
		setKey(key);
		start = false;
		conexiones = new ArrayList<Conexion>();
		votos = new Bag<String>();
		try {
			receptor = new ServerSocket(0);
		} catch (IOException e) {
			e.printStackTrace();
		}
		port = receptor.getLocalPort();
	}

	public void Empezar() {
		try {
			// Aqui se le manda a la interfaz la ip:puerto y la llave para conectarse.
			while (!getInicio() && conexiones.size() < nConexiones) {
				Socket socketConexion = receptor.accept();
				Conexion conexion = new Conexion(socketConexion, this);
				String msg = conexion.getIn().readLine();
				if (msg.startsWith("INICIAR:")) {
					String llav = desEncriptar(msg.replaceFirst("INICIAR:", ""));
					if (llav.equals(new String(llave))) {
						String x = "CONFIRMACION:" + encriptar("" + socketConexion.getLocalPort());
						conexion.getOut().println(x);
						conexiones.add(conexion);
						interfaz.conexionRealizada(conexion);
					} else {
						conexion.getOut().println("ERROR");
						socketConexion.close();
					}
				} else {
					conexion.getOut().println("ERROR");
					socketConexion.close();
				}
			}

		} catch (IOException e) {

		} finally {
			try {
				receptor.close();
			} catch (IOException e2) {
			}
		}
	}

	public synchronized void iniciarVotaciones() {
		start = true;
	}

	public synchronized boolean getInicio() {
		return start;
	}

	public String getInfo() {
		return "" + ip + ":" + port;
	}

	public String getLlave() {
		return new String(llave);
	}

	/**
	 * Metodo que crea la llave para conectarse a la aplicacion
	 * 
	 * @param tam
	 * @return
	 */
	public String generarLlave(int tam) {
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < tam; i++) {
			int character = (int) (Math.random() * VALORES.length());
			builder.append(VALORES.charAt(character));
		}
		return builder.toString();
	}

	public void setKey(String key) {
		llave = key.getBytes();
	}

	public String encriptar(String msg) {
		return msg;
	}

	public String desEncriptar(String msg) {
		return msg;
	}

	public synchronized String votar(String cedula) {
		int rand = (int) (Math.random() * (conexiones.size()));
		Conexion con = conexiones.get(rand);
		con.votar(cedula);
		return "" + rand;
	}

	public void finalizarVotos() {
		while (conexiones.size() > 0) {
			for (int i = 0; i < conexiones.size(); i++) {
				if (conexiones.get(i).votando() == false) {
					conexiones.get(i).getOut().println("FIN");
					conexiones.remove(i);
				}
			}
		}
	}

	public synchronized void agregarVoto(String voto) {
		votos.add(voto);

		// Decidir como mandar el voto a la base de datos.
	}

}