package auxiliares.generadorPDF;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfDocument;
import com.itextpdf.text.pdf.PdfWriter;

/**
 * Clase que, dado un nombre de archivo, una ubicación y un contenido, crea un PDF.
 */
public class GeneradorPDF {
	public static void generarPDF(String nombrePDF, String ubicacion, String contenido) throws FileNotFoundException, DocumentException
	{		
		FileOutputStream archivo = new FileOutputStream(ubicacion + "\\" + nombrePDF + ".pdf");
	      Document documento = new Document();
	      PdfWriter.getInstance(documento, archivo);
	      documento.open();
	      documento.add(new Paragraph(contenido + "\n"));
	      documento.close();
	}
}
