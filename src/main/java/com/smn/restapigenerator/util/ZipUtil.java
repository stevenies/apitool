package com.smn.restapigenerator.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Utility class for creating ZIP files from directories.
 */
public class ZipUtil {

    /**
     * Creates a ZIP file from the contents of a directory.
     * @param sourceDir the directory to zip
     * @param zipFile the resulting ZIP file
     * @throws IOException if an I/O error occurs
     */
    public static void zipDirectory(File sourceDir, File zipFile) throws IOException {
        try (ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(zipFile))) {
            zipFileRecursive(sourceDir, sourceDir.getName(), zos);
        }
    }

    private static void zipFileRecursive(File fileToZip, String fileName, ZipOutputStream zos) throws IOException {
        if (fileToZip.isHidden()) {
            return;
        }
        if (fileToZip.isDirectory()) {
            File[] children = fileToZip.listFiles();
            if (children == null || children.length == 0) {
                zos.putNextEntry(new ZipEntry(fileName + "/"));
                zos.closeEntry();
            } else {
                for (File childFile : children) {
                    zipFileRecursive(childFile, fileName + "/" + childFile.getName(), zos);
                }
            }
            return;
        }
        try (FileInputStream fis = new FileInputStream(fileToZip)) {
            ZipEntry zipEntry = new ZipEntry(fileName);
            zos.putNextEntry(zipEntry);
            byte[] bytes = new byte[1024];
            int length;
            while ((length = fis.read(bytes)) >= 0) {
                zos.write(bytes, 0, length);
            }
        }
    }

}