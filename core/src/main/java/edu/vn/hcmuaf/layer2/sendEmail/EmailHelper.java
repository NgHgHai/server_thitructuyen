package edu.vn.hcmuaf.layer2.sendEmail;



import javax.mail.*;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

public class EmailHelper {
    static final String username = "hoanghailata@gmail.com";
    static final String password = "fwjsdtuxjbbmrokc";

    public static String sendEmail(String to, String subject, String text) throws MessagingException {
        Session session = null;
        try {
            Properties properties = new Properties();
            properties.load(EmailHelper.class.getClassLoader().getResourceAsStream("email.properties"));

            session = Session.getInstance(properties, new Authenticator() {
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(username, password);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            return "Authentication failed";
        }
        // Wrap a message in session
        MimeMessage message = new MimeMessage(session);
        message.setSubject(subject);
        message.setFrom(username);
//        message.setFrom("greenhoot@gmail.com");
        message.setRecipients(Message.RecipientType.TO, to);
        message.setContent(text, "text/html");
//        message.setText(text);
        try {
            Transport.send(message);
        } catch (Exception e) {
            e.printStackTrace();
            return "Send email failed";
        }
        // Send message

        return "Email sent successfully";
    }
    public static String sendVerifyCode(String to, String code) throws MessagingException {
        String codeReplace= ":code:";
        String contend = HtmlReader.readFileHtml(HtmlReader.verifyCode);
        contend = contend.replace(codeReplace,code);
      return  sendEmail(to,"Veryfy email from GreenHoot",contend);
    }

    public static String sendResetPass(String to, String code) throws MessagingException {
        String codeReplace= ":code:";
        String contend = HtmlReader.readFileHtml(HtmlReader.verifyCode);
        contend = contend.replace(codeReplace,code);
        return sendEmail(to,"Reset password",contend);
    }

//    public static String sendOrderConfirm(String to,Order order) throws MessagingException{
//        String contend = HtmlText.verifyCode;
//
//        return sendEmail(to,"Check your Order",contend);
//    }

    public static void main(String[] args) throws MessagingException {
        String text = HtmlReader.readFileHtml(HtmlReader.verifyCode);
        String statuss = sendVerifyCode("20130166@st.hcmuaf.edu.vn", "1234576");

        System.out.println(statuss);
    }
}
