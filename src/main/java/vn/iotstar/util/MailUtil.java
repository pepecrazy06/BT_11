package vn.iotstar.util;
import jakarta.mail.*;
import jakarta.mail.internet.*;
import java.util.Properties;
public class MailUtil {
 public static void sendOTP(String toEmail,String otp)throws Exception{
  String email=System.getenv("SMTP_USERNAME"),password=System.getenv("SMTP_PASSWORD");
  if(email==null || email.isBlank() || password==null || password.isBlank())throw new IllegalStateException("Chưa cấu hình SMTP_USERNAME và SMTP_PASSWORD.");
  Properties p=new Properties();p.setProperty("mail.smtp.auth","true");p.setProperty("mail.smtp.starttls.enable","true");p.setProperty("mail.smtp.starttls.required","true");
  p.setProperty("mail.smtp.host",System.getenv().getOrDefault("SMTP_HOST","huynhuthx602@gmail.com"));p.setProperty("mail.smtp.port",System.getenv().getOrDefault("SMTP_PORT","sua lai mk"));
  p.setProperty("mail.smtp.connectiontimeout","10000");p.setProperty("mail.smtp.timeout","10000");p.setProperty("mail.smtp.writetimeout","10000");
  Session session=Session.getInstance(p,new Authenticator(){protected PasswordAuthentication getPasswordAuthentication(){return new PasswordAuthentication(email,password);}});
  MimeMessage m=new MimeMessage(session);m.setFrom(new InternetAddress(email,"Huy Books","UTF-8"));m.setRecipients(Message.RecipientType.TO,InternetAddress.parse(toEmail));
  m.setSubject("Huy Books — Mã xác thực tài khoản","UTF-8");m.setText("Xin chào,\n\nMã xác thực của bạn là: "+otp+"\nMã có hiệu lực trong 5 phút. Không chia sẻ mã với người khác.\n\nHuy Books — Thái Nhựt Huy","UTF-8");Transport.send(m);
 }
}