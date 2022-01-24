package com.example.demo_test_upload_image.rest;


import com.example.demo_test_upload_image.model.AppResponse;
import com.example.demo_test_upload_image.model.User;
import com.example.demo_test_upload_image.service.FileStorageService;
import com.example.demo_test_upload_image.service.UserService;
import com.example.demo_test_upload_image.utils.AppConstants;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;

@RestController
public class UserRestController {

    @Autowired
    UserService userService;
    ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    FileStorageService fileStorageService;

    @RequestMapping(value = AppConstants.USER_URI, method = RequestMethod.POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public AppResponse createEmployee(
            @RequestParam(value = AppConstants.USER_PARAM_PHONE, required = true) String   phone,
            @RequestParam(value = AppConstants.USER_PARAM_PASSWORD, required = true) String password,
            @RequestParam(value = AppConstants.USER_PARAM_FIRSTNAME, required = true) String firstname,
            @RequestParam(value = AppConstants.USER_PARAM_LASTNAME, required = true) String lastname,
            @RequestParam(value = AppConstants.USER_PARAM_MAIL, required = true) String mail,
            @RequestParam(required = true, value = AppConstants.USER_FILE_PARAM) MultipartFile file)
            throws JsonParseException, JsonMappingException, IOException {
        String fileName = fileStorageService.storeFile(file);
        String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath().path(AppConstants.DOWNLOAD_PATH)
                .path(fileName).toUriString();

        User user = new User();
        user.setImage(fileDownloadUri);
        user.setPhone(phone);
        user.setPassword(password);
        user.setFirstname(firstname);
        user.setLastname(lastname);
        user.setMail(mail);


        userService.createUser(user);

        return new AppResponse(AppConstants.SUCCESS_CODE, AppConstants.SUCCESS_MSG);
    }

    @RequestMapping(value = AppConstants.USER_URI, method = RequestMethod.GET)

    public List<User> getAllUser() {
        return userService.getAllUser();
    }



    @GetMapping("/downloadFile/{fileName:.+}")
    public ResponseEntity<Resource> downloadFile(@PathVariable String fileName, HttpServletRequest request) {
        // Load file as Resource
        Resource resource = fileStorageService.loadFileAsResource(fileName);

        // Try to determine file's content type
        String contentType = null;
        try {
            contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
        } catch (IOException ex) {
            //   logger.info("Could not determine file type.");
        }

        // Fallback to the default content type if type could not be determined
        if(contentType == null) {
            contentType = "application/octet-stream";
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }
}