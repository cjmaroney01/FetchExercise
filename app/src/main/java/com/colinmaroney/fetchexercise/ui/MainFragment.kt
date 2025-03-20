package com.colinmaroney.fetchexercise.ui

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.VisibleForTesting
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.colinmaroney.fetchexercise.R
import com.colinmaroney.fetchexercise.network.HiringRepo
import com.colinmaroney.fetchexercise.viewmodel.HiringViewModel

class MainFragment: Fragment() {
    @VisibleForTesting
    internal val viewModel: HiringViewModel by viewModels {
        HiringViewModel.Factory(HiringRepo())
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.main_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recycler = view.findViewById<RecyclerView>(R.id.main_recycler)
        recycler.layoutManager = LinearLayoutManager(requireContext(),
            LinearLayoutManager.VERTICAL,
            false
            )

        addObservers(recycler)

        viewModel.fetchHiringData()
    }

    @VisibleForTesting
    internal fun addObservers(recycler: RecyclerView) {
        viewModel.hiringData.observe(viewLifecycleOwner) { hiringData ->
            hiringData?.let { data ->
                val adapter = HiringDataAdapter(data)
                recycler.adapter = adapter
            }
        }

        viewModel.hiringError.observe(viewLifecycleOwner) { isError ->
            if (isError) {
                // in real life there would be better, more professional error handling
                AlertDialog.Builder(requireContext())
                    .setMessage(getString(R.string.an_error_occurred))
                    .setPositiveButton(getString(R.string.ok), object: DialogInterface.OnClickListener {
                        override fun onClick(dialogInterface: DialogInterface?, p1: Int) {
                            dialogInterface?.dismiss()
                        }
                    })
                    .show()
            }
        }
    }
}