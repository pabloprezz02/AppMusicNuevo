package descuento;

public class DescuentoParados extends Descuento {
	
	private static final int PORCENTAJE_PARADOS = 35;
	
	public DescuentoParados()
	{
		super();
	}
	
	@Override
	public float aplicarDescuento(float precio) {
		return precio * (1 - PORCENTAJE_PARADOS / 100);
	}
}
