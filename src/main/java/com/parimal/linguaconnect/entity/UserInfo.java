package com.parimal.linguaconnect.entity;

import java.util.ArrayList;

import org.hibernate.annotations.CurrentTimestamp;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.*;

@Entity
@Table(name = "userinfo")
@Data
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserInfo {
    @Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
    @Column(nullable = false)
	private String username;
    @Column(unique = true,nullable = false)
    private String email;
    @Column(nullable = false)
	private String password;
	@CurrentTimestamp
	@Column(updatable = false,nullable =false)
	@JsonIgnore
	private java.sql.Timestamp createdon;

    @ManyToMany(fetch = FetchType.EAGER,cascade = CascadeType.ALL)
	@JoinTable(name = "user_role",joinColumns = @JoinColumn(name = "user_id",referencedColumnName = "id"),
		inverseJoinColumns = @JoinColumn(name = "role_id",referencedColumnName = "id")
	)
	private List<Role> roles = new ArrayList<>();

	@OneToMany(mappedBy = "userInfo")
	private List<Token> tokens;
}
