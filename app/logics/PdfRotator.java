package logics;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.Map;

import models.PageConfig;
import play.Play;

import com.itextpdf.awt.geom.AffineTransform;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfImportedPage;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfWriter;

import constants.Constant;

public class PdfRotator {

	private Rectangle getRectangle(double height, double width) {
		float fheight = (float) height;
		float fwidth = (float) width;
		Rectangle rectangle = new Rectangle(fwidth * Constant.URX_VALUE, fheight * Constant.URX_VALUE);
		return rectangle;
	}

	public int rotatePdf(String targetPdf, String sourcePdf, Map<Integer, PageConfig> pageConfigs, int multiply) {
		System.out.println("start rotatePdf");
		
		Collection<PageConfig> collection = pageConfigs.values();
		PageConfig oneConfig = (PageConfig) (collection.toArray()[0]); 
		
//		double height = Double.valueOf(Play.configuration.getProperty("rotate.height"));
//		double width = Double.valueOf(Play.configuration.getProperty("rotate.width"));
		
		double height = Double.valueOf(oneConfig.height);
		double width = Double.valueOf(oneConfig.width);
		
//		if (multiply != 0) {
//			height = height * multiply;
//			width = width * multiply;
//		}
		Document document = new Document(getRectangle(height, width));
		PdfWriter writer = null;
		try {
			writer = PdfWriter.getInstance(document, new FileOutputStream(Play.configuration.getProperty("rotate.outputdir") + targetPdf));
		} catch (FileNotFoundException e) { 
			e.printStackTrace();
		} catch (DocumentException e) {
			e.printStackTrace();
		}
		document.open();

		PdfReader origPdfReader = null;
		try {
			origPdfReader = new PdfReader(Play.configuration.getProperty("rotate.inputdir") + sourcePdf);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		int numberOfPages = origPdfReader.getNumberOfPages();
		double dx = Double.parseDouble(Play.configuration.getProperty("rotate.dx"));
		double dy = Double.parseDouble(Play.configuration.getProperty("rotate.dy"));
		for (int i = 1; i <= numberOfPages; i ++) {
			PageConfig config = pageConfigs.get(i);
			if (config == null) continue;
			PdfImportedPage importedPage = writer.getImportedPage(origPdfReader, i);

			PdfContentByte canvas = writer.getDirectContent();
			
			double angle = config.pdfAngle;
			double tx = config.tx;
			double ty = config.ty;
		    AffineTransform transform = AffineTransform.getRotateInstance(angle, importedPage.getWidth() / dx, importedPage.getHeight() / dy);
		    transform.translate(tx, ty);
		    
		    canvas.addTemplate(importedPage, transform);
		    document.newPage();
		}
		document.close();
		System.out.println("end rotatePdf");
		return numberOfPages;
	}
}
