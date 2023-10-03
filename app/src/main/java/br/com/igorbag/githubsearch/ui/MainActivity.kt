package com.example.githubsearcher.ui

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import br.com.igorbag.githubsearch.R
import br.com.igorbag.githubsearch.data.GitHubService
import br.com.igorbag.githubsearch.domain.Repository
import br.com.igorbag.githubsearch.ui.adapter.RepositoryAdapter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class MainActivity : AppCompatActivity() {

    lateinit var nomeUsuario: EditText
    lateinit var btnConfirmar: Button
    lateinit var listaRepositories: RecyclerView
    lateinit var githubApi: GitHubService
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setupView()
        showUserName()
        setupRetrofit()
        getAllReposByUserName()
        setupListeners()
    }
    fun setupView() {

        nomeUsuario = findViewById(R.id.et_nome_usuario)
        btnConfirmar = findViewById(R.id.btn_confirmar)
        listaRepositories = findViewById(R.id.rv_lista_repositories)

        sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
    }
    private fun setupListeners() {
        btnConfirmar.setOnClickListener {
            saveUserLocal()
        }
    }
    private fun saveUserLocal() {
        val userName = nomeUsuario.text.toString()
        val editor = sharedPreferences.edit()
        editor.putString("user_name", userName)
        editor.apply()
    }
    private fun showUserName() {
        val savedUserName = sharedPreferences.getString("user_name", "")
        nomeUsuario.setText(savedUserName)
    }
    fun setupRetrofit() {
        val baseUrl = "https://api.github.com/"
        val retrofit = Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        githubApi = retrofit.create(GitHubService::class.java)

    }
    fun getAllReposByUserName() {

        val user = nomeUsuario.text.toString()

        val call = githubApi.getAllRepositoriesByUser(user)

        call.enqueue(object : Callback<List<Repository>> {
            override fun onResponse(
                call: Call<List<Repository>>,
                response: Response<List<Repository>>
            ) {
                if (response.isSuccessful) {
                    val repositories = response.body()
                    if (repositories != null) {
                        setupAdapter(repositories)
                    } else {
                        Toast.makeText(this@MainActivity, "O usuário não possui repositórios.", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this@MainActivity, "Erro ao buscar repositórios.", Toast.LENGTH_SHORT).show()
                }
            }
            override fun onFailure(call: Call<List<Repository>>, t: Throwable) {
                Toast.makeText(this@MainActivity, "Erro de rede: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
    fun setupAdapter(list: List<Repository>) {
        val adapter = RepositoryAdapter(list,
            repositoryItemClickListener = { repository ->
                val urlRepository = repository.htmlUrl
                openBrowser(urlRepository)
            },
            btnShareItemClickListener = { repository ->
                shareRepositoryLink(repository.htmlUrl)
            }
        )
        listaRepositories.adapter = adapter
        listaRepositories.layoutManager = LinearLayoutManager(this)
    }
    fun shareRepositoryLink(urlRepository: String) {
        val sendIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, urlRepository)
            type = "text/plain"
        }
        val shareIntent = Intent.createChooser(sendIntent, null)
        startActivity(shareIntent)
    }
    fun openBrowser(urlRepository: String) {
        startActivity(
            Intent(
                Intent.ACTION_VIEW,
                Uri.parse(urlRepository)
            )
        )
    }
}