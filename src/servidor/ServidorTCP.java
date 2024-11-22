package servidor;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;

/**
 * TODO: Complementa esta clase para que acepte conexiones TCP con clientes para
 * recibir un boleto, generar la respuesta y finalizar la sesion
 */
public class ServidorTCP {
	private Socket socketCliente;
	private ServerSocket socketServidor;
	private BufferedReader entrada;
	private PrintWriter salida;

	private String[] respuesta;
	private int[] combinacion;
	private int reintegro;
	private int complementario;

	/**
	 * Constructor
	 */
	public ServidorTCP(int puerto) {
		try {
			socketServidor = new ServerSocket(puerto);
			System.out.println("Esperando conexión...");
			socketCliente = socketServidor.accept();
			System.out.println("Conexión aceptada: " + socketCliente);
			entrada = new BufferedReader(new InputStreamReader(socketCliente.getInputStream()));
			salida = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socketCliente.getOutputStream())), true);
		} catch (IOException e) {
			System.out.println("No puede escuchar en el puerto: " + puerto);
			System.exit(-1);
		}
		this.respuesta = new String[9];
		this.respuesta[0] = "Boleto inv�lido - N�meros repetidos";
		this.respuesta[1] = "Boleto inv�lido - n�meros incorretos (1-49)";
		this.respuesta[2] = "6 aciertos";
		this.respuesta[3] = "5 aciertos + complementario";
		this.respuesta[4] = "5 aciertos";
		this.respuesta[5] = "4 aciertos";
		this.respuesta[6] = "3 aciertos";
		this.respuesta[7] = "Reintegro";
		this.respuesta[8] = "Sin premio";
		generarCombinacion();
		imprimirCombinacion();
	}

	/**
	 * @return Debe leer la combinacion de numeros que le envia el cliente
	 */
	public String leerCombinacion() {
		try {
			return entrada.readLine();
		} catch (IOException e) {
			return "No se ha podido leer correctamente";
		}

	}

	/**
	 * @return Debe devolver una de las posibles respuestas configuradas
	 */
	public String comprobarBoleto(String linea) {

		String[] num = linea.split(" ");

		int[] boleto = new int[6];
		for (int i = 0; i < 6; i++) {
			boleto[i] = Integer.parseInt(num[i]);
			if (boleto[i] < 1 || boleto[i] > 49) {
				return this.respuesta[1];
			}
		}

		for (int i = 0; i < boleto.length; i++) {
			for (int j = i + 1; j < boleto.length; j++) {
				if (boleto[i] == boleto[j]) {
					return this.respuesta[0];
				}
			}
		}
		return acierto(boleto);
		


	}
	
	public String acierto(int[] boleto) {
		int aciertos = 0;
		for (int i = 0; i < boleto.length; i++) {
			for (int j = 0; j < combinacion.length; j++) {
				if (boleto[i] == combinacion[j]) {
					aciertos++;
				}
			}
		}
		if (aciertos == 6) {
			return this.respuesta[2];
		} else if (aciertos == 5) {
			return this.respuesta[4];
		} else if (aciertos == 4) {
			return this.respuesta[5];
		} else if (aciertos == 3) {
			return this.respuesta[6];
		}
		return this.respuesta[8];

	}

	/**
	 * @param respuesta se debe enviar al ciente
	 */
	public void enviarRespuesta(String respuesta) {
		salida.println(respuesta);
	}

	/**
	 * Cierra el servidor
	 */
	public void finSesion() {
		try {
			socketCliente.close();
			socketServidor.close();
			entrada.close();
			salida.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Metodo que genera una combinacion. NO MODIFICAR
	 */
	private void generarCombinacion() {
		Set<Integer> numeros = new TreeSet<Integer>();
		Random aleatorio = new Random();
		while (numeros.size() < 6) {
			numeros.add(aleatorio.nextInt(49) + 1);
		}
		int i = 0;
		this.combinacion = new int[6];
		for (Integer elto : numeros) {
			this.combinacion[i++] = elto;
		}
		this.reintegro = aleatorio.nextInt(49) + 1;
		this.complementario = aleatorio.nextInt(49) + 1;
	}

	/**
	 * Metodo que saca por consola del servidor la combinacion
	 */
	private void imprimirCombinacion() {
		System.out.print("Combinaci�n ganadora: ");
		for (Integer elto : this.combinacion)
			System.out.print(elto + " ");
		System.out.println("");
		System.out.println("Complementario:       " + this.complementario);
		System.out.println("Reintegro:            " + this.reintegro);
	}

}
