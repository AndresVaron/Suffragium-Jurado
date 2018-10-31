package interfaz;


public class ThreadEspera extends Thread{
	
	Principal principal;
	
	public ThreadEspera(Principal principal) {
		this.principal = principal;
	}
	
	public void run() {
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		principal.threadEmpezar();
	}

}
