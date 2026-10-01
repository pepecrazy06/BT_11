package vn.iotstar.dao;
import jakarta.persistence.*;
import vn.iotstar.entity.User;
import vn.iotstar.util.*;
public class UserDAO {
 public User login(String email,String password){
  EntityManager em=JPAUtil.getEntityManager();
  try{
   User u=em.createQuery("select u from User u where lower(u.email)=:email",User.class).setParameter("email",email==null?"":email.trim().toLowerCase(java.util.Locale.ROOT)).getResultStream().findFirst().orElse(null);
   if(u==null || !PasswordUtil.matches(password,u.getPasswd()))return null;
   if(!u.getPasswd().startsWith("pbkdf2$")){em.getTransaction().begin();u.setPasswd(PasswordUtil.hash(password));em.getTransaction().commit();}
   return u;
  }catch(RuntimeException e){if(em.getTransaction().isActive())em.getTransaction().rollback();throw e;}finally{em.close();}
 }
 public boolean exists(String email){EntityManager em=JPAUtil.getEntityManager();try{return em.createQuery("select count(u) from User u where lower(u.email)=:email",Long.class).setParameter("email",email.toLowerCase(java.util.Locale.ROOT)).getSingleResult()>0;}finally{em.close();}}
 public void save(User u){EntityManager em=JPAUtil.getEntityManager();try{em.getTransaction().begin();em.persist(u);em.getTransaction().commit();}catch(RuntimeException e){if(em.getTransaction().isActive())em.getTransaction().rollback();throw e;}finally{em.close();}}
}