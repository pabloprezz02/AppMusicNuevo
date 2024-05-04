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
	public int getIndice()
	{
		return indice;
	}
	
	// Método setIndice.
	public void setIndice(int indice)
	{
		int tamanoLista = lista.size();
		if(indice < 0 || indice > getTamano() - 1)
			return;
		this.indice = indice;
	}
	
	// Añadir elemento. Se actualiza el índice.
	public void addElemento(E elemento)
	{
		lista.add(elemento);
		indice = lista.size() - 1;	// Se sitúa el índice en la última Canción.
	}
	
	// Eliminar primer elemento.
	public void eliminarPrimero()
	{
		lista.remove(0);
		indice--;
	}
	
	// Método para tomar el siguiente elemento.
	public E getSiguienteElemento()
	{
		// Solo actualiza y devuelve elemento si puede.
		E elemento = lista.get((indice + 1) % lista.size());
		indice++;
		return elemento;
//		if(lista.size() > indice)
//		{
//			
//		}
//		return null;
	}
	
	// Método para tomar el elemento anterior.
	public E getElementoAnterior() 
	{
		if(indice == 0)
		{
			indice = lista.size();
		}
		
		E elemento = lista.get(indice - 1);
		indice--;
		return elemento;
//		// Solo actualiza y devuelve elemento si puede.
//		if(0 < indice && lista.size() > 0)
//		{
//			E elemento = lista.get(indice - 1);
//			indice--;
//			return elemento;
//		}
//		return null;
	}
	
	// Método para tomar un elemento dado el índice.
	public E getElemento(int indice)
	{
		if(indice < 0 || indice > lista.size())
			return null;
		E elemento = lista.get(indice);
		setIndice(indice);
		return elemento;
	}
	
	// Método para tomar el elemento actual.
	public E getActual() 
	{
		if(lista.size() > 0)
		{
			return lista.get(indice);
		}
		return null;
	}
	
	// Método para ir al primer elemento.
	public E getPrimero()
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
	public E getUltimo()
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
