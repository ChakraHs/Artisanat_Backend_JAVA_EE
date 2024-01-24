package com.Pf_Artis.shared;

import java.io.File;
import java.util.UUID;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.Part;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class UtilsUploadImage {

	private String saveDirectory;
	
	public String generateUniqueFileName(Part part) {
        String originalFileName = getFileName(part);
        String extension = "";
        int lastDotIndex = originalFileName.lastIndexOf(".");
        if (lastDotIndex > 0) {
            extension = originalFileName.substring(lastDotIndex);
        }
        // Generate a unique filename using UUID
        return UUID.randomUUID().toString() + extension;
    }
	
	
	public String getFileName(Part part) {
        // Get the content-disposition header to extract the file name
        String contentDisposition = part.getHeader("content-disposition");
        String[] elements = contentDisposition.split(";");
        for (String element : elements) {
            if (element.trim().startsWith("filename")) {
                // Extract and return the file name
                return element.substring(element.indexOf('=') + 1).trim().replace("\"", "");
            }
        }
        return "";
    }
	
	
	
	public String getUploadPath(HttpServletRequest request) {
        String applicationPath = request.getServletContext().getRealPath("");
        System.out.println(applicationPath);
        System.out.println(File.separator);
        return applicationPath + saveDirectory;
    }
	
}
