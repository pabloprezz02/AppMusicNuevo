package observador;

import java.util.Vector;

public class Observable {
	// BOOLEANO QUE INDICA SI HA CAMBIADO
	private boolean changed = false;
	// VECTOR DE OBSERVADORES
	public static Vector<Observer> obs = new Vector<Observer>();
	
	// CONSTRUCTOR
	public Observable() {}
	
	// AÑADIR OBSERVER
	public synchronized void addObserver(Observer o) {
		obs.add(o);
	}
	
	// ELIMINAR OBSERVER
	public synchronized void deleteObserver(Observer o) {
		obs.remove(o);
	}
	
//	// MÉTODO QUE DEPENDERÁ DEL TIPO DE OBSERVADOR. POR DEFECTO NO HACE NADA, PERO EL CONTROLADOR LO MODIFICA.
//	public boolean comprobar(Observer o) {
//		return true;
//	}
	
	// NOTIFICAR A LOS OBSERVADORES
	public void notifyObservers(Object arg) {
		Object[] arrayLocal;
		synchronized(this) {
			if(!changed)
				return;
			arrayLocal = obs.toArray();
			clearChanged();
		}
		
		for(int i=0; i<arrayLocal.length; i++) {
			Observer o = (Observer) arrayLocal[i];
			o.update(this, arg);
//			if(comprobar(o))
//				o.update(this, arg);
		}
	}
	
	// PONER CHANGED A TRUE
	public synchronized void setChanged() {
		changed = true;
	}
	
	// PONER CHANGED A FALSE
	public synchronized void clearChanged() {
		changed = false;
	}
	
	// VER SI ESTÁ CAMBIADO
	public synchronized boolean hasChanged() {
		return changed;
	}
}
