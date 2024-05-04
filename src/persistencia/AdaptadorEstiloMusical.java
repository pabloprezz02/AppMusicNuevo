package persistencia;

import java.util.List;

import modelo.EstiloMusical;
import tds.driver.FactoriaServicioPersistencia;
import tds.driver.ServicioPersistencia;

public class AdaptadorEstiloMusical implements IAdaptadorEstiloMusical {
	// SINGLETON
	private static AdaptadorEstiloMusical unicaInstancia;
	private static ServicioPersistencia servicioPersistencia;
	
	private AdaptadorEstiloMusical() {
		servicioPersistencia=FactoriaServicioPersistencia.getInstance().getServicioPersistencia();
	}
	
	public static AdaptadorEstiloMusical getUnicaInstancia() {
		if(unicaInstancia==null) {
			unicaInstancia=new AdaptadorEstiloMusical();
		}
		return unicaInstancia;
	}

	@Override
	public void registrarEstiloMusical(EstiloMusical estilo) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void modificarEstiloMusical(EstiloMusical estilo) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void eliminarEstiloMusical(EstiloMusical estilo) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public EstiloMusical recuperarEstiloMusical(int codigo) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<EstiloMusical> recuperarTodosEstilosMusicales() {
		// TODO Auto-generated method stub
		return null;
	}
	
}
