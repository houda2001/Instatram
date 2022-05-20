package com.example.instatram.ui.home


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.example.instatram.R
import com.example.instatram.databinding.FragmentHomeBinding



class HomeFragment : Fragment() {



    private lateinit var homeViewModel: HomeViewModel

    private lateinit var recyclerView: RecyclerView

    private var _binding: FragmentHomeBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        val view = inflater.inflate(R.layout.fragment_home, container, false)
        recyclerView = view.findViewById(R.id.recyclerView)

        homeViewModel = ViewModelProvider(this).get(HomeViewModel::class.java)
        homeViewModel.stationData.observe(viewLifecycleOwner, {
            val adapter = MainRecyclerAdapter(requireContext(), homeViewModel, it)
            recyclerView.adapter = adapter
        })
        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onResume() {
        super.onResume()
        homeViewModel.wasClicked = false
    }
}