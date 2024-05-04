package auxiliares.generadorPDF;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfDocument;
import com.itextpdf.text.pdf.PdfWriter;

public class GeneradorPDF {
	public static void generarPDF(String nombrePDF, String ubicacion, String contenido) throws FileNotFoundException, DocumentException
	{
//		String ubi = ""; 
//		if(ubicacion.endsWith("\\"))
//		{
//			for(int i = 0; i < ubicacion.length() - 1; i++)
//				ubi += ubicacion.charAt(i);
//			
//			ubicacion = ubi;
//		}
//		PdfWriter writer = new PdfWriter(ubicacion + "\\" + nombrePDF);
//		PdfDocument pdf = new PdfDocument(writer);
//		Document document = new
		
		FileOutputStream archivo = new FileOutputStream(ubicacion + "\\" + nombrePDF + ".pdf");
	      Document documento = new Document();
	      PdfWriter.getInstance(documento, archivo);
	      documento.open();
	      documento.add(new Paragraph(contenido));
	      documento.close();
	}
}
