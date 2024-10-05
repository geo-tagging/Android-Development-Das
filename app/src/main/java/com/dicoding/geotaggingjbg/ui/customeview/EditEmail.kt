package com.dicoding.geotaggingjbg.ui.customeview

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatEditText
import com.dicoding.geotaggingjbg.R
import java.util.regex.Pattern

class EditEmail : AppCompatEditText {

    constructor(context: Context) : super(context) {

    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {

    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {

    }

    override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
        val emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"
        val inputText = s.toString()
        val isValidEmail = Pattern.matches(emailPattern, inputText)

        if (!isValidEmail) {
            setError(resources.getString(R.string.email_valid_validaiton), null)
        } else {
            error = null
        }
    }
}