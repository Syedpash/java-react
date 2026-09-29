 package com.eyeapss.taskmanager.controller;

  import com.eyeapss.taskmanager.dto.AuthResponse;
  import com.eyeapss.taskmanager.dto.LoginRequest;
  import com.eyeapss.taskmanager.dto.RegisterRequest;
  import com.eyeapss.taskmanager.entity.User;
  import com.eyeapss.taskmanager.repository.UserRepository;
  import com.eyeapss.taskmanager.security.JwtUtil;
  import lombok.RequiredArgsConstructor;
  import org.springframework.http.ResponseEntity;
  import org.springframework.security.authentication.AuthenticationManager;
  import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
  import org.springframework.security.core.userdetails.UserDetails;
  import org.springframework.security.core.userdetails.UserDetailsService;
  import org.springframework.security.crypto.password.PasswordEncoder;
  import org.springframework.web.bind.annotation.*;

  @RestController
  @RequestMapping("/auth")
  @RequiredArgsConstructor
  public class AuthController {

      private final UserRepository userRepository;
      private final PasswordEncoder passwordEncoder;
      private final AuthenticationManager authenticationManager;
      private final JwtUtil jwtUtil;
      private final UserDetailsService userDetailsService;

      @PostMapping("/register")
      public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest request) {
          if (userRepository.existsByUsername(request.getUsername())) {
              return ResponseEntity.badRequest().build();
          }
          if (userRepository.existsByEmail(request.getEmail())) {
              return ResponseEntity.badRequest().build();
          }

          User user = User.builder()
                  .username(request.getUsername())
                  .email(request.getEmail())
                  .password(passwordEncoder.encode(request.getPassword()))
                  .role(User.Role.ROLE_USER)
                  .build();

          userRepository.save(user);

          UserDetails userDetails = userDetailsService.loadUserByUsername(user.getUsername());
          String token = jwtUtil.generateToken(userDetails);

          return ResponseEntity.ok(new AuthResponse(token, user.getUsername(), user.getRole().name()));
      }

      @PostMapping("/login")
      public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
          authenticationManager.authenticate(
                  new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
          );

          UserDetails userDetails = userDetailsService.loadUserByUsername(request.getUsername());
          String token = jwtUtil.generateToken(userDetails);

          User user = userRepository.findByUsername(request.getUsername()).orElseThrow();

          return ResponseEntity.ok(new AuthResponse(token, user.getUsername(), user.getRole().name()));
      }
  }