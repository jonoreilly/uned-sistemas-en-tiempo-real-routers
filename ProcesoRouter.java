public class ProcesoRouter extends Thread {
	private BufferMensajes bufferEntrada;
	private BufferMensajes[] buffersSalida;
	// Para estadisticas
	private boolean estaProcesandoMensaje = false;
	private String nombre;
	
	/**
	 * @param bufferEntrada Buffer de mensajes de entrada
	 * @param buffersSalida Lista de buffers de mensajes de salida, ordenados por prioridad
	 * @param nombre Nombre del proceos para estadisticas
	 */
	public ProcesoRouter(BufferMensajes bufferEntrada, BufferMensajes[] buffersSalida, String nombre) {
		this.bufferEntrada = bufferEntrada;
		this.buffersSalida = buffersSalida;
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
				
				// Primer intento de transferencia del mensaje a una de las lineas
				
				boolean mensajeTransferido = false;
				
				for (BufferMensajes bufferSalida : buffersSalida) {
					if (!bufferSalida.getIsLleno()) {
						this.estaProcesandoMensaje = false;

						Estadisticas.log(this.nombre + " -> " + bufferSalida.getNombre() + ": " + mensaje);
						
						bufferSalida.meterMensaje(mensaje);						
						
						mensajeTransferido = true;
						
						break;
					}
				}
				
				if (mensajeTransferido) {
					continue;
				}

				// Todas las lineas estan saturadas
				
				Estadisticas.log("Todas las lineas estan saturadas, " + this.nombre + " a la espera: " + mensaje);

				// Usamos este monitor para notificarnos cuando alguna linea tenga espacio
				Object monitor = new Object();

				synchronized (monitor) {
					// Creamos un notificador por cada linea
					for (BufferMensajes bufferSalida : buffersSalida) {
						Thread notificador = new Thread(
							new Runnable() { 
								public void run() {
									try {				
										synchronized (bufferSalida) {
											while (bufferSalida.getIsLleno()) {
												bufferSalida.wait();
											}
		
											synchronized (monitor) {
												monitor.notifyAll();
											}	
										}
									} catch (InterruptedException e) {
										Estadisticas.log("## Error ProcesoRouter monitor");
	
										e.printStackTrace();
									}
								}
							}
						);
						
						notificador.start();
					}

					// Esperamos a notificaciones de cambio en las lineas e intentamos transferir el mensaje a una de ellas
					while(true) {
						monitor.wait();
						
						for (BufferMensajes bufferSalida : buffersSalida) {
							if (!bufferSalida.getIsLleno()) {
								this.estaProcesandoMensaje = false;

								Estadisticas.log(this.nombre + " -> " + bufferSalida.getNombre() + ": " + mensaje);
								
								bufferSalida.meterMensaje(mensaje);

								mensajeTransferido = true;
								
								break;
							}
						}
						
						if (mensajeTransferido) {
							break;
						}
		            }	
				}
			} catch(Exception e) {
				Estadisticas.log("## Error ProcesoRouter " + this.nombre);

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
