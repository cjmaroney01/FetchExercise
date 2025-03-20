package com.colinmaroney.fetchexercise.ui

import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import com.colinmaroney.fetchexercise.R

class MainActivity : FragmentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
    }
}