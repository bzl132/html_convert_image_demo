package create;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;

public class FileDemo {

    public static void main(String[] args) throws IOException {
//        URL url = new URL("file:/D:\\project\\demo\\src\\main\\resources\\logo.png");
//        InputStream inputStream = url.openStream();
//        Files.copy(inputStream, Paths.get("D:\\project\\demo\\src\\main\\resources\\logo1.png"));
        String s = new File(".").toURI().toURL().toExternalForm();
        System.out.println(s);

    }
}
