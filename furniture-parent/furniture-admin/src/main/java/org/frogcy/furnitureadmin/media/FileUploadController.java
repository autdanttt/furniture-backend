package org.frogcy.furnitureadmin.media;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/upload")
public class FileUploadController {
    private final AssetService assetService;
    public FileUploadController(AssetService assetService) {
        this.assetService = assetService;
    }

    @PostMapping
    public ResponseEntity<?> upload(@RequestParam("file") MultipartFile file){
        String filename = assetService.uploadToCloudinary(file, "upload");
        Map<String, String> map = new HashMap<>();
        map.put("url", filename);
        return ResponseEntity.ok(map);
    }
}