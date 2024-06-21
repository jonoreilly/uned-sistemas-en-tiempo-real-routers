
public class BufferMensajes {
	private String[] mensajes;
	private int capacidad;
	private String nombre;
	private int porSacar = 0; // Indica cuantos elementos han sido introducidos y aun no se han sacado
	private int indexSacar = 0; // Apunta al proximo elemento por sacar del buffer
	
	/**
	 * Buffer de mensajes ordenado como una cola (FIFO)
	 * @param capacidad Maximo numero de elementos que caben en el buffer
	 * @param nombre Nombre del buffer para estadisticas
	 */
	public BufferMensajes(int capacidad, String nombre) {
		this.mensajes = new String[capacidad];
		this.capacidad = capacidad;
		this.nombre = nombre;
	}

	public boolean getIsLleno() {
		return this.porSacar == this.capacidad;  
	}

	public boolean getIsVacio() {
		return this.porSacar == 0;  
	}

	public synchronized void meterMensaje(String mensaje) throws InterruptedException {
		while (this.getIsLleno()) {
			wait();
		}
		
		int indexMeter = (this.indexSacar + this.porSacar) % this.capacidad;
		
		mensajes[indexMeter] = mensaje; 
		
		this.porSacar++;
		
		Estadisticas.mostrarEstadisticas();
		
		notifyAll();
	}
	
	public synchronized String sacarMensaje() throws InterruptedException {
		while (this.getIsVacio()) {
			wait();
		}
		
		String mensaje = mensajes[this.indexSacar];
		
		this.indexSacar = (this.indexSacar + 1) % this.capacidad;
		
		this.porSacar--;
		
		notifyAll();
		
		return mensaje;
	}
	
	// Para estadisticas
	
	public int getCapacidad() {
		return this.capacidad;
	}
	
	public int getOcupacion() {
		return this.porSacar;
	}
	
	public String getNombre() {
		return this.nombre;
	}
}
