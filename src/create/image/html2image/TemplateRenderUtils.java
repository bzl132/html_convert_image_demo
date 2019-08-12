package create.image.html2image;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.log.NullLogChute;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
//import org.springframework.util.Assert;

import java.io.*;
import java.net.JarURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.MessageFormat;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.BiFunction;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;


public class TemplateRenderUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(TemplateRenderUtils.class);

    private static Map<String, String> templatePathMap;

    private static Map<String, String> resourcePathMap;

    /**
     * 根据模板Key获取模板
     * @param templateKey 模板Key
     * @param param 模板渲染用参数
     * @return
     */
    public static String renderTemplate(String templateKey, Map<String, Object> param) {
        String templateContent = getTemplate(templateKey);
        return render(templateContent, templateKey, param);
    }

    /**
     * 加载模板
     * @param templateKey
     * @return
     */
    private static String getTemplate(String templateKey) {
        String templateContent = templatePathMap.get(templateKey);
        URL url = TemplateRenderUtils.class.getClassLoader().getResource(TEMPLATE_PATH);
        String templatePath = url.toString();
        if (!(templatePath.indexOf("!/") > 0)) {
            try {
                templateContent = new String(Files.readAllBytes(Paths.get(templateContent)));
            } catch (IOException e) {
                LOGGER.error("读取转换模板失败", e);
                throw new RuntimeException("读取转换模板失败");
            }
        }
        return templateContent;
    }

    /**
     *
     * @param content 模板内容
     * @param templateTag 模板Key(模板文件名称)
     * @param param 模板渲染用参数
     * @return
     */
    public static String render(String content, String templateTag, Map<String, Object> param) {
        VelocityEngine ve = new VelocityEngine();
        VelocityContext context = new VelocityContext(param);
        //解决velocity LOG的问题
        ve.setProperty(RuntimeConstants.RUNTIME_LOG_LOGSYSTEM, new NullLogChute());
        ve.init();
        StringWriter writer = new StringWriter();
        ve.evaluate(context, writer, templateTag, content);
        return writer.toString();
    }


    private static volatile boolean isInit;

    public static boolean isJar;

    private static final String TEMPLATE_PATH ="template";

    static {
        LOGGER.info("TemplateRenderUtils开始加载模板列表");
        init();
        LOGGER.info(MessageFormat.format("TemplateRenderUtils加载模板列表成功，总计：[0]", templatePathMap.size()));
    }

    /**
     *
     */
    private static void init() {
        if (!isInit) {
            isInit = true;
            templatePathMap = new HashMap<>();
            resourcePathMap = new HashMap<>();
            URL url = TemplateRenderUtils.class.getClassLoader().getResource(TEMPLATE_PATH);
            String templatePath = url.toString();
            if (templatePath.indexOf("!/") > 0) {
                isJar = true;
                loadJarFile(templatePath);
            } else {
                isJar = false;
                loadDevFile();
            }
        }
    }

    private static void loadDevFile() {
        final Map<String, String> localTemplatePathMap = templatePathMap;
        File parentDir = null;
        URL url = TemplateRenderUtils.class.getResource("/template");
        parentDir = new File(url.getPath());
//        Assert.isTrue(parentDir.exists(), "Html2Image模板目录文件夹不存在");
        if (parentDir.isDirectory()) {
            for (File templateFile : parentDir.listFiles()) {
                if (templateFile.getName().endsWith(".vm")) {
                    localTemplatePathMap.put(templateFile.getName(),templateFile.getPath());
                } else {
                    resourcePathMap.put(templateFile.getName(),templateFile.getPath().replace('\\', '/'));
                }
            }
        }
    }

    private static void loadJarFile(String templatePath) {
        LOGGER.info("开始获取Jar下文件");
        final Map<String, String> localTemplatePathMap = templatePathMap;
        String jarPath = templatePath.substring(0, templatePath.indexOf("!/") + 2);
        URL jarURL = null;
        try {
            jarURL = new URL(jarPath);
            JarURLConnection jarCon = (JarURLConnection) jarURL.openConnection();
            JarFile jarFile = jarCon.getJarFile();
            Enumeration<JarEntry> jarEntrys = jarFile.entries();
            while (jarEntrys.hasMoreElements()) {
                JarEntry entry = jarEntrys.nextElement();
                String name = entry.getName();
                if (name.startsWith("WEB-INF/classes/" + TEMPLATE_PATH) && !entry.isDirectory()) {
                    if (name.endsWith(".vm")) {
                        BufferedReader reader = new BufferedReader(new InputStreamReader(TemplateRenderUtils.class.getClassLoader().getResourceAsStream(name), Charset.forName("UTF-8")));
                        StringBuffer sb = new StringBuffer();
                        String temp;
                        while ((temp = reader.readLine()) != null) {
                            sb.append(temp);
                        }
                        localTemplatePathMap.put(name.replace("WEB-INF/classes/" + TEMPLATE_PATH + "/", ""), sb.toString());
                    } else if (name.endsWith(".png")) {
                        String pngName = name.replace("WEB-INF/classes/" + TEMPLATE_PATH + "/", "");
                        resourcePathMap.put(pngName, templatePath + "/" + pngName);
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.error("读取War中的模板文件报错", e);
        }
    }

    public static void main(String[] args) {
        init();
    }

    public static Map<String,String> getResourcePath() {
        return resourcePathMap;
    }

    public static <T> Map<String,T> buildClassRowMap(int row, int line, BiFunction<Integer, Integer, T> generator) {
        Map<String, T> resutlMap = new TreeMap<>();
        for (int i = 0; i < row; i++) {
            final int fRow = i;
            for (int j = 1; j <= line; j++) {
                final int fLine = j;
                resutlMap.put(i + "#" + j, generator.apply(fRow, fLine));
            }
        }
        return resutlMap;
    }
}
