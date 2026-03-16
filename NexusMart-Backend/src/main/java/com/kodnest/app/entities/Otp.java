package com.kodnest.app.entities;

import java.time.LocalDateTime;
import java.util.Objects;

import jakarta.persistence.*;

@Entity
@Table(name = "otp")
public class Otp {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int otpid;

    @Column(nullable = false)
    private int otpvalue;

    @Column(nullable = false)
    private LocalDateTime createdat;

    @Column(nullable = false)
    private boolean verified;

    @ManyToOne
    @JoinColumn(name = "uid", nullable = false)
    private User user;

    // ✅ REQUIRED BY JPA
    public Otp() {
    }

    // ✅ ONLY SAFE CONSTRUCTOR
    public Otp(int otpvalue, LocalDateTime createdat, User user) {
        this.otpvalue = otpvalue;
        this.createdat = createdat;
        this.user = user;
        this.verified = false; // 🔐 IMPORTANT
    }

    // getters & setters

    public int getOtpid() {
        return otpid;
    }

    public int getOtpvalue() {
        return otpvalue;
    }

    public void setOtpvalue(int otpvalue) {
        this.otpvalue = otpvalue;
    }

    public LocalDateTime getCreatedat() {
        return createdat;
    }

    public void setCreatedat(LocalDateTime createdat) {
        this.createdat = createdat;
    }

    public boolean isVerified() {
        return verified;
    }

    public void setVerified(boolean verified) {
        this.verified = verified;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public String toString() {
        return "Otp [otpid=" + otpid +
               ", otpvalue=" + otpvalue +
               ", createdat=" + createdat +
               ", verified=" + verified +
               "]";
    }

    @Override
    public int hashCode() {
        return Objects.hash(otpid);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Otp other = (Otp) obj;
        return otpid == other.otpid;
    }
}
