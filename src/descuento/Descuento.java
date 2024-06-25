package descuento;

public abstract class Descuento {		
	
	private float porcentaje;
	/**
	 * Método para aplicar un descuento.
	 * @param precio: precio al que se le aplica el descuento.
	 * @return el precio con el descuento aplicado.
	 */
	public abstract float aplicarDescuento(float precio);
	
	public float getPorcentaje()
	{
		return porcentaje;
	}
	
	public Descuento(float porcentaje)
	{
		this.porcentaje = porcentaje;
	}
}
