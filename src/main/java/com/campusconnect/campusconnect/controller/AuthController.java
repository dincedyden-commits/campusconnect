package com.campusconnect.campusconnect.controller;
import com.campusconnect.campusconnect.entity.User; import com.campusconnect.campusconnect.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder; import org.springframework.web.bind.annotation.*; import jakarta.servlet.http.HttpSession; import java.util.*;
@RestController @RequestMapping("/api/auth")
public class AuthController {
 private static final String RESET_CODE="resetCode"; private static final String RESET_USER="resetUser";
 private final UserRepository users; private final PasswordEncoder encoder;
 public AuthController(UserRepository u,PasswordEncoder e){users=u;encoder=e;}
 @PostMapping("/login") public Map<String,Object> login(@RequestBody Map<String,String> body,HttpSession session){
  String id=Optional.ofNullable(body.get("identifier")).orElse("").trim(); String pw=body.getOrDefault("password","");
  User u=users.findByUsernameIgnoreCase(id).orElseGet(()->users.findByEmailIgnoreCase(id).orElse(null));
  if(u==null || !encoder.matches(pw,u.getPassword())) throw new IllegalArgumentException("Invalid username/email or password");
  if (u.getUsername().equalsIgnoreCase("campusconnect")) { u.setRole("ADMIN"); users.save(u); } session.setAttribute("userId",u.getId()); return Map.of("id",u.getId(),"username",u.getUsername(),"fullName",u.getFullName(),"role",u.getRole());
 }
 @PostMapping("/reset/request") public Map<String,Object> resetRequest(@RequestBody Map<String,String> b,HttpSession s){
  String id=Optional.ofNullable(b.get("identifier")).orElse("").trim(); User u=users.findByUsernameIgnoreCase(id).orElseGet(()->users.findByEmailIgnoreCase(id).orElse(null));
  if(u==null) throw new IllegalArgumentException("No account found for that username/email");
  String code=String.format("%06d",new Random().nextInt(1000000)); s.setAttribute(RESET_CODE,code); s.setAttribute(RESET_USER,u.getId()); s.setMaxInactiveInterval(600);
  return Map.of("message","Verification code generated for this session","code",code);
 }
 @PostMapping("/reset/confirm") public Map<String,String> resetConfirm(@RequestBody Map<String,String> b,HttpSession s){
  Object expected=s.getAttribute(RESET_CODE), uid=s.getAttribute(RESET_USER); String code=b.getOrDefault("code","");
  if(expected==null||uid==null||!expected.toString().equals(code)) throw new IllegalArgumentException("Invalid or expired verification code");
  String pw=b.getOrDefault("password",""); if(pw.length()<8||!pw.matches(".*\\d.*")||!pw.matches(".*[^A-Za-z0-9].*")) throw new IllegalArgumentException("Password must be at least 8 characters and include a number and symbol");
  User u=users.findById(((Number)uid).longValue()).orElseThrow();u.setPassword(encoder.encode(pw));users.save(u);s.removeAttribute(RESET_CODE);s.removeAttribute(RESET_USER);return Map.of("message","Password reset successfully");
 }
 @PostMapping("/logout") public Map<String,String> logout(HttpSession s){s.invalidate(); return Map.of("message","Logged out");}
 @GetMapping("/me") public Object me(HttpSession s){Object id=s.getAttribute("userId"); if(id==null) return Map.of("authenticated",false); return Map.of("authenticated",true,"id",id);}
}