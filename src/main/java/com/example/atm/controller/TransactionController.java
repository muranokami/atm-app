package com.example.atm.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.atm.Service.TransactionService;
import com.example.atm.Service.UserService;
import com.example.atm.entity.Transaction;
import com.example.atm.entity.User;
import com.example.atm.repository.TransactionRepository;
import com.example.atm.repository.UserRepository;

@Controller
@RequestMapping("/transaction")
public class TransactionController {
  @Autowired
  private TransactionRepository transactionRepository;
  
  @Autowired
  private UserRepository userRepository;
  
  @Autowired 
  private TransactionService transactionService;
  
  @Autowired
  private UserService userService;
  
  @GetMapping("/transactions") 
      public String showTransactionHistory(@AuthenticationPrincipal UserDetails userDetails, Model model) {
          User user = userRepository.findByUsername(userDetails.getUsername()).orElseThrow(() -> new RuntimeException("ユーザーが見つかりません")) ;
          
          List<Transaction> transactions = transactionRepository.findByUserOrderByTransactionTimeDesc(user);
          model.addAttribute("transactions", transactions);
          model.addAttribute("username", user.getUsername());
          
          return "transactions";
      }
  
  @GetMapping("/deposit")
  public String showDepositForm(Model model) {
      return "deposit";  // deposit.html を返す
  }
  
  @PostMapping("/deposit")
  public String deposit(@RequestParam int amount,
                        @AuthenticationPrincipal UserDetails userDetails,
                        RedirectAttributes redirectAttributes) {
      if (amount < 100 || amount > 500000) {
          redirectAttributes.addFlashAttribute("errorMessage", "入金額は100円以上500000以下で指定して下さい");
          return "redirect:/transaction/deposit";
      }
      userService.deposit(userDetails.getUsername(), amount);
      transactionService.recordTransaction(userDetails.getUsername(), "deposit", amount);
      redirectAttributes.addFlashAttribute("successMessage", amount +"円入金が完了しました");
      return "redirect:/atm/";
  }
  
  @GetMapping("/withdraw")
  public String showWithdrawForm(Model model) {
      return "withdraw";  
  }
  
  @PostMapping("/withdraw")
  public String withdraw(@RequestParam int amount, 
                         @AuthenticationPrincipal UserDetails userDetails, 
                         RedirectAttributes redirectAttributes) {
     if (amount < 100 || amount > 500000) {
         redirectAttributes.addFlashAttribute("errorMessage", "出金額は100円以上500000以下で指定して下さい");
         return "redirect:/transaction/withdraw";
     }
     boolean success = userService.withdraw(userDetails.getUsername(), amount);
     if (success) {
         transactionService.recordTransaction(userDetails.getUsername(), "withdraw", amount);
         redirectAttributes.addFlashAttribute("successMessage", amount + "円出金が完了しました");
     } else {
         redirectAttributes.addFlashAttribute("errorMessage", "残高が不足しています");
     }
      return "redirect:/atm/";
  }
}
