package observador.componenteObservador;

import javax.swing.JComponent;

import observador.Observable;
import observador.Observer;

public abstract class ComponenteObservador implements Observer {
	private JComponent componente;
	
	protected ComponenteObservador(JComponent componente)
	{
		this.componente = componente;
	}
	
	protected JComponent getComponente()
	{
		return componente;
	}
	
	@Override
	public abstract void update(Observable o, Object arg);
}
