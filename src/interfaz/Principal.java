package interfaz;

import java.util.Scanner;

import main.Conexion;
import main.Main;

public class Principal {

	Scanner reader;
	Main main ;
	public Principal() {
		reader = new Scanner(System.in);
		//Pido numero de maquinas de votacion. 
		// por ahora son 2:
		main = new Main(2,this); //2 es el numero de maquinas de voto que se van a conectar.
		main.getInfo();
		main.getLlave();
		//Imprime la info en la pantalla.
		System.out.println("Conectarse a: "+main.getInfo());
		System.out.println("Con la llave: "+main.getLlave());
		main.Empezar();
		main.votar("123456");
		
	}
	
	public void conexionRealizada(Conexion con) {
		System.out.println("Conexion Realizada:");
		System.out.println(con);
	}
	
	public void msgError(String err) {
		System.out.println(err);
	}
	
	public static void main(String[] args) {
		new Principal();
	}
	
	
	public void iniciar() {
		//Metodo para iniciar antes.
		//Cambiar interfaz para que reciba info de cedula!
	}
	
	public void ingresarVoto() {
		//Remplazar esto con codigo de verificacion de la cedula.
		String ced = reader.nextLine();
		String puesto = main.votar(ced);
		System.out.println("Identidad confirmada.");
		System.out.println("Dirijase al puesto: " + puesto +" para realizar su voto.");
	}

}

