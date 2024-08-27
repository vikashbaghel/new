package com.app.rupyz.sales.expense

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.rupyz.R
import com.app.rupyz.databinding.ExpenseListItemBinding
import com.app.rupyz.generic.helper.DateFormatHelper
import com.app.rupyz.generic.utils.AppConstant
import com.app.rupyz.generic.utils.CalculatorHelper
import com.app.rupyz.model_kt.ExpenseDataItem

class ExpensesListAdapter(
    private var data: ArrayList<ExpenseDataItem>?,
    private var listener: IExpensesListener
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.expense_list_item, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as MyViewHolder).bindItem(data!![position], listener)
    }

    override fun getItemCount(): Int {
        return data!!.size
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding = ExpenseListItemBinding.bind(itemView)
        @SuppressLint("SetTextI18n")
        fun bindItem(model: ExpenseDataItem, listener: IExpensesListener) {

            binding.tvExpenseName.text = model.name


            binding.tvExpenseDate.text =
                DateFormatHelper.getMonthDate(model.expenseDateTime)


            if (!model.description.isNullOrEmpty()) {
                binding.tvDescription.visibility = View.VISIBLE
                binding.tvDescription.text = model.description
            } else {
                binding.tvDescription.visibility = View.GONE
            }

            binding.tvExpenseCaptured.text =
                "${CalculatorHelper().convertCommaSeparatedAmount(model.amount, AppConstant.TWO_DECIMAL_POINTS)}"

            itemView.setOnClickListener {
                listener.getExpensesDetails(
                    model
                )
            }

        }
    }

    interface IExpensesListener {
        fun getExpensesDetails(model: ExpenseDataItem)
    }
}