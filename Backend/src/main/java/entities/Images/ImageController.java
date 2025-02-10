package entities.Images;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.ResponseEntity;
import java.io.IOException;
import java.sql.Blob;
import javax.sql.rowset.serial.SerialBlob;
import java.sql.SQLException;

@RestController
@RequestMapping("/images")
@Tag(name = "Image Management", description = "APIs for managing images related to ingredients and recipes")

public class ImageController {

    @Autowired
    private ImageRepository imageRepository;

    @Operation(
            summary = "Get image by ingredient ID",
            description = "Retrieve an image associated with the given ingredient ID."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Image retrieved successfully",
                    content = @Content(mediaType = MediaType.IMAGE_JPEG_VALUE)),
            @ApiResponse(responseCode = "404", description = "Image not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping(value = "/ingredient/{iid}", produces = MediaType.IMAGE_JPEG_VALUE)
    public ResponseEntity<byte[]> getImageByIid(@PathVariable int iid) {
        Image image = imageRepository.findByIid(iid);
        if (image == null || image.getData() == null) {
            return ResponseEntity.notFound().build();
        }

        try {
            byte[] imageData = image.getData().getBytes(1, (int) image.getData().length());  // Convert Blob to byte[]
            return ResponseEntity.ok(imageData);  // Return the image data as a byte array
        } catch (SQLException e) {
            return ResponseEntity.status(500).body(null);
        }
    }

    @Operation(
            summary = "Get image by recipe ID and step",
            description = "Retrieve an image for a specific step in a recipe."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Image retrieved successfully",
                    content = @Content(mediaType = MediaType.IMAGE_JPEG_VALUE)),
            @ApiResponse(responseCode = "404", description = "Image not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping(value = "/recipe/step/{rid}/{step}", produces = MediaType.IMAGE_JPEG_VALUE)
    public ResponseEntity<byte[]> getImageByRidAndStep(@PathVariable int rid, @PathVariable int step) {
        Image image = imageRepository.findByRidAndStep(rid, step);
        if (image == null || image.getData() == null) {
            return ResponseEntity.notFound().build();
        }

        try {
            byte[] imageData = image.getData().getBytes(1, (int) image.getData().length());  // Convert Blob to byte[]
            return ResponseEntity.ok(imageData);  // Return the image data as a byte array
        } catch (SQLException e) {
            return ResponseEntity.status(500).body(null);
        }
    }

    @Operation(
            summary = "Get image by recipe ID",
            description = "Retrieve an image associated with the given recipe ID (not tied to a specific step)."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Image retrieved successfully",
                    content = @Content(mediaType = MediaType.IMAGE_JPEG_VALUE)),
            @ApiResponse(responseCode = "404", description = "Image not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping(value = "/recipe/{rid}", produces = MediaType.IMAGE_JPEG_VALUE)
    public ResponseEntity<byte[]> getImageByRid(@PathVariable int rid) {

        Image image = imageRepository.findByRidAndStepIsNull(rid);
        if (image == null || image.getData() == null) {
            return ResponseEntity.notFound().build();
        }

        try {
            byte[] imageData = image.getData().getBytes(1, (int) image.getData().length());  // Convert Blob to byte[]
            return ResponseEntity.ok(imageData);  // Return the image data as a byte array
        } catch (SQLException e) {
            return ResponseEntity.status(500).body(null);
        }
    }

    @Operation(
            summary = "Upload image for an ingredient",
            description = "Upload a new image for the specified ingredient ID."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Image uploaded successfully"),
            @ApiResponse(responseCode = "409", description = "Conflict: Image already exists"),
            @ApiResponse(responseCode = "500", description = "Failed to upload image")
    })
    @PostMapping("/ingredient/{iid}")
    public ResponseEntity<String> uploadImageForIngredient(@PathVariable int iid, @RequestParam("image") MultipartFile imageFile) {
        // Check if the ingredient ID already has an image
        if (imageRepository.findByIid(iid) != null) {
            return ResponseEntity.status(409).body("Conflict: Image already exists for ingredient ID: " + iid);
        }

        try {
            Blob imageBlob = new SerialBlob(imageFile.getBytes());  // Create a Blob from byte array
            Image image = new Image();
            image.setData(imageBlob);  // Store image data as Blob in the database
            image.setIid(iid);  // Associate with ingredient
            imageRepository.save(image);

            return ResponseEntity.ok("Image uploaded successfully for ingredient ID: " + iid);
        } catch (IOException | SQLException e) {
            e.printStackTrace();  // Log the error
            return ResponseEntity.status(500).body("Failed to upload image: " + e.getMessage());
        }
    }

    @Operation(
            summary = "Update image for ingredient",
            description = "Update the image for the specified ingredient ID."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Image updated successfully"),
            @ApiResponse(responseCode = "404", description = "Image not found"),
            @ApiResponse(responseCode = "500", description = "Failed to update image")
    })
    @PatchMapping("/ingredient/{iid}")
    public ResponseEntity<String> updateImageForIngredient(@PathVariable int iid, @RequestParam("image") MultipartFile newImageFile) {
        // Check if an image already exists for the given ingredient ID
        Image existingImage = imageRepository.findByIid(iid);
        if (existingImage == null) {
            return ResponseEntity.status(404).body("Image not found for ingredient ID: " + iid);
        }

        try {
            Blob newImageBlob = new SerialBlob(newImageFile.getBytes());
            existingImage.setData(newImageBlob); // Update the existing image with the new Blob data
            imageRepository.save(existingImage);

            return ResponseEntity.ok("Image updated successfully for ingredient ID: " + iid);
        } catch (IOException | SQLException e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Failed to update image: " + e.getMessage());
        }
    }

    @Operation(
            summary = "Upload image for a recipe",
            description = "Upload a new image for the specified recipe ID."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Image uploaded successfully"),
            @ApiResponse(responseCode = "409", description = "Conflict: Image already exists"),
            @ApiResponse(responseCode = "500", description = "Failed to upload image")
    })
    @PostMapping("/recipe/{rid}")
    public ResponseEntity<String> uploadImageForRecipe(@PathVariable int rid, @RequestParam("image") MultipartFile imageFile) {
        // Check if the recipe ID already has an image
        if (imageRepository.findByRidAndStepIsNull(rid) != null) {
            return ResponseEntity.status(409).body("Conflict: Image already exists for recipe ID: " + rid);
        }

        try {
            Blob imageBlob = new SerialBlob(imageFile.getBytes());  // Create a Blob from byte array
            Image image = new Image();
            image.setData(imageBlob);  // Store image data as Blob in the database
            image.setRid(rid);  // Associate with recipe
            imageRepository.save(image);

            return ResponseEntity.ok("Image uploaded successfully for recipe ID: " + rid);
        } catch (IOException | SQLException e) {
            e.printStackTrace();  // Log the error for debugging
            return ResponseEntity.status(500).body("Failed to upload image: " + e.getMessage());
        }
    }

    @Operation(
            summary = "Update image for recipe",
            description = "Update the image for the specified recipe ID."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Image updated successfully"),
            @ApiResponse(responseCode = "404", description = "Image not found"),
            @ApiResponse(responseCode = "500", description = "Failed to update image")
    })
    @PatchMapping("/recipe/{rid}")
    public ResponseEntity<String> updateImageForRecipe(@PathVariable int rid, @RequestParam("image") MultipartFile newImageFile) {
        // Check if an image already exists for the given recipe ID
        Image existingImage = imageRepository.findByRidAndStepIsNull(rid);
        if (existingImage == null) {
            return ResponseEntity.status(404).body("Image not found for recipe ID: " + rid);
        }

        try {
            Blob newImageBlob = new SerialBlob(newImageFile.getBytes());
            existingImage.setData(newImageBlob);
            imageRepository.save(existingImage);

            return ResponseEntity.ok("Image updated successfully for recipe ID: " + rid);
        } catch (IOException | SQLException e) {
            e.printStackTrace();  // Log the error 
            return ResponseEntity.status(500).body("Failed to update image: " + e.getMessage());
        }
    }

    @Operation(
            summary = "Upload image for a recipe step",
            description = "Upload a new image for the specific recipe and step."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Image uploaded successfully"),
            @ApiResponse(responseCode = "409", description = "Conflict: Image already exists"),
            @ApiResponse(responseCode = "500", description = "Failed to upload image")
    })
    @PostMapping("/recipe/step/{rid}/{step}")
    public ResponseEntity<String> uploadImageForRecipeStep(@PathVariable int rid, @PathVariable int step, @RequestParam("image") MultipartFile imageFile) {

        if (imageRepository.findByRidAndStep(rid, step) != null) {
            return ResponseEntity.status(409).body("Conflict: Image already exists for recipe ID: " + rid + " and step: " + step);
        }

        try {
            Blob imageBlob = new SerialBlob(imageFile.getBytes());  // Create a Blob from byte array
            Image image = new Image();
            image.setData(imageBlob);  // Store image data as Blob in the database
            image.setRid(rid);  // Associate with recipe
            image.setStep(step);  // Associate with specific step
            imageRepository.save(image);

            return ResponseEntity.ok("Image uploaded successfully for recipe ID: " + rid + " and step: " + step);
        } catch (IOException | SQLException e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Failed to upload image: " + e.getMessage());
        }
    }

    @Operation(
            summary = "Update image for recipe step",
            description = "Update the image for a specific recipe step."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Image updated successfully"),
            @ApiResponse(responseCode = "404", description = "Image not found"),
            @ApiResponse(responseCode = "500", description = "Failed to update image")
    })
    @PatchMapping("/recipe/step/{rid}/{step}")
    public ResponseEntity<String> updateImageForRecipeStep(@PathVariable int rid, @PathVariable int step, @RequestParam("image") MultipartFile newImageFile) {

        Image existingImage = imageRepository.findByRidAndStep(rid, step);
        if (existingImage == null) {
            return ResponseEntity.status(404).body("Image not found for recipe ID: " + rid + " and step: " + step);
        }

        try {
            Blob newImageBlob = new SerialBlob(newImageFile.getBytes());
            existingImage.setData(newImageBlob);
            imageRepository.save(existingImage);

            return ResponseEntity.ok("Image updated successfully for recipe ID: " + rid + " and step: " + step);
        } catch (IOException | SQLException e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Failed to update image: " + e.getMessage());
        }
    }
}