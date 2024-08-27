package com.app.rupyz.sales.expense

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.rupyz.R
import com.app.rupyz.databinding.ActivityTotalExpenseDetailsBinding
import com.app.rupyz.generic.base.BaseActivity
import com.app.rupyz.generic.helper.DateFormatHelper
import com.app.rupyz.generic.helper.PaginationScrollListener
import com.app.rupyz.generic.utils.AppConstant
import com.app.rupyz.generic.utils.AppConstant.REJECTED
import com.app.rupyz.generic.utils.CalculatorHelper
import com.app.rupyz.generic.utils.DeleteDialog
import com.app.rupyz.generic.utils.Utils
import com.app.rupyz.model_kt.ExpenseDataItem
import com.app.rupyz.model_kt.ExpenseTrackerDataItem

class TotalExpenseDetailsActivity : BaseActivity(),
    SubmitWithoutEndDateBottomSheetDialogFragment.IExpensesSubmitListener,
    ExpensesListAdapter.IExpensesListener, DeleteDialog.IOnClickListener,
    ExpenseRejectedBottomSheetDialogFragment.IExpenseRejectedListener {
    private lateinit var binding: ActivityTotalExpenseDetailsBinding
    private var isDataChange: Boolean = false
    private lateinit var viewModel: ExpenseViewModel
    private var expenseId: Int = -1
    private var staffRole: String = ""

    private var expenseTrackerModel: ExpenseTrackerDataItem? = null
    private lateinit var adapter: ExpensesListAdapter

    private var expenseList = ArrayList<ExpenseDataItem>()

    private var isPageLoading = false
    private var isApiLastPage = false
    private var currentPage = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTotalExpenseDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this)[ExpenseViewModel::class.java]

        initRecyclerView()
        initObservers()

        binding.mainContent.visibility = View.GONE

        if (intent.hasExtra(AppConstant.TOTAL_EXPENSE_DETAILS)) {
            expenseTrackerModel = intent.getParcelableExtra(AppConstant.TOTAL_EXPENSE_DETAILS)

            if (expenseTrackerModel != null) {
                expenseId = expenseTrackerModel?.id!!

                binding.tvToolbarTitle.text = expenseTrackerModel?.name
            }
        }

        if (intent.hasExtra(AppConstant.STAFF_ROLE)) {
            staffRole = intent.getStringExtra(AppConstant.STAFF_ROLE)!!

            if (staffRole == AppConstant.STAFF) {
                when (expenseTrackerModel?.status) {

                    AppConstant.ACTIVE -> {
                        binding.btnCancel.text = resources.getString(R.string.delete)
                        binding.btnAdd.text = resources.getString(R.string.submit)

                    }

                    REJECTED -> {
                        binding.btnCancel.text = resources.getString(R.string.delete)
                        binding.btnAdd.text = resources.getString(R.string.re_submit)

                    }

                    else -> {
                        binding.ivEdit.visibility = View.GONE
                        if (hasInternetConnection()) {
                            binding.btnLayout.visibility = View.GONE
                            binding.tvAddNewExpense.visibility = View.GONE
                        } else if (expenseTrackerModel?.isSyncedToServer == false) {
                            binding.btnLayout.visibility = View.VISIBLE
                            binding.btnAdd.visibility = View.GONE
                            binding.btnCancel.text = resources.getText(R.string.delete)
                        }
                    }
                }
            } else {
                binding.ivEdit.visibility = View.GONE
                binding.tvAddNewExpense.visibility = View.GONE
                binding.tvToolbarTitle.text = expenseTrackerModel?.createdByName

                when (expenseTrackerModel?.status) {

                    AppConstant.PENDING_EXPENSE -> {
                        binding.btnCancel.text = resources.getString(R.string.reject)
                        binding.btnAdd.text = resources.getString(R.string.approve)

                    }

                    AppConstant.APPROVED -> {
                        binding.btnCancel.text = resources.getString(R.string.back)
                        binding.btnAdd.text = resources.getString(R.string.paid)
                    }

                    else -> {
                        binding.ivEdit.visibility = View.GONE
                        binding.btnLayout.visibility = View.GONE
                        binding.tvAddNewExpense.visibility = View.GONE
                    }
                }
            }
        }


        loadExpenseList()


        binding.tvAddNewExpense.setOnClickListener {
            someActivityResultLauncher.launch(
                Intent(this, AddExpenseActivity::class.java).putExtra(
                    AppConstant.TOTAL_EXPENSE_DETAILS,
                    expenseTrackerModel
                )
            )
        }

        binding.ivClose.setOnClickListener { onBackPressedDispatcher.onBackPressed() }

        binding.btnAdd.setOnClickListener {
            if (expenseList.size > 0) {

                if (staffRole == AppConstant.STAFF) {
                    submitExpense()
                } else {
                    updateExpenseStatusByAdmin()
                }
            } else {
                Toast.makeText(this, "Please add your expenses list", Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnCancel.setOnClickListener {
            if (staffRole == AppConstant.STAFF) {
                DeleteDialog.showDeleteDialog(
                    this,
                    expenseTrackerModel,
                    0,
                    resources.getString(R.string.delete_reimbursement),
                    resources.getString(R.string.delete_reimbursement_message),
                    this
                )
            } else {
                when (expenseTrackerModel?.status) {

                    AppConstant.PENDING_EXPENSE -> {
                        showRejectDialog()
                    }

                    AppConstant.APPROVED -> {
                        onBackPressedDispatcher.onBackPressed()
                    }
                }
            }
        }

        binding.ivEdit.setOnClickListener {
            someActivityResultLauncher.launch(
                Intent(
                    this,
                    AddExpenseHeadActivity::class.java
                ).putExtra(AppConstant.EXPENSE_DETAILS, expenseTrackerModel)
            )
        }
    }

    private fun updateExpenseStatusByAdmin() {
        when (expenseTrackerModel?.status) {

            AppConstant.PENDING_EXPENSE -> {
                updateExpenseStatus(AppConstant.APPROVED, "")
            }

            AppConstant.APPROVED -> {
                updateExpenseStatus(AppConstant.PAID, "")
            }
        }
    }

    private fun updateExpenseStatus(status: String, reason: String) {
        viewModel.updateExpensesStatus(expenseId, status, reason)
    }

    private fun submitExpense() {
        val fragment = SubmitWithoutEndDateBottomSheetDialogFragment(expenseTrackerModel, this)
        fragment.show(supportFragmentManager, AppConstant.SUBMITTED)
    }

    private fun showRejectDialog() {
        Utils.hideKeyboard(this)

        val fragment = ExpenseRejectedBottomSheetDialogFragment(this)
        fragment.show(supportFragmentManager, AppConstant.ORDER_REJECTED)
    }


    private var someActivityResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result: ActivityResult ->
        if (result.resultCode == RESULT_OK && result.data != null) {
            currentPage = 1
            loadExpenseList()

            isDataChange = true
        }
    }

    private fun initObservers() {
        viewModel.totalExpenseDetailsLiveData.observe(this) {
            binding.progressBar.visibility = View.GONE
            if (it.error == false) {
                initData(it.data!!)
            }
        }

        viewModel.updateExpenseStatusLiveData.observe(this) {
            Toast.makeText(this, "${it.message}", Toast.LENGTH_SHORT).show()
            if (it.error == false) {
                finishWithResponse()
            }
        }

        viewModel.expenseLiveData.observe(this) {
            isPageLoading = false
            if (it.error == false) {
                it.data?.let { list ->

                    if (currentPage == 1) {
                        expenseList.clear()
                    }

                    expenseList.addAll(list)
                    adapter.notifyDataSetChanged()

                    if (list.size < 30) {
                        isApiLastPage = true
                    }
                }
            } else {
                Toast.makeText(this, "${it.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun initRecyclerView() {
        val linearLayoutManager = LinearLayoutManager(this)
        binding.rvListOfExpenses.layoutManager = linearLayoutManager

        adapter =
            ExpensesListAdapter(
                expenseList,
                this
            )

        binding.rvListOfExpenses.adapter = adapter

        binding.rvListOfExpenses.addOnScrollListener(object :
            PaginationScrollListener(linearLayoutManager) {
            override fun loadMoreItems() {
                isPageLoading = true
                currentPage += 1
                loadExpenseList()
            }

            override fun isLastPage(): Boolean {
                return isApiLastPage
            }

            override fun isLoading(): Boolean {
                return isPageLoading
            }
        })

    }

    private fun loadExpenseList() {
        binding.progressBar.visibility = View.VISIBLE
        viewModel.getTotalExpenseDetails(expenseId, hasInternetConnection())
        viewModel.getExpenseList(expenseId, hasInternetConnection())
    }

    @SuppressLint("SetTextI18n")
    private fun initData(model: ExpenseTrackerDataItem) {
        expenseTrackerModel = model
        binding.tvExpenseName.text = model.name

        if (!model.endDateTime.isNullOrEmpty()) {
            binding.tvExpenseDate.text =
                DateFormatHelper.getMonthDate(model.startDateTime) + " - " + DateFormatHelper.getMonthDate(
                    model.endDateTime
                )
        } else {
            binding.tvExpenseDate.text =
                DateFormatHelper.getMonthDate(model.startDateTime)
        }

        if (!model.description.isNullOrEmpty()) {
            binding.tvDescription.visibility = View.VISIBLE
            binding.tvDescription.text = model.description
        } else {
            binding.tvDescription.visibility = View.GONE
        }

        binding.tvExpenseCaptured.text =
            "${
                CalculatorHelper().convertCommaSeparatedAmount(
                    model.totalAmount ?: 0.0,
                    AppConstant.TWO_DECIMAL_POINTS
                )
            }"

        binding.mainContent.visibility = View.VISIBLE
    }

    override fun getExpensesDetails(model: ExpenseDataItem) {
        someActivityResultLauncher.launch(
            Intent(
                this,
                ExpenseDetailActivity::class.java
            ).putExtra(AppConstant.EXPENSE_DETAILS, model)
                .putExtra(AppConstant.EXPENSE_ID, expenseId)
                .putExtra(AppConstant.TOTAL_EXPENSE_DETAILS, expenseTrackerModel)
                .putExtra(AppConstant.STAFF_ROLE, staffRole)
                .putExtra(AppConstant.EXPENSE_STATUS, expenseTrackerModel?.status)
        )
    }


    override fun expenseRejected(reason: String) {
        updateExpenseStatus(REJECTED, reason)
    }

    override fun onBackPressed() {
        if (isDataChange) {
            finishWithResponse()
        } else {
            super.onBackPressed()
        }
    }

    private fun finishWithResponse() {
        val intent = Intent()
        setResult(RESULT_OK, intent)
        finish()
    }

    override fun expenseSubmit(endDate: String) {
        val model = ExpenseTrackerDataItem()
        if (expenseTrackerModel?.status == REJECTED) {
            model.isResubmitted = true
        }
        model.status = AppConstant.SUBMITTED
        model.endDateTime = endDate
        viewModel.updateTotalExpenses(expenseId, model)
    }

    override fun onDelete(model: Any, position: Any) {
        viewModel.deleteTotalExpenses(expenseId, hasInternetConnection())
    }
}