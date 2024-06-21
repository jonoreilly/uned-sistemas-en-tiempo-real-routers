import java.util.ArrayList;

public class Estadisticas {
	private static ArrayList<ProcesoRouter> procesosRouter = new ArrayList<>();
	private static ArrayList<ProcesoLinea> procesosLinea = new ArrayList<>();
	private static ArrayList<BufferMensajes> buffers = new ArrayList<>();
	private static int contadorEntrada = 0;
	private static int contadorSalida = 0;
	
	// ## Cambiar a true para habilitar estadisticas mas detalladas ##
	public static boolean HABILITAR_ESTADISTICAS = true; 
	
	public static void addProcesoRouterAMedir(ProcesoRouter proceso) {
		Estadisticas.procesosRouter.add(proceso);
	}
	
	public static void addProcesoLineaAMedir(ProcesoLinea proceso) {
		Estadisticas.procesosLinea.add(proceso);
	}
	
	public static void addBufferAMedir(BufferMensajes buffer) {
		Estadisticas.buffers.add(buffer);
	}
	
	public synchronized static void mostrarEstadisticas() {
		if (Estadisticas.HABILITAR_ESTADISTICAS) {
			System.out.println();
			System.out.println("Estadisticas:");
			System.out.println("Entrada: " + Estadisticas.contadorEntrada);
	
			for (ProcesoRouter proceso : Estadisticas.procesosRouter) {
				System.out.println(proceso.getNombre() + " procesando mensajes = " + (proceso.getEstaProcesandoMensaje() ? 1 : 0));
			}
	
			for (ProcesoLinea proceso : Estadisticas.procesosLinea) {
				System.out.println(proceso.getNombre() + " procesando mensajes = " + (proceso.getEstaProcesandoMensaje() ? 1 : 0));
			}
			
			for (BufferMensajes buffer : Estadisticas.buffers) {
				System.out.println(buffer.getNombre() + " = " + buffer.getOcupacion() + " / " + buffer.getCapacidad());
			}
	
			System.out.println("Salida: " + Estadisticas.contadorSalida);
		}
	}
	
	public synchronized static void log(String mensaje) {
		System.out.println();
		System.out.println(mensaje);
	}
	
	public synchronized static void contarEntrada() {
		Estadisticas.contadorEntrada++;
	}

	public synchronized static void contarSalida() {
		Estadisticas.contadorSalida++;
	}
}
