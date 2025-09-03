package com.miguelguedes.aulafirebase

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.miguelguedes.aulafirebase.databinding.ActivityMainBinding
import com.miguelguedes.aulafirebase.databinding.ActivityPrincipalBinding

class PrincipalActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityPrincipalBinding.inflate(layoutInflater)
    }

    private val autenticacao by lazy {
        FirebaseAuth.getInstance()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.btnDeslogar.setOnClickListener {

            deslogarUsuario()
        }

    }

    private fun deslogarUsuario() {
        autenticacao.signOut()
        startActivity(
            Intent(this, MainActivity::class.java)
        )
    }
}