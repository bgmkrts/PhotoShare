package com.begumkaratas.photoshare

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.begumkaratas.photoshare.databinding.FragmentFeedBinding
import com.begumkaratas.photoshare.databinding.FragmentUploadBinding


class UploadFragment : Fragment() {


    private var _binding: FragmentUploadBinding? = null
    private val binding get() = _binding!!
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.yukleButton.setOnClickListener { yukleTiklandi(it) }
        binding.imageView.setOnClickListener {gorselSec(it)  }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentUploadBinding.inflate(inflater, container, false)
        return binding.root
    }
    fun yukleTiklandi(view: View){

    }

    fun gorselSec(view: View){

    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }



}