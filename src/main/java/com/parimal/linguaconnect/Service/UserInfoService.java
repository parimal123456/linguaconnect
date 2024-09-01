package com.parimal.linguaconnect.Service;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.parimal.linguaconnect.entity.Role;
import com.parimal.linguaconnect.entity.UserInfo;
import com.parimal.linguaconnect.repository.UserInfoRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserInfoService implements UserDetailsService {
    private final UserInfoRepository userInfoRepository;
    
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        UserInfo user= userInfoRepository.findByEmail(email).orElseThrow(()->new UsernameNotFoundException("username not found"));
        return new User(user.getEmail(),user.getPassword(),mapRolesToAuthorities(user.getRoles()));
    }

    private Collection<GrantedAuthority> mapRolesToAuthorities(List<Role> roles){
        return roles.stream().map(role->new SimpleGrantedAuthority(role.getName())).collect(Collectors.toList());
    }

    public UserInfo save(UserInfo userInfo){
        return userInfoRepository.save(userInfo);
    }
    
    public Optional<UserInfo> findByEmail(String email){
        return userInfoRepository.findByEmail(email);
    }
    
    public boolean existsByUsername(String username){
        return userInfoRepository.existsByUsername(username);
    }

    public boolean existsByEmail(String email){
        return userInfoRepository.existsByEmail(email);
    }

}
