package com.miguelguedes.aulafirebase

import android.Manifest
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import com.miguelguedes.aulafirebase.databinding.ActivityPrincipalBinding
import com.miguelguedes.aulafirebase.databinding.ActivityUploadImageBinding
import com.miguelguedes.aulafirebase.helper.Permission
import com.squareup.picasso.Picasso
import java.io.ByteArrayOutputStream
import java.util.UUID

class UploadImageActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityUploadImageBinding.inflate(layoutInflater)
    }

    private val autenticacao by lazy {
        FirebaseAuth.getInstance()
    }

    private val armazenamento by lazy {
        FirebaseStorage.getInstance()
    }

    private var uriImagemSeliconada: Uri? = null
    private var bitMapImagemSeliconada: Bitmap? = null

    private val abrirCamera = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        bitMapImagemSeliconada = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            result.data?.extras?.getParcelable("data", Bitmap::class.java)
        } else {
            result.data?.extras?.getParcelable("data")
        }
        binding.imageSelecionado.setImageBitmap(bitMapImagemSeliconada)

    }

    private val abrirGaleria = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) {
            binding.imageSelecionado.setImageURI(uri)
            uriImagemSeliconada = uri
            Toast.makeText(this, "Imagem selecionada", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Nenhuma imagem selecionada", Toast.LENGTH_SHORT).show()
        }
    }

    private val permissoes = listOf(
        Manifest.permission.CAMERA,
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.RECORD_AUDIO,
        Manifest.permission.ACCESS_COARSE_LOCATION
    )

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String?>,
        grantResults: IntArray,
        deviceId: Int
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults, deviceId)

        Log.i("permissao_app", "rquestCode: $requestCode")

        permissions.forEachIndexed { index, valor ->
            Log.i("permissao_app", "permissao: $valor status: $index")
        }

        grantResults.forEachIndexed { index, valor->
            Log.i("permissao_app", "permissao: $valor status: $index")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        Permission.requisitarPermissao(
            this,
            permissoes,
            100
        )

        binding.btnGaleria.setOnClickListener{
            abrirGaleria.launch("image/*")//MIME type
        }

        binding.btnCamera.setOnClickListener{
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            abrirCamera.launch(intent)
        }

        binding.btnUpload.setOnClickListener{
//            uploadGaleria()
            uploadCamera()
        }

        binding.btnRecuperar.setOnClickListener{
            recuperarImagem()
        }
    }

    private fun recuperarImagem() {
        val idUsuario = autenticacao.currentUser?.uid
        if (idUsuario != null) {
            armazenamento
                .getReference("fotos")
                .child(idUsuario)
                .child("foto.jpg")
                .downloadUrl
                .addOnSuccessListener { urlFirebase ->
                    Picasso.get()
                        .load(urlFirebase)
                        .into(binding.imageRecuperada)
                }
        }

    }

    private fun uploadGaleria() {

        val idUsuario = autenticacao.currentUser?.uid
//        val nomeImage = UUID.randomUUID().toString()
        if ( uriImagemSeliconada != null /*&& idUsuario != null*/) {
            armazenamento
                .getReference("fotos")
                .child(idUsuario!!)
                .child("foto.jpg")
                .putFile(uriImagemSeliconada!!)
                .addOnSuccessListener { task ->
                    Toast.makeText(this, "Sucesso ao fazer Upload", Toast.LENGTH_SHORT).show()
                    task.metadata?.reference?.downloadUrl?.addOnSuccessListener { urlFirebase ->
                        Toast.makeText(this, urlFirebase.toString(), Toast.LENGTH_SHORT).show()
                    }
                }.addOnFailureListener { erro ->
                    Toast.makeText(this, "Erro ao fazer upload", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun uploadCamera() {

        val idUsuario = autenticacao.currentUser?.uid
//        val nomeImage = UUID.randomUUID().toString()
        val outputStream = ByteArrayOutputStream()
        bitMapImagemSeliconada?.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
        if ( bitMapImagemSeliconada != null && idUsuario != null) {
            armazenamento
                .getReference("fotos")
                .child(idUsuario)
                .child("foto.jpg")
                .putBytes(outputStream.toByteArray())
                .addOnSuccessListener { task ->
                    Toast.makeText(this, "Sucesso ao fazer Upload", Toast.LENGTH_SHORT).show()
                    task.metadata?.reference?.downloadUrl?.addOnSuccessListener { urlFirebase ->
                        Toast.makeText(this, urlFirebase.toString(), Toast.LENGTH_SHORT).show()
                    }
                }.addOnFailureListener { erro ->
                    Toast.makeText(this, "Erro ao fazer upload", Toast.LENGTH_SHORT).show()
                }
        }
    }

}