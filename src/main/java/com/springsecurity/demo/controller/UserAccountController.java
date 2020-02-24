package com.springsecurity.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.springsecurity.demo.dto.CommonDTO;
import com.springsecurity.demo.dto.userDTO;
import com.springsecurity.demo.model.ConfirmationToken;
import com.springsecurity.demo.model.User;
import com.springsecurity.demo.service.EmailSenderService;
import com.springsecurity.demo.service.repository.ConfirmationTokenRepository;
import com.springsecurity.demo.service.repository.UserRepository;
import com.springsecurity.demo.utilities.Utility;

import io.swagger.annotations.Api;

@RestController
@RequestMapping("user")
@Api(tags="User Resgistration & Email Confirmation")
@CrossOrigin
public class UserAccountController {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private ConfirmationTokenRepository confirmationTokenRepository;

	@Autowired
	private EmailSenderService emailSenderService;

	Utility utils = new Utility();

	@PostMapping("/register")
	public CommonDTO registerUser(@RequestBody userDTO dto) throws Exception {
		CommonDTO result = new CommonDTO();
		try {
			if (validateUserDTO(dto)) {
				User existingUser = userRepository.findByEmailIdIgnoreCase(dto.getEmail());
				if (existingUser != null) {
					result.setResult(dto.getEmail() + " allready exists");
					return result;
				} else {
					User user = setUserModal(dto);
					userRepository.save(user);
					ConfirmationToken confirmationToken = new ConfirmationToken(user);
					confirmationTokenRepository.save(confirmationToken);
					emailSenderService.sendEmail(user, confirmationToken);
					result.setMessage("mail send to " + user.getEmailId());
					result.setResult("successfulRegisteration");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
		return result;
	}

	@GetMapping("/confirm-account")
	public CommonDTO confirmUserAccount(@RequestParam("token") String confirmationToken) throws Exception {
		CommonDTO result = new CommonDTO();
		try {
			if (utils.isNotNull(confirmationToken)) {
				ConfirmationToken token = confirmationTokenRepository.findByConfirmationToken(confirmationToken);
				if (token != null) {
					User user = userRepository.findByEmailIdIgnoreCase(token.getUser().getEmailId());
					user.setEnabled(true);
					userRepository.save(user);
					result.setResult("accountVerified");
				} else {
					result.setMessage("The link is invalid or broken!");
					result.setResult("error");
				}
			} else {
				result.setMessage("The link is invalid or broken!");
				result.setResult("error");
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}

		return result;
	}

	private User setUserModal(userDTO dto) throws Exception {
		User user = null;
		try {
			user = new User(dto.getEmail(), dto.getPassword(), dto.getFirstName(), dto.getLastName());
		} catch (Exception e) {
			throw e;
		}
		return user;
	}

	private boolean validateUserDTO(userDTO dto) throws Exception {
		boolean result = false;
		try {
			if (utils.isNotNull(dto.getEmail()) && utils.isNotNull(dto.getFirstName())
					&& utils.isNotNull(dto.getLastName()) && utils.isNotNull(dto.getPassword())) {
				result = true;
			}
		} catch (Exception e) {
			throw e;
		}
		return result;
	}

}
