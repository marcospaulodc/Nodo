package Trabalho5;
/**
 * @author Marcos Paulo de Castro
 */

import java.util.List;

public class Nodo 
{
	NodoModel dadosNodo = new NodoModel();

	public Nodo(int id, List<EstruturaNodos> listaNodos, boolean primeiroLider)
	{
			this.dadosNodo.id  = id;
			this.dadosNodo.listaEstruturaNodos = listaNodos;
			this.dadosNodo.ip = listaNodos.get(id).ipMaquina;
			this.dadosNodo.portTCP = listaNodos.get(id).porta;
			this.dadosNodo.portUDP = listaNodos.get(id).porta;
			this.dadosNodo.primeiroNodo = primeiroLider;

			if(dadosNodo.primeiroNodo)
			{
				this.dadosNodo.lider = true;
				this.dadosNodo.possuiToken = true;
			}
			
			dadosNodo.nodoFluxo.start();
			
			new NodoTCPServidor(dadosNodo).start();
			
			new NodoUDPServidor(dadosNodo).start();
	}
}
