package com.example.atm.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.atm.details.CustomUserDetails;
import com.example.atm.entity.User;
import com.example.atm.repository.UserRepository;
import com.example.atm.result.RegisterResult;
import com.example.atm.result.RegisterResultType;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Service
@Transactional
public class UserService {
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private HttpServletRequest request;
    
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
    
    public User findByUsername(String username) {
        Optional<User> optionalUser = userRepository.findByUsername(username);
        if (optionalUser.isEmpty()) {
            System.out.println("【DEBUG】UserRepository.findByUsernameがnullを返しました。username=" + username);
        } else {
            System.out.println("【DEBUG】UserRepository.findByUsernameで取得したユーザー: " + optionalUser.get().getUsername());
        }
        return userRepository.findByUsername(username).orElseThrow(() -> new RuntimeException("ユーザーが見つかりません：" + username));
    }
    
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
    
    public void save(User user) {
        userRepository.save(user);
    }
    
    public void autoLogin(User user) {
        CustomUserDetails userDetails = new CustomUserDetails(user);
        UsernamePasswordAuthenticationToken auth =
                new UsernamePasswordAuthenticationToken(userDetails, user.getPassword(), userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);
        HttpSession session = request.getSession(true);
        session.setAttribute("SPRING_SECURITY_CONTEXT", SecurityContextHolder.getContext());
    }
    
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
    
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }
    
    //ハッシュ化するためのコード
    private boolean isBCryptHash(String password) {
        return password != null && password.matches("^\\$2[aby]\\$\\d{2}\\$.{53}$");
    }
    
    //ハッシュ化されているのかを確認する
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
