package interfaz;

import javafx.scene.Node;

public class ThreadVotar extends Thread {
	private Principal principal;
	private String cedula;
	private String nombre;
	private Node nuevo;
	
	public ThreadVotar(Principal principal,String cedula,String nombre,Node nuevo) {
		this.principal = principal;
		this.cedula = cedula;
		this.nombre = nombre;
		this.nuevo = nuevo;
	}
	
	public void run() {
		principal.votar(cedula, nombre,nuevo);
	}

}
