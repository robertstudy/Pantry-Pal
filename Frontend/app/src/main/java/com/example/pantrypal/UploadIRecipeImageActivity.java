package com.example.pantrypal;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.Request;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class UploadIRecipeImageActivity extends AppCompatActivity {

Button selectBtn;
Button uploadBtn;
ImageView mImageView;
Uri selectiedUri;

// replace this with the actual address
// 10.0.2.2 to be used for localhost if running springboot on the same host
private static String UPLOAD_URL = "http://coms-3090-007.class.las.iastate.edu:8080/images/recipe/";

private ActivityResultLauncher<String> mGetContent;

@Override
protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    EdgeToEdge.enable(this);
    setContentView(R.layout.activity_upload_image);
    ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
        Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
        v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
        return insets;
    });

    Intent intent = getIntent();
    int id = intent.getIntExtra("id", 1);
    UPLOAD_URL = UPLOAD_URL + id;
    mImageView = findViewById(R.id.imageSelView);
    selectBtn = findViewById(R.id.selectBtn);

    // select image from gallery
    mGetContent = registerForActivityResult(new ActivityResultContracts.GetContent(),
            uri -> {
                // Handle the returned Uri
                if (uri != null) {
                    selectiedUri = uri;
                    ImageView imageView = findViewById(R.id.imageSelView);
                    imageView.setImageURI(uri);
                }
            });

    selectBtn.setOnClickListener(v -> mGetContent.launch("image/*"));
    uploadBtn = findViewById(R.id.uploadBtn);
    uploadBtn.setOnClickListener(v -> uploadImage());
    FloatingActionButton homeButton = findViewById(R.id.return_button7);


    homeButton.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(UploadIRecipeImageActivity.this, HomeActivity.class);
            startActivity(intent);
        }
    });
}


/**
 * Uploads an image to a remote server using a multipart Volley request.
 *
 * This method creates and executes a multipart request using the Volley library to upload
 * an image to a predefined server endpoint. The image data is sent as a byte array and the
 * request is configured to handle multipart/form-data content type. The server is expected
 * to accept the image with a specific key ("image") in the request.
 *
 */
private void uploadImage(){
    Log.d("URI path", selectiedUri.getLastPathSegment());
    byte[] imageData = convertImageUriToBytes(selectiedUri);
    Log.d("URI", selectiedUri.toString());
    MultipartRequest multipartRequest = new MultipartRequest(
            Request.Method.POST,
            UPLOAD_URL,
            imageData,
            selectiedUri.getLastPathSegment().replace(':', '_'),
            response -> {
                // Handle response
                Toast.makeText(getApplicationContext(), response,Toast.LENGTH_LONG).show();
                Log.d("Upload", "Response: " + response);
            },
            error -> {
                // Handle error
                Toast.makeText(getApplicationContext(), error.getMessage(),Toast.LENGTH_LONG).show();
                Log.e("Upload", "Error: " + error.getMessage());
            }
    );

    VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(multipartRequest);
}

/**
 * Converts the given image URI to a byte array.
 *
 * This method takes a URI pointing to an image and converts it into a byte array. The conversion
 * involves opening an InputStream from the content resolver using the provided URI, and then
 * reading the content into a byte array. This byte array represents the binary data of the image,
 * which can be used for various purposes such as uploading the image to a server.
 *
 * @param imageUri The URI of the image to be converted. This should be a content URI that points
 *                 to an image resource accessible through the content resolver.
 * @return A byte array representing the image data, or null if the conversion fails.
 *
 */
private byte[] convertImageUriToBytes(Uri imageUri) {
    try {
        InputStream inputStream = getContentResolver().openInputStream(imageUri);
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();

        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];

        int len;
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }

        return byteBuffer.toByteArray();
    } catch (IOException e) {
        e.printStackTrace();
    }
    return null;
}

}
