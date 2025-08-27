package com.smn.restapigenerator.model;

import java.io.File;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class ApiCode {

    private String language = "java"; // Programming language for the API implementation
    private File apiCodeDir; // Directory containing the generated API code
    private Date dateGenerated;

    public ApiCode() {
    }

    public ApiCode(File apiCodeDir) {
        this.apiCodeDir = apiCodeDir;
        this.dateGenerated = new Date();
    }

    @JsonIgnore
    public boolean isValid() {
        return this.apiCodeDir != null && this.apiCodeDir.exists();
    }

    public String getLanguage() {
        return this.language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public File getApiCodeDir() {
        return this.apiCodeDir;
    }

    public void setApiCodeDir(File apiCodeDir) {
        this.apiCodeDir = apiCodeDir;
    }

    @JsonIgnore
    public File getApiCodeZip() {
        return this.apiCodeDir == null ? null : new File(this.apiCodeDir.getParentFile(), "apiCode.zip");
    }

    public Date getDateGenerated() {
        return this.dateGenerated;
    }

    public void setDateGenerated(Date dateGenerated) {
        this.dateGenerated = dateGenerated;
    }

}

