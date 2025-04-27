package com.farmtomarket.application.config;

import com.farmtomarket.application.exception.SystemAuthException;
import com.farmtomarket.application.model.User;
import com.farmtomarket.application.repository.UserRepository;
import com.farmtomarket.application.utils.Constant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Optional;

@Service
public class JwtUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Autowired
    public JwtUserDetailsService(UserRepository userRepository){
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = null;
        try {
            user = getUser(username);
        }catch (Exception e){
            throw new SystemAuthException(e.getMessage());
        }
        return new org.springframework.security.core.userdetails
                .User(user.getUsername(), user.getPassword(), new ArrayList<>());
    }

    public User getUser(String username){
        Optional<User> users = userRepository.findByUsername(username);
        if (users.isEmpty()){
            throw new SystemAuthException(Constant.INVALID_CREDENTIALS);
        }
        return users.get();
    }
}
