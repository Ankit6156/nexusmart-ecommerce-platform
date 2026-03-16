package com.kodnest.app.userControllers;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.kodnest.app.entities.LoginRequest;
import com.kodnest.app.entities.User;
import com.kodnest.app.userServices.AuthServiceContract;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
public class AuthController {

    private final AuthServiceContract authService;

    public AuthController(AuthServiceContract authService) {
        this.authService = authService;
    }

    // 🔐 STEP 1: LOGIN → SEND OTP
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {

        try {
            authService.authenticate(
                    loginRequest.getUsername(),
                    loginRequest.getPassword()
            );

            return ResponseEntity.ok(Map.of(
                    "message", "OTP sent to registered email"
            ));

        } catch (RuntimeException e) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    // ✅ STEP 2: VERIFY OTP → JWT + COOKIE
    @GetMapping("/verify")
    public ResponseEntity<?> verifyOtp(
            @RequestParam int otpvalue,
            HttpServletResponse response) {

        try {
            User user = authService.verifyOtp(otpvalue, response);

            return ResponseEntity.ok(Map.of(
                    "message", "OTP verified successfully",
                    "username", user.getUsername(),
                    "role", user.getRole().name()
            ));

        } catch (RuntimeException e) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", e.getMessage()));
        }
    }
    
 // 🔐 FORGOT PASSWORD → SEND OTP
    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestParam String username) {

        try {
            authService.sendForgotPasswordOtp(username);

            return ResponseEntity.ok(Map.of(
                    "message", "OTP sent to registered email"
            ));

        } catch (RuntimeException e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    // ✅ VERIFY FORGOT PASSWORD OTP
    @GetMapping("/forgot-password/verify-otp")
    public ResponseEntity<?> verifyForgotOtp(
            @RequestParam String username,
            @RequestParam int otpvalue) {

        try {
            authService.verifyForgotPasswordOtp(username, otpvalue);

            return ResponseEntity.ok(Map.of(
                    "message", "OTP verified. You can reset password now"
            ));

        } catch (RuntimeException e) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    // 🔁 RESET PASSWORD
    @PostMapping("/forgot-password/reset")
    public ResponseEntity<?> resetPassword(
            @RequestParam String username,
            @RequestParam String newPassword) {

        authService.resetPassword(username, newPassword);

        return ResponseEntity.ok(Map.of(
                "message", "Password reset successful"
        ));
    }
    
    @PostMapping("/logout")
    public ResponseEntity<Map<String, String>> logout(HttpServletRequest request, HttpServletResponse response) {
    try {
    // Retrieve authenticated user from the request
    User user = (User) request.getAttribute("authenticatedUser");
    // Delegate logout operation to the service layer
    authService.logout(user);
    // Clear the authentication token cookie
    Cookie cookie = new Cookie("authToken", null);
    cookie.setHttpOnly(true);
    cookie.setMaxAge(0);
    cookie.setPath("/");
    response.addCookie(cookie);
    // Success response
    Map<String, String> responseBody = new HashMap<>();
    responseBody.put("message", "Logout successful");
    return ResponseEntity.ok(responseBody);
    } catch (RuntimeException e) {
    // Error response
    Map<String, String> errorResponse = new HashMap<>();
    errorResponse.put("message", "Logout failed");
    return ResponseEntity.status(500).body (errorResponse);
       }
   
    }
    
    
}
