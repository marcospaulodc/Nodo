package Trabalho5;
/**
 * @author Marcos Paulo de Castro
 */

//Classe para estruturação das informações de cada nodo
public class EstruturaNodos 
{
	int id;
	String ipMaquina;
	int porta;

	public EstruturaNodos(int id,String ipMaquina, int porta )
	{
		this.id = id;
		this.ipMaquina = ipMaquina;
		this.porta = porta;
	}
}