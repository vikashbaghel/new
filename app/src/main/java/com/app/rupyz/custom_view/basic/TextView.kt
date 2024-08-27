package com.app.rupyz.custom_view.basic

import android.content.Context
import android.util.AttributeSet
import android.util.TypedValue
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.marginBottom
import androidx.core.view.marginEnd
import androidx.core.view.marginStart
import androidx.core.view.marginTop
import com.app.rupyz.R
import com.app.rupyz.custom_view.type.TextViewType
import com.app.rupyz.custom_view.type.TextViewType.ERROR
import com.app.rupyz.custom_view.type.TextViewType.HINT
import com.app.rupyz.custom_view.type.TextViewType.LABEL
import com.app.rupyz.custom_view.type.TextViewType.NONE
import com.app.rupyz.custom_view.type.TextViewType.TEXT
import com.app.rupyz.custom_view.type.TextViewType.TITLE
import com.app.rupyz.generic.helper.enumContains
import com.app.rupyz.generic.helper.toDp

class TextView : AppCompatTextView {

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

    fun init(attrs: AttributeSet?) {
        if (attrs != null) {
            val a = context.obtainStyledAttributes(attrs, R.styleable.TextView)
            val type = a.getString(R.styleable.TextView_textViewType)
            val editType = if (enumContains<TextViewType>(type ?: "")) {
                TextViewType.valueOf(type ?: TEXT.value)
            } else {
                NONE
            }
            setTypeFace(context, editType)
            setTextColor(editType)
            setDefaultStyle(editType)
            a.recycle()
        }
    }


    private fun setDefaultStyle(type: TextViewType) {
        val inputParams = FrameLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        when (type) {
            TEXT -> {
                setTextSize(TypedValue.COMPLEX_UNIT_DIP, 12f)
                lineHeight = 20.toDp()
                typeface = ResourcesCompat.getFont(context, R.font.poppins_regular)
                setTextColor(resources.getColor(R.color.color_000000, null))
                inputParams.setMargins(
                    if (marginStart == 0) {
                        10
                    } else {
                        marginStart
                    }, if (marginTop == 0) {
                        20
                    } else {
                        marginTop
                    }, if (marginBottom == 0) {
                        10
                    } else {
                        marginBottom
                    }, if (marginEnd == 0) {
                        0
                    } else {
                        marginEnd
                    }
                )
                setPadding(
                    totalPaddingStart, if (paddingTop == 0) {
                        15
                    } else {
                        totalPaddingTop
                    }, totalPaddingEnd, if (paddingBottom == 0) {
                        15
                    } else {
                        totalPaddingBottom
                    }
                )
            }

            HINT -> {
                setTextSize(TypedValue.COMPLEX_UNIT_DIP, 12f)
                lineHeight = resources.getDimension(R.dimen.size_20sp).toInt()
                typeface = ResourcesCompat.getFont(context, R.font.poppins_regular)
                setTextColor(resources.getColor(R.color.color_727176, null))
                inputParams.setMargins(
                    if (marginStart == 0) {
                        10
                    } else {
                        marginStart
                    }, if (marginTop == 0) {
                        0
                    } else {
                        marginTop
                    }, if (marginBottom == 0) {
                        10
                    } else {
                        marginBottom
                    }, if (marginEnd == 0) {
                        0
                    } else {
                        marginEnd
                    }
                )
                setPadding(
                    totalPaddingStart, if (paddingTop == 0) {
                        0
                    } else {
                        totalPaddingTop
                    }, totalPaddingEnd, if (paddingBottom == 0) {
                        15
                    } else {
                        totalPaddingBottom
                    }
                )
            }

            ERROR -> {
                setTextSize(TypedValue.COMPLEX_UNIT_DIP, 12f)
                lineHeight = resources.getDimension(R.dimen.size_20sp).toInt()
                typeface = ResourcesCompat.getFont(context, R.font.poppins_regular)
                setTextColor(resources.getColor(R.color.red, null))
                inputParams.setMargins(
                    if (marginStart == 0) {
                        10
                    } else {
                        marginStart
                    }, if (marginTop == 0) {
                        20
                    } else {
                        marginTop
                    }, if (marginBottom == 0) {
                        10
                    } else {
                        marginBottom
                    }, if (marginEnd == 0) {
                        0
                    } else {
                        marginEnd
                    }
                )
                setPadding(
                    totalPaddingStart, if (paddingTop == 0) {
                        0
                    } else {
                        totalPaddingTop
                    }, totalPaddingEnd, if (paddingBottom == 0) {
                        15
                    } else {
                        totalPaddingBottom
                    }
                )
            }

            TITLE -> {
                setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16f)
                lineHeight = 24.toDp()
                typeface = ResourcesCompat.getFont(context, R.font.poppins_semibold)
                setTextColor(resources.getColor(R.color.color_000000, null))
                isSingleLine = true

                inputParams.topMargin = TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP,
                    15f,
                    resources.displayMetrics
                ).toInt()
                inputParams.marginStart = TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP,
                    0f,
                    resources.displayMetrics
                ).toInt()
                inputParams.marginEnd = TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP,
                    0f,
                    resources.displayMetrics
                ).toInt()
                inputParams.bottomMargin = TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP,
                    15f,
                    resources.displayMetrics
                ).toInt()

                setPadding(
                    totalPaddingStart + TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP,
                        0f,
                        resources.displayMetrics
                    ).toInt(),
                    totalPaddingTop + TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP,
                        20f,
                        resources.displayMetrics
                    ).toInt(),
                    totalPaddingEnd + TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP,
                        0f,
                        resources.displayMetrics
                    ).toInt(),
                    totalPaddingBottom + TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP,
                        10f,
                        resources.displayMetrics
                    ).toInt()
                )

            }

            LABEL -> {
                setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14f)
                lineHeight = 24.toDp()
                typeface = ResourcesCompat.getFont(context, R.font.poppins_regular)
                setTextColor(resources.getColor(R.color.color_727176, null))
                isSingleLine = true

                inputParams.setMargins(
                    if (marginStart == 0) {
                        10
                    } else {
                        marginStart
                    }, if (marginTop == 0) {
                        20
                    } else {
                        marginTop
                    }, if (marginBottom == 0) {
                        10
                    } else {
                        marginBottom
                    }, if (marginEnd == 0) {
                        0
                    } else {
                        marginEnd
                    }
                )
                setPadding(
                    totalPaddingStart, if (paddingTop == 0) {
                        15
                    } else {
                        totalPaddingTop
                    }, totalPaddingEnd, if (paddingBottom == 0) {
                        15
                    } else {
                        totalPaddingBottom
                    }
                )

            }

            NONE -> {
                // NO Styling
            }
        }
        layoutParams = inputParams

    }

    private fun setTypeFace(context: Context, type: TextViewType) {
        try {
            typeface = when (type) {
                TEXT -> {
                    ResourcesCompat.getFont(context, R.font.poppins_regular)
                }

                HINT -> {
                    ResourcesCompat.getFont(context, R.font.poppins_regular)
                }

                ERROR -> {
                    ResourcesCompat.getFont(context, R.font.poppins_regular)
                }

                TITLE -> {
                    ResourcesCompat.getFont(context, R.font.poppins_bold)
                }

                LABEL -> {
                    ResourcesCompat.getFont(context, R.font.poppins_semibold)
                }

                NONE -> {
                    typeface
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setTextColor(type: TextViewType) {
        try {
            setTextColor(
                when (type) {
                    TEXT -> {
                        resources.getColor(R.color.color_000000, null)
                    }

                    HINT -> {
                        resources.getColor(R.color.color_727176, null)
                    }

                    ERROR -> {
                        resources.getColor(R.color.red, null)
                    }

                    TITLE -> {
                        resources.getColor(R.color.color_000000, null)
                    }

                    LABEL -> {
                        resources.getColor(R.color.color_727176, null)
                    }

                    NONE -> {
                        currentTextColor
                    }
                }
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    fun setTextViewType(type: TextViewType) {
        setTypeFace(context, type)
        setTextColor(type)
        setDefaultStyle(type)
    }


}

