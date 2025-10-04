package org.frogcy.furniturecustomer.media;

import com.cloudinary.Cloudinary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

@Service
public class AssetService {
    @Autowired
    private Cloudinary cloudinary;

    public String uploadToCloudinary(MultipartFile file, String folder) {
        try {
            String mediaId = UUID.randomUUID().toString();
            String publicId = folder + "/" + mediaId;

            Map<String, Object> options = Map.of(
                    "public_id", publicId,
                    "overwrite", true
            );

            Map uploadResult = cloudinary.uploader().upload(file.getBytes(), options);
            return (String) uploadResult.get("secure_url");
        } catch (IOException e) {
            throw new RuntimeException("Upload failed", e);
        }
    }

}
