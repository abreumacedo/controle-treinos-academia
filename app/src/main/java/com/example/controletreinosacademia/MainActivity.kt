package com.example.controletreinosacademia

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        auth = FirebaseAuth.getInstance()

        val currentUser = auth.currentUser

        if (currentUser != null) {
            // Usuário logado, verificar tipo no Firestore
            verificarTipoUsuario(currentUser.uid)
        } else {
            // Redirecionar para tela de login se não estiver logado
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }

    private fun verificarTipoUsuario(uid: String) {
        db.collection("usuarios").document(uid).get()
            .addOnSuccessListener { doc ->
                if (doc.exists()) {
                    val tipo = doc.getString("tipo")
                    when (tipo) {
                        "aluno" -> {
                            startActivity(Intent(this, AlunoActivity::class.java))
                            finish()
                        }
                        "treinador" -> {
                            startActivity(Intent(this, TreinadorActivity::class.java))
                            finish()
                        }
                        else -> {
                            Toast.makeText(this, "Tipo de usuário desconhecido", Toast.LENGTH_SHORT).show()
                            Log.w("MainActivity", "Tipo de usuário inválido: $tipo")
                        }
                    }
                } else {
                    Toast.makeText(this, "Usuário não encontrado no Firestore", Toast.LENGTH_SHORT).show()
                    Log.e("MainActivity", "Documento do usuário não existe: $uid")
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Erro ao buscar dados do usuário", Toast.LENGTH_SHORT).show()
                Log.e("MainActivity", "Erro ao buscar tipo de usuário", it)
            }
    }
}
