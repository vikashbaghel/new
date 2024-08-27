package com.app.rupyz.sales.customer.adapters

import android.annotation.SuppressLint
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.rupyz.R
import com.app.rupyz.databinding.ItemViewStepsBinding
import com.app.rupyz.model_kt.Sections

class AddCustomerStepsAdapter(
    private var displayWidth: Int,
    private val onItemClickListener: (Int, String) -> Unit
) : RecyclerView.Adapter<AddCustomerStepsAdapter.AddCustomerStepsViewHolder>() {

    private var steps: ArrayList<Sections?> = ArrayList()
    private var currentStep = 1
    private var completedStep = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddCustomerStepsViewHolder {
//		if (displayWidth == -1){
//			displayWidth = Resources.getSystem().displayMetrics.widthPixels
//		}
        val binding =
            ItemViewStepsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
//		binding.parent.layoutParams.width = (( displayWidth / steps.size )  - 30 )
        return AddCustomerStepsViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return steps.size
    }

    @Suppress("t")
    @SuppressLint("UseCompatLoadingForDrawables")
    override fun onBindViewHolder(holder: AddCustomerStepsViewHolder, position: Int) {
        holder.binding.apply {

            "${(position + 1)}".also { tvStepCount.text = it }

            tvStepTittle.text = steps[position]?.name?.replace(" ", "\n")

            if (currentStep == (position + 1)) {
                tvStepCount.resources.getDrawable(R.drawable.ic_step_selected_background, null)
                    .also { tvStepCount.background = it }
                tvStepCount.setTextColor(Color.WHITE)
            } else {
                tvStepCount.resources.getDrawable(R.drawable.ic_step_unselected_background, null)
                    .also { tvStepCount.background = it }
                tvStepCount.setTextColor(Color.BLACK)
            }
            if ((position + 1) <= completedStep) {
                tvStepCount.text = ""
                tvStepCount.resources.getDrawable(R.drawable.ic_step_completed_background, null)
                    .also { tvStepCount.background = it }
            }

            when (position) {
                0 -> {
                    if (completedStep >= 1) {
                        ivEndLine.background =
                            (ivStartLine.resources.getDrawable(R.drawable.ic_line, null))
                    } else {
                        ivEndLine.background =
                            (ivStartLine.resources.getDrawable(R.drawable.ic_doted_line, null))
                    }
                    ivStartLine.setBackgroundColor(Color.TRANSPARENT)
                }

                steps.size - 1 -> {
                    if (completedStep == (steps.size - 1)) {
                        ivStartLine.background =
                            (ivStartLine.resources.getDrawable(R.drawable.ic_line, null))
                    } else {
                        ivStartLine.background =
                            (ivStartLine.resources.getDrawable(R.drawable.ic_doted_line, null))
                    }
                    ivEndLine.setBackgroundColor(Color.TRANSPARENT)
                }

                else -> {
                    if ((position + 1) <= completedStep) {
                        ivStartLine.background =
                            (ivStartLine.resources.getDrawable(R.drawable.ic_line, null))
                        ivEndLine.background =
                            (ivStartLine.resources.getDrawable(R.drawable.ic_line, null))
                    } else {
                        if (completedStep != 0 && ((position + 1) == (completedStep + 1))) {
                            ivStartLine.background =
                                (ivStartLine.resources.getDrawable(R.drawable.ic_line, null))
                            ivEndLine.background =
                                (ivStartLine.resources.getDrawable(R.drawable.ic_doted_line, null))
                        } else {
                            ivStartLine.background =
                                (ivStartLine.resources.getDrawable(R.drawable.ic_doted_line, null))
                            ivEndLine.background =
                                (ivStartLine.resources.getDrawable(R.drawable.ic_doted_line, null))
                        }
                    }
                }
            }

            tvStepCount.setOnClickListener {
                if (position < steps.size) {
                    onItemClickListener.invoke((position + 1), tvStepTittle.text.toString())
                }
            }

        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setCurrentStep(step: Int) {
        if (step <= (this.steps.size) && step >= 1) {
            this.currentStep = step
            this.completedStep = (step - 1)
            notifyDataSetChanged()
        }
    }

    fun getCurrentStep(): Int {
        return currentStep
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setSteps(steps: List<Sections?>) {
        this.steps.addAll(steps)
        notifyDataSetChanged()
    }

    inner class AddCustomerStepsViewHolder(val binding: ItemViewStepsBinding) :
        RecyclerView.ViewHolder(binding.root) {}


}