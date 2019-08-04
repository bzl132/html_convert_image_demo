package create;

import create.image.html2image.Html2ImageDemo;
import create.pdf.itext.Html2PdfUtil;
import org.xhtmlrenderer.util.XRLog;

import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.function.BiConsumer;

/**
 * @Author: long
 * @Description:
 * @Date: Create in 2019.08.04 23:42
 * @Modified By:
 * @ClassName: create.DemoTest
 */
public class DemoTest {
    public static void main(String[] args) throws Exception {
        // convertToImage((htmlstr, index ) -> Html2ImageDemo.convert(htmlstr, "图片" + index));
        // convertToImage((htmlstr, index ) -> Html2PdfUtil.convert(htmlstr, "图片" + index));
        // convertToImage((htmlstr, index ) -> Html2ImageDemo.convertByImagePackage(htmlstr, "图片" + index));

        // Method init = XRLog.class.getDeclaredMethod("init");
        // init.setAccessible(true);
        // init.invoke(null);
        // Thread.sleep(1000);
        // convertToImage((htmlstr, index ) -> Html2ImageDemo.convertByXmlRenderer(htmlstr, "图片" + index));

    }

    private static void convertToImage(BiConsumer<String, Integer> consumer) {
        try {
            String htmlstr = new String(Files.readAllBytes(Paths.get("D:\\test.html")));
            for (int i = 0; i < 50; i++) {
                final int index = i;
                new Thread(
                        () -> consumer.accept(htmlstr, index)
                ).start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
