package com.example.controletreinosacademia

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.controletreinosacademia.databinding.ActivityAlunoBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

class AlunoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAlunoBinding
    private val db = FirebaseFirestore.getInstance()
    private val userId = FirebaseAuth.getInstance().currentUser!!.uid

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAlunoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        carregarInfoUsuario()
        carregarPlano()

        binding.btnRegistrarProgresso.setOnClickListener {
            registrarProgresso()
        }

        binding.btnEnviarRevisao.setOnClickListener {
            val mensagem = binding.editMensagemRevisao.text.toString().trim()

            if (mensagem.isEmpty()) {
                Toast.makeText(this, "Escreva sua solicitação", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val revisao = hashMapOf(
                "mensagem" to mensagem,
                "dataSolicitacao" to FieldValue.serverTimestamp()
            )

            db.collection("revisoes").document(userId).set(revisao)
                .addOnSuccessListener {
                    Toast.makeText(this, "Revisão solicitada com sucesso!", Toast.LENGTH_SHORT).show()
                    binding.editMensagemRevisao.setText("")
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Erro ao enviar solicitação", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun carregarPlano() {
        db.collection("planosTreino").document(userId).get()
            .addOnSuccessListener { doc ->
                if (doc.exists()) {
                    val descricao = doc.getString("descricao") ?: "Sem descrição"
                    binding.textDescricaoPlano.text = descricao
                } else {
                    binding.textDescricaoPlano.text = "Nenhum plano de treino atribuído."
                }
            }
            .addOnFailureListener {
                binding.textDescricaoPlano.text = "Erro ao carregar plano."
            }
    }

    private fun registrarProgresso() {
        val dataHoje = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        val progresso = hashMapOf(
            "treinoConcluido" to true,
            "dataRegistro" to FieldValue.serverTimestamp()
        )

        db.collection("progresso").document(userId)
            .collection("datas").document(dataHoje).set(progresso)
            .addOnSuccessListener {
                Toast.makeText(this, "Progresso registrado!", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Erro ao registrar progresso", Toast.LENGTH_SHORT).show()
            }
    }

    private fun carregarInfoUsuario() {
        db.collection("usuarios").document(userId).get()
            .addOnSuccessListener { doc ->
                if (doc.exists()) {
                    val nome = doc.getString("nome") ?: "Sem nome"
                    val email = doc.getString("email") ?: "Sem email"
                    val tipo = doc.getString("tipo") ?: "Desconhecido"

                    val info = "Usuário: $nome\nEmail: $email\nTipo: $tipo"
                    binding.textInfoUsuario.text = info
                }
            }
            .addOnFailureListener {
                binding.textInfoUsuario.text = "Erro ao carregar dados do usuário."
            }
    }
}
