package com.app.rupyz.ui.organization.adapter

import androidx.recyclerview.widget.RecyclerView
import android.view.ViewGroup
import android.view.LayoutInflater
import android.annotation.SuppressLint
import android.content.Context
import android.text.TextWatcher
import com.app.rupyz.ui.organization.onboarding.activity.ClaimMaskedDetailsActivity
import android.text.Editable
import com.app.rupyz.databinding.AuthorizedSignatoriesListInsideItemBinding

class AuthorizedSignatoriesListAdapter(private val listdata: List<String>, private val mContext: Context) :
    RecyclerView.Adapter<AuthorizedSignatoriesListAdapter.ViewHolder>() {

    private var binding: AuthorizedSignatoriesListInsideItemBinding? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        binding = AuthorizedSignatoriesListInsideItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(
            binding!!
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, @SuppressLint("RecyclerView") position: Int) {
        binding!!.txtMaskedName.text = listdata[position]
        binding!!.etMaskedName.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                (mContext as ClaimMaskedDetailsActivity).updateAuthorized(position, s.toString())
            }

            override fun afterTextChanged(s: Editable) {}
        })
    }

    override fun getItemCount(): Int {
        return listdata.size
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    class ViewHolder(var binding: AuthorizedSignatoriesListInsideItemBinding) :
        RecyclerView.ViewHolder(
            binding.root
        )
}