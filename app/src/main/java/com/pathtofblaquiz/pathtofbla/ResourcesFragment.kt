package com.pathtofblaquiz.pathtofbla

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_resources.*

/**
 * Resources Fragment shows a list of all the categories that are available for users to study from
 */
class ResourcesFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_resources, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerViewResources.layoutManager = LinearLayoutManager(this.context)
        val adapterResources = ResourcesAdapter()
        recyclerViewResources.adapter = adapterResources
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            ResourcesFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }
}