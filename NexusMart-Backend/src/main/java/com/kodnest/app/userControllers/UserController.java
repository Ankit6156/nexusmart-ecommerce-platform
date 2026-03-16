package com.kodnest.app.userControllers;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kodnest.app.entities.User;
import com.kodnest.app.entities.Userdao;
import com.kodnest.app.userServices.UserServiceContract;

import jakarta.servlet.http.HttpServletRequest;

@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
@RestController
@RequestMapping("/api/users")
public class UserController {
	
	private final UserServiceContract userService;

	public UserController(UserServiceContract userService) {
		super();
		this.userService = userService;
	}
	
	@PostMapping("/register")
	public ResponseEntity<?> regiserUser(@RequestBody User user) {
		try {
		User registeredUser = userService.registerUser(user);
		return ResponseEntity.ok(Map.of("message", "User registered Successfully", "user", new Userdao(registeredUser.getUserId(), registeredUser.getUsername(), registeredUser.getEmail(),  registeredUser.getRole().toString()) ));
		} catch (RuntimeException e) {
			return  ResponseEntity.badRequest().body(Map.of("error",  e.getMessage()));
		}
	}

    @GetMapping("/profile")
    public ResponseEntity<?> getProfile(HttpServletRequest request) {

        //  AuthenticationFilter already sets authenticated user
        User user = (User) request.getAttribute("authenticatedUser");

        if (user == null) {
            return ResponseEntity.status(401)
                    .body("User not authenticated");
        }

        return ResponseEntity.ok(user);
    }
    
}
