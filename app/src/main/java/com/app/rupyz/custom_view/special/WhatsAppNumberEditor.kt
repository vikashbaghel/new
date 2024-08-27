package com.app.rupyz.custom_view.special

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.util.AttributeSet
import android.util.Log
import android.view.Gravity
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import com.app.rupyz.R
import com.app.rupyz.custom_view.CustomViewUtils.getLabel
import com.app.rupyz.custom_view.basic.EditInputLayout
import com.app.rupyz.custom_view.basic.TextView
import com.app.rupyz.custom_view.type.FormItemType
import com.app.rupyz.custom_view.type.TextViewType
import com.app.rupyz.generic.helper.hideView
import com.app.rupyz.generic.helper.isValidPhoneNumber
import com.app.rupyz.generic.helper.showView
import com.app.rupyz.generic.utils.AppConstant
import com.app.rupyz.generic.utils.Connectivity.Companion.hasInternetConnection
import com.app.rupyz.model_kt.FormItemsItem
import com.app.rupyz.model_kt.NameAndValueSetInfoModel
import com.app.rupyz.ui.more.MoreViewModel
import com.google.android.material.textfield.TextInputLayout.END_ICON_NONE

class WhatsAppNumberEditor : LinearLayoutCompat {

    private var inputLayout: EditInputLayout? = null
    private var formFields: FormItemsItem? = null
    private val moreViewModel: MoreViewModel =
        ViewModelProvider(context as FragmentActivity)[MoreViewModel::class.java]
    private var isMobileNumberUnique = false
    private var oldNumber: String? = null
    private var formItemType: FormItemType? = null
    private var defaultStrokeColor: Int = resources.getColor(R.color.color_322E80, null)
    private var defaultInactiveStrokeColor: Int = resources.getColor(R.color.color_DDDDDD, null)
    private var defaultValidatedStrokeColor: Int = resources.getColor(R.color.color_322E80, null)
    private var defaultErrorStrokeColor: Int = resources.getColor(R.color.red, null)
    private var defaultHintColor: Int = resources.getColor(R.color.color_727176, null)
    private var serverErrorMessage: String? = null
    private val hintTextView = TextView(context)


    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(
        context,
        attrs,
        defStyle
    ) {
        init(attrs)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(attrs)
    }

    constructor(context: Context) : super(context, null) {
        init(null)
    }

    @SuppressLint("CustomViewStyleable")
    private fun init(attrs: AttributeSet?) {
        orientation = VERTICAL
        gravity = Gravity.CENTER_VERTICAL
        try {
            for (i in 0 until childCount) {
                val view = getChildAt(i)
                if (view is EditInputLayout) {
                    removeView(view)
                }
            }
        } catch (e: Exception) {
            Log.e(javaClass.name, "init: $e")
        }
        inputLayout = if (attrs != null) {
            EditInputLayout(context, attrs)
        } else {
            EditInputLayout(context)
        }
        val a = context.obtainStyledAttributes(attrs, R.styleable.EditText)

        hintTextView.setTextViewType(TextViewType.ERROR)
        hintTextView.hideView()

        inputLayout?.editText?.hideProgressBar()
        addView(inputLayout)
        addView(hintTextView)
        setTypeFace(context)
        setTextColor()
        setHintColor()
        setDefaultStyle()
        observeCustomerList()
        a.recycle()
    }


    private fun setTypeFace(context: Context) {
        try {
            val typeface = ResourcesCompat.getFont(context, R.font.poppins_regular)
            inputLayout?.typeface = typeface
            inputLayout?.editText?.typeface = typeface
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setTextColor() {
        try {
            inputLayout?.editText?.setTextColor(resources.getColor(R.color.color_000000, null))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setHintColor() {
        try {
            inputLayout?.editText?.setHintTextColor(resources.getColor(R.color.color_727176, null))
            inputLayout?.hintTextColor =
                ColorStateList.valueOf(resources.getColor(R.color.color_727176, null))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun setDefaultStyle() {

        formFields?.let {
            inputLayout?.setEditTextType(FormItemType.MOBILE_NUMBER, it)
        }

        inputLayout?.hint = getLabel(
            (inputLayout?.editText?.hasFocus() ?: false || inputLayout?.editText?.text.isNullOrBlank()
                .not()), formFields
        )

        inputLayout?.editText?.addTextChangedListener {
            if (inputLayout?.editText?.hasFocus() == true && it.toString().length == 10 && hasInternetConnection(
                    context
                )
            ) {
                isMobileNumberUnique = false
                if (oldNumber.isNullOrBlank().not() && oldNumber.toString().trim().equals(it.toString().trim(),true) ) {
                    isMobileNumberUnique = true
                    hintTextView.hideView()
                    changeBorderColor(false)
                } else {
                    if (it.toString().isValidPhoneNumber()) {
                        hintTextView.hideView()
                        checkMobileNumberValidity(it.toString())
                    } else {
                        hintTextView.text = resources.getString(R.string.invalid_phone_number)
                        hintTextView.showView()
                        changeBorderColor(true)
                    }
                }
            } else {
                if (inputLayout?.editText?.hasFocus() == true) {
                    isMobileNumberUnique = false
                    hintTextView.text = resources.getString(R.string.invalid_phone_number)
                    hintTextView.showView()
                    changeBorderColor(false)
                }
                if (inputLayout?.editText?.text.isNullOrEmpty()) {
                    hintTextView.hideView()
                }
            }

        }

    }


    fun setWhatsAppMobileViewType(formFieldsData: FormItemsItem) {
        formFields = formFieldsData
        setDefaultStyle()
        setTypeFace(context)
        setTextColor()
        setHintColor()
    }


    fun getFieldValue(): NameAndValueSetInfoModel {
        val model = NameAndValueSetInfoModel()
        model.name = formFields?.fieldProps?.name
        model.label = formFields?.fieldProps?.label
        model.isRequired = formFields?.fieldProps?.required
        model.isCustom = formFields?.isCustom
        model.type = formFields?.type
        model.subModuleType = formFields?.type
        model.subModuleId = formFields?.fieldProps?.name
        model.value = inputLayout?.editText?.text.toString()
        return model
    }

    fun getErrorMessage(): String? {
        return serverErrorMessage
    }

    private fun checkMobileNumberValidity(mobileNumber: String) {
        inputLayout?.editText?.showProgressBar()
        moreViewModel.validateWhatsAppNumber(mobileNumber, AppConstant.MODULE_CUSTOMER)
    }

    fun getIsMobileNumberUnique(): Boolean {
        return isMobileNumberUnique
    }

    private fun observeCustomerList() {
        moreViewModel.verifyWhatsAppNumberLiveData.observe((context as FragmentActivity)) { data ->
            data?.let {
                inputLayout?.editText?.hideProgressBar()
                isMobileNumberUnique = data.data?.isUsed == false
                changeBorderColor(data.data?.isUsed == true)
                if (isMobileNumberUnique.not()) {
                    hintTextView.text = data.message
                    hintTextView.showView()
                }
                serverErrorMessage = data.message
            }
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun changeBorderColor(isUsed: Boolean) {
        if (isUsed) {
            
            if (inputLayout?.editText?.text?.length == 10 && inputLayout?.editText?.isProgressBarShowing() == false) {
               /* inputLayout?.editText?.setCompoundDrawablesWithIntrinsicBounds(
                    null, null, resources.getDrawable(R.drawable.ic_alert_error, null), null
                )*/
                inputLayout?.endIconMode = END_ICON_NONE
                inputLayout?.isEndIconCheckable = false
                inputLayout?.isEndIconVisible = true
                inputLayout?.endIconDrawable = resources.getDrawable(R.drawable.ic_alert_error, null)
                inputLayout?.setEndIconTintList(ColorStateList.valueOf(Color.RED))
            } else {
                /*inputLayout?.editText?.setCompoundDrawablesWithIntrinsicBounds(
                    null, null, null, null
                )*/
                inputLayout?.isEndIconVisible = false
            }

            inputLayout?.boxStrokeColor =
                if (inputLayout?.editText?.text?.length == 10 && inputLayout?.editText?.isProgressBarShowing() == false) {
                    defaultErrorStrokeColor
                } else {
                    defaultInactiveStrokeColor
                }

            val states = arrayOf(
                intArrayOf(android.R.attr.state_focused),
                intArrayOf(-android.R.attr.state_focused),
                intArrayOf(android.R.attr.state_enabled),
                intArrayOf(-android.R.attr.state_enabled),
                intArrayOf(-android.R.attr.state_window_focused)
            )
            val colors = intArrayOf(
                if (inputLayout?.editText?.text?.length == 10 && inputLayout?.editText?.isProgressBarShowing() == false) {
                    defaultErrorStrokeColor
                } else {
                    defaultInactiveStrokeColor
                }, // focused
                if (inputLayout?.editText?.text?.length == 10 && inputLayout?.editText?.isProgressBarShowing() == false) {
                    defaultErrorStrokeColor
                } else {
                    defaultInactiveStrokeColor
                }, // unfocused
                if (inputLayout?.editText?.text?.length == 10 && inputLayout?.editText?.isProgressBarShowing() == false) {
                    defaultErrorStrokeColor
                } else {
                    defaultInactiveStrokeColor
                }, // enabled
                if (inputLayout?.editText?.text?.length == 10 && inputLayout?.editText?.isProgressBarShowing() == false) {
                    defaultErrorStrokeColor
                } else {
                    defaultInactiveStrokeColor
                },  // disabled
                if (inputLayout?.editText?.text?.length == 10 && inputLayout?.editText?.isProgressBarShowing() == false) {
                    defaultErrorStrokeColor
                } else {
                    defaultInactiveStrokeColor
                },  // window focused
            )
            inputLayout?.setBoxStrokeColorStateList(ColorStateList(states, colors))
            inputLayout?.defaultHintTextColor = (ColorStateList.valueOf(defaultHintColor))
            inputLayout?.hintTextColor = (ColorStateList.valueOf(defaultHintColor))

            val focusChangeListener = OnFocusChangeListener { _, hasFocus ->
                if (hasFocus) {
                    inputLayout?.boxStrokeColor =
                        if (inputLayout?.editText?.text?.length == 10 && inputLayout?.editText?.isProgressBarShowing() == false) {
                            defaultErrorStrokeColor
                        } else {
                            defaultStrokeColor
                        }
                    inputLayout?.setBoxStrokeColorStateList(
                        ColorStateList.valueOf(
                            if (inputLayout?.editText?.text?.length == 10 && inputLayout?.editText?.isProgressBarShowing() == false) {
                                defaultErrorStrokeColor
                            } else {
                                defaultStrokeColor
                            }
                        )
                    )
                    inputLayout?.defaultHintTextColor = (ColorStateList.valueOf(
                        if (inputLayout?.editText?.text?.length == 10 && inputLayout?.editText?.isProgressBarShowing() == false) {
                            defaultErrorStrokeColor
                        } else {
                            defaultStrokeColor
                        }
                    ))
                    inputLayout?.hintTextColor = (ColorStateList.valueOf(
                        if (inputLayout?.editText?.text?.length == 10 && inputLayout?.editText?.isProgressBarShowing() == false) {
                            defaultErrorStrokeColor
                        } else {
                            defaultStrokeColor
                        }
                    ))
                } else {
                    if (inputLayout?.editText?.text.isNullOrBlank().not()) {
                        val focusedStates = arrayOf(
                            intArrayOf(android.R.attr.state_focused),
                            intArrayOf(-android.R.attr.state_focused),
                            intArrayOf(android.R.attr.state_active),
                            intArrayOf(-android.R.attr.state_active),
                        )
                        val focusedColors = intArrayOf(
                            if (inputLayout?.editText?.text?.length == 10 && inputLayout?.editText?.isProgressBarShowing() == false) {
                                defaultErrorStrokeColor
                            } else {
                                defaultStrokeColor
                            }, // focused
                            if (inputLayout?.editText?.text?.length == 10 && inputLayout?.editText?.isProgressBarShowing() == false) {
                                defaultErrorStrokeColor
                            } else {
                                defaultStrokeColor
                            }, // unfocused
                            if (inputLayout?.editText?.text?.length == 10 && inputLayout?.editText?.isProgressBarShowing() == false) {
                                defaultErrorStrokeColor
                            } else {
                                defaultStrokeColor
                            },  // active
                            if (inputLayout?.editText?.text?.length == 10 && inputLayout?.editText?.isProgressBarShowing() == false) {
                                defaultErrorStrokeColor
                            } else {
                                defaultStrokeColor
                            }  // inactive
                        )
                        inputLayout?.setBoxStrokeColorStateList(
                            ColorStateList(
                                focusedStates,
                                focusedColors
                            )
                        )
                        inputLayout?.defaultHintTextColor = ColorStateList.valueOf(
                            if (inputLayout?.editText?.text?.length == 10 && inputLayout?.editText?.isProgressBarShowing() == false) {
                                defaultErrorStrokeColor
                            } else {
                                defaultStrokeColor
                            }
                        )
                        inputLayout?.hintTextColor = ColorStateList.valueOf(
                            if (inputLayout?.editText?.text?.length == 10 && inputLayout?.editText?.isProgressBarShowing() == false) {
                                defaultErrorStrokeColor
                            } else {
                                defaultStrokeColor
                            }
                        )
                        inputLayout?.boxStrokeColor =
                            if (inputLayout?.editText?.text?.length == 10 && inputLayout?.editText?.isProgressBarShowing() == false) {
                                defaultErrorStrokeColor
                            } else {
                                defaultStrokeColor
                            }
                    } else {
                        inputLayout?.setBoxStrokeColorStateList(ColorStateList(states, colors))
                        inputLayout?.defaultHintTextColor = ColorStateList.valueOf(defaultHintColor)
                        inputLayout?.hintTextColor = ColorStateList.valueOf(defaultHintColor)
                        inputLayout?.boxStrokeColor = defaultInactiveStrokeColor
                    }
                }
                inputLayout?.hint = getLabel(
                    (hasFocus || inputLayout?.editText?.text.isNullOrBlank().not()),
                    formFields
                )
            }

            onFocusChangeListener = focusChangeListener
            inputLayout?.editText?.onFocusChangeListener = focusChangeListener


        } else {

            if (inputLayout?.editText?.text?.length == 10 && inputLayout?.editText?.isProgressBarShowing() == false) {
                /*inputLayout?.editText?.setCompoundDrawablesWithIntrinsicBounds(
                    null, null, resources.getDrawable(R.drawable.ic_circle_tick, null), null
                )*/
                inputLayout?.endIconMode = END_ICON_NONE
                inputLayout?.isEndIconCheckable = false
                inputLayout?.isEndIconVisible = true
                isMobileNumberUnique = true
                inputLayout?.endIconDrawable = resources.getDrawable(R.drawable.ic_circle_tick, null)
                inputLayout?.setEndIconTintList(ColorStateList.valueOf(Color.GREEN))
            } else {
                /*inputLayout?.editText?.setCompoundDrawablesWithIntrinsicBounds(
                    null, null, null, null
                )*/
                inputLayout?.isEndIconVisible = false
            }

            inputLayout?.boxStrokeColor =
                if (inputLayout?.editText?.text?.length == 10 && inputLayout?.editText?.isProgressBarShowing() == false) {
                    defaultValidatedStrokeColor
                } else {
                    defaultInactiveStrokeColor
                }

            val states = arrayOf(
                intArrayOf(android.R.attr.state_focused),
                intArrayOf(-android.R.attr.state_focused),
                intArrayOf(android.R.attr.state_enabled),
                intArrayOf(-android.R.attr.state_enabled),
                intArrayOf(-android.R.attr.state_window_focused)
            )
            val colors = intArrayOf(
                if (inputLayout?.editText?.text?.length == 10 && inputLayout?.editText?.isProgressBarShowing() == false) {
                    defaultValidatedStrokeColor
                } else {
                    defaultInactiveStrokeColor
                }, // focused
                if (inputLayout?.editText?.text?.length == 10 && inputLayout?.editText?.isProgressBarShowing() == false) {
                    defaultValidatedStrokeColor
                } else {
                    defaultInactiveStrokeColor
                }, // unfocused
                if (inputLayout?.editText?.text?.length == 10 && inputLayout?.editText?.isProgressBarShowing() == false) {
                    defaultValidatedStrokeColor
                } else {
                    defaultInactiveStrokeColor
                }, // enabled
                if (inputLayout?.editText?.text?.length == 10 && inputLayout?.editText?.isProgressBarShowing() == false) {
                    defaultValidatedStrokeColor
                } else {
                    defaultInactiveStrokeColor
                },  // disabled
                if (inputLayout?.editText?.text?.length == 10 && inputLayout?.editText?.isProgressBarShowing() == false) {
                    defaultValidatedStrokeColor
                } else {
                    defaultInactiveStrokeColor
                },  // window focused
            )
            inputLayout?.setBoxStrokeColorStateList(ColorStateList(states, colors))
            inputLayout?.defaultHintTextColor = (ColorStateList.valueOf(defaultHintColor))
            inputLayout?.hintTextColor = (ColorStateList.valueOf(defaultHintColor))

            val focusChangeListener = OnFocusChangeListener { _, hasFocus ->
                if (hasFocus) {
                    inputLayout?.boxStrokeColor =
                        if (inputLayout?.editText?.text?.length == 10 && inputLayout?.editText?.isProgressBarShowing() == false) {
                            defaultValidatedStrokeColor
                        } else {
                            defaultStrokeColor
                        }
                    inputLayout?.setBoxStrokeColorStateList(
                        ColorStateList.valueOf(
                            if (inputLayout?.editText?.text?.length == 10 && inputLayout?.editText?.isProgressBarShowing() == false) {
                                defaultValidatedStrokeColor
                            } else {
                                defaultStrokeColor
                            }
                        )
                    )
                    inputLayout?.defaultHintTextColor = (ColorStateList.valueOf(
                        if (inputLayout?.editText?.text?.length == 10 && inputLayout?.editText?.isProgressBarShowing() == false) {
                            defaultValidatedStrokeColor
                        } else {
                            defaultStrokeColor
                        }
                    ))
                    inputLayout?.hintTextColor = (ColorStateList.valueOf(
                        if (inputLayout?.editText?.text?.length == 10 && inputLayout?.editText?.isProgressBarShowing() == false) {
                            defaultValidatedStrokeColor
                        } else {
                            defaultStrokeColor
                        }
                    ))
                } else {
                    if (inputLayout?.editText?.text.isNullOrBlank().not()) {
                        val focusedStates = arrayOf(
                            intArrayOf(android.R.attr.state_focused),
                            intArrayOf(-android.R.attr.state_focused),
                            intArrayOf(android.R.attr.state_active),
                            intArrayOf(-android.R.attr.state_active),
                        )
                        val focusedColors = intArrayOf(
                            if (inputLayout?.editText?.text?.length == 10 && inputLayout?.editText?.isProgressBarShowing() == false) {
                                defaultValidatedStrokeColor
                            } else {
                                defaultStrokeColor
                            }, // focused
                            if (inputLayout?.editText?.text?.length == 10 && inputLayout?.editText?.isProgressBarShowing() == false) {
                                defaultValidatedStrokeColor
                            } else {
                                defaultStrokeColor
                            }, // unfocused
                            if (inputLayout?.editText?.text?.length == 10 && inputLayout?.editText?.isProgressBarShowing() == false) {
                                defaultValidatedStrokeColor
                            } else {
                                defaultStrokeColor
                            },  // active
                            if (inputLayout?.editText?.text?.length == 10 && inputLayout?.editText?.isProgressBarShowing() == false) {
                                defaultValidatedStrokeColor
                            } else {
                                defaultStrokeColor
                            }  // inactive
                        )
                        inputLayout?.setBoxStrokeColorStateList(
                            ColorStateList(
                                focusedStates,
                                focusedColors
                            )
                        )
                        inputLayout?.defaultHintTextColor = ColorStateList.valueOf(
                            if (inputLayout?.editText?.text?.length == 10 && inputLayout?.editText?.isProgressBarShowing() == false) {
                                defaultValidatedStrokeColor
                            } else {
                                defaultStrokeColor
                            }
                        )
                        inputLayout?.hintTextColor = ColorStateList.valueOf(
                            if (inputLayout?.editText?.text?.length == 10 && inputLayout?.editText?.isProgressBarShowing() == false) {
                                defaultValidatedStrokeColor
                            } else {
                                defaultStrokeColor
                            }
                        )
                        inputLayout?.boxStrokeColor =
                            if (inputLayout?.editText?.text?.length == 10 && inputLayout?.editText?.isProgressBarShowing() == false) {
                                defaultValidatedStrokeColor
                            } else {
                                defaultStrokeColor
                            }
                    } else {
                        inputLayout?.setBoxStrokeColorStateList(ColorStateList(states, colors))
                        inputLayout?.defaultHintTextColor = ColorStateList.valueOf(defaultHintColor)
                        inputLayout?.hintTextColor = ColorStateList.valueOf(defaultHintColor)
                        inputLayout?.boxStrokeColor = defaultInactiveStrokeColor
                    }
                }
                inputLayout?.hint = getLabel(
                    (hasFocus || inputLayout?.editText?.text.isNullOrBlank().not()),
                    formFields
                )
            }

            onFocusChangeListener = focusChangeListener
            inputLayout?.editText?.onFocusChangeListener = focusChangeListener

        }
    }


    fun getFormFields(): FormItemsItem? {
        return formFields
    }

    fun getFieldType(): FormItemType {
        return formItemType ?: FormItemType.NUMBERS
    }

    fun setFormItemType(type: FormItemType) {
        this.formItemType = type
    }

    fun setValue(phoneNumber: String?, isVerified: Boolean = true) {
        isMobileNumberUnique = isVerified
        oldNumber = phoneNumber
        inputLayout?.editText?.setText(phoneNumber)
        if (isVerified) {
            changeBorderColor(false)
        } else {
            phoneNumber?.let { checkMobileNumberValidity(it) }
        }
    }

}