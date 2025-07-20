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

/**
 * ユーザー登録・新規登録処理を担当するコントローラ
 */
@Controller
public class UserController {
  @Autowired
  private UserService userService;
  
  @Autowired
  private HttpServletRequest request;
  
  /**
   * ユーザー登録フォーム表示
   */
  @GetMapping("/register")
  public String showRegisterForm() {
      return "register";
  }
  
  /**
   * ユーザー登録後に自動ログインを行う
   */
  
  private void autoLogin(User user) {
      CustomUserDetails userDetails = new CustomUserDetails(user);
      
      // Spring Securityの認証トークンを作成して認証コンテキストにセット
      UsernamePasswordAuthenticationToken authToken =
          new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
      SecurityContextHolder.getContext().setAuthentication(authToken);
      
      // セッションに認証情報を保存
      HttpSession session = request.getSession(true);
      session.setAttribute("SPRING_SECURITY_CONTEXT", SecurityContextHolder.getContext());

      System.out.println("【DEBUG】autoLogin完了: " + user.getUsername());
  }
  
  /**
   * ユーザー登録処理
   * バリデーション・重複チェックなどはUserServiceに委譲
   * 登録成功時は自動ログインしてホーム画面へリダイレクト
   * 登録失敗時はエラーメッセージを表示して登録画面に戻る
   */
  @PostMapping("/register")
  public String registerUser(@RequestParam String username,
                             @RequestParam String email,
                             @RequestParam String password,
                             @RequestParam String pin,
                             @RequestParam int balance,
                             RedirectAttributes redirectAttributes) {
      
      //登録処理をUserServiceに委譲
      RegisterResult result = userService.registerUser(username, email, password, pin, balance);
      
      if (result.isSuccess()) {
          //登録成功時はユーザーをDBから取得して自動ログイン
          User user = userService.findByUsername(username);
          autoLogin(user);
          
          //フラッシュメッセージを追加してホーム画面へリダイレクト
          redirectAttributes.addFlashAttribute("registerSuccessMessage", "登録に成功しました。ログインしました。");
          redirectAttributes.addFlashAttribute("username", username);
          return "redirect:/atm/";//ホームへリダイレクト
      } else {
          //登録失敗時はエラーメッセージをフラッシュ属性に追加して登録画面へリダイレクト
          redirectAttributes.addFlashAttribute("errorMessage", result.getResultType().getMessage());
          return "redirect:/register";
      }
      
     
  }
    
}
