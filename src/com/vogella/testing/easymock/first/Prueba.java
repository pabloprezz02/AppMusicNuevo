package com.vogella.testing.easymock.first;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.LinkedList;

import javax.swing.JList;

import org.junit.jupiter.api.Test;

import com.vogella.*;

import auxiliares.ListObserver;
import observador.Observable;

public class Prueba {	

	public static void main(String[] args) {
		test1();
	}
	public static void test1()
	{
		ListObserver lista = new ListObserver(new JList<String>(), "Listas");
		Observable observador = new Observable();
		observador.addObserver(lista);
		observador.setChanged();
		LinkedList<String> listas = new LinkedList<String>();
		listas.add("Listas");
		listas.add("Playlist1");
		observador.notifyObservers(listas);
		boolean sonIguales = true;
		LinkedList<String> contenido = lista.getContenido();
		
		contenido.stream().forEach(playl -> System.out.println(playl));
//		for(int i = 0; i < listas.size(); i++)
//		{
//			contenido.stream().forEach(lista -> System.out.println(lista));
////			if(!contenido.get(i).equals(listas.get(i)))
////			{
////				sonIguales = false;
////			}
//		}
//		assertTrue(sonIguales);
	}
}
