package com.kodnest.app.repositories;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.kodnest.app.entities.Otp;
import com.kodnest.app.entities.User;

@Repository
public interface OtpRepository extends JpaRepository<Otp, Integer>{
	
		public Otp findByOtpvalue(int otpvalue);
		public void deleteByUser(User user);
		Optional<Otp> findByUserAndOtpvalue(User user, int otpvalue);
		Optional<Otp> findLatestByUser(User user);
}
