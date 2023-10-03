package br.com.igorbag.githubsearch.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import br.com.igorbag.githubsearch.R
import br.com.igorbag.githubsearch.domain.Repository

class RepositoryAdapter(
    private val repositories: List<Repository>,
    private val repositoryItemClickListener: (Repository) -> Unit,
    var btnShareItemClickListener: (Repository) -> Unit,
) : RecyclerView.Adapter<RepositoryAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.repository_item, parent, false)
        return ViewHolder(view)
    }
    override fun getItemCount(): Int = repositories.size
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.nameTextView.text = repositories[position].name
        holder.itemView.setOnClickListener {
            val repository = repositories[position]
            repositoryItemClickListener(repository)
        }
        holder.btnShare.setOnClickListener {
            val repository = repositories[position]
            btnShareItemClickListener(repository)
        }
    }
    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nameTextView: TextView = view.findViewById(R.id.tv_nomerepositorio)
        val btnShare: ImageView = view.findViewById(R.id.iv_share)


    }
}


