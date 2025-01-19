package com.example.immovision.services.image;


import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Service
public class CloudinaryImageService {

    private final Cloudinary cloudinary;

    @Autowired
    public CloudinaryImageService(Cloudinary cloudinary) {
        this.cloudinary = cloudinary;
    }


    public Map<String, String> uploadImage(MultipartFile file) throws IOException {

        Map<String, Object> uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.emptyMap());


        String assetId = (String) uploadResult.get("asset_id"); // The unique Asset ID
        String publicId = (String) uploadResult.get("public_id"); // The Public ID
        String secureUrl = (String) uploadResult.get("secure_url"); // The HTTPS URL


        Map<String, String> result = new HashMap<>();
        result.put("assetId", assetId);
        result.put("publicId", publicId);
        result.put("secureUrl", secureUrl);

        return result;
    }

    public void deleteImage(String imageUrl) {
        try {
            // Extraire le public_id de l'URL Cloudinary
            String publicId = imageUrl.substring(imageUrl.lastIndexOf("/") + 1, imageUrl.lastIndexOf("."));
            
            // Supprimer l'image de Cloudinary
            cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
        } catch (IOException e) {
            throw new RuntimeException("Failed to delete image from Cloudinary", e);
        }
    }
}