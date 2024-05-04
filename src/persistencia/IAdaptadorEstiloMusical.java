package persistencia;

import java.util.List;

import modelo.EstiloMusical;

public interface IAdaptadorEstiloMusical {
	public void registrarEstiloMusical(EstiloMusical estilo);
	public void modificarEstiloMusical(EstiloMusical estilo);
	public void eliminarEstiloMusical(EstiloMusical estilo);
	public EstiloMusical recuperarEstiloMusical(int codigo);
	public List<EstiloMusical> recuperarTodosEstilosMusicales();
}
