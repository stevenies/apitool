package com.smn.restapigenerator.model;

import java.util.Date;

public class User implements Comparable<User> {

    private String nameFirst = "";
    private String nameLast = "";
    private String company = "";
    private String email = "";
    private String accessToken = "";
    private Date accessExpiration;

    private ApiSpec apiSpec = new ApiSpec();
    private ApiCode apiCode = new ApiCode();

    public User() {
    }

    public String getNameFirst() {
        return nameFirst;
    }

    public void setNameFirst(String nameFirst) {
        this.nameFirst = nameFirst;
    }

    public String getNameLast() {
        return nameLast;
    }

    public void setNameLast(String nameLast) {
        this.nameLast = nameLast;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public Date getAccessExpiration() {
        return accessExpiration;
    }

    public void setAccessExpiration(Date accessExpiration) {
        this.accessExpiration = accessExpiration;
    }

    public ApiSpec getApiSpec() {
        return apiSpec;
    }

    public void setApiSpec(ApiSpec apiSpec) {
        this.apiSpec = apiSpec;
    }

    public ApiCode getApiCode() {
        return apiCode;
    }

    public void setApiCode(ApiCode apiCode) {
        this.apiCode = apiCode;
    }

   @Override
    public int compareTo(User o) {
        return this.email.compareTo(o.email);
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
        return "User [email=" + email + ", accessToken=" + accessToken + ", accessExpiration=" + accessExpiration + "]";
    }

}
