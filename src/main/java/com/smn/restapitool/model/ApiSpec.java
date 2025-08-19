package com.smn.restapitool.model;

import java.io.File;
import java.util.Date;

public class ApiSpec {

    private File apiSpecFile; // File where the API spec's swagger text is stored
    private Date dateGenerated;

    private String title = "";
	private String description = "";
	private String version = "";
	private boolean makeSEARCH;
	private boolean makeGET;
	private boolean makePOST;
	private boolean makePUT;
	private boolean makePATCH;
	private boolean makeDELETE;
	private String serverDomain = "";
	private String contextRoot = "";
	private String port = "";

    public ApiSpec() {
    }

    public ApiSpec(File apiSpecFile) {
        this.apiSpecFile = apiSpecFile;
        this.dateGenerated = new Date();
    }

    public File getApiSpecFile() {
        return apiSpecFile;
    }

    public void setApiSpecFile(File apiSpecFile) {
        this.apiSpecFile = apiSpecFile;
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
    
    public Date getDateGenerated() {
        return dateGenerated;
    }

    public void setDateGenerated(Date dateGenerated) {
        this.dateGenerated = dateGenerated;
    }

}

