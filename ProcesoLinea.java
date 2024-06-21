
public class ProcesoLinea extends Thread {
	private BufferMensajes bufferEntrada;
	// Para estadisticas
	private boolean estaProcesandoMensaje = false;
	private String nombre;
	
	/**
	 * @param bufferEntrada Buffer de mensajes de entrada
	 * @param nombre Nombre del proceos para estadisticas
	 */
	public ProcesoLinea(BufferMensajes bufferEntrada, String nombre) {
		this.bufferEntrada = bufferEntrada;
		this.nombre = nombre;
	}
	
	public void run() {
		while (true) {
			try {
				// Sacamos el mensaje del buffer
				
				String mensaje = this.bufferEntrada.sacarMensaje();
				
				this.estaProcesandoMensaje = true;
				
				Estadisticas.log(this.bufferEntrada.getNombre() + " -> " + this.nombre + ": " + mensaje);
				
				Estadisticas.mostrarEstadisticas();
				
				// Enviamos el mensaje

				// Simular retardo 5 - 10 segundos para el procesamiento del mensaje
				int retardo = (int)(5000 + (5000 * Math.random())); 
				Thread.sleep(retardo);
				
				this.estaProcesandoMensaje = false;
				
				Estadisticas.contarSalida();

				Estadisticas.log(this.nombre + " -> salida: " + mensaje);

				Estadisticas.mostrarEstadisticas();
				
			} catch(Exception e) {
				Estadisticas.log("## Error ProcesoLinea");

				e.printStackTrace();
			}
		}
	}
	
	// Para estadisticas
	
	public String getNombre() {
		return this.nombre;
	}

	public boolean getEstaProcesandoMensaje() {
		return this.estaProcesandoMensaje;
	}
}
