package persistencia;

import java.util.List;

public interface Adaptador {
	// REGISTRO DE UN OBJETO
	public void registrar(Object objeto);
	// ELIMINACIÓN DE UN OBJETO
	public void modificar(Object objeto);
	// RECUPERACIÓN DE UN OBJETO
	public Object recuperar(int codigo);
	// RECUPERACIÓN DE TODOS LOS OBJETOS
	public List<Object> recuperarTodos();
	// ELIMINAR UN OBJETO
	public void eliminar(Object objeto);
}
