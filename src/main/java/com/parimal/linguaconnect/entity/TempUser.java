package com.parimal.linguaconnect.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "tempuser")
@Data
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TempUser {
    @Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
    @Column(nullable = false)
	private String tempUsername;
    @Column(unique = true,nullable = false)
    private String tempEmail;
    @Column(nullable = false)
	private String tempPassword;

    @Column(name="verification_code")
	private String verificationCode;
	@Column(name="verification_expiration")
	private LocalDateTime verificationCodeExpiresAt;

}