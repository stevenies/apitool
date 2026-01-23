package com.smn.restapigenerator.ui;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.smn.restapigenerator.exception.ExceptionUserDoesntExist;
import com.smn.restapigenerator.exception.ExceptionUserExists;
import com.smn.restapigenerator.model.ApiCode;
import com.smn.restapigenerator.model.ApiSpec;
import com.smn.restapigenerator.model.User;
import com.smn.restapigenerator.model.uml.DomainModel;
import com.smn.restapigenerator.model.uml.Entity;
import com.smn.restapigenerator.service.EmailService;
import com.smn.restapigenerator.service.ToolService;
import com.smn.restapigenerator.service.UserService;
import com.smn.restapigenerator.service.ToolService.DtoReadUMLFile;
import com.smn.restapigenerator.util.CryptoUtil;
import com.smn.restapigenerator.util.StringUtil;
import com.smn.restapigenerator.util.ZipUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.zip.ZipOutputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;
import static com.smn.restapigenerator.util.StringUtil.tabs;

@Controller
public class UIController {

	public static final int LICENSE_COST_ONE_DAY = 1;
	public static final int LICENSE_COST_ONE_WEEK = 149;
	public static final int LICENSE_COST_ONE_MONTH = 499;

	private static final String VIEW_API_SPEC_FORM = "viewApiSpecForm";
    private static final String VIEW_API_CODE_FORM = "viewApiCodeForm";
	private static final String VIEW_USER_ADMIN_FORM = "viewUsers";

	private static final Logger logger = LoggerFactory.getLogger(UIController.class);

	@Autowired
	private UserService userService;

	@Autowired
	private ToolService toolService;

	@Autowired
	private EmailService emailService;

	@Value("${paypal.client-id}")
    private String paypalClientId;

	@Value("${paypal.sandbox-client-id}")
    private String paypalSandboxClientId;

	@PostMapping("/register")
	public String register(
		@RequestParam(required = false, defaultValue = "") String nameFirst,
		@RequestParam(required = false, defaultValue = "") String nameLast,
		@RequestParam(required = false, defaultValue = "") String company,
		@RequestParam(required = false, defaultValue = "") String email,
		@RequestParam(required = false, defaultValue = "") String phone,
		HttpServletRequest request) {

		List<String> errors = new ArrayList<>();
		request.setAttribute("loginErrors", null);
		request.setAttribute("registrationErrors", errors);
		request.setAttribute("registrationSuccess", false);

		// Verify that the required fields are provided.
		if (StringUtil.isEmpty(nameFirst)) {
			errors.add("Enter your first name");
		} else {
			nameFirst = StringUtil.trim(nameFirst);
			request.setAttribute("nameFirst", nameFirst);
		}

		if (StringUtil.isEmpty(nameLast)) {
			errors.add("Enter your last name");
		} else {
			nameLast = StringUtil.trim(nameLast);
			request.setAttribute("nameLast", nameLast);
		}

		if (StringUtil.isEmpty(company)) {
			errors.add("Enter your company name or 'Self' if not employed");
		} else {
			company = StringUtil.trim(company);
			request.setAttribute("company", company);
		}

		if (StringUtil.isEmpty(email) || !StringUtil.isValidEmail(email)) {
			errors.add("Enter a valid email address using the format user@example.com");
		} else {
			email = StringUtil.trim(email);
			request.setAttribute("email", email);
		}

		if (StringUtil.isEmpty(phone) || !StringUtil.isValidPhone(phone)) {
			errors.add("Enter a valid phone number using the format 555-123-4567 (US) or<br/> +44 20 7946 0958 (International)");
		} else {
			phone = StringUtil.trim(phone);
			request.setAttribute("phone", phone);
		}

		if (errors.isEmpty()) {
			try {
				this.userService.createUser(nameFirst, nameLast, company, email, phone);
				logger.info("User created successfully: {} {} {} {} {}", nameFirst, nameLast, company, email, phone);

				// Send the user an email to validate their email address.
				sendEmailValidateAddress(request, email);

			} catch (ExceptionUserExists e) {
				errors.add("Another user with the same name or email address already exists");
				logger.warn("Account already exists: {} {} {} {} {}", nameFirst, nameLast, company, email, phone);

			} catch (Throwable t) {
				errors.add("An unexpected error occurred: " + t.getMessage());
				logger.error("An unexpected error occurred while sending email to {}: {}", email, t.getMessage(), t);
			}
		}

		request.setAttribute("registrationSuccess", errors.isEmpty());
		return "login";
	}

	@GetMapping("/emailVerified")
	public String emailVerified(
		@RequestParam(required = false, defaultValue = "") String email,
		@RequestParam(required = false, defaultValue = "0") int key,
		HttpServletRequest request) {

		List<String> errors = new ArrayList<>();
		request.setAttribute("enrollmentErrors", errors);
		request.setAttribute("emailVerified", true);

		int masterKey = StringUtil.makeKey(email);
		if (key != masterKey) {
			errors.add("Email verification failed. The provided key is invalid.");
			logger.warn("Email verification failed for email: {}", email);
			request.setAttribute("emailVerified", false);
			return "activationForm";
		}

		email = StringUtil.trim(email);
		request.setAttribute("email", email);
		return "activationForm";
	}

	@PostMapping("/activate")
	public String activate(
		@RequestParam(required = false, defaultValue = "") String email,
		@RequestParam(required = false, defaultValue = "") String password,
		HttpServletRequest request,
		HttpSession session) {

		List<String> errors = new ArrayList<>();
		request.setAttribute("enrollmentErrors", errors);

		email = StringUtil.trim(email);
		request.setAttribute("email", email);

		// Verify that the required fields are provided.
		if (StringUtil.isEmpty(password)) {
			errors.add("Enter a password to secure your account");
		} else {
			password = StringUtil.trim(password);
			request.setAttribute("password", password);
		}

		User user = null;
		if (errors.isEmpty()) {
			try {
				user = this.userService.activateUser(email, password);
				logger.info("User activated successfully: {}", email);
			} catch (ExceptionUserDoesntExist e) {
				errors.add("An account doesn't exist with the specified email address");
				logger.warn("Account doesn't exist for email: {}", email);
			} catch (Throwable t) {
				errors.add("An unexpected error occurred: " + t.getMessage());
				logger.error("An unexpected error occurred while activating user {}: {}", email, t.getMessage(), t);
			}
		}

		if (user == null || !errors.isEmpty()) {
			return "activationForm";
		} else {
			ApiSpec apiSpec = user.getApiSpec();
			ApiCode apiCode = user.getApiCode();

			session.setAttribute("user", user);
			session.setAttribute("apiSpec", apiSpec);
			session.setAttribute("apiCode", apiCode);
			return "apiSpecForm";
		}
	}

	@GetMapping("/viewPasswordResetForm")
	public String viewPasswordResetForm(
		@RequestParam(required = false, defaultValue = "") String email,
		@RequestParam(required = false, defaultValue = "0") int key,
		HttpServletRequest request) {

		List<String> errors = new ArrayList<>();
		request.setAttribute("errors", errors);

		int masterKey = StringUtil.makeKey(email);
		if (key != masterKey) {
			errors.add("Password reset failed. The provided key is invalid.");
			logger.warn("Password reset key verification failed for email: {}", email);
		}

		request.setAttribute("key", key);
		request.setAttribute("email", StringUtil.trim(email));
		return "passwordResetForm";
	}

	@PostMapping("/resetPassword")
	public String resetPassword(
		@RequestParam(required = false, defaultValue = "0") int key,
		@RequestParam(required = false, defaultValue = "") String email,
		@RequestParam(required = false, defaultValue = "") String password,
		@RequestParam(required = false, defaultValue = "") String confirmPassword,
		HttpServletRequest request) {

		List<String> errors = new ArrayList<>();
		request.setAttribute("errors", errors);
		request.setAttribute("key", key);
		request.setAttribute("email", email);

		// Verify that the required fields are provided.
		if (StringUtil.isEmpty(password)) {
			errors.add("Enter your new password");
		} else if (StringUtil.isEmpty(confirmPassword)) {
			errors.add("Confirm your new password");
		} else if (!password.equals(confirmPassword)) {
			errors.add("The new password and confirmation do not match");
		}

		User user = null;
		if (errors.isEmpty()) {
			try {
				user = this.userService.findUserByEmail(email);
				this.userService.updateUser(user, password);
			} catch (ExceptionUserDoesntExist e) {
				errors.add("An account doesn't exist for email " + email);
			}
		}

		if (user == null || !errors.isEmpty()) {
			for (String error : errors) {
				logger.warn("Password reset error for user {}: {}", email, error);
			}
		} else {
			request.setAttribute("success", true);
		}
		return "passwordResetForm";
	}

	@PostMapping("/login")
	public String login(
		@RequestParam(required = false, defaultValue = "") String email,
		@RequestParam(required = false, defaultValue = "") String password,
		@RequestParam(required = false, defaultValue = "false") boolean forgotPassword,
		@RequestParam(required = false, defaultValue = "false") String referrer,
	    @Value("${application.usePayPalSandbox}") boolean usePayPalSandbox,
	    @Value("${application.includeDebugLicense}") boolean includeDebugLicense,
		HttpServletRequest request,
		HttpSession session) {

		List<String> errors = new ArrayList<>();
		request.setAttribute("referrer", referrer);
		request.setAttribute("loginErrors", errors);
		request.setAttribute("registrationErrors", null);
		request.setAttribute("includeDebugLicense", includeDebugLicense);

		// Verify that the required fields are provided.
		if (StringUtil.isEmpty(email) || !StringUtil.isValidEmail(email)) {
			errors.add("Enter a valid email address");
		}

		if (!forgotPassword && StringUtil.isEmpty(password)) {
			errors.add("Enter the password you specified when your account was registered");
		}

		User user = null;
		if (errors.isEmpty()) {
			try {
				user = this.userService.findUserByEmail(email);
				if (user == null) {
					errors.add("An account doesn't exist for email " + email);
				} else if (!forgotPassword) {
					String encryptedPassword = CryptoUtil.encrypt(password);
					if (encryptedPassword == null || !encryptedPassword.equals(user.getPassword())) {
						errors.add("The password is incorrect");
					}
				}
			} catch (ExceptionUserDoesntExist e) {
				errors.add("An account doesn't exist for email " + email);
			}
		}

		if (user == null || !errors.isEmpty()) {
			for (String error : errors) {
				logger.warn("Failed login: {}", error);
			}
			return "login";

		} else {
			if (forgotPassword) {
				try {
					this.sendEmailResetPassword(request, email);
				} catch (Exception e) {
					errors.add("An error occurred while sending the password reset email: " + e.getMessage());
					logger.error("Error sending password reset email to {}: {}", email, e.getMessage(), e);
				}
				request.setAttribute("resetPasswordEmailed", true);
				return "login";
			}

			session.setAttribute("user", user);

			// Determine whether the license has expired
			if (!user.isAdmin()) {
				Date now = new Date();
				Date accessExpiry = user.getAccessExpiryDate();
				if (accessExpiry != null && now.after(accessExpiry)) {
					logger.warn("License expired for user: {}", email);
					request.setAttribute("paypalClientId", usePayPalSandbox ? paypalSandboxClientId : paypalClientId);
					return "buyLicense";
				}
			}

			// The license is valid. Proceed to log in the user.
			ApiSpec apiSpec = user.getApiSpec();
			ApiCode apiCode = user.getApiCode();

			session.setAttribute("apiSpec", apiSpec);
			session.setAttribute("apiCode", apiCode);

			boolean viewApiSpecForm = VIEW_API_SPEC_FORM.equalsIgnoreCase(referrer);
			boolean viewApiCodeForm = VIEW_API_CODE_FORM.equalsIgnoreCase(referrer);
			boolean viewaAdminForm = VIEW_USER_ADMIN_FORM.equalsIgnoreCase(referrer);

			if (viewApiSpecForm) {
				logger.info("Successful login - redirecting to API Specification Form: {}", email);
				return "apiSpecForm";
			} else if (viewApiCodeForm) {
				logger.info("Successful login - redirecting to API Code Form: {}", email);
				return "apiCodeForm";
			} else if (viewaAdminForm) {
				logger.info("Successful login - redirecting to User Admin Form: {}", email);
				return "redirect:viewUsers";
			} else {
				logger.error("Invalid referring page for user: {}", email);
				return "login";
			}
		}
	}

	@GetMapping("/viewApiSpecForm")
	public String viewAPISpecForm(
		Model model,
	    @Value("${application.usePayPalSandbox}") boolean usePayPalSandbox,
		HttpServletRequest request,
		HttpSession session) {

		request.setAttribute("referrer", VIEW_API_SPEC_FORM);
		request.setAttribute("errors", new ArrayList<>());

		// Verify that the user session is valid.
		User user = (User) session.getAttribute("user");
		if (user == null || user.getPassword() == null || user.getPassword().isEmpty()) {
			return "login";
		}
		session.setAttribute("user", user);

		// Determine whether the license has expired
		if (!user.isAdmin()) {
			Date now = new Date();
			Date accessExpiry = user.getAccessExpiryDate();
			if (accessExpiry != null && now.after(accessExpiry)) {
				String email = user.getEmail();
				logger.warn("License expired for user: {}", email);
				request.setAttribute("paypalClientId", usePayPalSandbox ? paypalSandboxClientId : paypalClientId);
				return "buyLicense";
			}
		}

		// The license is valid. Show the API Specification Form.
		ApiSpec apiSpec = user.getApiSpec();
		session.setAttribute("apiSpec", apiSpec);
		return "apiSpecForm";
	}

	@PostMapping("/doApiSpecForm")
	public String doApiSpecForm (
		@RequestParam(required = false) MultipartFile domainModel,
        @RequestParam(required = false) String title,
        @RequestParam(required = false) String description,
		@RequestParam(required = false) String version,
		@RequestParam(required = false) boolean makeGET,
		@RequestParam(required = false) boolean makePOST,
		@RequestParam(required = false) boolean makePUT,
		@RequestParam(required = false) boolean makeDELETE,
		@RequestParam(required = false) boolean makeSEARCH,
		@RequestParam(required = false) String serverDomain,
		@RequestParam(required = false) String contextRoot,
		@RequestParam(required = false) String port,
		HttpServletRequest request,
		HttpSession session) {

		List<String> errors = new ArrayList<>();
		request.setAttribute("errors", errors);

		// Verify that the user session is valid.
		User user = (User) session.getAttribute("user");
		if (user == null || user.getPassword() == null || user.getPassword().isEmpty()) {
			return "login";
		}
		String email = user.getEmail();

        try {
			this.generateApiSpec(
				user, title, description, version, domainModel,
				makeSEARCH, makeGET, makePOST, makePUT, makeDELETE,
				serverDomain, contextRoot, port, errors);
			boolean success = errors.size() == 0;
			if (success) {
				logger.info("API specification generated successfully for user: {}", email);
				ApiSpec apiSpec = user.getApiSpec();
				session.setAttribute("apiSpec", apiSpec);
			} else {
				logger.info("API specification generation FAILED for user: {}", email);
			}
		} catch (Exception e) {
			logger.error("Failed to process API Spec form data: {}", e.getMessage(), e);
			errors.add(e.getMessage());
		}
		return "apiSpecForm";
	}

	@GetMapping("/viewApiCodeForm")
	public String viewAPICodeForm(
		@Value("${application.usePayPalSandbox}") boolean usePayPalSandbox,
		HttpServletRequest request,
		HttpSession session) {

		request.setAttribute("referrer", VIEW_API_CODE_FORM);
		request.setAttribute("errors", new ArrayList<>());

		// Verify that the user session is valid.
		User user = (User) session.getAttribute("user");
		if (user == null || user.getPassword() == null || user.getPassword().isEmpty()) {
			return "login";
		}

		// Determine whether the license has expired
		if (!user.isAdmin()) {
			Date now = new Date();
			Date accessExpiry = user.getAccessExpiryDate();
			if (accessExpiry != null && now.after(accessExpiry)) {
				String email = user.getEmail();
				logger.warn("License expired for user: {}", email);
				request.setAttribute("paypalClientId", usePayPalSandbox ? paypalSandboxClientId : paypalClientId);
				return "buyLicense";
			}
		}

		// The license is valid. Show the API Code Form.
		ApiSpec apiSpec = user.getApiSpec();
 		ApiCode apiCode = user.getApiCode();

		session.setAttribute("user", user);
		session.setAttribute("apiSpec", apiSpec);
		session.setAttribute("apiCode", apiCode);
		return "apiCodeForm";
	}

	@PostMapping("/doApiCodeForm")
	public String doApiCodeForm (
		HttpServletRequest request,
		HttpSession session) {

		List<String> errors = new ArrayList<>();
		request.setAttribute("errors", errors);

		// Verify that the user session is valid.
		User user = (User) session.getAttribute("user");
		if (user == null || user.getPassword() == null || user.getPassword().isEmpty()) {
			return "login";
		}
		String email = user.getEmail();

        ApiCode apiCode = null;
		try {
			apiCode = this.toolService.generateCode(user, errors);
			boolean success = errors.size() == 0;
			if (success) {
				logger.info("API code generated successfully for user: {}", email);
			} else {
				logger.info("API code generation FAILED for user: {}", email);
			}
		} catch (Throwable t) {
			logger.error("Failed to process API Code form data: {}", t.getMessage(), t);
			request.setAttribute("errors", List.of("Failed to process form data: " + t.getMessage()));
		}
		session.setAttribute("apiCode", apiCode);
		return "apiCodeForm";
	}

	@GetMapping("/apiSpecFile")
    public ResponseEntity<InputStreamResource> getApiSpecFile(HttpSession session) {

		User user = (User) session.getAttribute("user");
		if (user == null || user.getPassword() == null || user.getPassword().isEmpty()) {
            return ResponseEntity.notFound().build();
		}

		// Obtain the filesystem path to the API specification file.
		ApiSpec apiSpec = user.getApiSpec();
		if (!apiSpec.isValid()) {
			return ResponseEntity.notFound().build();
		}
		File swaggerFile = apiSpec.getSwaggerFile();
        if (!swaggerFile.exists()) {
            return ResponseEntity.notFound().build();
        }

		// Stream the file's contents to the caller.
        InputStreamResource resource;
		try {
			resource = new InputStreamResource(new FileInputStream(swaggerFile));
			return ResponseEntity.ok()
				.contentLength(swaggerFile.length())
				.contentType(MediaType.APPLICATION_JSON)
				.body(resource);
		} catch (FileNotFoundException e) {
			logger.error("API specification file not found: {}", e.getMessage(), e);
            return ResponseEntity.notFound().build();
		}
     }

	@PostMapping("/apiSpecFile")
	public ResponseEntity<String> saveApiSpecFile(@RequestBody String content, HttpSession session) {

		User user = (User) session.getAttribute("user");
		if (user == null || user.getPassword() == null || user.getPassword().isEmpty()) {
			return ResponseEntity.status(HttpServletResponse.SC_FORBIDDEN).body("Unauthorized");
		}

		// Obtain the filesystem path to the API specification file.
		ApiSpec apiSpec = user.getApiSpec();
		if (!apiSpec.isValid()) {
			return ResponseEntity.notFound().build();
		}
		File swaggerFile = apiSpec.getSwaggerFile();
        if (!swaggerFile.exists()) {
            return ResponseEntity.notFound().build();
        }

		// Save the uploaded text to the specified file (adjust path as needed)
		try {
			Path filePath = swaggerFile.toPath();
			Files.createDirectories(filePath.getParent());
			Files.writeString(filePath, content, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
			logger.info("API specification file updated successfully for user: {}", user.getEmail());
			return ResponseEntity.ok("File saved successfully.");
		} catch (Exception e) {
			logger.error("Failed to update API specification file for user {}: {}", user.getEmail(), e.getMessage(), e);
			return ResponseEntity.status(500).body("Failed to save file: " + e.getMessage());
		}
	}

    @GetMapping("/apiCodeZipFile")
    public ResponseEntity<StreamingResponseBody> apiCodeZipFile(HttpSession session) {

		User user = (User) session.getAttribute("user");
		if (user == null || user.getPassword() == null || user.getPassword().isEmpty()) {
            return ResponseEntity.notFound().build();
		}

		// Obtain the filesystem path to the API code zip file.
		ApiCode apiCode = user.getApiCode();
		if (!apiCode.isValid()) {
			return ResponseEntity.notFound().build();
		}
		File apiCodeDir = apiCode.getApiCodeDir();
        if (!apiCodeDir.exists()) {
            return ResponseEntity.notFound().build();
		}

		// Stream the directory as a ZIP file.
		StreamingResponseBody stream = outputStream -> {
			try (ZipOutputStream zos = new ZipOutputStream(outputStream)) {
				ZipUtil.zipDirectoryRecursive(apiCodeDir, apiCodeDir.getName(), zos);
				zos.finish();
			}
		};
		return ResponseEntity.ok()
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + apiCodeDir.getName() + ".zip\"")
				.contentType(MediaType.APPLICATION_OCTET_STREAM)
				.body(stream);
	}

	@GetMapping("/viewUsers")
	public String viewUsers(
		@RequestParam(required = false, defaultValue = "0") long userId,
		HttpServletRequest request,
		HttpSession session) {
	
		// Verify that the user session is valid and user has admin privileges
		User user = (User) session.getAttribute("user");
		if (user == null) {
			session.setAttribute("referrer", VIEW_USER_ADMIN_FORM);
			return "login";
		} else if (!user.isAdmin()) {
			logger.warn("Unauthorized attempt to view users by non-admin user: {}", user.getEmail());
			session.setAttribute("referrer", VIEW_USER_ADMIN_FORM);
			return "login";
		}

		List<User> users = userService.getAllUsers();
		ArrayList<User> sortedUsers = new ArrayList<>(users);
		sortedUsers.sort(null);	

		StringBuilder htmlTable = new StringBuilder();
		for (User u : sortedUsers) {
			long id = u.getId();
			String emailStatus = u.getEmailStatus().name();
			String company = u.getCompany();
			String nameFirst = u.getNameFirst();
			String nameLast = u.getNameLast();
			String email = u.getEmail();
			String phone = u.getPhone();
			String accessExpiry = u.getAccessExpiryFormatted();

			if (id == userId) {
				htmlTable.append(tabs(5)).append("<tr>\n");

				htmlTable.append(tabs(6)).append("<td>");
				htmlTable.append("<button id=\"saveButton\" onclick=\"return saveUser()\">Save</button>");
				htmlTable.append(this.makeHiddenInputField("userId", String.valueOf(id)));
				htmlTable.append("</td>\n");

				htmlTable.append(tabs(6)).append("<td>").append(StringUtil.toHTML(emailStatus)).append("</td>\n");
				htmlTable.append(tabs(6)).append("<td>").append(this.makeInputField("company", company)).append("</td>\n");
				htmlTable.append(tabs(6)).append("<td>").append(this.makeInputField("nameFirst", nameFirst)).append("</td>\n");
				htmlTable.append(tabs(6)).append("<td>").append(this.makeInputField("nameLast", nameLast)).append("</td>\n");
				htmlTable.append(tabs(6)).append("<td>").append(this.makeInputField("email", email)).append("</td>\n");
				htmlTable.append(tabs(6)).append("<td>").append(this.makeInputField("phone", phone)).append("</td>\n");
				htmlTable.append(tabs(6)).append("<td>").append(this.makeInputField("accessExpiry", accessExpiry)).append("</td>\n");
				htmlTable.append(tabs(5)).append("</tr>\n");
			} else {
				htmlTable.append(tabs(5)).append("<tr>\n");
				htmlTable.append(tabs(6)).append("<td>").append("<button onclick=\"return editUser(").append(u.getId()).append(")\">Edit</button>").append("</td>\n");
				htmlTable.append(tabs(6)).append("<td>").append(StringUtil.toHTML(emailStatus)).append("</td>\n");
				htmlTable.append(tabs(6)).append("<td>").append(StringUtil.toHTML(company)).append("</td>\n");
				htmlTable.append(tabs(6)).append("<td>").append(StringUtil.toHTML(nameFirst)).append("</td>\n");
				htmlTable.append(tabs(6)).append("<td>").append(StringUtil.toHTML(nameLast)).append("</td>\n");
				htmlTable.append(tabs(6)).append("<td>").append(StringUtil.toHTML(email)).append("</td>\n");
				htmlTable.append(tabs(6)).append("<td>").append(StringUtil.toHTML(phone)).append("</td>\n");
				htmlTable.append(tabs(6)).append("<td>").append(u.getAccessExpiryFormatted()).append("</td>\n");
				htmlTable.append(tabs(5)).append("</tr>\n");
			}
		
		}
		request.setAttribute("userTable", htmlTable.toString());
		return "admin";
	}
	
	@PostMapping("/saveUser")
    public String saveUser(
        @RequestParam(required = false, defaultValue = "0") long userId,
        @RequestParam(required = false, defaultValue = "") String company,
        @RequestParam(required = false, defaultValue = "") String nameFirst,
        @RequestParam(required = false, defaultValue = "") String nameLast,
        @RequestParam(required = false, defaultValue = "") String email,
        @RequestParam(required = false, defaultValue = "") String phone,
        @RequestParam(required = false, defaultValue = "") String accessExpiry,
		HttpServletRequest request,
        HttpSession session) {

        // Verify that the user session is valid and user has admin privileges
        User currentUser = (User) session.getAttribute("user");
 		if (currentUser == null) {
			logger.warn("Unauthorized attempt to update user");
			return "login";
		} else if (!currentUser.isAdmin()) {
			logger.warn("Unauthorized update attempt by non-admin user: {}", currentUser.getEmail());
			return "login";
		}

        List<String> errors = new ArrayList<>();
		session.setAttribute("errors", errors);

 		User user = null;
        try {
            user = userService.findUserById(userId);
        } catch (Exception e) {
 			errors.add("User doesn't exist");
		}

		// Validate and update user fields
		if (!StringUtil.isEmpty(email) && !StringUtil.isValidEmail(email)) {
			errors.add("Invalid email format");
		}

		if (!StringUtil.isEmpty(phone) && !StringUtil.isValidPhone(phone)) {
			errors.add("Invalid phone format. Use 555-123-4567 (US) or +44 20 7946 0958 (International)");
		}

		Date accessExpiryDate = null;
		if (!StringUtil.isEmpty(accessExpiry)) {
			try {
				accessExpiryDate = StringUtil.parseDate(accessExpiry);
			} catch (ParseException e) {
				errors.add("Invalid date format for Expiration Date. Use MM/DD/YY");
			}
		}

		if (user != null && errors.isEmpty()) {
			userService.updateUser(user, company, nameFirst, nameLast, email, phone, accessExpiryDate);
			logger.info("User updated successfully: {} {} {} {} {}", company, nameFirst, nameLast, email, phone);
		}

 		return "redirect:viewUsers";
    }

	boolean jsonToBoolean(JsonNode parentJsonNode, String fieldName) {
		JsonNode fieldJsonNode = parentJsonNode.get(fieldName);
		String value = fieldJsonNode != null ? fieldJsonNode.asText() : "";
		return Boolean.parseBoolean(value);
	}

	String jsonToString(JsonNode parentJsonNode, String fieldName) {
		JsonNode fieldJsonNode = parentJsonNode.get(fieldName);
		String value = fieldJsonNode != null ? fieldJsonNode.asText() : "";
		return value;
	}

	private String makeInputField(String fieldName, String value) {
		return "<input type=\"text\" id=\"" + fieldName + "\" name=\"" + fieldName + "\" value=\"" + StringUtil.toHTML(value) + "\" />";
	}

    private String makeHiddenInputField(String fieldName, String value) {
        return "<input type=\"hidden\" id=\"" + fieldName + "\" name=\"" + fieldName + "\" value=\"" + StringUtil.toHTML(value) + "\" />";
    }
	
	private void sendEmailValidateAddress(HttpServletRequest request, String email) {
		try {
			String scheme = request.getScheme();
			String serverName = request.getServerName();
			int serverPort = request.getServerPort();
			String urlDomain =
				scheme
				+ "://"
				+ serverName
				+ (serverPort == 80 || serverPort == 443 ? "" : ":" + serverPort);
			String subject = "Welcome to REST API Generator";
			String resourcePath = "templates/welcome.html";
			ClassPathResource resource = new ClassPathResource(resourcePath);
			int key = StringUtil.makeKey(email);
			String htmlBody = new String(resource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
			htmlBody = String.format(htmlBody, urlDomain, email, key);
			this.emailService.sendHtmlEmail(email, subject, htmlBody);
			logger.info("Welcome email sent to {}", email);

		} catch (Throwable t) {
			logger.error("Failed to send email to {}: {}", email, t.getMessage(), t);
		}
	}

	private void sendEmailResetPassword(HttpServletRequest request, String email) {
		try {
			String scheme = request.getScheme();
			String serverName = request.getServerName();
			int serverPort = request.getServerPort();
			String urlDomain =
				scheme
				+ "://"
				+ serverName
				+ (serverPort == 80 || serverPort == 443 ? "" : ":" + serverPort);
			String subject = "Password Reset Request";
			String resourcePath = "templates/resetPassword.html";
			ClassPathResource resource = new ClassPathResource(resourcePath);
			int key = StringUtil.makeKey(email);
			String htmlBody = new String(resource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
			htmlBody = String.format(htmlBody, urlDomain, email, key);
			this.emailService.sendHtmlEmail(email, subject, htmlBody);
			logger.info("Password reset email sent to {}", email);

		} catch (Throwable t) {
			logger.error("Failed to send email to {}: {}", email, t.getMessage(), t);
		}
	}

	private void generateApiSpec(
		User user,
		String title,
		String description,
		String version,
		MultipartFile file,
		boolean makeSEARCH, boolean makeGET, boolean makePOST, boolean makePUT, boolean makeDELETE,
		String serverDomain,
		String contextRoot,
		String port,
		List<String> errors) throws IOException {
	
		// Delete a previously generated Swagger file, if any.
		ApiSpec apiSpec = user.getApiSpec();
		this.toolService.deleteSwagger(apiSpec);

		// Verify that the required fields are provided.
		if (StringUtil.isEmpty(title)) {
			errors.add("Specify the title clients will use to refer to the API");
		} else {
			title = StringUtil.trim(title);
		}
		apiSpec.setTitle(title);
		apiSpec.setDescription(description);

		if (StringUtil.isEmpty(version) || !StringUtil.isValidVersion(version)) {
			errors.add("Specify the API's version using the format 'major.minor' where major and minor are numeric values (e.g., 1.0)");
		} else {
			version = StringUtil.trim(version);
		}
		apiSpec.setVersion(version);

		String filename = file == null ? "" : file.getOriginalFilename();
		if (StringUtil.isEmpty(filename)) {
			errors.add("Select the file containing the API's business domain model");
		}

		apiSpec.setMakeSEARCH(makeSEARCH);
		apiSpec.setMakeGET(makeGET);
		apiSpec.setMakePOST(makePOST);
		apiSpec.setMakePUT(makePUT);
		apiSpec.setMakeDELETE(makeDELETE);
		
		if (StringUtil.isEmpty(serverDomain)) {
			errors.add("Specify the domain where the API will be hosted");
		} else {
			serverDomain = StringUtil.trim(serverDomain);
		}
		apiSpec.setServerDomain(serverDomain);

		if (StringUtil.isEmpty(contextRoot)) {
			errors.add("Specify the context root for the API's various endpoint URIs");
		} else {
			contextRoot = StringUtil.trim(contextRoot);
		}
		apiSpec.setContextRoot(contextRoot);

		if (StringUtil.isEmpty(port)) {
			errors.add("Specify the server port where the API will be hosted");
		} else {
			port = StringUtil.trim(port);
			try {
				int portNumber = Integer.parseInt(port);
				if (portNumber < 1 || portNumber > 65535) {
					errors.add("Specify a valid port number between 1 and 65535");
				}
			} catch (NumberFormatException e) {
				errors.add("Specify a valid port number between 1 and 65535");
			}
		}
		apiSpec.setPort(port);

		if (file == null ||!errors.isEmpty()) {
			return;
		}

		// Read the information model into memory.
		DtoReadUMLFile status = this.toolService.readUMLFile(filename, file.getBytes());
		String error = status.getError();
		if (error != null) {
			errors.add(error);
			return;
		}

		// Create a new Domain Model instance populated with entities from the UML file.
		List<Entity> entities = status.getEntities();
		DomainModel domainModel = new DomainModel(title, description, version, entities);

		// Process the domain model to generate the API's swagger.
		this.toolService.generateSwagger(user, domainModel, makeSEARCH, makeGET, makePOST, makePUT, makeDELETE, serverDomain, contextRoot, port, status.getIssues());
	}

}