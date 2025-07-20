package com.example.atm.details;

import java.util.Collection;
import java.util.Collections;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.example.atm.entity.User;

/**
 * Spring SecurityのUserDetailsインターフェースを実装したクラス。
 * 認証に使用するユーザー情報をラップし、Spring Securityに提供します。
 */
public class CustomUserDetails implements UserDetails {
    
    private final User user; // アプリケーションのUserエンティティ
    
    // コンストラクタでUserを注入
    public CustomUserDetails(User user) {
        this.user = user;
    }
    
    // ユーザーIDを取得
    public Long getId() {
        return user.getId();
    }
    
    // ユーザーのメールアドレスを取得（認証以外の情報として使用可能）
    public String getEmail() {
        return user.getEmail();
    }
    
    // 権限情報を返す
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.emptyList();
    }
    
    // ユーザーのパスワードを返す
    @Override
    public String getPassword() {
        return user.getPassword();
    }
    
    // ユーザー名
    @Override
    public String getUsername() {
        return user.getUsername();
    }
    
    // アカウントが期限切れでないかどうか（trueなら有効）
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }
    
    // アカウントがロックされていないかどうか（trueなら有効）
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }
    
    // 資格情報（パスワード）が期限切れでないか（trueなら有効）
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }
    
    // ユーザーが有効かどうか（trueならログイン可能）
    @Override
    public boolean isEnabled() {
        return true;
    }
    
    // 元のUserエンティティを取得（追加情報を使いたいときに便利）
    public User getUser() {
        return this.user;
    }

}
