package create.pdf.itext;

/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2019 iText Group NV
    Authors: iText Software.
 
    For more information, please contact iText Software at this address:
    sales@itextpdf.com
 */
/*
 * This example is part of the iText 7 tutorial.
 */

import com.itextpdf.io.font.FontConstants;
import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.List;
import com.itextpdf.layout.element.ListItem;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.property.UnitValue;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.StringTokenizer;

/**
 * Simple Hello World example.
 */
public class C01E01_HelloWorld {
    
    public static final String DEST = "results/chapter01/table.pdf";
    public static final String DATA = "results/source/data.csv";

    public static void main(String args[]) throws IOException {
        File file = new File(DEST);
        file.getParentFile().mkdirs();
        new C01E01_HelloWorld().drawText(DEST);
    }
    
    public void createPdf(String dest) throws IOException {
        //Initialize PDF writer
        PdfWriter writer = new PdfWriter(dest);
 
        //Initialize PDF document
        PdfDocument pdf = new PdfDocument(writer);
        
        // Initialize document
        Document document = new Document(pdf);
 
        //Add paragraph to the document
        document.add(new Paragraph("Hello World!"));
 
        //Close document
        document.close();
    }

    public void createPdf2(String dest) throws IOException {
        PdfWriter writer = new PdfWriter(dest);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf);
// Create a PdfFont
        PdfFont font = PdfFontFactory.createFont(FontConstants.TIMES_ROMAN);
// Add a Paragraph
        document.add(new Paragraph("iText is:").setFont(font));
// Create a List
        List list = new List()
                .setSymbolIndent(12)
                .setListSymbol("\u2022")
                .setFont(font);
// Add ListItem objects
        list.add(new ListItem("Never gonna give you up"))
                .add(new ListItem("Never gonna let you down"))
                .add(new ListItem("Never gonna run around and desert you"))
                .add(new ListItem("Never gonna make you cry"))
                .add(new ListItem("Never gonna say goodbye"))
                .add(new ListItem("Never gonna tell a lie and hurt you"));
// Add the list
        document.add(list);
        document.close();
    }
    public void createTablePdf3(String dest) throws IOException {
        PdfWriter writer = new PdfWriter(dest);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf, PageSize.A4.rotate());
        document.setMargins(20, 20, 20, 20);
        //中文字体
        PdfFont font = PdfFontFactory.createFont("C:/Windows/Fonts/simhei.ttf", PdfEncodings.IDENTITY_H,true);

        PdfFont bold = PdfFontFactory.createFont(FontConstants.HELVETICA_BOLD);
        Table table = new Table(new float[]{4, 1, 3, 4, 3, 3, 3, 3, 1});
        table.setWidth(UnitValue.createPercentValue(100));
        BufferedReader br = new BufferedReader(new FileReader(DATA));
        String line = br.readLine();
        process(table, line, bold, true);
        while ((line = br.readLine()) != null) {
            process(table, line, font, false);
        }
        br.close();
        document.add(table);
        document.close();
    }
    public void process(Table table, String line, PdfFont font, boolean isHeader) {
        StringTokenizer tokenizer = new StringTokenizer(line, ";");
        while (tokenizer.hasMoreTokens()) {
            if (isHeader) {
                table.addHeaderCell(
                        new Cell().add(
                                new Paragraph(tokenizer.nextToken()).setFont(font)));
            } else {
                table.addCell(
                        new Cell().add(
                                new Paragraph(tokenizer.nextToken()).setFont(font)));
            }
        }
    }

    public void createLine(String dest) throws IOException {
        PdfDocument pdf = new PdfDocument(new PdfWriter(dest));
        PageSize ps = PageSize.A4.rotate();
        PdfPage page = pdf.addNewPage(ps);
        PdfCanvas canvas = new PdfCanvas(page);
// Draw the axes

        canvas.moveTo(-406, 0)
                .lineTo(406, 0)
                .lineTo(200,200)
                .stroke();
        pdf.close();
    }

    public void drawText(String dest) throws IOException {
        java.util.List<String> text = new ArrayList<String>();
        text.add("         Episode V         ");
        text.add("  THE EMPIRE STRIKES BACK  ");
        text.add("It is a dark time for the");
        text.add("Rebellion. Although the Death");
        text.add("Star has been destroyed,");
        text.add("Imperial troops have driven the");
        text.add("Rebel forces from their hidden");
        text.add("base and pursued them across");
        text.add("the galaxy.");
        text.add("Evading the dreaded Imperial");
        text.add("Starfleet, a group of freedom");
        text.add("fighters led by Luke Skywalker");
        text.add("has established a new secret");
        text.add("base on the remote ice world");
        text.add("of Hoth...");

        PdfDocument pdf = new PdfDocument(new PdfWriter(dest));
        PageSize ps = PageSize.A4.rotate();
        PdfPage page = pdf.addNewPage(ps);
        PdfCanvas canvas = new PdfCanvas(page);
        canvas.concatMatrix(1, 0, 0, 1, 0, ps.getHeight());
        canvas.beginText()
                .setFontAndSize(PdfFontFactory.createFont(FontConstants.COURIER_BOLD), 14)
                .setLeading(14 * 1.2f)
                .moveText(70, -40);

        for (String s : text) {
            //Add text and move to the next line
            canvas.newlineShowText(s);
        }
        canvas.endText();


    }
}
