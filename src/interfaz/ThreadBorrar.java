package interfaz;

import javafx.scene.Node;

public class ThreadBorrar extends Thread {

	Principal principal;
	Node node;

	public ThreadBorrar(Principal principal, Node node) {
		this.principal = principal;
		this.node = node;
	}

	public void run() {
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		principal.borrar(node);
	}

}
