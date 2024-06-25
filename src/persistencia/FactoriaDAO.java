package persistencia;

public abstract class FactoriaDAO {
	// SINGLETON
	private static FactoriaDAO unicaInstancia=null;
	
	public static final String DAO_APPMUSIC = "persistencia.APPMUSICFactoriaDAO";
	
	// CONSTRUCTOR
	protected FactoriaDAO() {}
	
	// GET ÚNICA INSTANCIA
	// Solo existe un tipo de FactoriaDAO, así que solo se podría crear un tipo de objeto.
	public static FactoriaDAO getUnicaInstancia(String tipo) throws DAOException {
		if(unicaInstancia==null) {
			try {
				unicaInstancia=(FactoriaDAO) Class.forName(tipo).newInstance();
			}catch(Exception e) {
				throw new DAOException(e.getMessage());
			}
		}
		return unicaInstancia;
	}
	
	public static FactoriaDAO getUnicaInstancia() throws DAOException {
		if(unicaInstancia==null) {
			return getUnicaInstancia(DAO_APPMUSIC);
		}
		return unicaInstancia;
	}
	
	public abstract IAdaptadorUsuarioDAO getAdaptadorUsuario();
	public abstract IAdaptadorPlaylist getAdaptadorPlaylist();
	public abstract IAdaptadorCancion getAdaptadorCancion();
}
