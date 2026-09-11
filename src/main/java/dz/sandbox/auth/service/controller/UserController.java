package dz.sandbox.auth.service.controller;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UserController {

  @GetMapping("/me")
  public String me(Authentication authentication) {
    return "Authenticated user: " + authentication.getName();
  }
}
