package edu.vn.hcmuaf.layer2.sendEmail;

import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;

public class HtmlReader {
    public static final String verifyCode = "/VerifyCode.html";
    public static final  String resetPass = "/ResetPass.html";
    public static final  String cartProduct = "/CartProduct.html";
    public static final  String checkOrder = "/CheckOrder.html";

//    public  static String readFileHtml(String path) {
//        String content = "";
//        try {
//            content = new String(Files.readAllBytes(Paths.get(path)));
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return content;
//    }

    public static String readFileHtml(String path) {
        StringBuilder content = new StringBuilder();
        try (InputStream inputStream = HtmlReader.class.getResourceAsStream(path);
             Scanner scanner = new Scanner(inputStream, "UTF-8")) {
            while (scanner.hasNextLine()) {
                content.append(scanner.nextLine()).append("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return content.toString();
    }

    public static void main(String[] args) {
        System.out.println(readFileHtml(verifyCode));
    }
}
