package com.smn.restapitool.model;

import java.io.File;
import java.util.Date;

public class ApiCode {

    private File apiZipFile; // iip file containing the generated API code
    private Date dateGenerated;

    public ApiCode() {
    }

    public ApiCode(File apiZipFile) {
        this.apiZipFile = apiZipFile;
        this.dateGenerated = new Date();
    }

    public File getApiZipFile() {
        return apiZipFile;
    }

    public void setApiZipFile(File apiZipFile) {
        this.apiZipFile = apiZipFile;
    }

    public Date getDateGenerated() {
        return dateGenerated;
    }

    public void setDateGenerated(Date dateGenerated) {
        this.dateGenerated = dateGenerated;
    }

}

