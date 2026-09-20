package dz.sandbox.auth.service.service;

import dz.sandbox.auth.service.dto.LoginDto;
import dz.sandbox.auth.service.dto.RegisterDto;
import dz.sandbox.auth.service.dto.ResponseDto;
import dz.sandbox.auth.service.entity.DzUser;
import dz.sandbox.auth.service.entity.Role;
import dz.sandbox.auth.service.exception.AuthServiceException;
import dz.sandbox.auth.service.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@Slf4j
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
      throw new AuthServiceException("Username already exists", HttpStatus.CONFLICT);
    }

    DzUser user = new DzUser();
    user.setUsername(registerDto.getUsername());
    user.setPassword(passwordEncoder.encode(registerDto.getPassword()));
    user.setRole(Role.USER);
    user.setName(registerDto.getName());
    user.setLastname(registerDto.getLastname());
    user.setEmail(registerDto.getEmail());
    user.setFailedLoginAttempts(0);
    user.setLockedUntil(null);
    userRepository.save(user);
  }

  public ResponseDto login(LoginDto request) {
    DzUser user =
        userRepository
            .findByUsername(request.getUsername())
            .orElseThrow(() -> new BadCredentialsException("Invalid username or password"));

    checkUserBlocked(user);

    try {
      authenticationManager.authenticate(
          new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
    } catch (Exception e) {
      registerLoginAttempt(user);
      throw e;
    }

    String token = jwtService.generateToken(user);
    return new ResponseDto(token);
  }

  private void registerLoginAttempt(DzUser dzUser) {
    int failedAttempts = dzUser.getFailedLoginAttempts() + 1;
    dzUser.setFailedLoginAttempts(failedAttempts);

    if (failedAttempts >= 3) {
      dzUser.setLockedUntil(LocalDateTime.now().plusMinutes(5));
    }

    userRepository.save(dzUser);
  }

  private void checkUserBlocked(DzUser dzUser) {
    LocalDateTime now = LocalDateTime.now();
    LocalDateTime lockedUntil = dzUser.getLockedUntil();

    if (lockedUntil == null) {
      return;
    }

    if (lockedUntil.isAfter(now)) {
      throw new AuthServiceException(
          "Account temporarily locked. Try again later.", HttpStatus.UNAUTHORIZED);
    }

    dzUser.setLockedUntil(null);
    dzUser.setFailedLoginAttempts(0);

    userRepository.save(dzUser);
  }
}
