package com.example.atm.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.atm.details.CustomUserDetails;
import com.example.atm.entity.Transaction;
import com.example.atm.entity.User;
import com.example.atm.repository.TransactionRepository;
import com.example.atm.repository.UserRepository;
import com.example.atm.result.RegisterResult;
import com.example.atm.result.RegisterResultType;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

/**
 * ATMアプリのユーザーに関連するビジネスロジックを提供するサービスクラス
 * ユーザー登録、入金・出金、振込、取引履歴の記録、ログイン処理などを担当
 */
@Service
@Transactional
public class UserService {
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private HttpServletRequest request;
    
    @Autowired
    private TransactionRepository transactionRepository;
    
    /**
     * ユーザーを新規登録し、登録後に自動ログインを行う
     */
    
    @Transactional
    public RegisterResult registerUser(String username, String email, String password, String pin, int balance) {
        System.out.println("=== registerUser メソッド呼び出し ===");
        if (userRepository.findByUsername(username).isPresent()) {
            return new RegisterResult(false, RegisterResultType.DUPLICATE_USERNAME); //ユーザー名重複
        }
        
        if (userRepository.existsByEmail(email)) {
            return new RegisterResult(false, RegisterResultType.INVALID_DOMAIN);//メールアドレス重複
        }
        
        if (!isStrongPassword(password)) {
            return new RegisterResult(false, RegisterResultType.WEAK_PASSWORD);//パスワード強度が不足
        }
        
        if (userRepository.findByUsername(username).isPresent()) {
            System.out.println("== 既にユーザー名が存在します ==");
            return new RegisterResult(false, RegisterResultType.DUPLICATE_USERNAME);
        }
        
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setPin(pin);
        user.setBalance(balance);
        
        try {
            userRepository.save(user);
            System.out.println("=== 保存成功 ===");
        } catch (Exception e) {
            System.out.println("=== 保存失敗 ===");
            e.printStackTrace();
            return new RegisterResult(false, RegisterResultType.ERROR);
        }
        
        autoLogin(user);
        return new RegisterResult(true, RegisterResultType.SUCCESS);
    }
    
    /**
     * 指定されたユーザー名に対応するユーザーを取得
     */
    public User findByUsername(String username) {
        Optional<User> optionalUser = userRepository.findByUsername(username);
        if (optionalUser.isEmpty()) {
            System.out.println("【DEBUG】UserRepository.findByUsernameがnullを返しました。username=" + username);
        } else {
            System.out.println("【DEBUG】UserRepository.findByUsernameで取得したユーザー: " + optionalUser.get().getUsername());
        }
        return userRepository.findByUsername(username).orElseThrow(() -> new RuntimeException("ユーザーが見つかりません：" + username));
    }
    
    /**
     * パスワードが強固かどうかをチェック
     */
    
    private boolean isStrongPassword(String password) {
        if (password.length() < 8) {
            return false;
        }
        boolean hasLetter = false;
        boolean hasDigit = false;
        
        for (char c : password.toCharArray()) {
            if (Character.isLetter(c)) hasLetter = true;
            if (Character.isDigit(c)) hasDigit = true;
        }
        return hasLetter && hasDigit;
    }
    
    /*
     * ユーザー情報を保存
     */
    public void save(User user) {
        userRepository.save(user);
    }
    
    /*
     * 登録時に自動でログインするための処理
     */
    public void autoLogin(User user) {
        CustomUserDetails userDetails = new CustomUserDetails(user);
        UsernamePasswordAuthenticationToken auth =
                new UsernamePasswordAuthenticationToken(userDetails, user.getPassword(), userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);
        HttpSession session = request.getSession(true);
        session.setAttribute("SPRING_SECURITY_CONTEXT", SecurityContextHolder.getContext());
    }
    
    /**
     * 出金処理(残高が不足していない場合のみ可能)
     */
    public boolean withdraw(String username, int amount) {
        Optional<User> optionalUser = userRepository.findByUsername(username);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            if (user.getBalance() >= amount) {
                user.setBalance(user.getBalance() - amount);
                userRepository.save(user);
                return true;
            }
        }
        return false;
    }
    
    /*
     * 出金処理
     */
    public void deposit(String username, int amount) {
        Optional<User> optionalUser = userRepository.findByUsername(username);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            user.setBalance(user.getBalance() + amount);
            userRepository.save(user);
        } else {
            throw new RuntimeException("ユーザーが見つかりません:" + username);
        }
    }
    
    /**
     * 指定されたユーザー名が存在するかどうかを確認
     */
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }
    
    /**
     * パスコードがBCrypt形式で暗号化されているかを判定
     */
    private boolean isBCryptHash(String password) {
        return password != null && password.matches("^\\$2[aby]\\$\\d{2}\\$.{53}$");
    }
    
    /**
     * 平文パスワードが登録されていた場合、BCryptで暗号化して再保存
     */
    public void encodePlainPassword() {
        List<User> users = userRepository.findAll();
        for (User user : users) {
            String pw = user.getPassword();
            if (!isBCryptHash(pw)) {
                user.setPassword(passwordEncoder.encode(pw));
                userRepository.save(user);
            }
        }
    }
    
    
    
    /**
     * ユーザー間の振込処理。送信側・受信側共に取引履歴が記録される
     */
    @Transactional
    public String transfer(Long fromUserId, Long toUserId, Integer amount) {
        if (fromUserId.equals(toUserId)) {
            return "自分自身には振り込めません";
        }
        
        Optional<User> fromOpt = userRepository.findById(fromUserId);
        Optional<User> toOpt = userRepository.findById(toUserId);
        if (fromOpt.isEmpty() || toOpt.isEmpty()) {
            return "送信元または送信先ユーザーが存在しません";
        }
        
        User fromUser = fromOpt.get();
        User toUser = toOpt.get();
        
        if (fromUser.getBalance() < amount) {
            return "残高が不足しています";
        }
        
        // 残高更新
        fromUser.setBalance(fromUser.getBalance() - amount);
        toUser.setBalance(toUser.getBalance() + amount);
        userRepository.save(fromUser);
        userRepository.save(toUser);
        
        // 取引履歴（送信側）
        Transaction fromTransaction = new Transaction();
        fromTransaction.setUser(fromUser);
        fromTransaction.setType("transfer_sent");
        fromTransaction.setAmount(-amount);
        fromTransaction.setCounterpartUsername(toUser.getUsername());
        transactionRepository.save(fromTransaction);
        
        // 取引履歴（受信側）
        Transaction toTransaction = new Transaction();
        toTransaction.setUser(toUser);
        toTransaction.setType("transfer_received");
        toTransaction.setAmount(amount);
        toTransaction.setCounterpartUsername(fromUser.getUsername());
        transactionRepository.save(toTransaction);
        
        return "success";
    }
    
    /**
     * 現在ログイン中のユーザー情報を取得
     */
    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof CustomUserDetails) {
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            return userDetails.getUser();
        }
        throw new RuntimeException("現在ログイン中のユーザーが取得できませんでした。");
    }
    
    /**
     * ログイン時刻を更新(ログ機能に利用可能)
     */
    public void loginTime(String username) {
        Optional<User> optionaluser = userRepository.findByUsername(username);
        if (optionaluser.isPresent()) {
            User user = optionaluser.get();
            user.setLoginTime(LocalDateTime.now());
            userRepository.save(user);
            System.out.println("【DEBUG】ログイン時刻を更新しました：" + user.getLoginTime());
        } else {
            System.out.println("【DEBUG】loginTime： ユーザーが見つかりません:" + username);
        }
    }
 
}
