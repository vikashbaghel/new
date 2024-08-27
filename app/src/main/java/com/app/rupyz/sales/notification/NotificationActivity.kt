package com.app.rupyz.sales.notification

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.rupyz.databinding.ActivityNotificationBinding
import com.app.rupyz.generic.helper.PaginationScrollListener
import com.app.rupyz.generic.utils.AppConstant
import com.app.rupyz.model_kt.NotificationListItemModel
import com.app.rupyz.sales.customer.CustomerFeedbackDetailActivity
import com.app.rupyz.sales.lead.LeadDetailsActivity
import com.app.rupyz.sales.orderdispatch.OrderDispatchHistoryActivity
import com.app.rupyz.sales.orders.OrderDetailActivity
import com.app.rupyz.sales.payment.PaymentDetailsActivity
import com.google.gson.JsonObject

class NotificationActivity : AppCompatActivity(), NotificationAdapter.INotificationActionListener {
    private lateinit var binding: ActivityNotificationBinding
    private var isDataRead: Boolean = false
    private lateinit var notificationAdapter: NotificationAdapter
    private var list = ArrayList<NotificationListItemModel>()

    private lateinit var notificationViewModel: NotificationViewModel

    private var isPageLoading = false
    private var isApiLastPage = false

    private var currentPage = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNotificationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        notificationViewModel = ViewModelProvider(this)[NotificationViewModel::class.java]

        initRecyclerView()
        initObservers()

        getNotificationList()

        binding.swipeToRefresh.setOnRefreshListener {
            currentPage = 1
            getNotificationList()
            binding.swipeToRefresh.isRefreshing = false
        }

        binding.tvMarkSeenAll.setOnClickListener {
            val jsonObject = JsonObject()
            jsonObject.addProperty("mark_all_as_read", true)
            notificationViewModel.readNotification(jsonObject)

            list.forEach { it.isSeen = true }
            notificationAdapter.notifyDataSetChanged()

            isDataRead = true
        }

        binding.ivClose.setOnClickListener {
            onBackPressed()
        }
    }

    private fun initObservers() {
        notificationViewModel.notificationLiveData.observe(this) { data ->
            if (data.error != true) {
                isPageLoading = false

                if (data?.data?.unreadCount != null && data.data.unreadCount > 0) {
                    binding.tvMarkSeenAll.visibility = View.VISIBLE
                }

                data.data?.results?.let { it ->
                    if (it.isNotEmpty()) {

                        if (currentPage == 1) {
                            list.clear()
                        }
                        list.addAll(it)
                        notificationAdapter.notifyDataSetChanged()

                        if (list.size < 30) {
                            isApiLastPage = true
                        }
                    } else {
                        isApiLastPage = true
                        if (currentPage == 1) {
                            binding.clEmptyData.visibility = View.VISIBLE
                        }
                    }
                }
            } else {
                Toast.makeText(this, data.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun getNotificationList() {
        notificationViewModel.getNotificationList(currentPage)
    }

    private fun initRecyclerView() {
        val linearLayoutManager = LinearLayoutManager(this)
        binding.rvNotification.layoutManager = linearLayoutManager
        notificationAdapter = NotificationAdapter(list, this)
        binding.rvNotification.adapter = notificationAdapter


        binding.rvNotification.addOnScrollListener(object :
            PaginationScrollListener(linearLayoutManager) {
            override fun loadMoreItems() {
                isPageLoading = true
                currentPage += 1
                getNotificationList()
            }

            override fun isLastPage(): Boolean {
                return isApiLastPage
            }

            override fun isLoading(): Boolean {
                return isPageLoading
            }
        })
    }

    override fun navigateNotificationScreen(model: NotificationListItemModel, position: Int) {
        if (model.isSeen == false) {
            val jsonObject = JsonObject()
            jsonObject.addProperty("id", model.id)
            jsonObject.addProperty("is_seen", true)

            notificationViewModel.readNotification(jsonObject)
        }

        val moduleUid = model.payload?.moduleUid

        var intent: Intent? = null
        when (model.payload?.module_name) {
            "ORDER-DISPATCH" -> {
                intent = Intent(this, OrderDispatchHistoryActivity::class.java)
                if (model.payload.parentModuleUid != null) {
                    intent.putExtra(AppConstant.ORDER_ID, model.payload.parentModuleUid)
                }

                intent.putExtra(AppConstant.DISPATCH_ID, moduleUid)
                intent.putExtra(AppConstant.ORDER_CLOSE, false)
            }

            "ORDER" -> {
                intent = Intent(this, OrderDetailActivity::class.java)
                intent.putExtra(AppConstant.ORDER_ID, moduleUid)
            }

            "CUSTOMER-PAYMENT" -> {
                intent = Intent(this, PaymentDetailsActivity::class.java)
                intent.putExtra(AppConstant.PAYMENT_ID, moduleUid)
            }

            "LEAD" -> {
                intent = Intent(this, LeadDetailsActivity::class.java)
                intent.putExtra(AppConstant.LEAD_ID, moduleUid)
            }

            "LEAD-FEEDBACK", "CUSTOMER-FEEDBACK", "FOLLOWUP-REMINDERS" -> {
                intent = Intent(this, CustomerFeedbackDetailActivity::class.java)
                if (model.payload.parentModuleUid != null) {
                    intent.putExtra(AppConstant.ACTIVITY_ID, model.payload.parentModuleUid)
                }
            }
        }

        if (intent != null) {

            startActivity(intent)

            list[position].isSeen = true
            notificationAdapter.notifyItemChanged(position)

            isDataRead = true
        }
    }

    override fun onBackPressed() {
        if (isDataRead) {
            val intent = Intent()
            setResult(RESULT_OK, intent)
        }
        finish()
    }
}