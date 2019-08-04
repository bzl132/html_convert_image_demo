package create.image.html2image;


import gui.ava.html.Html2Image;
import gui.ava.html.renderer.ImageRenderer;
import org.w3c.dom.Document;
import org.xhtmlrenderer.context.AWTFontResolver;
import org.xhtmlrenderer.swing.Java2DRenderer;
import org.xhtmlrenderer.util.FSImageWriter;
import org.xml.sax.InputSource;

import javax.swing.border.EmptyBorder;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.StringReader;

/**
 * @Author: long
 * @Description:
 * @Date: Create in 2019.08.04 22:24
 * @Modified By:
 * @ClassName: create.image.html2image.Html2ImageDemo
 */
public class Html2ImageDemo {

    public static void convert(String htmlstr, String name) {
        long start = System.currentTimeMillis();

        try {
            byte[] bytes = HtmlToImageUtils.html2jpeg(Color.white, htmlstr,
                    1300, 1200, new EmptyBorder(0, 0,
                            0, 0));
            FileOutputStream stream = new FileOutputStream(new File("D:\\convertImage\\"+ name+".jpg"));
            stream.write(bytes);
            System.out.println("转换"+ name +"用时：" + (System.currentTimeMillis() - start));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void convertByImagePackage(String htmlStr, String name) {
        long start = System.currentTimeMillis();
        try {
            Html2Image convert = Html2Image.fromHtml(htmlStr);
            ImageRenderer renderer = convert.getImageRenderer();
            renderer.saveImage(new File("D:\\convertImage\\"+ name+".jpg"));
            System.out.println("转换"+ name +"用时：" + (System.currentTimeMillis() - start));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void convertByXmlRenderer(String htmlStr, String name) {
        int width = 1300, height = 1300;
        long start = System.currentTimeMillis();
        try {
            StringReader sr = new StringReader(htmlStr);
            InputSource is = new InputSource(sr);
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder=factory.newDocumentBuilder();
            Document doc = builder.parse(is);

            Java2DRenderer renderer = new Java2DRenderer(doc, width, height);
// 自动装载字体..
// 这样就不需要服务器安装字体了..(跳过第4步..
//             AWTFontResolver fontResolver = (AWTFontResolver) renderer.getSharedContext().getFontResolver();
            // fontResolver.setFontMapping(FontEnums.SIMSUN.getName(), FontEnums.SIMSUN.getFont());
            BufferedImage image = renderer.getImage();
            FSImageWriter imageWriter = new FSImageWriter();
            imageWriter.write(image, "D:\\convertImage\\"+ name+".jpg");
            System.out.println(name + ":" + (System.currentTimeMillis() - start));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}

// enum FontEnums {
//     SIMSUN("SimSun", "simsun.ttf"),
//     ;
//     private String name;
//     private String path;
//     private Font font;
//
//     FontEnums(String name, String path) {
//         this.name = name;
//         this.path = path;
//     }
//
//     public String getName() {
//         return this.name;
//     }
//
//     public Font getFont() {
//         if (null == font) {
//             InputStream is = null;
//             BufferedInputStream bis = null;
//             try {
//                 is = Resources.getResourceAsStream(path);
//                 bis = new BufferedInputStream(is);
//                 // 可能会报 "java.awt.FontFormatException: bad table, tag=23592960" 错误..
//                 font = Font.createFont(Font.TRUETYPE_FONT, bis);
//             } catch (Exception e) {
//             } finally {
//                 try {
//                     if (bis != null) {
//                         bis.close();
//                     }
//                 } catch (Exception e) {
//                 }
//                 try {
//                     if (is != null) {
//                         is.close();
//                     }
//                 } catch (Exception e) {
//                 }
//             }
//         }
//     }
// }