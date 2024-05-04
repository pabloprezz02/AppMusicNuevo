package descuento;

public class DescuentoJovenes extends Descuento {
	
	private static final int PORCENTAJE_JOVENES = 20;
	
	
	public DescuentoJovenes()
	{
		super();
	}
	
	@Override
	public float aplicarDescuento(float precio) 
	{
		return precio *(1 - PORCENTAJE_JOVENES / 100);
	}
}
