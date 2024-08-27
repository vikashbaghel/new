package com.app.rupyz.sales.gallery

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.app.rupyz.databinding.PictureGalleryListFragmentBinding
import com.app.rupyz.generic.base.BaseFragment
import com.app.rupyz.generic.helper.PaginationScrollListener
import com.app.rupyz.generic.utils.AppConstant
import com.app.rupyz.model_kt.gallery.PictureData
import com.app.rupyz.sales.filter.DateRangeSelectBottomSheetDialogFragment
import com.app.rupyz.sales.filter.GallerySortByBottomSheetDialogFragment
import com.app.rupyz.sales.gallery.adapter.PictureGalleryPicsAdapter
import java.util.Locale

@SuppressLint("NotifyDataSetChanged")
class AllPictureListFragment : BaseFragment(),
    GallerySortByBottomSheetDialogFragment.ISortingGalleryListener,
    DateRangeSelectBottomSheetDialogFragment.IDateRangeFilterListener,
    FilterActivity.IPictureFilterListener,
    FilterActivity.OnItemStateListener,
    PictureGalleryPicsAdapter.GalleryPictureInfoListener, View.OnClickListener {
    private lateinit var binding: PictureGalleryListFragmentBinding
    private lateinit var adapter: PictureGalleryPicsAdapter
    private var sortingOrder: String = AppConstant.SORTING_LEVEL_DESCENDING
    private var dateRange: String = AppConstant.THIS_MONTH
    private var startDateRange: String = ""
    private var endDateRange: String = ""
    private var isApiLastPage = false
    private var isPageLoading = false
    private var currentPage = 1
    private var filterCount = 0
    private var rangeCount = 2
    private var customerTypeFilterApply = false
    private var stateTypeFilterApply = false
    private var staffTypeFilterApply = false
    private var moduleTypeFilterApply = false
    private var subModuleTypeFilterApply = false

    private var stateTypeList = ArrayList<String>()
    private var moduleTypeList = ArrayList<String?>()
    private var subModuleTypeList = ArrayList<String?>()
    private var staffType = ArrayList<Int>()
    private var customerTypeList = ArrayList<Int>()
    private val pictureGalleryViewModel: PictureGalleryViewModel by viewModels()
    private var picDataList = ArrayList<PictureData?>()
    private var checkboxStateMap = HashMap<String, Boolean>()
    private var checkboxCustomerMap = HashMap<Int, Boolean>()
    private var checkboxStaffMap = HashMap<Int, Boolean>()
    private var checkboxActivityMap = HashMap<Int, Boolean>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = PictureGalleryListFragmentBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRecyclerView()
        pictureDataList()
        binding.tvSortBy.setOnClickListener(this)
        binding.tvFilterRange.setOnClickListener(this)
        binding.tvFilter.setOnClickListener(this)
    }


    private fun pictureDataList() {

        if (currentPage > 1) {
            binding.progressBar.visibility = View.VISIBLE
        } else {
            binding.progressBarMain.visibility = View.VISIBLE
        }
        isApiLastPage = false
        pictureGalleryViewModel.getPictureList(
            staffType,
            customerTypeList,
            moduleTypeList,
            0,
            subModuleTypeList,
            dateRange,
            startDateRange,
            endDateRange,
            stateTypeList,
            sortingOrder,
            currentPage,
            hasInternetConnection()
        )
    }

    // @SuppressLint("NotifyDataSetChanged")

    private fun initObservers() {

        // picture list observer data
        pictureGalleryViewModel.galleryLiveData.observe(requireActivity()) { data ->
            binding.progressBar.visibility = View.GONE
            binding.progressBarMain.visibility = View.GONE
            if (data.error == false) {
                if (data.data.isNullOrEmpty().not()) {
                    binding.clEmptyData.visibility = View.GONE
                    data.data?.let {
                        isPageLoading = false
                        if (currentPage == 1) {
                            picDataList.clear()
                        }

                        picDataList.addAll(it)
                        adapter.notifyDataSetChanged()
                        if (it.size < 30) {
                            isApiLastPage = true
                        }
                    }
                } else {
                    isApiLastPage = true
                    if (currentPage == 1) {
                        picDataList.clear()
                        adapter.notifyDataSetChanged()
                        binding.clEmptyData.visibility = View.VISIBLE
                    }
                }
            } else {
                showToast(data.message)
            }
        }


    }


    private fun initRecyclerView() {
        val linearLayoutManager = GridLayoutManager(requireContext(), 3)
        binding.rvImages.layoutManager = linearLayoutManager
        binding.clEmptyData.visibility = View.GONE
        adapter = PictureGalleryPicsAdapter(picDataList, this)
        binding.rvImages.adapter = adapter
        binding.rvImages.addOnScrollListener(object :
            PaginationScrollListener(linearLayoutManager) {
            override fun loadMoreItems() {
                isPageLoading = true
                currentPage += 1
                pictureDataList()
            }

            override fun isLastPage(): Boolean {
                return isApiLastPage
            }

            override fun isLoading(): Boolean {
                return isPageLoading
            }
        })

        initObservers()
    }


    override fun changeDateRangeFilter(range: String, flag: Int) {
        picDataList.clear()
        currentPage = 1
        dateRange = range
        rangeCount = flag
        binding.tvFilterRange.text = buildString {
            val formattedString =
                dateRange.substring(0, 1).uppercase(Locale.ROOT) + dateRange.substring(1)
                    .lowercase(Locale.ROOT)
            if (flag == 1 || flag == 2) {
                append(AppConstant.RANGE)
                append(AppConstant.THIS)
                append(formattedString)
            } else {
                append(AppConstant.RANGE)
                append(formattedString)
            }
        }
        startDateRange = ""
        endDateRange = ""
        pictureDataList()
        adapter.notifyDataSetChanged()

    }

    override fun dateRangeFilterWithCustomDate(
        startDate: String,
        endDate: String,
        dateRange: String
    ) {
        binding.tvFilterRange.text = buildString {
            append(AppConstant.RANGE)
            append(removeString(startDate))
            append(AppConstant.RANGE_DATE)
            append(removeString(endDate))
        }
        this.dateRange = dateRange
        rangeCount = 3
        startDateRange = startDate
        endDateRange = endDate
        picDataList.clear()
        adapter.notifyDataSetChanged()
        currentPage = 1
        pictureDataList()
    }



    override fun picInfo(pictureData: PictureData) {
        val dialog = FullScreenImageDialogFragment.newInstance(pictureData)
        dialog.show(childFragmentManager, FullScreenImageDialogFragment::class.java.name)
    }

    override fun onClick(v: View?) {

        when (v) {
            binding.tvFilter -> {
                val intent =
                    Intent(requireActivity(), FilterActivity.newInstance(this, this)::class.java)
                intent.putExtra(AppConstant.STATE_MAP, checkboxStateMap)
                intent.putExtra(AppConstant.CUSTOMER_MAP, checkboxCustomerMap)
                intent.putExtra(AppConstant.STAFF_MAP, checkboxStaffMap)
                intent.putExtra(AppConstant.ACTIVITY_MAP, checkboxActivityMap)
                intent.putExtra(AppConstant.STAFF, staffType)
                intent.putExtra(AppConstant.STATE, stateTypeList)
                intent.putExtra(AppConstant.CUSTOMER, customerTypeList)
                intent.putExtra(AppConstant.MODULE_TYPE, moduleTypeList)
                intent.putExtra(AppConstant.SUB_MODULE_TYPE, subModuleTypeList)
                startActivity(intent)
            }


            binding.tvSortBy -> {
                val fragment = GallerySortByBottomSheetDialogFragment.newInstance(
                    this,
                    sortingOrder
                )
                fragment.show(
                    childFragmentManager,
                    GallerySortByBottomSheetDialogFragment::class.java.name
                )

            }

            binding.tvFilterRange -> {

                val fragment = DateRangeSelectBottomSheetDialogFragment.newInstance(
                    this, rangeCount
                )
                fragment.show(
                    childFragmentManager,
                    DateRangeSelectBottomSheetDialogFragment::class.java.name
                )

            }
        }
    }



    private fun removeString(originalString: String): String {
        val startIndex = 0
        val endIndex = 5
        val modifiedString = originalString.removeRange(startIndex, endIndex)
        return modifiedString
    }

    override fun applyFilter(
        customerType: ArrayList<Int>, staffType: ArrayList<Int>,
        stateType: ArrayList<String>, moduleType: ArrayList<String?>,
        subModuleType: ArrayList<String?>
    ) {


        this.customerTypeList = customerType
        this.staffType = staffType
        this.stateTypeList = stateType
        this.moduleTypeList = moduleType
        this.subModuleTypeList = subModuleType
        picDataList.clear()


        if (customerTypeList.isNotEmpty() && customerTypeFilterApply.not()) {
            ++filterCount
            customerTypeFilterApply = true
        }
        if (stateTypeList.isNotEmpty() && stateTypeFilterApply.not()) {
            ++filterCount
            stateTypeFilterApply = true
        }
        if (staffType.isNotEmpty() && staffTypeFilterApply.not()) {
            ++filterCount
            staffTypeFilterApply = true

        }
        if (moduleTypeList.isNotEmpty() && moduleTypeFilterApply.not()) {
            ++filterCount
            moduleTypeFilterApply = true

        }
        if (subModuleTypeList.isNotEmpty() && subModuleTypeFilterApply.not()) {
            ++filterCount
            subModuleTypeFilterApply = true

        }

        if (customerTypeList.isEmpty() && customerTypeFilterApply) {
            --filterCount
            customerTypeFilterApply = false
        }
        if (stateTypeList.isEmpty() && stateTypeFilterApply) {
            --filterCount
            stateTypeFilterApply = false
        }
        if (staffType.isEmpty() && staffTypeFilterApply) {
            --filterCount
            staffTypeFilterApply = false

        }
        if (moduleTypeList.isEmpty() && moduleTypeFilterApply) {
            --filterCount
            moduleTypeFilterApply = false

        }
        if (subModuleTypeList.isEmpty() && subModuleTypeFilterApply) {
            --filterCount
            subModuleTypeFilterApply = false

        }
        binding.tvFilterCount.text = "$filterCount"
        binding.tvFilterCount.visibility = View.VISIBLE

        if (filterCount == 0) {
            binding.tvFilterCount.visibility = View.GONE
            customerTypeList.clear()
            staffType.clear()
            stateTypeList.clear()
            moduleTypeList.clear()
            subModuleTypeList.clear()
        }
        currentPage = 1
        adapter.notifyDataSetChanged()
        pictureDataList()
    }

    override fun applySorting(dateOrder: String) {
        sortingOrder = dateOrder
        picDataList.clear()
        currentPage = 1
        adapter.notifyDataSetChanged()
        pictureDataList()
    }

    override fun onItemStateHolder(
        checkboxStateMap: HashMap<String, Boolean>,
        checkboxCustomerMap: HashMap<Int, Boolean>,
        checkboxStaffMap: HashMap<Int, Boolean>,
        checkboxActivityMap: HashMap<Int, Boolean>
    ) {
        this.checkboxStateMap = checkboxStateMap
        this.checkboxCustomerMap = checkboxCustomerMap
        this.checkboxStaffMap = checkboxStaffMap
        this.checkboxActivityMap = checkboxActivityMap
    }


}