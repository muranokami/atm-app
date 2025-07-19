package com.example.atm.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.atm.Service.UserService;
import com.example.atm.details.CustomUserDetails;
import com.example.atm.entity.User;
import com.example.atm.result.RegisterResult;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Controller
public class UserController {
  @Autowired
  private UserService userService;
  
  @Autowired
  private org.springframework.security.authentication.AuthenticationManager authenticationManager;
  
  @Autowired
  private HttpServletRequest request;
  
  
  @GetMapping("/register")
  public String showRegisterForm() {
      return "register";
  }
  
  private void autoLogin(User user) {
      CustomUserDetails userDetails = new CustomUserDetails(user);
      UsernamePasswordAuthenticationToken authToken =
          new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
      SecurityContextHolder.getContext().setAuthentication(authToken);
      
      HttpSession session = request.getSession(true);
      session.setAttribute("SPRING_SECURITY_CONTEXT", SecurityContextHolder.getContext());

      System.out.println("【DEBUG】autoLogin完了: " + user.getUsername());
  }
  
  @PostMapping("/register")
  public String registerUser(@RequestParam String username,
                             @RequestParam String email,
                             @RequestParam String password,
                             @RequestParam String pin,
                             @RequestParam int balance,
                             RedirectAttributes redirectAttributes) {
      
      RegisterResult result = userService.registerUser(username, email, password, pin, balance);
      
      if (result.isSuccess()) {
          //自動ログイン
          User user = userService.findByUsername(username);
          autoLogin(user);//自動ログイン処理
          
          //フラッシュメッセージ追加
          redirectAttributes.addFlashAttribute("registerSuccessMessage", "登録に成功しました。ログインしました。");
          redirectAttributes.addFlashAttribute("username", username);
          return "redirect:/atm/";//ホームへリダイレクト
      } else {
          redirectAttributes.addFlashAttribute("errorMessage", result.getResultType().getMessage());
          return "redirect:/register";
      }
      
     
  }
    
}
