package descuento;

public class DescuentoParados extends Descuento {
	
	private static final float PORCENTAJE_PARADOS = (float) 35.0;
	
	public DescuentoParados()
	{
		super();
	}
	
	@Override
	public float aplicarDescuento(float precio) {
		return precio * (1 - (float)(PORCENTAJE_PARADOS / 100.0));
	}
}
