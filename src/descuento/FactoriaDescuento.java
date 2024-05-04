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
//		return Class<?>.forName(tipoDescuento).getDeclaredConstructor().newInstance();
		if(tipoDescuento.equals("DescuentoJovenes"))
			return new DescuentoJovenes();
		if(tipoDescuento.equals("DescuentoParados"))
			return new DescuentoParados();
		return null;
	}
}
