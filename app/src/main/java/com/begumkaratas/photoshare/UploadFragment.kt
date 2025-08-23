package com.begumkaratas.photoshare

import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.begumkaratas.photoshare.databinding.FragmentUploadBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.Firebase
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.storage
import java.util.UUID

class UploadFragment : Fragment() {

    private var _binding: FragmentUploadBinding? = null
    private val binding get() = _binding!!

    private lateinit var activityResultLauncher: ActivityResultLauncher<Intent>
    private lateinit var permissionLauncher: ActivityResultLauncher<String>
    private var secilenGorsel: Uri? = null
    private var secilenBitmap: Bitmap? = null

    private lateinit var auth: FirebaseAuth
    private lateinit var storage: FirebaseStorage
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = Firebase.auth
        storage = Firebase.storage
        db = Firebase.firestore
        registerLauncher()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUploadBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.imageView.setOnClickListener { gorselSec(it) }
        binding.yukleButton.setOnClickListener { yukleTiklandi(it) }
    }

    private fun gorselSec(view: View) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(requireContext(), android.Manifest.permission.READ_MEDIA_IMAGES)
                != PackageManager.PERMISSION_GRANTED
            ) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(),
                        android.Manifest.permission.READ_MEDIA_IMAGES)) {
                    Snackbar.make(view, "Galeriye gitmek için izin vermeniz gerekiyor", Snackbar.LENGTH_INDEFINITE)
                        .setAction("İzin Ver") { permissionLauncher.launch(android.Manifest.permission.READ_MEDIA_IMAGES) }
                        .show()
                } else {
                    permissionLauncher.launch(android.Manifest.permission.READ_MEDIA_IMAGES)
                }
            } else {
                val intentToGallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                activityResultLauncher.launch(intentToGallery)
            }
        } else {
            if (ContextCompat.checkSelfPermission(requireContext(), android.Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED
            ) {
                permissionLauncher.launch(android.Manifest.permission.READ_EXTERNAL_STORAGE)
            } else {
                val intentToGallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                activityResultLauncher.launch(intentToGallery)
            }
        }
    }

    private fun registerLauncher() {
        activityResultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == android.app.Activity.RESULT_OK) {
                    val data = result.data
                    if (data != null) {
                        secilenGorsel = data.data
                        if (secilenGorsel != null) {
                            Toast.makeText(requireContext(), "✅ Görsel seçildi", Toast.LENGTH_SHORT).show()
                            try {
                                secilenBitmap = if (Build.VERSION.SDK_INT >= 28) {
                                    val source = ImageDecoder.createSource(requireActivity().contentResolver, secilenGorsel!!)
                                    ImageDecoder.decodeBitmap(source)
                                } else {
                                    MediaStore.Images.Media.getBitmap(requireActivity().contentResolver, secilenGorsel)
                                }
                                binding.imageView.setImageBitmap(secilenBitmap)
                            } catch (e: Exception) {
                                e.printStackTrace()
                                Toast.makeText(requireContext(), "❌ Görsel işlenemedi", Toast.LENGTH_LONG).show()
                            }
                        }
                    }
                }
            }

        permissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) { result ->
                if (result) {
                    val intentToGallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                    activityResultLauncher.launch(intentToGallery)
                } else {
                    Toast.makeText(requireContext(), "İzni reddettiniz, izin gerekli", Toast.LENGTH_LONG).show()
                }
            }
    }

    private fun yukleTiklandi(view: View) {
        if (secilenGorsel == null) {
            Toast.makeText(requireContext(), "⚠️ Lütfen önce bir görsel seçin", Toast.LENGTH_LONG).show()
            return
        }

        Toast.makeText(requireContext(), "📤 Upload başladı", Toast.LENGTH_SHORT).show()

        val uuid = UUID.randomUUID()
        val gorselAdi = "$uuid.jpg"
        val reference = storage.reference
        val gorselRef = reference.child("images").child(gorselAdi)

        gorselRef.putFile(secilenGorsel!!)
            .addOnSuccessListener {
                Toast.makeText(requireContext(), "✅ Upload başarılı", Toast.LENGTH_SHORT).show()
                gorselRef.downloadUrl.addOnSuccessListener { uri ->
                    val downloadUrl = uri.toString()
                    val postMap = hashMapOf<String, Any>()
                    postMap["downloadUrl"] = downloadUrl
                    postMap["email"] = auth.currentUser!!.email.toString()
                    postMap["comment"] = binding.commentText.text.toString()
                    postMap["date"] = Timestamp.now()

                    db.collection("Posts").add(postMap)
                        .addOnSuccessListener {
                            Toast.makeText(requireContext(), "🔥 Firestore OK", Toast.LENGTH_SHORT).show()
                            // ✅ Action ile navigation
                            val navController = requireActivity()
                                .supportFragmentManager
                                .findFragmentById(R.id.fragmentContainerView4) // senin NavHostFragment id
                                ?.findNavController()

                            navController?.navigate(R.id.feedFragment)
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(requireContext(), "❌ Firestore Error: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
                        }
                }.addOnFailureListener { e ->
                    Toast.makeText(requireContext(), "❌ Download URL Error: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(requireContext(), "❌ Upload Error: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
