package com.app.rupyz.sales.product

import android.content.res.ColorStateList
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
import com.google.android.material.chip.Chip
import java.util.Locale


class ProductUnitBottomSheetDialogFragment(
    private val unitSelectedListener: UnitSelectedListener,
    private var unit: String,
) : BottomSheetDialogFragment() {
    private lateinit var binding: BottomSheetProductUnitBinding
//    private lateinit var unitRvNewAdapter: ProductUnitNewAdapter
    private var unitSelected: String? = null
    private var addProductViewModel: ProductViewModel? = null
    private var unitData: PackagingUnitResponseModel? = null
    private val newUnitList: ArrayList<ProductUnitModel> = ArrayList()
    private val preDefineUnitList: ArrayList<String> = ArrayList()
    private var previousChip: Chip? = null
    private var chipIdMap = hashMapOf<String,Int>()
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
                    binding.chipGroupUnit.removeAllViews()
                    newList.forEach {
                        val unitItem = ProductUnitModel(it, false)
                        newUnitList.add(unitItem)
                        unitItem.name?.let { it1 -> addNewChip(it1) }
                        tempUnitList.add(it)
                    }

//                    unitRvNewAdapter.notifyDataSetChanged()

                    preDefineUnitList.clear()
                    tempUnitList.toSet().forEach {
                        preDefineUnitList.add(it)
                    }
                    initArrayAdapter()
                }
            }
        }
    }

    private fun addNewChip(chip: String) {
        val inflater = LayoutInflater.from(requireContext())
        val newChip = inflater.inflate(R.layout.layout_chip_product_unit, binding.chipGroupUnit, false) as Chip

        newChip.text = chip
        newChip.transitionName = chip
        newChip.tag = chip
        newChip.isCloseIconVisible = false
        newChip.isCheckedIconVisible = false
        newChip.isClickable = true
        newChip.chipStrokeWidth = resources.getDimension(R.dimen.dimen_1dp)
        newChip.chipStrokeColor = ColorStateList.valueOf(resources.getColor(R.color.color_C8C8C8,null))
        

        binding.chipGroupUnit.isSingleSelection  = true
        binding.chipGroupUnit.addView(newChip)

        binding.chipGroupUnit.isSingleSelection = true
        chipIdMap.put(chip.uppercase(),newChip.id)
        binding.chipGroupUnit.setOnCheckedStateChangeListener { group, checkedIds ->
            if (checkedIds.isEmpty().not()){
                val selectedChip : Chip? = binding.chipGroupUnit.findViewById(checkedIds[0])
                val selectedText = selectedChip?.text.toString()
                binding.etUnit.setText(selectedText.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() })
                binding.etUnit.dismissDropDown()
                previousChip?.let {
                    //Add To reset Previous state
                    it.setTextColor(resources.getColor(R.color.color_C8C8C8,null))
                }
                selectedChip?.let {
                    it.setTextColor(resources.getColor(R.color.white,null))
                    previousChip = it
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
        binding.etUnit.setOnItemClickListener { parent, view, position, id ->
            val selectedItem = parent.getItemAtPosition(position).toString()
            chipIdMap[selectedItem.uppercase()]?.let {  chipId ->
                val selectedChip : Chip? = binding.chipGroupUnit.findViewById(chipId)
                selectedChip?.let { chip -> chip.isChecked = true }
            }
        }
    }

    private fun initRecyclerView() {
        val layoutManager = StaggeredGridLayoutManager(3, LinearLayoutManager.HORIZONTAL)
        layoutManager.gapStrategy = StaggeredGridLayoutManager.GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS
//        binding.rvNewUnit.layoutManager = layoutManager
//        unitRvNewAdapter =
//            ProductUnitNewAdapter(
//                newUnitList,
//                ProductUnitNewAdapter.OnClickListener { productUnit ->
//                    binding.etUnit.setText(productUnit.name?.capitalize())
//                    binding.etUnit.dismissDropDown()
//                    newUnitList.forEach { it.isSelected = false }
//                    newUnitList[productUnit.position!!].isSelected = true
//                    unitRvNewAdapter.notifyDataSetChanged()
//                })
//
//        binding.rvNewUnit.adapter = unitRvNewAdapter
    }

}