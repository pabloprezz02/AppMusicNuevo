package descuento;

public class FactoriaDescuento {
	// Singleton.
	private static FactoriaDescuento unicaInstancia = null;
	
	private FactoriaDescuento() {}
	
	public static FactoriaDescuento getUnicaInstancia()
	{
		if(unicaInstancia == null)
			unicaInstancia = new FactoriaDescuento();
		return unicaInstancia;
	}
	
	public Descuento crearDescuento(String tipoDescuento)
	{
		try {
			return (Descuento) Class.forName(tipoDescuento).newInstance();
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
//		if(tipoDescuento.equals("DescuentoJovenes"))
//			return new DescuentoJovenes();
//		if(tipoDescuento.equals("DescuentoParados"))
//			return new DescuentoParados();
	}
}
