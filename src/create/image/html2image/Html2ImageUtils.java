package create.image.html2image;

import org.apache.ibatis.io.Resources;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
//import org.springframework.util.Assert;
import org.w3c.dom.Document;
import org.xhtmlrenderer.context.AWTFontResolver;
import org.xhtmlrenderer.swing.Java2DRenderer;
import org.xhtmlrenderer.swing.SwingReplacedElementFactory;
import org.xhtmlrenderer.util.FSImageWriter;
import org.xhtmlrenderer.util.XRLog;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class Html2ImageUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(Html2ImageUtils.class);

    private static int DEFAULT_WIDTH = 2400;
    private static int DEFAULT_HEIGHT = 2200;

    /**
     *
     * @param htmlStr
     * @param width
     * @param height
     * @param outputStream
     */
    public static void convertHtmlString2Image(String htmlStr, Integer width, Integer height, OutputStream outputStream) {
//        Assert.notNull(htmlStr, "Html文件不能为空");
        StringReader sr = new StringReader(htmlStr);
        InputSource is = new InputSource(sr);
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(is);
            doConvert(doc, width, height, outputStream);
        } catch (ParserConfigurationException e) {
            LOGGER.error("构建DocumentBuilder报错");
            throw new IllegalArgumentException(e);
        } catch (SAXException e) {
            LOGGER.error("转换html到表格错误");
            throw new RuntimeException(e);
        } catch (IOException e) {
            LOGGER.error("写出文件报错");
            throw new RuntimeException(e);
        }
    }

    /**
     *
     * @param document
     * @param width
     * @param height
     * @param outputStream
     */
    public static void convertDocument2Image(Document document, Integer width, Integer height, OutputStream outputStream) {
//        Assert.notNull(document, "Html文件不能为空");
        try {
            doConvert(document, width, height, outputStream);
        }catch (IOException e) {
            LOGGER.error("写出文件报错", e);
        }
    }

    /**
     * @param document
     * @param width
     * @param height
     * @param outputStream
     */
    private static void doConvert(Document document, Integer width, Integer height, OutputStream outputStream) throws IOException {
//        Assert.notNull(document, "document文件不能为空");
        if (width == null || width <= 0) {
            width = DEFAULT_WIDTH;
        }
        if (height == null || height <= 0) {
            height = DEFAULT_HEIGHT;
        }

        Java2DRenderer renderer = new Java2DRenderer(document, width, height);
        //设置分辨率
        renderer.getSharedContext().setDotsPerPixel(2);
        renderer.getSharedContext().setReplacedElementFactory(new SwingReplacedElementFactory());
        renderer.getSharedContext().getTextRenderer().setSmoothingThreshold(1);

        AWTFontResolver fontResolver = (AWTFontResolver) renderer.getSharedContext().getFontResolver();
        fontResolver.setFontMapping(FontEnums.SIMSUN.getName(), FontEnums.SIMSUN.getFont());

        BufferedImage image = renderer.getImage();
        FSImageWriter imageWriter = new FSImageWriter();

        imageWriter.setWriteCompressionQuality(0.1f);
        imageWriter.write(image, outputStream);
    }

    static {
        try {
            //解决初始化未完成时访问报错问题
            Method init = XRLog.class.getDeclaredMethod("init");
            init.setAccessible(true);
            init.invoke(null);
        } catch (NoSuchMethodException e) {
            LOGGER.error("初始化XRLog，未找到特定方法", e);
        } catch (IllegalAccessException e) {
            LOGGER.error("初始化XRLog参数错误", e);
        } catch (InvocationTargetException e) {
            LOGGER.error("初始化XRLog报错", e);
        }
    }

    public enum FontEnums {
        SIMSUN("SimSun", "template/SimSun.ttf"),
        ;
        private String name;
        private String path;
        private Font font;

        FontEnums(String name, String path) {
            this.name = name;
            this.path = path;
        }

        public String getName() {
            return this.name;
        }

        public Font getFont() {
            if (null == font) {
                try (InputStream is = Resources.getResourceAsStream(path);
                     BufferedInputStream bis = new BufferedInputStream(is);
                      ) {
                    font = Font.createFont(Font.TRUETYPE_FONT, bis);
                } catch (Exception e) {
                    // 可能会报 "java.awt.FontFormatException: bad table, tag=23592960" 错误..
                    LOGGER.error("加载字体失败",e);
                }
            }
            return font;
        }
    }
}
