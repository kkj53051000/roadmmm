package com.roadmmm.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.TableGenerator;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter @Setter
@NoArgsConstructor
@TableGenerator(
		name = "USER_SEQ_GENERATOR",
		table = "ROADMMM_SEQUENCES",
		pkColumnValue = "USER_SEQ", allocationSize = 50)
public class User {
	@Id @GeneratedValue(strategy = GenerationType.AUTO, generator = "USER_SEQ_GENERATOR")
	@Column(name = "user_id")
	private long id;
	
	private String userid;
	private String userpw;
	private String nickname;
	private String email;
	
	public User(String userid, String userpw, String nickname, String email) {
		this.userid = userid;
		this.userpw = userpw;
		this.nickname = nickname;
		this.email = email;
	}
}
