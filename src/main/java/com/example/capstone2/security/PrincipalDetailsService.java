package com.example.capstone2.security;

import com.example.capstone2.domain.User;
import com.example.capstone2.exception.NoMatchingDataException;
import com.example.capstone2.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class PrincipalDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public PrincipalDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     *  principalDetails 생성을 위한 함수.
     *  username으로 user 조회, principalDetails 생성
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new NoMatchingDataException("username : " + username));
        return new PrincipalDetails(user);
    }


}