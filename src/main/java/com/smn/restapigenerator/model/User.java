package com.smn.restapigenerator.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.smn.restapigenerator.util.StringUtil;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
public class User implements Comparable<User> {

    public static enum EmailStatus {
        NEW,
        VERIFIED
    }

    private long id;
    private String email = "";
    private EmailStatus emailStatus = EmailStatus.NEW;
    private String password = "";
    private String company = "";
    private String nameFirst = "";
    private String nameLast = "";
    private String phone = "";
    private Date accountCreationDate = new Date();
    private Date accessExpiryDate;

    private ApiSpec apiSpec = new ApiSpec();
    private ApiCode apiCode = new ApiCode();

    public User() {
        this.id = StringUtil.makeId();
 
        // Compute the access expiration date.
		Calendar cal = Calendar.getInstance();
    	cal.add(Calendar.DAY_OF_YEAR, 1);
		Date date = cal.getTime();
		this.setAccessExpiryDate(date);
    }

    @JsonIgnore
    public boolean isAdmin() {
        return this.nameLast.equalsIgnoreCase("Nies");
    }

    @JsonIgnore
    public long getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public EmailStatus getEmailStatus() {
        return this.emailStatus;
    }

    public void setEmailStatus(EmailStatus emailStatus) {
        this.emailStatus = emailStatus;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getCompany() {
        return this.company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getNameFirst() {
        return this.nameFirst;
    }

    public void setNameFirst(String nameFirst) {
        this.nameFirst = nameFirst;
    }

    public String getNameLast() {
        return this.nameLast;
    }

    public void setNameLast(String nameLast) {
        this.nameLast = nameLast;
    }

     public String getPhone() {
        return this.phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Date getAccountCreationDate() {
        return this.accountCreationDate;
    }

    public void setAccountCreationDate(Date accountCreationDate) {
        this.accountCreationDate = accountCreationDate;
    }

    public Date getAccessExpiryDate() {
        return this.accessExpiryDate;
    }

    /**
     * @return access expiration date formatted as MM/DD/YY or empty string if accessExpiration is null
     */
    @JsonIgnore
    public String getAccessExpiryFormatted() {
        if (accessExpiryDate == null) {
            return "";
        }
        SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yy");
        return formatter.format(accessExpiryDate);
    }

    public void setAccessExpiryDate(Date accessExpiration) {
        this.accessExpiryDate = accessExpiration;
    }

    public ApiSpec getApiSpec() {
        return this.apiSpec;
    }

    public void setApiSpec(ApiSpec apiSpec) {
        this.apiSpec = apiSpec;
    }

    public ApiCode getApiCode() {
        return this.apiCode;
    }

    public void setApiCode(ApiCode apiCode) {
        this.apiCode = apiCode;
    }

   @Override
    public int compareTo(User o) {
        if (o == null || o.email == null) {
            return 1;
        } else if (this.email == null) {
            return -1;
        } else if (this.email.equalsIgnoreCase(o.email)) {
            return 0;
        } else if (this.company != null && !this.company.equalsIgnoreCase(o.company)) {
            return this.company.compareToIgnoreCase(o.company);
        } else if (this.nameFirst != null && !this.nameFirst.equalsIgnoreCase(o.nameFirst)) {
            return this.nameFirst.compareToIgnoreCase(o.nameFirst);
        } else if (this.nameLast != null && !this.nameLast.equalsIgnoreCase(o.nameLast)) {
            return this.nameLast.compareToIgnoreCase(o.nameLast);
        } else {
            return this.email.compareTo(o.email);
        }
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((email == null) ? 0 : email.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        User other = (User) obj;
        if (email == null) {
            if (other.email != null)
                return false;
        } else if (!email.equals(other.email))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "User [email=" + email + ", password=" + password + ", accessExpiryDate=" + accessExpiryDate + "]";
    }

}
