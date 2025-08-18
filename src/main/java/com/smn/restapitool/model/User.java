package com.smn.restapitool.model;

import java.io.File;
import java.util.Date;

public class User implements Comparable<User> {

    private String nameFirst = "";
    private String nameLast = "";
    private String company = "";
    private String email = "";
    private String accessToken = "";
    private Date accessExpiration;

    private String title = "";
	private String description = "Description TBD";
	private String version = "";
	private boolean makeSEARCH;
	private boolean makeGET;
	private boolean makePOST;
	private boolean makePUT;
	private boolean makePATCH;
	private boolean makeDELETE;
	private String serverDomain = "";
	private String contextRoot = "";
	private String port = "443";

    private File apiSpecFile;
    private Date apiSpecFileDate;
    private File apiCodeZipFile;
    private Date apiCodeZipFileDate;

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

    public File getApiSpecFile() {
        return apiSpecFile;
    }

    public void setApiSpecFile(File apiSpecFile) {
        this.apiSpecFile = apiSpecFile;
    }

    public Date getApiSpecFileDate() {
        return apiSpecFileDate;
    }

    public void setApiSpecFileDate(Date apiSpecFileDate) {
        this.apiSpecFileDate = apiSpecFileDate;
    }

    public File getApiCodeZipFile() {
        return apiCodeZipFile;
    }

    public void setApiCodeZipFile(File apiCodeZipFile) {
        this.apiCodeZipFile = apiCodeZipFile;
    }

    public Date getApiCodeZipFileDate() {
        return apiCodeZipFileDate;
    }

    public void setApiCodeZipFileDate(Date apiCodeZipFileDate) {
        this.apiCodeZipFileDate = apiCodeZipFileDate;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public boolean isMakeSEARCH() {
        return makeSEARCH;
    }

    public void setMakeSEARCH(boolean makeSEARCH) {
        this.makeSEARCH = makeSEARCH;
    }

    public boolean isMakeGET() {
        return makeGET;
    }

    public void setMakeGET(boolean makeGET) {
        this.makeGET = makeGET;
    }

    public boolean isMakePOST() {
        return makePOST;
    }

    public void setMakePOST(boolean makePOST) {
        this.makePOST = makePOST;
    }

    public boolean isMakePUT() {
        return makePUT;
    }

    public void setMakePUT(boolean makePUT) {
        this.makePUT = makePUT;
    }

    public boolean isMakePATCH() {
        return makePATCH;
    }

    public void setMakePATCH(boolean makePATCH) {
        this.makePATCH = makePATCH;
    }

    public boolean isMakeDELETE() {
        return makeDELETE;
    }

    public void setMakeDELETE(boolean makeDELETE) {
        this.makeDELETE = makeDELETE;
    }

    public String getServerDomain() {
        return serverDomain;
    }

    public void setServerDomain(String serverDomain) {
        this.serverDomain = serverDomain;
    }

    public String getContextRoot() {
        return contextRoot;
    }

    public void setContextRoot(String contextRoot) {
        this.contextRoot = contextRoot;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
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
        return "User [email=" + email + ", accessToken=" + accessToken + ", accessExpiration=" + accessExpiration
                + ", apiSpecFile=" + apiSpecFile + ", apiSpecFileDate=" + apiSpecFileDate + ", apiCodeZipFile="
                + apiCodeZipFile + ", apiCodeZipFileDate=" + apiCodeZipFileDate + "]";
    }

}
