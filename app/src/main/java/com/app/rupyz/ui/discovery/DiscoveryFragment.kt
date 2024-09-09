package com.app.rupyz.ui.discovery

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.rupyz.R
import com.app.rupyz.databinding.FragmentDiscoveryBinding
import com.app.rupyz.generic.utils.AppConstant
import com.app.rupyz.generic.utils.Utility
import com.app.rupyz.model_kt.OrgItem
import com.app.rupyz.model_kt.ProductItem
import com.app.rupyz.model_kt.ProductSource
import com.app.rupyz.ui.organization.profile.OrgProfileActivity

class DiscoveryFragment : Fragment(), FilterSelectedListener,
    DiscoverySelectedListener, RecentSearchClickListener {
    private lateinit var binding: FragmentDiscoveryBinding
    private lateinit var discoveryBusinessListAdapter: DiscoveryBusinessListAdapter
    private lateinit var discoveryProductListAdapter: DiscoveryProductListAdapter
    private lateinit var discoverySearchSuggestionListAdapter: DiscoverySearchSuggestionListAdapter
    private lateinit var discoveryViewModel: DiscoveryViewModel

    private var orgList = ArrayList<OrgItem>()
    private var productList = ArrayList<ProductItem>()
    private var suggestionList = ArrayList<String>()
    private var badge: Int = 0
    private var location: String = ""
    private var type: String = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentDiscoveryBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        discoveryViewModel = ViewModelProvider(this)[DiscoveryViewModel::class.java]

        initRecyclerView()

        initObservers()

        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                binding.clSuggestion.visibility = View.GONE
                if (s.toString().length > 1) {
                    getResult()
                }
            }
        })

        binding.etSearch.setOnFocusChangeListener { _, focusChange ->
            if (focusChange) {
                if (binding.etSearch.text.toString().isEmpty()) {
                    binding.clSuggestion.visibility = View.VISIBLE
                }
            }
        }

        binding.etSearch.setOnClickListener {
            if (binding.etSearch.text.toString().isEmpty()) {
                binding.clSuggestion.visibility = View.VISIBLE
            }
        }

        binding.etSearch.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                getResultWithHardSearch()
                hideSoftKeys()
                return@setOnEditorActionListener true
            }
            false
        }

        binding.ivSearch.setOnClickListener { v ->
            hideSoftKeys()
            getResultWithHardSearch()
        }

        getResult()
        discoveryViewModel.getDiscoverySearchHistory(1, AppConstant.DISCOVERY)

        binding.tvTypeOrg.setOnClickListener {
            if (type == AppConstant.ORGANIZATION) {
                type = ""
                binding.tvTypeOrg.setBackgroundResource(R.drawable.filter_non_selected_background)
                binding.tvTypeOrg.setTextColor(resources.getColor(R.color.tab_un_selected_color))
                getResult()
            } else {
                type = AppConstant.ORGANIZATION
                getResult()

                binding.tvTypeOrg.setBackgroundResource(R.drawable.details_button_style)
                binding.tvProductType.setBackgroundResource(R.drawable.filter_non_selected_background)
                binding.tvTypeOrg.setTextColor(resources.getColor(R.color.white))
                binding.tvProductType.setTextColor(resources.getColor(R.color.tab_un_selected_color))
            }
        }

        binding.tvProductType.setOnClickListener {
            if (type == AppConstant.PRODUCT) {
                type = ""
                binding.tvProductType.setBackgroundResource(R.drawable.filter_non_selected_background)
                binding.tvProductType.setTextColor(resources.getColor(R.color.tab_un_selected_color))
                getResult()
            } else {
                type = AppConstant.PRODUCT

                binding.tvProductType.setBackgroundResource(R.drawable.details_button_style)
                binding.tvTypeOrg.setBackgroundResource(R.drawable.filter_non_selected_background)

                binding.tvProductType.setTextColor(resources.getColor(R.color.white))
                binding.tvTypeOrg.setTextColor(resources.getColor(R.color.tab_un_selected_color))

                getResult()
            }
        }

        binding.tvLocationFilter.setOnClickListener {
            DiscoveryFilterBottomSheetDialogFragment(
                this,
                AppConstant.FILTER_LOCATION,
                location
            ).show(
                requireActivity().supportFragmentManager,
                AppConstant.FILTER_LOCATION
            )
        }
        binding.tvBadgeFiler.setOnClickListener {
            DiscoveryFilterBottomSheetDialogFragment(
                this,
                AppConstant.FILTER_BADGE, badge.toString()
            ).show(
                requireActivity().supportFragmentManager,
                AppConstant.FILTER_BADGE
            )
        }

        binding.tvLocationFilter.setCompoundDrawablesWithIntrinsicBounds(
            0,
            0,
            R.drawable.ic_arrow_drop_down_black,
            0
        )
        binding.tvBadgeFiler.setCompoundDrawablesWithIntrinsicBounds(
            0,
            0,
            R.drawable.ic_arrow_drop_down_black,
            0
        )


        binding.tvSeeAllBusiness.setOnClickListener {
            startActivity(
                Intent(requireContext(), ShowAllDiscoveryListActivity::class.java)
                    .putExtra(AppConstant.CONNECTION_TYPE, AppConstant.ORGANIZATION)
                    .putExtra(AppConstant.LOCATION, location)
                    .putExtra(AppConstant.BADGE, badge)
                    .putExtra(AppConstant.SEARCH_STRING, binding.etSearch.text.toString())
            )
        }

        binding.tvSeeAllProducts.setOnClickListener {
            startActivity(
                Intent(requireContext(), ShowAllDiscoveryListActivity::class.java)
                    .putExtra(AppConstant.CONNECTION_TYPE, AppConstant.PRODUCT)
                    .putExtra(AppConstant.LOCATION, location)
                    .putExtra(AppConstant.BADGE, badge)
                    .putExtra(AppConstant.SEARCH_STRING, binding.etSearch.text.toString())
            )
        }
    }


    private fun hideSoftKeys() {
        val imm =
            requireActivity().getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0)
    }


    private fun getResult() {
        binding.progressBar.visibility = View.VISIBLE
        discoveryViewModel.getDiscoveryList(
            type,
            binding.etSearch.text.toString(),
            location,
            badge
        )
    }


    private fun getResultWithHardSearch() {
        binding.progressBar.visibility = View.VISIBLE
        discoveryViewModel.getDiscoveryListWithHardSearch(
            type,
            binding.etSearch.text.toString(),
            location,
            badge
        )
    }

    private fun initObservers() {
        discoveryViewModel.discoveryListLiveData.observe(requireActivity()) { data ->
            data?.data.let {
                orgList.clear()
                productList.clear()
                if (isAdded) {
                    binding.rvBusinessItem.visibility = View.VISIBLE
                    binding.progressBar.visibility = View.GONE

                    if (it?.org!!.isNotEmpty()) {
                        orgList.addAll(it.org)
                        discoveryBusinessListAdapter.notifyDataSetChanged()

                        binding.tvBusinessCount.text = "Business (" + it.org.size + " results)"
                        binding.groupBusiness.visibility = View.VISIBLE
                    } else {
                        binding.groupBusiness.visibility = View.GONE
                    }

                    if (it.product!!.isNotEmpty()) {
                        productList.addAll(it.product)
                        discoveryProductListAdapter.notifyDataSetChanged()

                        binding.tvProductCount.text = "Product (" + it.product.size + " results)"
                        binding.groupProduct.visibility = View.VISIBLE

                    } else {
                        binding.groupProduct.visibility = View.GONE
                    }
                }
            }
        }
        discoveryViewModel.recentSearchLiveData.observe(requireActivity()) {
            if (!it.data.isNullOrEmpty()) {
                suggestionList.addAll(it.data)
                discoverySearchSuggestionListAdapter.notifyDataSetChanged()
            }
        }
    }

    private fun initRecyclerView() {
        binding.rvBusinessItem.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        discoveryBusinessListAdapter = DiscoveryBusinessListAdapter(orgList, this)
        binding.rvBusinessItem.adapter = discoveryBusinessListAdapter

        binding.rvProductItem.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        discoveryProductListAdapter = DiscoveryProductListAdapter(productList, this)
        binding.rvProductItem.adapter = discoveryProductListAdapter

        binding.rvSuggestion.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        discoverySearchSuggestionListAdapter =
            DiscoverySearchSuggestionListAdapter(suggestionList, this)
        binding.rvSuggestion.adapter = discoverySearchSuggestionListAdapter


    }

    override fun onTypeChange(filter: String?) {
        type = filter!!
        if (binding.etSearch.text.toString().length > 1) {
            getResult()
        }
    }

    override fun onLocationChange(filter: String?) {
        if (filter != "") {
            location = filter!!
            binding.tvLocationFilter.setBackgroundResource(R.drawable.details_button_style)
            binding.tvLocationFilter.setTextColor(requireActivity().resources.getColor(R.color.white))
            getResult()

            binding.tvLocationFilter.setCompoundDrawablesWithIntrinsicBounds(
                0,
                0,
                R.drawable.ic_arrow_drop_down,
                0
            )
        } else {
            binding.tvLocationFilter.setBackgroundResource(R.drawable.filter_non_selected_background)
            binding.tvLocationFilter.setTextColor(resources.getColor(R.color.tab_un_selected_color))
            binding.tvLocationFilter.setCompoundDrawablesWithIntrinsicBounds(
                0,
                0,
                R.drawable.ic_arrow_drop_down_black,
                0
            )
            location = ""
            getResult()
        }
    }

    override fun onBadgeChange(filter: String?) {
        if (filter != "") {
            badge = filter?.toInt()!!
            binding.tvBadgeFiler.setBackgroundResource(R.drawable.details_button_style)
            binding.tvBadgeFiler.setTextColor(requireActivity().resources.getColor(R.color.white))
            getResult()

            binding.tvBadgeFiler.setCompoundDrawablesWithIntrinsicBounds(
                0,
                0,
                R.drawable.ic_arrow_drop_down,
                0
            )
        } else {
            binding.tvBadgeFiler.setBackgroundResource(R.drawable.filter_non_selected_background)
            binding.tvBadgeFiler.setTextColor(resources.getColor(R.color.tab_un_selected_color))
            binding.tvBadgeFiler.setCompoundDrawablesWithIntrinsicBounds(
                0,
                0,
                R.drawable.ic_arrow_drop_down_black,
                0
            )
            badge = 0
            getResult()
        }
    }

    override fun onProductClick(slug: String?, product: ProductSource?) {
        startActivity(
            Intent(
                requireContext(),
                OrgProfileActivity::class.java
            )
                .putExtra(AppConstant.PROFILE_SLUG, slug)
                .putExtra(AppConstant.PRODUCT_INFO, product)
        )
    }

    override fun onOrgClick(slug: String?) {
        startActivity(
            Intent(
                requireContext(),
                OrgProfileActivity::class.java
            ).putExtra(AppConstant.PROFILE_SLUG, slug)
        )
    }

    override fun onProductShare(product: ProductSource?) {
        Utility.shareOthersProductWithAll(
            requireContext(), product?.name,
            AppConstant.getShareProductUrlForDiscovery(product?.orgSlug, product?.nanoId)
        )
    }

    override fun onSuggestionClick(string: String) {
        binding.clSuggestion.visibility = View.GONE
        binding.etSearch.setText(string, TextView.BufferType.EDITABLE)
        binding.etSearch.setSelection(string.length)
        getResult()
    }

}