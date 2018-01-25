package no.nav.foreldrepenger.mottak.http;

import java.io.IOException;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
class UploadController {

    @PostMapping(value = "/upload", consumes = "multipart/form-data")
    public String uploadingPost(@RequestParam("uploadingFiles") MultipartFile[] uploadingFiles) throws IOException {
        for (MultipartFile uploadedFile : uploadingFiles) {
            System.out.println(uploadedFile.getName());
        }
        return "OK";
    }

}
