package descuento;

public class DescuentoJovenes extends Descuento {
	
	private static final float PORCENTAJE_JOVENES = (float)20.0;
	
	
	public DescuentoJovenes()
	{
		super(PORCENTAJE_JOVENES);
	}
	
	@Override
	public float getPorcentaje() {
		return PORCENTAJE_JOVENES;
	}
	
	@Override
	public float aplicarDescuento(float precio) 
	{
		return precio *(1 - (float)(PORCENTAJE_JOVENES / 100.0));
	}
}
