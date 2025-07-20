package com.example.atm.controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

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
import com.example.atm.repository.UserRepository;

/**
 * 取引関連の画面表示と処理を担当するコントローラ
 * 
 * 入金、出金、振込、取引履歴の処理を提供
 */
@Controller
@RequestMapping("/transaction")
public class TransactionController {
  
  @Autowired
  private UserRepository userRepository;
  
  @Autowired 
  private TransactionService transactionService;
  
  @Autowired
  private UserService userService;
  
  /**
   * 取引履歴画面表示
   * 検索条件（取引種別、期間、相手ユーザー名）を指定して絞り込み表示も可能
   */
  
  @GetMapping("/transactions")
  public String showTransactionHistory(@AuthenticationPrincipal UserDetails userDetails,
                                       @RequestParam(required = false) String type,
                                       @RequestParam(required = false) String startDate,
                                       @RequestParam(required = false) String endDate,
                                       @RequestParam(required = false, name = "counterpart") String counterpartUsername,
                                       Model model) {
      User user = userRepository.findByUsername(userDetails.getUsername())
                  .orElseThrow(() -> new RuntimeException("ユーザーが見つかりません"));
      
      // 空文字やnullをnullに統一
      type = (type == null || type.isBlank()) ? null : type;
      counterpartUsername = (counterpartUsername == null || counterpartUsername.isBlank()) ? null : counterpartUsername;

      // 日付文字列をLocalDateに安全に変換
      LocalDate startLocalDate = null;
      LocalDate endLocalDate = null;
      try {
          if (startDate != null && !startDate.isBlank()) {
              startLocalDate = LocalDate.parse(startDate);
          }
          if (endDate != null && !endDate.isBlank()) {
              endLocalDate = LocalDate.parse(endDate);
          }
      } catch (Exception e) {
          model.addAttribute("errorMessage", "日付の形式が正しくありません。yyyy-MM-dd の形式で入力してください。");
          return "transactions";
      }

      LocalDateTime startDateTime = (startLocalDate != null) ? startLocalDate.atStartOfDay() : null;
      LocalDateTime endDateTime = (endLocalDate != null) ? endLocalDate.atTime(LocalTime.MAX) : null;

      // 相手ユーザー名の部分一致検索用に%を付与
      String counterpartFilter = (counterpartUsername != null) ? "%" + counterpartUsername + "%" : null;

      try {
          List<Transaction> transactions = transactionService.searchTransactions(user, type, startDateTime, endDateTime, counterpartFilter);
          model.addAttribute("transactions", transactions);
      } catch (Exception e) {
          e.printStackTrace();
          model.addAttribute("errorMessage", "取引履歴の取得中にエラーが発生しました: " + e.getMessage());
      }

      model.addAttribute("username", user.getUsername());
      model.addAttribute("searchType", type);
      model.addAttribute("searchStartDate", startLocalDate);
      model.addAttribute("searchEndDate", endLocalDate);
      model.addAttribute("searchCounterpart", counterpartUsername);

      return "transactions";
  }

  
  /**
   * 入金フォーム表示
   */
  @GetMapping("/deposit")
  public String showDepositForm(Model model) {
      return "deposit";
  }
  
  /**
   * 入金処理
   * 金額が妥当かチェックし、ユーザーの残高に加算し取引履歴を記録
   */
  @PostMapping("/deposit")
  public String deposit(@RequestParam int amount,
                        @AuthenticationPrincipal UserDetails userDetails,
                        RedirectAttributes redirectAttributes) {
      //入金額の妥当性チェック
      if (amount < 100 || amount > 500000) {
          redirectAttributes.addFlashAttribute("errorMessage", "入金額は100円以上500000以下で指定して下さい");
          return "redirect:/transaction/deposit";
      }
      
      //ユーザー残高に加算
      userService.deposit(userDetails.getUsername(), amount);
      
      //取引履歴を記録
      transactionService.recordTransaction(userDetails.getUsername(), "deposit", amount);
      
      redirectAttributes.addFlashAttribute("successMessage", amount +"円入金が完了しました");
      return "redirect:/atm/";
  }
  
  
  /**
   * 振込フォーム表示
   * 送金可能なユーザーリストを取得して画面に表示
   */
  @GetMapping("/transfer")
  public String showTransferForm(Model model) {
      List<User> users = userRepository.findAll();
      model.addAttribute("users", users);
      return "transfer";
  }
  
  /**
   * 振込処理
   * 金額チェック、送信元・送信先ユーザー存在確認、残高チェックを行い、
   * 振込を実行し結果に応じてメッセージを画面へ返す
   */
  @PostMapping("/transfer")
  public String transferMoney(@AuthenticationPrincipal UserDetails userDetails,
                              @RequestParam Long targetUserId,
                              @RequestParam Integer amount,
                              RedirectAttributes redirectAttributes) {
      
      //振込額チェック
      if (amount < 100 || amount > 500000) {
          redirectAttributes.addFlashAttribute("errorMessage", "振込額は100円以上500000円以下で指定して下さい");
          return "redirect:/transaction/transfer";
      }
      
      //送信元ユーザー取得
      Optional<User> optionalSender = userRepository.findByUsername(userDetails.getUsername());
      if (optionalSender.isEmpty()) {
          redirectAttributes.addFlashAttribute("errorMessage", "送信元が見つかりません");
          return "redirect:/transaction/transfer";
      }
      User sender = optionalSender.get();
      
      //送信先ユーザー取得
      Optional<User> optionalReceiver = userRepository.findById(targetUserId);
      if (optionalReceiver.isEmpty()) {
          redirectAttributes.addFlashAttribute("errorMessage", "送信先ユーザーが見つかりません");
          return "redirect:/transaction/transfer";
      }
      User receiver = optionalReceiver.get();
      
      //振込処理をUserServiceに委譲
      String result = userService.transfer(sender.getId(), targetUserId, amount);
      
      if ("success".equals(result)) {
          redirectAttributes.addFlashAttribute("successMessage", amount + "円を" + receiver.getUsername() + "さんに振込ました");
          return "redirect:/atm/";
      } else {
          redirectAttributes.addFlashAttribute("errorMessage", result);
          return "redirect:/transaction/transfer";
      }
  }
  
  /**
   * 出金フォーム表示
   */
  @GetMapping("/withdraw")
  public String showWithdrawForm(Model model) {
      return "withdraw";  
  }
  
  /**
   * 出金処理
   * 金額チェック、残高チェックを行い、残高が十分なら引き落とし、取引履歴を記録
   */
  @PostMapping("/withdraw")
  public String withdraw(@RequestParam int amount, 
                         @AuthenticationPrincipal UserDetails userDetails, 
                         RedirectAttributes redirectAttributes) {
      
     //出金額チェック
     if (amount < 100 || amount > 500000) {
         redirectAttributes.addFlashAttribute("errorMessage", "出金額は100円以上500000以下で指定して下さい");
         return "redirect:/transaction/withdraw";
     }
     
     //ユーザーの残高を減らす処理
     boolean success = userService.withdraw(userDetails.getUsername(), amount);
     
     
     if (success) {
         //取引履歴に出金記録を残す
         transactionService.recordTransaction(userDetails.getUsername(), "withdraw", amount);
         redirectAttributes.addFlashAttribute("successMessage", amount + "円出金が完了しました");
     } else {
         redirectAttributes.addFlashAttribute("errorMessage", "残高が不足しています");
     }
      return "redirect:/atm/";
  }
}
