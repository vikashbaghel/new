package com.app.rupyz.sales.payment

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.app.rupyz.R
import com.app.rupyz.databinding.ActivityPaymentDetailsBinding
import com.app.rupyz.generic.base.BaseActivity
import com.app.rupyz.generic.helper.DateFormatHelper
import com.app.rupyz.generic.model.org_image.ImageViewModel
import com.app.rupyz.generic.model.product.PicMapModel
import com.app.rupyz.generic.utils.AppConstant
import com.app.rupyz.generic.utils.CalculatorHelper
import com.app.rupyz.generic.utils.FileUtils
import com.app.rupyz.model_kt.OrgImageListModel
import com.app.rupyz.model_kt.order.payment.RecordPaymentData
import com.app.rupyz.sales.home.SalesMainActivity
import com.app.rupyz.sales.orderdispatch.LrPhotoListAdapter
import com.app.rupyz.ui.organization.profile.OrgPhotosViewActivity
import com.app.rupyz.ui.organization.profile.adapter.ProductImageViewPagerAdapter

class PaymentDetailsActivity : BaseActivity(),
        ProductImageViewPagerAdapter.ProductImageClickListener {
    private lateinit var binding: ActivityPaymentDetailsBinding
    private lateinit var model: RecordPaymentData
    private lateinit var recordPaymentViewModel: RecordPaymentViewModel

    private val pics: ArrayList<PicMapModel> = ArrayList()
    private lateinit var addPhotoListAdapter: LrPhotoListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPaymentDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        recordPaymentViewModel = ViewModelProvider(this)[RecordPaymentViewModel::class.java]

        initRecyclerView()
        initObservers()

        binding.mainContent.visibility = View.GONE
        binding.progressBar.visibility = View.VISIBLE

        if (intent.hasExtra(AppConstant.PAYMENT_INFO)) {
            model = intent.getParcelableExtra(AppConstant.PAYMENT_INFO)!!

            recordPaymentViewModel.getRecordPaymentDetails(model.id!!, hasInternetConnection())

        } else if (intent.hasExtra(AppConstant.PAYMENT_ID)) {
            recordPaymentViewModel.getRecordPaymentDetails(
                    intent.getIntExtra(
                            AppConstant.PAYMENT_ID,
                            0
                    ),
                    hasInternetConnection()
            )
        }

        binding.ivBack.setOnClickListener {
            onBackPressed()
        }
    }

    private fun initRecyclerView() {
        binding.rvPhotos.layoutManager = GridLayoutManager(this, 6)
        addPhotoListAdapter = LrPhotoListAdapter(pics, this)
        binding.rvPhotos.adapter = addPhotoListAdapter

    }


    private fun initObservers() {
        recordPaymentViewModel.getPaymentRecordDetailLiveData.observe(this) {
            binding.progressBar.visibility = View.GONE
            if (it.error == false) {
                it.data?.let { model ->
                    renderUi(model)
                }
            } else {
                Toast.makeText(this, "${it.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
    override fun onPdfClick(position: Int,url:String) {
        super.onPdfClick(position,url)
        if (url!=null)
        {
            FileUtils.openPdf(url, this)
        }


    }

    private fun renderUi(model: RecordPaymentData) {
        if (model.customer?.name != null) {
            binding.tvCustomerName.text = model.customer?.name
        }

        if (model.amount != null) {
            binding.tvPaymentAmount.text =
                    CalculatorHelper().convertCommaSeparatedAmount(model.amount, AppConstant.TWO_DECIMAL_POINTS)
        }

        if (model.paymentMode != null) {
            binding.tvPaymentMode.text = model.paymentMode
        }

        if (model.createdAt.isNullOrEmpty().not()) {
            binding.tvDate.text = DateFormatHelper.dateFormatEMI(model.createdAt)
        }

        if (model.paymentNumber.isNullOrEmpty().not()) {
            binding.tvTransactionId.text = "${model.paymentNumber}"
        }

        if (model.transactionTimeStamp.isNullOrEmpty().not()) {
            binding.tvTransactionDate.text = DateFormatHelper.dateFormatEMI(model.transactionTimeStamp)
            binding.groupTransactionDate.visibility = View.VISIBLE
        } else {
            binding.groupTransactionDate.visibility = View.GONE
        }

        if (model.status != null) {
            binding.tvPaymentStatus.text = model.status

            when (model.status) {
                AppConstant.STATUS_DISHONOUR -> {
                    binding.tvPaymentStatus.text = AppConstant.REJECTED
                    binding.tvPaymentStatus.setTextColor(resources.getColor(R.color.payment_rejected_text_color))
                }

                AppConstant.STATUS_APPROVED -> {
                    binding.tvPaymentStatus.setTextColor(resources.getColor(R.color.payment_approved_text_color))
                }

                else -> {
                    binding.tvPaymentStatus.setTextColor(resources.getColor(R.color.pending_text_color))
                }
            }
        }

        if (model.transactionRefNo.isNullOrEmpty().not()) {
            binding.tvTransactionRef.text = model.transactionRefNo
            binding.groupTransactionRef.visibility = View.VISIBLE
        } else {
            binding.groupTransactionRef.visibility = View.GONE
        }

        if (model.comment.isNullOrEmpty().not()) {
            binding.groupComments.visibility = View.VISIBLE
            binding.tvComment.text = model.comment
        } else if (model.rejectReason.isNullOrEmpty().not()) {
            binding.groupComments.visibility = View.VISIBLE
            binding.tvNotesHd.text = resources.getString(R.string.reject_reason)
            binding.tvComment.text = model.rejectReason
        } else {
            binding.groupComments.visibility = View.GONE
        }

        if (model.paymentImagesInfo.isNullOrEmpty().not()) {
            binding.groupImages.visibility = View.VISIBLE
            pics.clear()
            pics.addAll(model.paymentImagesInfo!!)
            addPhotoListAdapter.notifyDataSetChanged()
        } else {
            binding.groupImages.visibility = View.GONE
        }

        binding.mainContent.visibility = View.VISIBLE
    }

    override fun onImageClick(position: Int) {
        if (pics.size > 0) {
            val imageListModel = OrgImageListModel()

            val imageViewModelArrayList = ArrayList<ImageViewModel>()

            for (pic in pics) {
                val model = ImageViewModel(0, 0, pic.url)
                imageViewModelArrayList.add(model)
            }

            imageListModel.data = imageViewModelArrayList
            startActivity(
                    Intent(this, OrgPhotosViewActivity::class.java)
                            .putExtra(AppConstant.PRODUCT_INFO, imageListModel)
                            .putExtra(AppConstant.IMAGE_POSITION, position)
            )
        } else {
            Toast.makeText(this, "Something went wrong!!", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onBackPressed() {
        if (intent.hasExtra(AppConstant.NOTIFICATION)) {
            startActivity(
                    Intent(this, SalesMainActivity::class.java)
            )
        }
        finish()
    }
}