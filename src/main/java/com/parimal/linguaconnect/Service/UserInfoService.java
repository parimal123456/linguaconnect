package com.parimal.linguaconnect.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import java.util.*;
import java.util.stream.Collectors;

import com.parimal.linguaconnect.entity.UserInfo;
import com.parimal.linguaconnect.entity.Role;
import com.parimal.linguaconnect.repository.UserInfoRepository;

@Service
public class UserInfoService implements UserDetailsService {
    private UserInfoRepository userInfoRepository;
    @Autowired
    public UserInfoService(UserInfoRepository userInfoRepository){
        this.userInfoRepository=userInfoRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserInfo user= userInfoRepository.findByUsername(username).orElseThrow(()->new UsernameNotFoundException("username not found"));
        return new User(user.getUsername(),user.getPassword(),mapRolesToAuthorities(user.getRoles()));
    }

    private Collection<GrantedAuthority> mapRolesToAuthorities(List<Role> roles){
        return roles.stream().map(role->new SimpleGrantedAuthority(role.getName())).collect(Collectors.toList());
    }
}
