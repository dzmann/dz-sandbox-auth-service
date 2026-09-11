package dz.sandbox.auth.service.controller;

import dz.sandbox.auth.service.dto.LoginDto;
import dz.sandbox.auth.service.dto.RegisterDto;
import dz.sandbox.auth.service.dto.ResponseDto;
import dz.sandbox.auth.service.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

  private final AuthService authService;

  public AuthController(AuthService authService) {
    this.authService = authService;
  }

  @PostMapping("/register")
  @ResponseStatus(HttpStatus.CREATED)
  public void register(@Valid @RequestBody RegisterDto request) {
    authService.register(request);
  }

  @PostMapping("/login")
  public ResponseDto login(@Valid @RequestBody LoginDto request) {
    return authService.login(request);
  }
}
