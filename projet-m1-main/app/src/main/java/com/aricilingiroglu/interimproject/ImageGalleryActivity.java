package com.aricilingiroglu.interimproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

public class ImageGalleryActivity extends AppCompatActivity {

    private ImageView imageView;
    private Button buttonPrevious;
    private Button buttonNext;
    private Button buttonOk;

    private int[] imageResources = {R.drawable.image1, R.drawable.image2, R.drawable.image3};
    private int currentIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_gallery);

        imageView = findViewById(R.id.imageView);
        buttonPrevious = findViewById(R.id.buttonPrevious);
        buttonNext = findViewById(R.id.buttonNext);
        buttonOk = findViewById(R.id.buttonOk);

        buttonPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPreviousImage();
            }
        });

        buttonNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showNextImage();
            }
        });

        buttonOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateToProfil();
            }
        });
    }

    private void showPreviousImage() {
        if (currentIndex > 0) {
            currentIndex--;
            imageView.setImageResource(imageResources[currentIndex]);
        }
    }

    private void showNextImage() {
        if (currentIndex < imageResources.length - 1) {
            currentIndex++;
            imageView.setImageResource(imageResources[currentIndex]);
        }
    }

    private void navigateToProfil() {
        Intent intent = new Intent(ImageGalleryActivity.this, Profil.class);
        startActivity(intent);
    }
}
