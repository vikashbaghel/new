package com.app.rupyz.ui.discovery

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.appcompat.app.AppCompatActivity
import com.app.rupyz.databinding.ActivitySearchAllDiscoveryBinding

class SearchAllDiscoveryActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySearchAllDiscoveryBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchAllDiscoveryBinding.inflate(layoutInflater)

        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                if (s.toString().length > 1) {
                    getResult()
                }
            }
        })

        binding.ivBack.setOnClickListener { finish() }
    }

    private fun getResult() {

    }
}