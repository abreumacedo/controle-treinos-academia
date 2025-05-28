package com.example.controletreinosacademia

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.controletreinosacademia.databinding.ActivityTreinadorBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

class TreinadorActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTreinadorBinding
    private val db = FirebaseFirestore.getInstance()
    private val userId = FirebaseAuth.getInstance().currentUser!!.uid

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTreinadorBinding.inflate(layoutInflater)
        setContentView(binding.root)

        carregarInfoTreinador()

        binding.btnSalvarPlano.setOnClickListener {
            val alunoId = binding.editIdAluno.text.toString().trim()
            val descricao = binding.editDescricaoPlano.text.toString().trim()

            if (alunoId.isEmpty() || descricao.isEmpty()) {
                Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val plano = hashMapOf(
                "descricao" to descricao,
                "dataCriacao" to FieldValue.serverTimestamp()
            )

            db.collection("planosTreino").document(alunoId).set(plano)
                .addOnSuccessListener {
                    Toast.makeText(this, "Plano salvo!", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Erro ao salvar plano", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun carregarInfoTreinador() {
        db.collection("usuarios").document(userId).get()
            .addOnSuccessListener { doc ->
                if (doc.exists()) {
                    val nome = doc.getString("nome") ?: "Sem nome"
                    val email = doc.getString("email") ?: "Sem email"
                    val tipo = doc.getString("tipo") ?: "Desconhecido"

                    val info = "Usuário: $nome\nEmail: $email\nTipo: $tipo"
                    binding.textInfoTreinador.text = info
                }
            }
            .addOnFailureListener {
                binding.textInfoTreinador.text = "Erro ao carregar dados do treinador."
            }
    }
}
