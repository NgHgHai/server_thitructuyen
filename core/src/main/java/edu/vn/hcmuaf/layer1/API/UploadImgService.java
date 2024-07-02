package edu.vn.hcmuaf.layer1.API;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@WebServlet(name = "UploadImgService", urlPatterns = {"/upload"})
@MultipartConfig(fileSizeThreshold = 1024 * 1024 * 2, // 2MB
        maxFileSize = 1024 * 1024 * 30,      // 10MB
        maxRequestSize = 1024 * 1024 * 50)   // 50MB
public class UploadImgService extends HttpServlet {
    private static final String UPLOAD_DIR = "uploads";

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.getWriter().print("This is a POST service, please send a POST request to upload a file.");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String applicationPath = request.getServletContext().getRealPath("");
//        String applicationPath = "I:" + File.separator + "server";
        String uploadFilePath = applicationPath + File.separator + UPLOAD_DIR;
        System.out.println("uploadFilePath: " + uploadFilePath);
        // Create the upload directory if it doesn't exist
        File uploadDir = new File(uploadFilePath);
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }

        for (Part part : request.getParts()) {
            String fileName = getFileName(part);
            String uniqueFileName = generateUniqueFileName(fileName);
            part.write(uploadFilePath + File.separator + uniqueFileName);

            // Send back the URL of the uploaded file to the client
            String fileUrl = request.getRequestURL().toString().replace(request.getRequestURI(), "") +
                    request.getContextPath() + "/" + UPLOAD_DIR + "/" + uniqueFileName;
            response.getWriter().print(fileUrl);
        }
    }

    private String getFileName(Part part) {
        String contentDisposition = part.getHeader("content-disposition");
        String[] tokens = contentDisposition.split(";");
        for (String token : tokens) {
            if (token.trim().startsWith("filename")) {
                return token.substring(token.indexOf("=") + 2, token.length() - 1);
            }
        }
        return "";
    }

    private String generateUniqueFileName(String fileName) {
        String extension = fileName.substring(fileName.lastIndexOf("."));
        return UUID.randomUUID().toString() + extension;
    }
}