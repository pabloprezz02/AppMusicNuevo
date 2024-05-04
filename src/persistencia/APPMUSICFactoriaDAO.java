package persistencia;

public class APPMUSICFactoriaDAO extends FactoriaDAO {
	public APPMUSICFactoriaDAO() {}
	
	@Override
	public IAdaptadorUsuarioDAO getAdaptadorUsuario() {
		return AdaptadorUsuario.getUnicaInstancia();
	}
	
	@Override
	public IAdaptadorPlaylist getAdaptadorPlaylist() {
		return AdaptadorPlaylist.getUnicaInstancia();
	}
	
	@Override
	public IAdaptadorCancion getAdaptadorCancion() {
		return AdaptadorCancion.getUnicaInstancia();
	}
}
