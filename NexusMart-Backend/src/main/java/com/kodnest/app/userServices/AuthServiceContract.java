package com.kodnest.app.userServices;

import com.kodnest.app.entities.User;

import jakarta.servlet.http.HttpServletResponse;

public interface AuthServiceContract {

    public User authenticate(String username, String password);
    public String generateToken(User user);
   public  String generateNewToken(User user);
   public  void saveToken(User user, String token);
   public  User verifyOtp(int otp, HttpServletResponse response);
   public void sendForgotPasswordOtp(String username);
   public void verifyForgotPasswordOtp(String username, int otp);
  public  void resetPassword(String username, String newPassword);
  public boolean validateToken(String token);
  public String extractUsername(String token);
  public void logout (User user);
}
