package auxiliares.reproductor;

import java.util.List;

// PATRÓN COMPOSITE
public class ListaConIndice <E> {
	// Atributos.
	private List<E> lista;	// La lista que vamos a simular que tiene un índice.
	private int indice;
	
	// Constructor.
	public ListaConIndice(List<E> lista) throws Exception
	{
		if(lista == null)
			throw new Exception("La lista no puede ser nula.");
		this.lista = lista;	// No se copia, sino que se toma la referencia para mantenerla actualizada.
		indice = -1;
	}
	
	// Método que devuelve la lista.
	public List<E> getLista()
	{
		return lista;
	}
	
	// Método que devuelve el tamaño de la lista.
	public int getTamano()
	{
		return lista.size();
	}
	
	// Método que devuelve el índice.
	public synchronized int getIndice()
	{
		return indice;
	}
	
	// Método para tomar el siguiente elemento.
	public synchronized E getSiguienteElemento()
	{
		// Solo actualiza y devuelve elemento si puede.
		E elemento = lista.get((indice + 1) % lista.size());
		indice = (indice + 1) % lista.size();
		return elemento;
	}
	
	// Método para tomar el elemento anterior.
	public synchronized E getElementoAnterior() 
	{
		if(indice == 0)
		{
			indice = lista.size();
		}
		
		E elemento = lista.get(indice - 1);
		indice--;
		return elemento;
	}
	
	// Método para tomar el elemento actual.
	public synchronized E getActual() 
	{
		if(lista.size() > 0)
		{
			return lista.get(indice);
		}
		return null;
	}
	
	// Método para ir al primer elemento.
	public synchronized E getPrimero()
	{
		if(lista.size() > 0)
		{
			E elemento = lista.get(0);
			indice = 0;
			return elemento;
		}
		return null;
	}
	
	// Método para ir al último elemento.
	public synchronized E getUltimo()
	{
		if(lista.size() > 0)
		{
			E elemento = lista.get(lista.size() - 1);
			indice = lista.size() - 1;
			return elemento;
		}
		return null;
	}
	
	@Override
	public boolean equals(Object obj) 
	{
		if(obj == null)
			return false;
		if(this == obj)
			return true;
		if(obj.getClass() != this.getClass())
			return false;
		
		ListaConIndice<E> objeto = (ListaConIndice)obj;
		
		if(getTamano() != objeto.getTamano())
			return false;
		
		List<E> listaObjeto = objeto.getLista();
		for(int i = 0; i < getTamano(); i++)
		{
			if(!this.lista.get(i).equals(listaObjeto.get(i)))
				return false;
		}
		return true;
	}
}
