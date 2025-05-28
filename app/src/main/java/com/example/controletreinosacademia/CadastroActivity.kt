package com.example.controletreinosacademia

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.controletreinosacademia.databinding.ActivityCadastroBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class CadastroActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCadastroBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCadastroBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        binding.btnCadastrar.setOnClickListener {
            val nome = binding.editNome.text.toString().trim()
            val email = binding.editEmail.text.toString().trim()
            val senha = binding.editSenha.text.toString().trim()
            val tipo = if (binding.radioAluno.isChecked) "aluno" else "treinador"

            if (nome.isEmpty() || email.isEmpty() || senha.isEmpty()) {
                Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            auth.createUserWithEmailAndPassword(email, senha)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val userId = auth.currentUser!!.uid
                        val usuario = hashMapOf(
                            "nome" to nome,
                            "email" to email,
                            "tipo" to tipo
                        )

                        db.collection("usuarios").document(userId)
                            .set(usuario)
                            .addOnSuccessListener {
                                Toast.makeText(this, "Cadastro realizado!", Toast.LENGTH_SHORT).show()
                                val intent = if (tipo == "aluno") {
                                    Intent(this, AlunoActivity::class.java)
                                } else {
                                    Intent(this, TreinadorActivity::class.java)
                                }
                                startActivity(intent)
                                finish()
                            }
                            .addOnFailureListener {
                                Toast.makeText(this, "Erro ao salvar dados", Toast.LENGTH_SHORT).show()
                                Log.e("CadastroActivity", "Erro ao salvar no Firestore", it)
                            }
                    } else {
                        Toast.makeText(this, "Erro ao cadastrar: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                        Log.e("CadastroActivity", "Erro no cadastro", task.exception)
                    }
                }
        }
    }
}
