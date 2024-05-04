package descuento;

public abstract class Descuento {	
	/**
	 * Constructor por defecto.
	 */
	public Descuento(){}
	
	/**
	 * Método para aplicar un descuento.
	 * @param precio: precio al que se le aplica el descuento.
	 * @return el precio con el descuento aplicado.
	 */
	public abstract float aplicarDescuento(float precio);
}
