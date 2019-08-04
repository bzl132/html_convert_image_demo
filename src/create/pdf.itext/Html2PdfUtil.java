package create.pdf.itext;

import com.itextpdf.html2pdf.ConverterProperties;
import com.itextpdf.html2pdf.HtmlConverter;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.BlockElement;
import com.itextpdf.layout.element.IElement;
import com.itextpdf.layout.font.FontProvider;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

public class Html2PdfUtil {
    public static final float topMargin = 114f;
    public static final float bottomMargin = 156f;
    public static final float leftMargin = 90f;
    public static final float rightMargin = 90f;
    public static void convert(String html, String name) {

        long start = System.currentTimeMillis();

        try (
                FileOutputStream outputStream = new FileOutputStream(new File("D:\\convertImage\\"+ name+".pdf"));
                PdfWriter writer = new PdfWriter(outputStream);
                PdfDocument pdfDocument = new PdfDocument(writer);
            ) {
            ConverterProperties props = new ConverterProperties();
            FontProvider fp = new FontProvider();
            fp.addStandardPdfFonts();
            // ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            // fp.addDirectory(classLoader.getResource("fonts").getPath());
            // props.setFontProvider(fp);
            List<IElement> iElements = HtmlConverter.convertToElements(html, props);
            Document document = new Document(pdfDocument, PageSize.A4, true); // immediateFlush设置true和false都可以，false 可以使用 relayout
            document.setMargins(topMargin, rightMargin, bottomMargin, leftMargin);
            for (IElement iElement : iElements) {
                BlockElement blockElement = (BlockElement) iElement;
                blockElement.setMargins(1, 0, 1, 0);
                document.add(blockElement);
            }
            document.close();
            System.out.println("转换"+name+"用时：" +(System.currentTimeMillis() - start));
            outputStream.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}