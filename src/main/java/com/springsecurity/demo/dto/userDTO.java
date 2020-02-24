package com.springsecurity.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class userDTO {
	
	private String email;
	private String firstName;
	private String lastName;
	private String password;

}
