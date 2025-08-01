package com.example.atm.controller;
import java.security.Principal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.atm.Service.UserService;
import com.example.atm.details.CustomUserDetails;
import com.example.atm.entity.User;

/**
 * ATMコントローラークラス
 * 
 * ATMのホーム画面表示、残高照会表示、ログイン画面表示の処理を行う
 */
@Controller
@RequestMapping("/atm")
public class ATMController {
    @Autowired
    private UserService userService;
    
    /**
     * ホーム画面表示
     * ログインユーザーの情報を取得し、残高やログイン時刻等をモデルにセットしindex.htmlに返す
     * 未ログインの場合はログイン画面にリダイレクト
     */
    
    @GetMapping("/")
    public String home(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                       @ModelAttribute(value = "registerSuccessMessage") String registerSuccessMessage,
                       Model model, Principal principal) {
        System.out.println("【DEBUG】ATMController.home() 呼ばれました。userDetails=" + customUserDetails);
        if (customUserDetails == null) {
            System.out.println("【DEBUG】userDetailsがnull");
            return "redirect:/atm/login"; //ログイン画面へリダイレクト
        }
        
        //モデルの中身をデバッグ表示
        model.asMap().forEach((k, v) -> System.out.println("【DEBUG】model[" + k + "] = " + v));

        
        String username = customUserDetails.getUsername();
        System.out.println("【DEBUG】userDetails.getname() = " + username);
        
        //ユーザーの最終ログイン時刻を表示
        userService.loginTime(username);
        
        //ユーザー情報をDBから取得
        User user = userService.findByUsername(username);
        if(user == null) {
            System.out.println("【DEBUG】userService.findByUsernameがnullを返しました。username=" + username);
            model.addAttribute("errorMessage", "ユーザー情報が見つかりません。");
            return "error";//エラーページに遷移
        }
        
        System.out.println("【DEBUG】Userの残高: " + user.getBalance());
        
        //Viewに必要なデータをセット
        model.addAttribute("username", username);
        model.addAttribute("message", "ATMへようこそ");
        model.addAttribute("loginTime", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm")));
        model.addAttribute("balance", user.getBalance());
        
        return "index";//ホーム画面のテンプレート名
    }
    
    /**
     * 残高照会表示
     * 
     */
    @GetMapping("/balance")
    public String showBalance(@AuthenticationPrincipal CustomUserDetails customUserDetails, Model model) {
        String username = customUserDetails.getUsername();
        User user = userService.findByUsername(username);
        model.addAttribute("username", username);
        model.addAttribute("balance", user.getBalance());
        model.addAttribute("lastUpdated", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm")));
        return "balance";
    }
    
    /**
     * ログイン画面表示
     * ログイン失敗やログアウト時のメッセージ表示
     * 
     */
    
    @GetMapping("/login")
    public String login(@RequestParam(required = false) String username,
                        @RequestParam(required = false) String pin,
                        @RequestParam(required = false) String error,
                        @RequestParam(required = false) String logout,
                        Model model) {
        model.addAttribute("username", username);
        model.addAttribute("pin", pin);
        if (error != null) {
            model.addAttribute("loginerror", true);
        }
        if (logout != null) {
            model.addAttribute("logoutMessage", "ログアウトしました");
        }
        return "login"; 
    }
    
    

}