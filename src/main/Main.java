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
	private int nConexiones;
	private String ip;
	private int port;
	private ArrayList<Conexion> conexiones;
	private Principal interfaz;
	private byte[] llave;
	private Bag<String> votos;
	private boolean fin;

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
		fin = false;
		conexiones = new ArrayList<Conexion>();
		votos = new Bag<String>();
		try {
			receptor = new ServerSocket(8085);
		} catch (IOException e) {
			e.printStackTrace();
		}
		port = receptor.getLocalPort();
	}

	public void votaciones() {
		try {
			// Aqui se le manda a la interfaz la ip:puerto y la llave para conectarse.
			while (!fin) {
				Socket socketConexion = receptor.accept();
				Conexion conexion = new Conexion(socketConexion, this);
				String msg = conexion.getIn().readLine();
				System.out.println(msg);
				if (msg.startsWith("INICIAR:")) {
					if (nConexiones > conexiones.size()) {
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
						//No caben mas maquinas de votacion
						conexion.getOut().println("ERROR");
						socketConexion.close();
					}
				} else if (msg.startsWith("LECTURACEDULA:")) {
					System.out.println("OK");
					String info = msg.replaceFirst("LECTURACEDULA:", "");
					interfaz.confirmarIdentidad(info.split(",")[0], info.split(",")[1], info.split(",")[2],
							info.split(",")[3]);
				} else {
					//Protocolo no existe
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

	public synchronized boolean fin() {
		return fin;
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

	public String votar(String cedula) {
		Conexion con = null;
		int rand = 0;
		// Esperar hasta que una casilla se muestre.
		while (con == null || con.votando()) {
			rand = (int) (Math.random() * (conexiones.size()));
			con = conexiones.get(rand);
		}
		con.votar(cedula);
		return "" + rand;
	}

	public synchronized void finalizarVotos() {
		fin = true;
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