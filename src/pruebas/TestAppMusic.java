package pruebas;

import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;
import java.util.Random;
import org.junit.Test;

import controlador.Controlador;
import descuento.Descuento;
import descuento.DescuentoJovenes;
import descuento.FactoriaDescuento;
import modelo.Cancion;
import modelo.Usuario;
import persistencia.AdaptadorUsuario;

public class TestAppMusic {

	private AdaptadorUsuario adaptadorUsuario = AdaptadorUsuario.getUnicaInstancia();
	
//	private static String cadenaAleatoria()
//	{
//		int length = new Random().nextInt(100);
//		String cadena = "";
//		for(int i = 0; i < length; i++)
//		{
//			cadena += (char)(new Random().nextInt('a', 'z' + 1));
//		}
//		return cadena;
//	}
//	
//	@Test
//	public void testRegistrarUsuario()
//	{
//		String nombre = cadenaAleatoria();
//		char[] contrasena = cadenaAleatoria().toCharArray();
//		Date fecha = new Date();
//		String email = cadenaAleatoria() + "@um.es";
//		Controlador controlador = Controlador.getUnicaInstancia();
//		String creado = controlador.registrarUsuario(nombre, contrasena, email, fecha);
//
//		if(creado == null)
//		{
//			Usuario usuario = controlador.getUsuario(nombre);
//			assert usuario.getPassword().equals(new String(contrasena));
//			assert usuario.getNombre().equals(nombre);
//			assert usuario.getEmail().equals(email);
//			assert usuario.getCodigo() != 0;
//			controlador.shutdown();
//			adaptadorUsuario.eliminarUsuario(usuario);
//		}
//	}
//	
//	@Test
//	public void testReproduccion()
//	{
//		Controlador controlador = Controlador.getUnicaInstancia();
//		Usuario usuario = adaptadorUsuario.recuperarTodosUsuarios().get(0);
//		Collection<Cancion> canciones = controlador.buscarCanciones("", "", "Rock");
//		controlador.login(usuario.getNombre(), usuario.getPassword().toCharArray());
//		controlador.reproducirAleatoriamente(new LinkedList<Cancion>(canciones), usuario);
//		try {
//			Thread.sleep(1000);
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		}
//	}
	
	@Test
	public void testFactoriaDescuento()
	{
		DescuentoJovenes paraNombre = new DescuentoJovenes();
		String nombreClase = paraNombre.getClass().getName();
		System.out.println(nombreClase);
		Descuento dj = FactoriaDescuento.getUnicaInstancia().crearDescuento(nombreClase);
		assert dj.getPorcentaje() == (float)20.0;
	}

}
