package com.example.atm.details;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.atm.entity.User;
import com.example.atm.repository.UserRepository;



/**
 * Spring Security にユーザー情報を提供するためのサービスクラス。
 * ユーザー名（username）を元に、データベースからユーザー情報を取得します。
 * 認証時に Spring Security が自動的にこのクラスを利用します。
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {
    @Autowired
    private UserRepository userRepository;
    
    /**
     * ユーザー名からユーザー情報を取得し、UserDetails オブジェクトとして返す。
     * Spring Security の認証処理で使用される。
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // ユーザー名からユーザー情報を検索し、見つからなければ例外を投げる
        User user = userRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("ユーザーが見つかりません：" + username));
        // ユーザー情報をCustomUserDetailsにラップして返す（認証処理に利用）
        return new CustomUserDetails(user);
    }

}
