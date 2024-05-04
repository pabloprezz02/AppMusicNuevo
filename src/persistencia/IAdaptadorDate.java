package persistencia;

import java.sql.Date;

import modelo.Playlist;

public interface IAdaptadorDate {
	public void registrarDate(Date date);
	public void borrarDate(Date date);
	public void modificarDate(Date date);
	public Date recuperarDate(int codigo);
}
