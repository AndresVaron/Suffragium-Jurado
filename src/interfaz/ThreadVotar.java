package interfaz;

import javafx.geometry.Bounds;
import javafx.scene.Node;

public class ThreadVotar extends Thread {
	private Principal principal;
	private String cedula;
	private String nombre;
	private Node nuevo;
	private Bounds bounds;

	public ThreadVotar(Principal principal, String cedula, String nombre, Node nuevo, Bounds bounds) {
		this.principal = principal;
		this.cedula = cedula;
		this.nombre = nombre;
		this.nuevo = nuevo;
		this.bounds = bounds;
	}

	public void run() {
		principal.votar(cedula, nombre, nuevo, bounds);
	}

}
