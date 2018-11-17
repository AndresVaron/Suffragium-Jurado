package main;

import java.io.IOException;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import javax.net.ssl.HttpsURLConnection;

import interfaz.Principal;

public class Main extends Thread {

	private static final String VALORES = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

	private ServerSocket receptor;
	private int nConexiones;
	private String ip;
	private int port;
	private ArrayList<Conexion> conexiones;
	private Principal interfaz;
	private byte[] llave;
	private Bag<String> votos;
	private int estado;

	private Conexion conVotos;

	private ArrayList<String> lista = new ArrayList<String>();

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
		estado = 0;
		conexiones = new ArrayList<Conexion>();
		votos = new Bag<String>();
		try {
			receptor = new ServerSocket(0);
		} catch (IOException e) {
			e.printStackTrace();
		}
		port = receptor.getLocalPort();
		
		//Actualizar la base de datos
		String url = "http://157.253.238.75:80/Suffragium/api/jurado?dir=" + ip + ":" + port+"&key="+key;
		URL obj;
		try {
			obj = new URL(url);
			HttpURLConnection con = (HttpURLConnection) obj.openConnection();
			con.setRequestMethod("PUT");
			con.setRequestProperty("User-Agent", "Mozilla/5.0");
			con.getResponseCode();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void run() {
		try {
			// Aqui se le manda a la interfaz la ip:puerto y la llave para conectarse.
			while (!fin()) {
				Socket socketConexion = receptor.accept();
				Conexion conexion = new Conexion(socketConexion, this);
				String msg = conexion.getIn().readLine();
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
						// No caben mas maquinas de votacion
						conexion.getOut().println("ERROR");
						socketConexion.close();
					}
				} else if (msg.startsWith("LECTURACEDULA:")) {
					String info = msg.replaceFirst("LECTURACEDULA:", "");
					if (!lista.contains(info.split(",")[1]) && estado == 1) {
						lista.add(info.split(",")[1]);
						interfaz.confirmarIdentidad(info.split(",")[0], info.split(",")[1], info.split(",")[2],
								info.split(",")[3], info.split(",")[4], info.split(",")[5]);
					}
					socketConexion.close();
				} else if (msg.startsWith("VOTOS")) {
					conVotos = conexion;
				} else {
					// Protocolo no existe
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
		if (estado == -1) {
			return true;
		}
		return false;
	}

	public synchronized void empezar() {
		estado = 1;
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

	public String votar(String cedula, String municipio, String departamento) {
		Conexion con = null;
		int rand = 0;
		if (conexiones.size() > 0) {
			// Esperar hasta que una casilla se muestre.
			while (con == null || con.votando()) {
				rand = (int) (Math.random() * (conexiones.size()));
				con = conexiones.get(rand);
			}
			con.votar(cedula, municipio, departamento);
		}
		return "" + (rand + 1);
	}

	public synchronized void finalizarVotos() {
		estado = -1;
		while (conexiones.size() > 0) {
			for (int i = 0; i < conexiones.size(); i++) {
				if (conexiones.get(i).votando() == false) {
					conexiones.get(i).getOut().println("FIN");
					conexiones.remove(i);
				}
			}
		}
	}

	public synchronized void agregarVoto(String voto, String municipio, String departamento) {
		votos.add(voto);
		if (conVotos != null) {
			conVotos.getOut().println(voto);// Mandar Voto al servidor.
		}
		String url = "http://157.253.238.75:80/Suffragium/api/votos?voto="+voto.trim()+"&municipio="+municipio.trim()+"&departamento="+departamento.trim();
		URL obj;
		try {
			obj = new URL(url);
			HttpURLConnection con = (HttpURLConnection) obj.openConnection();
			con.setRequestMethod("POST");
			con.setRequestProperty("User-Agent", "Mozilla/5.0");
			con.getResponseCode();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void noVotar(String cedula) {
		lista.remove(cedula);
	}

}