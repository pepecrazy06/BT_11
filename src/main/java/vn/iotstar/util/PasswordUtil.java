package vn.iotstar.util;
import java.security.*;
import java.util.Base64;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
public final class PasswordUtil {
 private static final int ITERATIONS=120000;
 private PasswordUtil(){}
 public static String hash(String password){
  byte[] salt=new byte[16];new SecureRandom().nextBytes(salt);
  return "pbkdf2$"+ITERATIONS+"$"+Base64.getEncoder().encodeToString(salt)+"$"+Base64.getEncoder().encodeToString(derive(password,salt,ITERATIONS));
 }
 private static byte[] derive(String password,byte[] salt,int rounds){
  PBEKeySpec spec=new PBEKeySpec(password.toCharArray(),salt,rounds,256);
  try{return SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256").generateSecret(spec).getEncoded();}catch(Exception e){throw new IllegalStateException(e);}finally{spec.clearPassword();}
 }
 public static boolean matches(String password,String stored){
  if(password==null || stored==null)return false;
  if(!stored.startsWith("pbkdf2$"))return MessageDigest.isEqual(password.getBytes(java.nio.charset.StandardCharsets.UTF_8),stored.getBytes(java.nio.charset.StandardCharsets.UTF_8));
  try{String[] p=stored.split("\\$");int rounds=Integer.parseInt(p[1]);if(rounds<10000 || rounds>1000000)return false;
   return MessageDigest.isEqual(derive(password,Base64.getDecoder().decode(p[2]),rounds),Base64.getDecoder().decode(p[3]));
  }catch(Exception e){return false;}
 }
}