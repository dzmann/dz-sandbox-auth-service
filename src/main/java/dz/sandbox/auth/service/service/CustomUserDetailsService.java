package dz.sandbox.auth.service.service;

import dz.sandbox.auth.service.entity.DzUser;
import dz.sandbox.auth.service.repository.UserRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

  private final UserRepository userRepository;

  public CustomUserDetailsService(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

    DzUser dzUser =
        userRepository
            .findByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException("DzUser not found: " + username));

    return User.builder()
        .username(dzUser.getUsername())
        .password(dzUser.getPassword())
        .roles(dzUser.getRole().name())
        .build();
  }
}
