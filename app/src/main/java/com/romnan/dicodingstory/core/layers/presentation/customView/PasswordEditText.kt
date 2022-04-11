package com.romnan.dicodingstory.core.layers.presentation.customView

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatEditText
import com.romnan.dicodingstory.R

class PasswordEditText : AppCompatEditText {

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    init {
        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                if (s != null && s.length < MIN_LENGTH) {
                    this@PasswordEditText.error =
                        this@PasswordEditText.context.getString(R.string.em_password_min_length)
                }
            }

        })
    }

    companion object {
        private const val MIN_LENGTH = 6
    }
}