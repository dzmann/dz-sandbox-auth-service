package dz.sandbox.auth.service.service;

import dz.sandbox.auth.service.dto.LoginDto;
import dz.sandbox.auth.service.dto.RegisterDto;
import dz.sandbox.auth.service.dto.ResponseDto;
import dz.sandbox.auth.service.entity.DzUser;
import dz.sandbox.auth.service.entity.Role;
import dz.sandbox.auth.service.repository.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final AuthenticationManager authenticationManager;
  private final JwtService jwtService;

  public AuthService(
      UserRepository userRepository,
      PasswordEncoder passwordEncoder,
      AuthenticationManager authenticationManager,
      JwtService jwtService) {

    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
    this.authenticationManager = authenticationManager;
    this.jwtService = jwtService;
  }

  public void register(RegisterDto registerDto) {
    if (userRepository.existsByUsername(registerDto.getUsername())) {
      throw new IllegalArgumentException("Username already exists");
    }

    DzUser user = new DzUser();
    user.setUsername(registerDto.getUsername());
    user.setPassword(passwordEncoder.encode(registerDto.getPassword()));
    user.setRole(Role.USER);
    userRepository.save(user);
  }

  public ResponseDto login(LoginDto request) {
    authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
    DzUser user = userRepository.findByUsername(request.getUsername()).orElseThrow();
    String token = jwtService.generateToken(user);
    return new ResponseDto(token);
  }
}
