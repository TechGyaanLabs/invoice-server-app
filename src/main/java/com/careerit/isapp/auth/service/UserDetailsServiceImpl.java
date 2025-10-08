package com.careerit.isapp.auth.service;

import com.careerit.isapp.auth.domain.Role;
import com.careerit.isapp.auth.domain.User;
import com.careerit.isapp.auth.dto.SignupRequest;
import com.careerit.isapp.auth.repo.RoleRepository;
import com.careerit.isapp.auth.repo.UserRepository;
import com.careerit.isapp.domain.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final RoleRepository roleRepository;



    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));
        return UserDetailsImpl.build(user);
    }

    public User loadAppUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));
    }
    public User signup(SignupRequest signupRequest) {
        User user = new User();
        user.setUsername(signupRequest.getUsername());
        user.setPassword(bCryptPasswordEncoder.encode(signupRequest.getPassword()));
        user.setEmail(signupRequest.getEmail());
        Set<String> strRoles  = signupRequest.getRoles();
        Set<Role> roles = new HashSet<>();
        if(strRoles == null || strRoles.isEmpty()){
            log.info("Roles are empty, trying add default role as USER");
            Role userRole = roleRepository.findByName("ROLE_USER")
                    .orElseThrow(() -> new RuntimeException("Error: ROLE_USER not found."));
            roles.add(userRole);
        }else{
            strRoles.forEach(role -> {
                String r = role.equalsIgnoreCase("admin")? "ROLE_ADMIN":"ROLE_USER";
                log.info("User :{} has {} role", user.getUsername(), r);
                Role found = roleRepository.findByName(r)
                        .orElseThrow(() -> new RuntimeException("Error: Role "+ r +"not found."));
                roles.add(found);
            });
        }
        user.setRoles(roles);
        userRepository.save(user);
        log.info("User : {} has been successfully signed in with id : {}", user.getUsername(), user.getId());
        return user;
    }
}
