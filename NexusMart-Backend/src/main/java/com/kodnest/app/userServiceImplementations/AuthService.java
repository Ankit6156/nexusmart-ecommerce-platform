package com.kodnest.app.userServiceImplementations;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Optional;
import java.util.Random;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kodnest.app.entities.JWTToken;
import com.kodnest.app.entities.Otp;
import com.kodnest.app.entities.User;
import com.kodnest.app.repositories.JWTTokenRepository;
import com.kodnest.app.repositories.OtpRepository;
import com.kodnest.app.repositories.UserRepository;
import com.kodnest.app.userServices.AuthServiceContract;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;

@Service
public class AuthService implements AuthServiceContract {

    private final UserRepository userRepository;
    private final JWTTokenRepository jwtTokenRepository;
    private final OtpRepository otpRepository;
	JavaMailSender mailSender;
    private final BCryptPasswordEncoder passwordEncoder;
    private final Key signingKey;

    public AuthService(
            UserRepository userRepository,
            JWTTokenRepository jwtTokenRepository,
            OtpRepository otpRepository,
          	JavaMailSender mailSender,
            @Value("${jwt.secret}") String jwtSecret) {

        this.userRepository = userRepository;
        this.jwtTokenRepository = jwtTokenRepository;
        this.otpRepository =  otpRepository;
        this.mailSender =  mailSender;
        this.passwordEncoder = new BCryptPasswordEncoder();

        //  HS512 requires at least 64 bytes
        if (jwtSecret.getBytes(StandardCharsets.UTF_8).length < 64) {
            throw new IllegalArgumentException(
                "jwt.secret must be at least 64 bytes for HS512"
            );
        }

        this.signingKey = Keys.hmacShaKeyFor(
                jwtSecret.getBytes(StandardCharsets.UTF_8)
        );
    }

    //  AUTHENTICATION
    @Transactional
    @Override
    public User authenticate(String username, String password) {

        User user = userRepository.findByUsername(username)
                .orElseThrow(() ->
                        new RuntimeException("Invalid username or password"));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("Invalid username or password");
        }

        //  Remove old OTP if exists
        otpRepository.deleteByUser(user);
        otpRepository.flush(); 

        // ✅ Generate 6-digit OTP
        int otp = 100000 + new Random().nextInt(900000);

        Otp newOtp = new Otp(otp, LocalDateTime.now(), user);
        otpRepository.save(newOtp);

        // ✅ Send OTP mail
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(user.getEmail());
        message.setSubject("Sales Savvy - Login OTP");
        message.setText(
            "Hello " + user.getUsername() +
            "\n\nYour OTP is: " + otp +
            "\nValid for 2 minutes.\n\nThank you."
        );

        mailSender.send(message);

        return user;
    }


    //  TOKEN HANDLING (reuse if valid)
    @Override
    public String generateToken(User user) {

        LocalDateTime now = LocalDateTime.now();

        Optional<JWTToken> existingTokenOpt =
                jwtTokenRepository.findByUser_UserId(user.getUserId());


        if (existingTokenOpt.isPresent()) {
            JWTToken existingToken = existingTokenOpt.get();

            if (now.isBefore(existingToken.getExpiresAt())) {
                return existingToken.getToken(); // reuse token
            } else {
                jwtTokenRepository.delete(existingToken); // expired
            }
        }

        String newToken = generateNewToken(user);
        saveToken(user, newToken);
        return newToken;
    }

    //  CREATE NEW JWT
    @Override
    public String generateNewToken(User user) {

        return Jwts.builder()
                .setSubject(user.getUsername())
                .claim("role", user.getRole().name())
                .setIssuedAt(new Date())
                .setExpiration(
                        new Date(System.currentTimeMillis() + 3600_000) // 1 hour
                )
                .signWith(signingKey, SignatureAlgorithm.HS512)
                .compact();
    }

    //  SAVE TOKEN
    @Override
    public void saveToken(User user, String token) {

        JWTToken jwtToken = new JWTToken(
                user,
                token,
                LocalDateTime.now().plusHours(1)
        );

        jwtTokenRepository.save(jwtToken);
    }
    @Transactional
	@Override
	public User verifyOtp(int otp, HttpServletResponse response) {

	    Otp ref = otpRepository.findByOtpvalue(otp);

	    if (ref == null) {
	        throw new RuntimeException("Invalid OTP");
	    }

	    // ⏱ OTP expiry check (2 minutes)
	    long minutes =
	        ChronoUnit.MINUTES.between(
	            ref.getCreatedat(),
	            LocalDateTime.now()
	        );

	    if (minutes >= 2) {
	        otpRepository.delete(ref);
	        throw new RuntimeException("OTP expired");
	    }

	    User user = ref.getUser();

	    // ✅ Generate JWT
	    String token = generateNewToken(user);
	    saveToken(user, token);

	    //  Set cookie
	    Cookie cookie = new Cookie("authToken", token);
	    cookie.setHttpOnly(true);
	    cookie.setSecure(false); // true in production (HTTPS)
	    cookie.setPath("/");
	    cookie.setMaxAge(3600);

	    response.addCookie(cookie);

	    //  Remove OTP after success
	    otpRepository.delete(ref);

	    return user;
	}
	
	//  SEND OTP FOR FORGOT PASSWORD
    @Transactional
	@Override
	public void sendForgotPasswordOtp(String username) {

	    User user = userRepository.findByUsername(username)
	            .orElseThrow(() -> new RuntimeException("User not found"));

	    // Remove old OTP
	    otpRepository.deleteByUser(user);
	    otpRepository.flush();
	    int otp = 100000 + new Random().nextInt(900000);

	    Otp newOtp = new Otp(otp, LocalDateTime.now(), user);
	    otpRepository.save(newOtp);

	    SimpleMailMessage message = new SimpleMailMessage();
	    message.setTo(user.getEmail());
	    message.setSubject("Sales Savvy - Password Reset OTP");
	    message.setText(
	            "Hello " + user.getUsername() +
	            "\n\nYour password reset OTP is: " + otp +
	            "\nValid for 2 minutes.\n\nThank you."
	    );

	    mailSender.send(message);
	}

	// ✅ VERIFY FORGOT PASSWORD OTP
	@Override
	public void verifyForgotPasswordOtp(String username, int otp) {

	    User user = userRepository.findByUsername(username)
	            .orElseThrow(() -> new RuntimeException("User not found"));

	    Otp ref = otpRepository.findByUserAndOtpvalue(user, otp)
	            .orElseThrow(() -> new RuntimeException("Invalid OTP"));

	    long minutes = ChronoUnit.MINUTES.between(
	            ref.getCreatedat(),
	            LocalDateTime.now()
	    );

	    if (minutes >= 2) {
	        otpRepository.delete(ref);
	        throw new RuntimeException("OTP expired");
	    }

	    // ✅ MARK VERIFIED
	    ref.setVerified(true);
	    otpRepository.save(ref);
	}

	

	//  RESET PASSWORD
	@Override
	public void resetPassword(String username, String newPassword) {

	    User user = userRepository.findByUsername(username)
	            .orElseThrow(() -> new RuntimeException("User not found"));

	    Otp ref = otpRepository.findLatestByUser(user)
	            .orElseThrow(() -> new RuntimeException("OTP not verified"));

	    if (!ref.isVerified()) {
	        throw new RuntimeException("OTP verification required");
	    }

	    user.setPassword(passwordEncoder.encode(newPassword));
	    userRepository.save(user);

	    //  Remove OTP after reset
	    otpRepository.delete(ref);
	}

	@Override
	public boolean validateToken(String token) {

		    try {
		        System.err.println("VALIDATING TOKEN...");

		        // 🔐 Parse and validate JWT signature
		        Jwts.parserBuilder()
		                .setSigningKey(signingKey)
		                .build()
		                .parseClaimsJws(token);

		        // 📦 Check token in database
		        Optional<JWTToken> jwtToken =
		                jwtTokenRepository.findByToken(token);

		        if (jwtToken.isPresent()) {

		            System.err.println(
		                    "Token Expiry: " + jwtToken.get().getExpiresAt()
		            );
		            System.err.println(
		                    "Current Time: " + LocalDateTime.now()
		            );

		            return jwtToken.get()
		                    .getExpiresAt()
		                    .isAfter(LocalDateTime.now());
		        }

		        return false;

		    } catch (Exception e) {
		        System.err.println(
		                "Token validation failed: " + e.getMessage()
		        );
		        return false;
		    }

	}

	@Override
	public String extractUsername(String token) {
		return Jwts.parserBuilder()
	            .setSigningKey(signingKey)
	            .build()
	            .parseClaimsJws(token)
	            .getBody()
	            .getSubject();
	}
	@Transactional
	  @Override
	    public void logout(User user) {
	        jwtTokenRepository.deleteByUserId(user.getUserId());
	    }

}
