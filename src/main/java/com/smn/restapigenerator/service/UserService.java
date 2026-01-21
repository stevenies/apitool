package com.smn.restapigenerator.service;

import com.smn.restapigenerator.exception.ExceptionUserDoesntExist;
import com.smn.restapigenerator.exception.ExceptionUserExists;
import com.smn.restapigenerator.model.User;
import com.smn.restapigenerator.model.User.EmailStatus;
import com.smn.restapigenerator.persistence.UserRepository;
import com.smn.restapigenerator.util.CryptoUtil;
import com.smn.restapigenerator.util.StringUtil;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UserService {

	private static final Logger logger = LoggerFactory.getLogger(UserService.class);
	
	@Autowired
	private UserRepository userRepository;

	/**
	 * Create a new user account and send them a welcome email.  They must validate
	 * their email address before they can register for API access.
	 * @throws ExceptionUserExists if a user already exists with the same email or name.
	 */
    public User createUser(
		String nameFirst,
		String nameLast,
		String company,
		String email,
		String phone) throws ExceptionUserExists {
	
		// Determine if the user already exists.
		for (User aUser : this.userRepository.getAllUsers()) {
			String aUserEmail = aUser.getEmail();
			String aUserNameFirst = aUser.getNameFirst();
			String aUserNameLast = aUser.getNameLast();
			String aUserCompany = aUser.getCompany();
			if (aUserEmail.equalsIgnoreCase(email)) {
				throw new ExceptionUserExists();
			} else if (aUserNameFirst.equalsIgnoreCase(nameFirst) &&
				aUserNameLast.equalsIgnoreCase(nameLast) &&
				aUserCompany.equalsIgnoreCase(company)) {
				throw new ExceptionUserExists();
			}
		}

		// Create a new user.	
		User user = new User();
		user.setNameFirst(nameFirst);
		user.setNameLast(nameLast);
		user.setCompany(company);
		user.setEmail(email);
		user.setPhone(phone);

		try {
			this.userRepository.addUser(user);
			this.userRepository.saveToJsonFile();
		} catch (IOException e) {
			logger.error("Failed to save user to JSON file: {}", e.getMessage(), e);
		}
		return user;
    }
	
	/**
	 * Register a user for API access by setting their access token and access expiration date.
	 * @throws ExceptionUserDoesntExist if a user with the given email does not exist.
	 */
    public User activateUser(String email, String password) throws ExceptionUserDoesntExist {

		// Find the user and set their password.
		User user = this.findUserByEmail(email);
		user.setPassword(CryptoUtil.encrypt(password));
		user.setEmailStatus(EmailStatus.VERIFIED);

		// Set their access expiration date to 24 hours from now for use as a trial period.
		Calendar cal = Calendar.getInstance();
    	cal.add(Calendar.DAY_OF_YEAR, 1);
		Date date = cal.getTime();
		user.setAccessExpiryDate(date);

		try {
			this.userRepository.saveToJsonFile();
		} catch (IOException e) {
			logger.error("Failed to save user to JSON file: {}", e.getMessage(), e);
		}
		return user;
    }
	
	public List<User> getAllUsers() {
		return this.userRepository.getAllUsers();
	}

	public User findUserById(long id) throws ExceptionUserDoesntExist {
		User user = this.userRepository.findUserById(id);
		return user;
	}

	public User findUserByEmail(String email) throws ExceptionUserDoesntExist {
		User user = this.userRepository.findUserByEmail(email);
		return user;
	}

    public void updateUser(
        User user,
		String company,
        String nameFirst,
        String nameLast,
        String email,
        String phone,
        Date accessExpiry) {
	
		if (!StringUtil.isEmpty(company)) {
		    user.setCompany(StringUtil.trim(company));
		}
		if (!StringUtil.isEmpty(nameFirst)) {
		    user.setNameFirst(StringUtil.trim(nameFirst));
		}
		if (!StringUtil.isEmpty(nameLast)) {
		    user.setNameLast(StringUtil.trim(nameLast));
		}
		if (!StringUtil.isEmpty(email)) {
		    user.setEmail(StringUtil.trim(email));
		}
		if (!StringUtil.isEmpty(phone)) {
		    user.setPhone(StringUtil.trim(phone));
		}
		if (accessExpiry != null) {
		    user.setAccessExpiryDate(accessExpiry);
		}
		try {
			this.userRepository.saveToJsonFile();
		} catch (IOException e) {
			logger.error("Failed to save user to JSON file: {}", e.getMessage(), e);
		}
	}
 
	/**
	 * Update the user's access expiry date.
	 * @param user The user to update.
	 * @param accessExpiry Date when the license expires.  If null then deactivate the license.
	 */
	public void updateUser(User user, Date accessExpiry) {
		if (accessExpiry == null) {
			user.setAccessExpiryDate(new Date());
			user.setLicenseActive(false);
		} else {
		    user.setAccessExpiryDate(accessExpiry);
			user.setLicenseActive(true);
		}
		try {
			this.userRepository.saveToJsonFile();
		} catch (IOException e) {
			logger.error("Failed to save user to JSON file: {}", e.getMessage(), e);
		}
	}
 
}
