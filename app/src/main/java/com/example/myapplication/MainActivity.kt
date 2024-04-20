package com.example.myapplication

import android.Manifest
import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.Firebase
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.storage
import org.w3c.dom.Text

class MainActivity : AppCompatActivity() {

    private val CAMERA_PERMISSION_CODE = 100
    private val IMAGE_CAPTURE_CODE = 101
    private val LOCATION_PERMISSION_CODE = 102
     private var latitude:String=""
    private var longitude:String=""
    val storage = Firebase.storage
    val firestore = Firebase.firestore

    private lateinit var imageUri: Uri
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btnCapture = findViewById<Button>(R.id.CaptureBtn)

        btnCapture.setOnClickListener {
            // Check camera permissions




            if (checkCameraPermissions()) {
                openCamera()
            } else {
                requestCameraPermissions()
            }
        }


        // Initialize FusedLocationProviderClient
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        val sub=findViewById<Button>(R.id.SubmitBtn)
        sub.setOnClickListener {
            databaseInsert();
        }
    }

    private fun checkCameraPermissions(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestCameraPermissions() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.CAMERA),
            CAMERA_PERMISSION_CODE
        )
    }

    private fun openCamera() {
        val values = ContentValues()
        values.put(MediaStore.Images.Media.TITLE, "New Picture")
        values.put(MediaStore.Images.Media.DESCRIPTION, "From the Camera")
        imageUri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)!!



        // Camera intent
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
        startActivityForResult(cameraIntent, IMAGE_CAPTURE_CODE)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CAMERA_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openCamera()
            } else {
                Toast.makeText(this, "Camera permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == IMAGE_CAPTURE_CODE && resultCode == Activity.RESULT_OK) {
            val imageView = findViewById<ImageView>(R.id.trash)
            imageView.setImageURI(imageUri)
            val btnname=findViewById<Button>(R.id.CaptureBtn);
            btnname.visibility=View.INVISIBLE
            val btnbak=findViewById<Button>(R.id.final_back)
            btnbak.visibility=View.INVISIBLE
            var sub=findViewById<Button>(R.id.SubmitBtn)
            sub.visibility=View.VISIBLE

            getLastLocation()


        }
    }

    private fun databaseInsert() {
val     SubmitBtn=findViewById<Button>(R.id.SubmitBtn)
val imagess=findViewById<ImageView>(R.id.trash)
        val finalanimvar=findViewById<TextView>(R.id.finaltxt)
        val btnbak=findViewById<Button>(R.id.final_back)
        // Load the fade-out animation
            val fadeOutAnimation = AnimationUtils.loadAnimation(this, R.anim.trashanim)
        val fadeInAnimation = AnimationUtils.loadAnimation(this, R.anim.planeanim)
        val finalAnimation= AnimationUtils.loadAnimation(this,R.anim.finaltxtanim)
            // Start the fade-out animation on the button
            SubmitBtn.startAnimation(fadeOutAnimation)
imagess.startAnimation(fadeOutAnimation)
        finalanimvar.startAnimation(finalAnimation)
        btnbak.startAnimation(finalAnimation)
        val rocketwala = findViewById<ImageView>(R.id.rock)
//        val last_text = findViewById<TextView>(R.id.smile)
        rocketwala.startAnimation(fadeInAnimation)


        // You may also set a listener to handle the end of the animation if needed
            fadeOutAnimation.setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationStart(animation: Animation?) {
                    // Perform actions when the animation starts
                }

                override fun onAnimationEnd(animation: Animation?) {
                    // Perform actions when the animation ends
                    // For example, set the button's visibility to GONE
                    SubmitBtn.visibility = View.INVISIBLE
                    imagess.visibility=View.GONE
                    rocketwala.visibility=View.VISIBLE

                    rocketwala.x += 1000f
                    rocketwala.y += 50f

                    finalanimvar.visibility=View.VISIBLE
                    btnbak.visibility=View.VISIBLE
                }

                override fun onAnimationRepeat(animation: Animation?) {
                    // Perform actions on animation repeat, if needed
                }
            })

        // Reference to Firebase Cloud Storage
        val storageRef = storage.reference

        // Define a reference for the image file in Firebase Storage
        val imageRef = storageRef.child("images/${imageUri.lastPathSegment}")

        // Upload the image
        imageRef.putFile(imageUri)
            .addOnSuccessListener { taskSnapshot ->
                // Get the download URL of the uploaded image
                imageRef.downloadUrl.addOnSuccessListener { uri ->
                    val imageUrl = uri.toString()
                    Toast.makeText(this, imageUrl, Toast.LENGTH_SHORT).show()
                    // Get location information (assuming you obtained it in getLastLocation())
                    val location = GeoPoint(latitude.toDouble(), longitude.toDouble())

                    // Create a data map to store in Firestore
                    val data = hashMapOf(
                        "imageUrl" to imageUrl,
                        "location" to location
                    )

                    // Add data to Firestore
                    firestore.collection("Trash_location")
                        .add(data)
                        .addOnSuccessListener { documentReference ->
                            Toast.makeText(this, "Submitted Successfully", Toast.LENGTH_SHORT).show()
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(this, "Failed to submit", Toast.LENGTH_SHORT).show()
                        }
                }
            }
            .addOnFailureListener { e ->
                // Handle upload errors
            }
    }


    private fun getLastLocation() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_CODE
            )
            return
        }

        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                if (location != null) {
                    // Use the location here as needed
                    latitude = location.latitude.toString()
                    longitude = location.longitude.toString()
                    // For demonstration, we're just showing a toast message with the latitude and longitude
                    Toast.makeText(
                        this,
                        "Latitude: $latitude, Longitude: $longitude",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    Toast.makeText(this, "Location not available", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Failed to get location: ${e.message}", Toast.LENGTH_SHORT)
                    .show()
            }
    }
}