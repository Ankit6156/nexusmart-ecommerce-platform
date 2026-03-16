package com.kodnest.app.userServiceImplementations;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.kodnest.app.entities.User;
import com.kodnest.app.repositories.UserRepository;
import com.kodnest.app.userServices.UserServiceContract;

@Service
public class UserService  implements UserServiceContract{
	
	private final UserRepository userRepository;
	private final BCryptPasswordEncoder passwordEncoder;
	
 @Autowired
	public UserService(UserRepository userRepository) {
		super();
		this.userRepository = userRepository;
		passwordEncoder = new BCryptPasswordEncoder();
	}

	@Override
	public User registerUser(User user) {
		//  Check if username and email is aleady exists
		if(userRepository.findByUsername(user.getUsername()).isPresent()) {
			throw new RuntimeException("Username is already taken");
		}
		if(userRepository.findByEmail(user.getEmail()).isPresent()) {
			throw new RuntimeException("Email is already register");
		}
		// Encode password
		user.setPassword(passwordEncoder.encode(user.getPassword()));
		
		// save the user
		return userRepository.save(user);
	}

}
