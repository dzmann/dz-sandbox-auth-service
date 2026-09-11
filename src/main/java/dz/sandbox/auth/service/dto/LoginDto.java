package dz.sandbox.auth.service.dto;

import lombok.Data;

@Data
public class LoginDto {
  private String username;
  private String password;
}
