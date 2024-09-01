package com.parimal.linguaconnect.entity;

import java.util.List;

import org.hibernate.annotations.CurrentTimestamp;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
	private List<Role> roles;

	@OneToMany(mappedBy = "userInfo")
	private List<Token> tokens;
}
