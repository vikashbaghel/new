package com.app.rupyz.sales.gallery

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.AppCompatButton
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.Group
import androidx.fragment.app.DialogFragment
import com.app.rupyz.R
import com.app.rupyz.databinding.GalleryFullScreenIamgeViewBinding
import com.app.rupyz.generic.helper.BlurUtils
import com.app.rupyz.generic.helper.DateFormatHelper
import com.app.rupyz.generic.utils.AppConstant
import com.app.rupyz.generic.utils.ImageUtils
import com.app.rupyz.model_kt.gallery.PictureData
import com.app.rupyz.sales.customer.CustomerDetailActivity
import com.app.rupyz.sales.customer.CustomerFeedbackDetailActivity
import com.app.rupyz.sales.lead.LeadDetailsActivity
import com.app.rupyz.sales.orders.OrderDetailActivity
import com.app.rupyz.sales.payment.PaymentDetailsActivity
import com.google.android.material.bottomsheet.BottomSheetBehavior

class FullScreenImageDialogFragment : DialogFragment() {
    private lateinit var binding: GalleryFullScreenIamgeViewBinding
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<View>

    companion object {
        private var pictureData: PictureData? = null
        fun newInstance(pictureData: PictureData): FullScreenImageDialogFragment {
            this.pictureData = pictureData
            return FullScreenImageDialogFragment()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = GalleryFullScreenIamgeViewBinding.inflate(inflater)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.ivClose.setOnClickListener {
            dismiss()
        }

        setUpBottomSheetListener()

        binding.imageView.setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        }

        ImageUtils.loadImage(pictureData?.image_url, binding.imageView)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setUpBottomSheetListener() {
        dialog?.let { dialog ->
            val bottomSheetView = dialog.findViewById<ConstraintLayout>(R.id.picture_gallery_detail)
            val tvHeading = dialog.findViewById<TextView>(R.id.tv_heading)
            val tvActivity = dialog.findViewById<TextView>(R.id.tv_activity)
            val tvDateAndTime = dialog.findViewById<TextView>(R.id.tv_date_and_time)
            val tvLocation = dialog.findViewById<TextView>(R.id.tv_location)
            val tvCustomerOrLead = dialog.findViewById<TextView>(R.id.tv_customer_or_lead)
            val buttonProceed = dialog.findViewById<AppCompatButton>(R.id.button_proceed)
            val groupLocation = dialog.findViewById<Group>(R.id.group_location)


            if (bottomSheetView != null) {
                bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetView)
                bottomSheetBehavior.isHideable = false
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED

                tvHeading?.setOnClickListener {
                    if (bottomSheetBehavior.state == BottomSheetBehavior.STATE_COLLAPSED) {
                        bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
                    } else {
                        bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
                    }
                }
                buttonProceed.setOnClickListener {
                    getFeedbackDetails(pictureData!!)
                }
            }


            pictureData?.apply {

                tvActivity.text = sub_module_type

                if (geo_address.isNullOrEmpty().not()) {
                    tvLocation.text = geo_address
                } else {
                    groupLocation.visibility = View.GONE
                }

                val dateTime = DateFormatHelper.getPictureDate(created_at)
                Log.e("time", "$created_at")
                tvDateAndTime.text = dateTime

                tvCustomerOrLead.text = sub_module_type
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_FRAME, R.style.FullScreenDialogTheme)
    }

    override fun onStart() {
        super.onStart()

        dialog?.window?.let { window ->
            val background = getActivityBackground()
            val blurredBackground = BlurUtils.blur(requireContext(), background)
            window.setBackgroundDrawable(BitmapDrawable(resources, blurredBackground))
        }
    }

    private fun getActivityBackground(): Bitmap {
        val rootView = requireActivity().findViewById<View>(android.R.id.content)
        rootView.isDrawingCacheEnabled = true
        val bitmap = Bitmap.createBitmap(rootView.drawingCache)
        rootView.isDrawingCacheEnabled = false
        return bitmap
    }

    private fun getFeedbackDetails(model: PictureData) {
        when (model.module_type) {
            AppConstant.ORDER -> {
                startActivity(
                    Intent(
                        requireContext(), OrderDetailActivity::class.java
                    ).putExtra(AppConstant.ORDER_ID, model.module_id)
                )
            }

            AppConstant.LEAD -> {
                startActivity(
                    Intent(
                        requireContext(), LeadDetailsActivity::class.java
                    ).putExtra(AppConstant.LEAD_ID, model.module_id)
                )
            }

            AppConstant.CUSTOMER -> {
                startActivity(
                    Intent(
                        requireContext(), CustomerDetailActivity::class.java
                    ).putExtra(AppConstant.CUSTOMER_ID, model.module_id)
                )
            }

            AppConstant.PAYMENT -> {
                startActivity(
                    Intent(
                        requireContext(), PaymentDetailsActivity::class.java
                    ).putExtra(AppConstant.PAYMENT_ID, model.module_id)
                )
            }

            AppConstant.ORDER_DISPATCH -> {
                startActivity(
                    Intent(
                        requireContext(), OrderDetailActivity::class.java
                    ).putExtra(AppConstant.ORDER_ID, model.sub_module_type)
                )
            }


            else -> {
                startActivity(
                    Intent(
                        requireContext(), CustomerFeedbackDetailActivity::class.java
                    ).putExtra(AppConstant.ACTIVITY_ID, model.module_id)
                )
            }
        }
    }

}