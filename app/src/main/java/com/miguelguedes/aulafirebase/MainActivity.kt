package com.miguelguedes.aulafirebase

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.miguelguedes.aulafirebase.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    private val autenticacao by lazy {
        FirebaseAuth.getInstance()
    }

    private val bancoDados by lazy {
        FirebaseFirestore.getInstance()
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

        binding.btnExecutar.setOnClickListener {

            //salvarDados()
//            atualizarRemoverDados()
//            listarDados()
            pesquisarDados()

//            cadastroUsuario()
            logarUsuario()
        }
    }

    private fun pesquisarDados() {
        val refUsuarios = bancoDados
            .collection("usuarios")
//            .whereEqualTo("nome", "Miguel Guedes")
//            .whereNotEqualTo("nome", "Miguel Guedes")
//            .whereIn("nome", listOf("Miguel Guedes", "Ana Maria"))
//            .whereNotIn("nome", listOf("Miguel Guedes", "Ana Maria"))
//            .whereArrayContains("conhecimentos", "hilt")
// >,>=,<,<=
//            .whereGreaterThan("idade", "30")
//            .whereGreaterThanOrEqualTo("idade", "22")

//            .whereLessThan("idade", "30")
//            .whereLessThanOrEqualTo("idade", "22")
//            .whereGreaterThanOrEqualTo("idade", "35")
//            .whereLessThanOrEqualTo("idade", "56")
//            .orderBy("idade", Query.Direction.ASCENDING)
            .orderBy("idade", Query.Direction.DESCENDING)


        refUsuarios.addSnapshotListener{ querySnapshot, error ->

            val listaDocuments = querySnapshot?.documents

            var listaResultado = ""
            listaDocuments?.forEach { documentSnapshot ->
                val dados = documentSnapshot.data
                if (dados != null) {
                    val nome = dados["nome"] as String
                    val idade = dados["idade"] as String
                    listaResultado += "Nome: $nome, Idade: $idade \n"

                }
            }

            binding.textResultado.text = listaResultado
        }
    }

    private fun salvardadosUsuario(nome: String, idade: String){
        val idUsuariologado = autenticacao.currentUser?.uid
        if (idUsuariologado != null) {

            val dados = mapOf(
                "nome" to nome,
                "idade" to idade,
                //...varios outros dados
            )

            bancoDados
                .collection("usuarios")
                .document(idUsuariologado)
                .set(dados)
        }
    }

    private fun listarDados() {

//        salvardadosUsuario("Miguel Guedes", "22")

        val idUsuariologado = autenticacao.currentUser?.uid

        if (idUsuariologado != null) {

            val referenciaUsuario = bancoDados
                .collection("usuarios")
//                .document(idUsuariologado)

            referenciaUsuario.addSnapshotListener{ querySnapshot, error ->

                val listaDocuments = querySnapshot?.documents

                var listaResultado = ""
                listaDocuments?.forEach { documentSnapshot ->
                    val dados = documentSnapshot.data
                    if (dados != null) {
                        val nome = dados["nome"] as String
                        val idade = dados["idade"] as String
                        listaResultado += "Nome: $nome, Idade: $idade \n"

                    }
                }

                binding.textResultado.text = listaResultado

                /*val dados = documentSnapshot?.data
                if (dados != null) {
                    val nome = dados["nome"] as String
                    val idade = dados["idade"] as String
                    val texto = "Nome: $nome, Idade: $idade"

                    binding.textResultado.text = texto
                }*/
            }

            /*referenciaUsuario.get().addOnSuccessListener { documentSnapShot ->
                val dados = documentSnapShot.data
                if (dados != null) {
                    val nome = dados["nome"] as String
                    val idade = dados["idade"] as String
                    val texto = "Nome: $nome, Idade: $idade"

                    binding.textResultado.text = texto
                }

            }*/

        }


    }

    private fun atualizarRemoverDados() {

        val dados = mapOf(
            "nome" to "jose",
            "idade" to 30,
            //"cpf" to "111.222.333-44"
        )

        autenticacao.currentUser?.uid

        val referenciaUsuario = bancoDados
            .collection("usuarios")
            //.document("1")

        //referenciaJose.set(dados)
        referenciaUsuario
//            .update("nome", "Jose Silva")
//            .delete()
            .add(dados)
            .addOnSuccessListener{
                exibirMesagem("Dados atualizados com sucesso")
            }.addOnFailureListener{ exception ->
                exibirMesagem("Erro ao atualizar os dados")
            }
    }

    private fun salvarDados() {

        val dados = mapOf(
            "nome" to "jose",
            "idade" to 30,
            "cpf" to "111.222.333-44"
        )

        bancoDados
            .collection("usuarios")
            .document("2")
            .set(dados)
            .addOnSuccessListener{
                exibirMesagem("Dados salvos com sucesso")
            }.addOnFailureListener{ exception ->
                exibirMesagem("Erro ao salvar os dados")
            }

    }

    override fun onStart() {
        super.onStart()
        logarUsuario()
        //verificarUserLogado()
    }

    private fun verificarUserLogado() {

        //autenticacao.signOut()
        val usuario = autenticacao.currentUser
        val id = usuario?.uid

        if (usuario != null) {
            exibirMesagem("Usuário logado com id: $id")
            startActivity(
                Intent(this, PrincipalActivity::class.java)
            )
        }
    }

    private fun logarUsuario() {


        val email = "miguelguedes.oli@gmail.com"
        val senha = "1983@Mi03"

        //Tela de Autenticação do app
        autenticacao.signInWithEmailAndPassword(email, senha)
            .addOnSuccessListener { authResult ->
                binding.textResultado.text = "Sucesso ao logar usuário"
                startActivity(
                    Intent(this, UploadImageActivity::class.java)
                )
            }.addOnFailureListener{ exception ->
                binding.textResultado.text = "Falha ao logar usuário: ${exception.message}"
            }

    }

    private fun cadastroUsuario() {

        //Dados Digitados pelo usuario
        val email = "teste@gmail.com"
        val senha = "1983@Mi03"
        val nome = "Teste Upload"
        val idade = "22"

        //Tela de Autenticação do app
        autenticacao.createUserWithEmailAndPassword(email, senha)
            .addOnSuccessListener { authResult ->

                val email = authResult.user?.email
                val id = authResult.user?.uid

                //Salvar dados do usuário
                salvardadosUsuario(nome, idade)


                binding.textResultado.text = "Sucesso: $id - $email"
            }.addOnFailureListener { exception ->
                val mensagemErro = exception.message
                binding.textResultado.text = "Falha: $mensagemErro"
            }
    }

    private fun exibirMesagem(texto: String){
        Toast.makeText(this, texto, Toast.LENGTH_LONG).show()
    }
}


