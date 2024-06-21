import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * Sistemas en tiempo real - PEC 2 
 * @author Jon Oreilly del Cerro
 */

public class Main {
	public static void main(String[] args) throws Exception {
		// ## Antes de empezar ##
		// 
		// Este programa es la solución al ejercicio 1 de la PEC 2 de "Sistemas en tiempo real".
		//
		// Hay 3 componentes principales:
		//
		//   - ProcesoRouter: implementa un router que lee mensajes de un buffer de entrada y se 
		//        los pasa a uno de sus bufferes de salida. Si todos los bufferes de salida están
		//        llenos el router esperará a que uno se libre para pasarle el mensaje.
		//
		//   - ProcesoLinea: simula una linea que lee mensajes de un buffer de entrada y los manda
		//        a un sistema externo. Tiene un retardo simulado de entre 5 y 10 segundos.
		//
		//   - BufferMensajes: implementa un simple buffer FIFO (o de cola) que almacena mensajes
		//        entre dos de los sistemas. Tiene una capacidad máxima configurable. Las lecturas
		//        destruyen el dato (pop), y tanto las lecturas como las escrituras son atómicas.
		//
		// En la configuración actual, hay 1 router y 3 lineas, y cada uno de los dispositivos tiene
		// un buffer de entrada propio. Pero esto no es estrictamente necesario. Se puede configurar 
		// para que varios sistemas compartan un mismo buffer, o para que haya varios routers que
		// pasen sus mensajes a distintas (o las mismas) lineas.
		//
		// Para facilitar el depuramiento y verificación del programa, tenemos el módulo de Estadisticas.
		// Esta clase se utiliza para imprimir por pantalla el estado de todos los bufferes y sistemas
		// del programa a cada paso del proceso del paso de mensajes. Cada transferencia entre dos 
		// sistemas se ilustra como " origen -> destino : mensaje ", y viene acompañado de una 
		// descripción detallada de la cantidad de mensajes que hay en cada sistema. Puede desactivar
		// esta descripción con la bandera Estadisticas.HABILITAR_ESTADISTICAS.
		//
		// Respecto a la introducción de mensajes en el sistema, se pueden crear nuevos mensajes 
		// escribiendo directamente en la consola, generando un mensaje por cada linea. También hay 
		// un segmento de código (un poco más abajo) que genera 40 mensajes iniciales.
		//
		
		BufferMensajes bufferRouter = new BufferMensajes(100, "buffer Router");
		BufferMensajes bufferLineaA = new BufferMensajes(10, "buffer Linea A");
		BufferMensajes bufferLineaB = new BufferMensajes(10, "buffer Linea B");
		BufferMensajes bufferLineaC = new BufferMensajes(10, "buffer Linea C");
		
		ProcesoRouter procesoRouter = new ProcesoRouter(
			bufferRouter, 
			new BufferMensajes[]{ bufferLineaA, bufferLineaB, bufferLineaC }, 
			"proceso Router 1"
		);
		
		ProcesoLinea procesoLineaA = new ProcesoLinea(bufferLineaA, "proceso Linea A");
		ProcesoLinea procesoLineaB = new ProcesoLinea(bufferLineaB, "proceso Linea B");
		ProcesoLinea procesoLineaC = new ProcesoLinea(bufferLineaC, "proceso Linea C");
		
		// Estadisticas
		Estadisticas.addProcesoRouterAMedir(procesoRouter);
		Estadisticas.addProcesoLineaAMedir(procesoLineaA);
		Estadisticas.addProcesoLineaAMedir(procesoLineaB);
		Estadisticas.addProcesoLineaAMedir(procesoLineaC);
		Estadisticas.addBufferAMedir(bufferRouter);
		Estadisticas.addBufferAMedir(bufferLineaA);
		Estadisticas.addBufferAMedir(bufferLineaB);
		Estadisticas.addBufferAMedir(bufferLineaC);
		
		procesoRouter.start();
		procesoLineaA.start();
		procesoLineaB.start();
		procesoLineaC.start();
				
		// Mensajes iniciales: inicializa el sistema con la cantidad deseada de mensajes 
		// (ajuste esto a un número mas asequible al test que desea realizar)
		
		int cantidadMensajesIniciales = 40;
		
		for (int i = 1; i < cantidadMensajesIniciales; i++) {
	        Main.meterMensaje(bufferRouter, "mensaje " + i);
		}
		
		// Introducir mensajes mediante la linea de comandos para pasarlos al router
		while(true) {
	        BufferedReader reader = new BufferedReader(
	            new InputStreamReader(System.in));

	        String mensaje = reader.readLine();
	        
	        Main.meterMensaje(bufferRouter, mensaje);
		}
	}
	
	private static void meterMensaje(BufferMensajes bufferRouter, String mensaje) {
		Estadisticas.contarEntrada();

		Estadisticas.log("entrada -> " + bufferRouter.getNombre() + ": " + mensaje);

        try {
			bufferRouter.meterMensaje(mensaje);
		} catch (InterruptedException e) {
			Estadisticas.log("## Error Main meterMensaje: " + mensaje);
			
			e.printStackTrace();
		}
	}
}
