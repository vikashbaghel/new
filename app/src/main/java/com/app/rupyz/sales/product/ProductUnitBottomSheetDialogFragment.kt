package com.app.rupyz.sales.product

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.app.rupyz.R
import com.app.rupyz.databinding.BottomSheetProductUnitBinding
import com.app.rupyz.model_kt.ProductUnitModel
import com.app.rupyz.model_kt.packagingunit.PackagingUnitData
import com.app.rupyz.model_kt.packagingunit.PackagingUnitResponseModel
import com.app.rupyz.ui.organization.profile.activity.addproduct.ProductUnitNewAdapter
import com.app.rupyz.ui.organization.profile.activity.addproduct.UnitSelectedListener
import com.google.android.material.bottomsheet.BottomSheetDialogFragment


class ProductUnitBottomSheetDialogFragment(
    private val unitSelectedListener: UnitSelectedListener,
    private var unit: String,
) : BottomSheetDialogFragment() {
    private lateinit var binding: BottomSheetProductUnitBinding
    private lateinit var unitRvNewAdapter: ProductUnitNewAdapter
    private var unitSelected: String? = null
    private var addProductViewModel: ProductViewModel? = null
    private var unitData: PackagingUnitResponseModel? = null
    private val newUnitList: ArrayList<ProductUnitModel> = ArrayList()
    private val preDefineUnitList: ArrayList<String> = ArrayList()

    private lateinit var adapter: ArrayAdapter<String>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = BottomSheetProductUnitBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_FRAME, R.style.CustomBottomSheetDialogTheme)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        addProductViewModel = ViewModelProvider(this)[ProductViewModel::class.java]

        val list = resources.getStringArray(R.array.product_unit_list)
        preDefineUnitList.addAll(list)

        initObservers()
        initRecyclerView()
        initArrayAdapter()

        addProductViewModel!!.getPackagingUnit()

        binding.tvContinueResult.setOnClickListener {
            if (binding.etUnit.text.isNotEmpty()) {
                val packagingUnitData = PackagingUnitData()
                val index =
                    newUnitList.indexOfLast { it.name?.lowercase() == binding.etUnit.text.toString() }
                if (index != -1) {
                    packagingUnitData.name = newUnitList[index].name
                } else {
                    packagingUnitData.name = binding.etUnit.text.toString()
                }
                unitSelected = binding.etUnit.text.toString().capitalize()
                unitSelected?.let {
                    unitSelectedListener.onUnitSelected(unitSelected!!.capitalize(), unit)
                    dismiss()
                }
                addProductViewModel!!.addPackagingUnit(packagingUnitData)

                binding.etUnit.text = null
                binding.etUnit.hint = "Enter Packaging Unit"
                dismiss()
            } else {
                Toast.makeText(context, "Please Enter Some Unit", Toast.LENGTH_LONG).show()
            }
        }


        binding.ivClose.setOnClickListener { dismiss() }

    }

    private fun initObservers() {
        addProductViewModel!!.packagingUnitResponseModel.observe(this) { data ->
            data?.let { model ->
                unitData = model
                val newList = model.packUnitResponse
                val tempUnitList: ArrayList<String> = ArrayList()

                tempUnitList.addAll(preDefineUnitList)
                if (newList.size > 0) {
                    newList.forEach {
                        val unitItem = ProductUnitModel(it, false)
                        newUnitList.add(unitItem)
                        tempUnitList.add(it)
                    }

                    unitRvNewAdapter.notifyDataSetChanged()

                    preDefineUnitList.clear()
                    tempUnitList.toSet().forEach {
                        preDefineUnitList.add(it)
                    }
                    initArrayAdapter()
                }
            }
        }
    }

    private fun initArrayAdapter() {
        adapter = ArrayAdapter<String>(
            requireContext(),
            android.R.layout.select_dialog_item, preDefineUnitList
        )

        binding.etUnit.threshold = 1
        binding.etUnit.setAdapter(adapter)
    }

    private fun initRecyclerView() {
        val layoutManager = StaggeredGridLayoutManager(3, LinearLayoutManager.HORIZONTAL)
        layoutManager.gapStrategy = StaggeredGridLayoutManager.GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS
        binding.rvNewUnit.layoutManager = layoutManager
        unitRvNewAdapter =
            ProductUnitNewAdapter(
                newUnitList,
                ProductUnitNewAdapter.OnClickListener { productUnit ->
                    binding.etUnit.setText(productUnit.name?.capitalize())
                    binding.etUnit.dismissDropDown()
                    newUnitList.forEach { it.isSelected = false }
                    newUnitList[productUnit.position!!].isSelected = true
                    unitRvNewAdapter.notifyDataSetChanged()
                })

        binding.rvNewUnit.adapter = unitRvNewAdapter
    }

}