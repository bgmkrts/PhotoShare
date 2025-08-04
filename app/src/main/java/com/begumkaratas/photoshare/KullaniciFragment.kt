package com.begumkaratas.photoshare

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.begumkaratas.photoshare.databinding.FragmentKullaniciBinding
import java.sql.DriverManager.println

class KullaniciFragment : Fragment() {

    private var _binding: FragmentKullaniciBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentKullaniciBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.kayitButton.setOnClickListener {
            kayitOl(it)
        }
        binding.girisButton.setOnClickListener {
            giris(it)
        }
    }

    fun kayitOl(view: View) {
        println("kayıt ol tıklandı")
    }

    fun giris(view: View) {
        println("giriş yapıldı")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
