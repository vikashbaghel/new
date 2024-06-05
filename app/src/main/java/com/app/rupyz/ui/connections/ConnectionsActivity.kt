package com.app.rupyz.ui.connections

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.app.rupyz.R
import com.app.rupyz.databinding.ActivityConnectionsBinding
import com.app.rupyz.generic.utils.AppConstant
import com.app.rupyz.generic.utils.Utility
import com.app.rupyz.model_kt.ConnectionListItem
import com.app.rupyz.model_kt.NetWorkConnectModel
import com.app.rupyz.ui.organization.profile.OrgProfileActivity


class ConnectionsActivity : AppCompatActivity(), ConnectionActionListener {
    private lateinit var binding: ActivityConnectionsBinding
    private lateinit var connectionAdapter: ConnectionAdapter
    private lateinit var connectionViewModel: ConnectionViewModel

    private var connectionList = ArrayList<ConnectionListItem>()

    private var connectionActionPosition = -1
    private var updated: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityConnectionsBinding.inflate(layoutInflater)

        connectionViewModel = ViewModelProvider(this)[ConnectionViewModel::class.java]

        if (intent.hasExtra(AppConstant.CONNECTION_TYPE)) {
            if (intent.getStringExtra(AppConstant.CONNECTION_TYPE)
                    .equals(AppConstant.MY_CONNECTION)
            ) {
                binding.tvToolbarTitle.text =
                    "My Connection (${intent.getIntExtra(AppConstant.MY_CONNECTION_COUNT, 0)})"
                connectionViewModel.getConnectionList("ACCEPTED")
            } else {
                binding.tvToolbarTitle.text =
                    "Invitation (${intent.getIntExtra(AppConstant.INVITATION_COUNT, 0)})"
                connectionViewModel.getRequestedConnection("PENDING")
            }
        }

        initRecyclerView()
        initObservers()

        binding.ivBack.setOnClickListener { finish() }
    }

    private fun initObservers() {
        connectionViewModel.getConnectionListData().observe(this) { data ->
            data.data?.let { it ->
                if (!it.results.isNullOrEmpty()) {
                    connectionList.addAll(it.results)
                    connectionAdapter.notifyDataSetChanged()
                } else {
                    binding.tvNoRecordFound.visibility = View.VISIBLE
                }
            }
        }

        connectionViewModel.getConnectionActionData().observe(this) {
            Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()

            updated = true
            if (connectionActionPosition != -1) {
                connectionList.removeAt(connectionActionPosition)
                connectionAdapter.notifyItemRemoved(connectionActionPosition)
            }
        }
    }

    private fun initRecyclerView() {
        binding.rvConnections.layoutManager = GridLayoutManager(this, 2)
        connectionAdapter = ConnectionAdapter(connectionList, this)
        binding.rvConnections.adapter = connectionAdapter
    }


    override fun onAccept(target_id: Int, position: Int) {
        connectionActionPosition = position
        val model = NetWorkConnectModel(target_id, "ACCEPT")
        connectionViewModel.connectionAction(model)
    }

    override fun onDecline(target_id: Int, position: Int) {
        connectionActionPosition = position
        val model = NetWorkConnectModel(target_id, "DECLINE")
        connectionViewModel.connectionAction(model)
    }

    override fun onShowInfo(model: ConnectionListItem) {
        startActivity(
            Intent(
                this,
                OrgProfileActivity::class.java
            ).putExtra(AppConstant.PROFILE_SLUG, model.slug)
        )
    }

    override fun onBackPressed() {
        if (updated) {
            val intent = Intent()
            setResult(RESULT_OK, intent)
            finish()
        } else {
            finish()
        }
    }

    override fun onShareConnection(model: ConnectionListItem, position: Int) {
        Utility.shareOthersProfileWithAll(this, model.legalName, model.slug)
    }

    override fun onRemoveConnection(connectionListItem: ConnectionListItem, position: Int) {
        connectionActionPosition = position
        val model = NetWorkConnectModel(connectionListItem.targetId, "REMOVE")
        connectionViewModel.connectionAction(model)
    }
}