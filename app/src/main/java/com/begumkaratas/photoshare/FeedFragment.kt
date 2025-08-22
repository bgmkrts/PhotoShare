package com.begumkaratas.photoshare

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.navigation.Navigation
import com.begumkaratas.photoshare.databinding.FragmentFeedBinding
import com.begumkaratas.photoshare.databinding.FragmentKullaniciBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth


class FeedFragment : Fragment(), PopupMenu.OnMenuItemClickListener {
    private var _binding: FragmentFeedBinding? = null
    private val binding get() = _binding!!

    private lateinit var popUpMenu: PopupMenu

    private lateinit var auth:FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth= Firebase.auth

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.floatingActionButton.setOnClickListener {
            floatingButtonTiklandi(it)

        }

        popUpMenu = PopupMenu(requireContext(), binding.floatingActionButton)
        val inflater = popUpMenu.menuInflater
        inflater.inflate(R.menu.my_popup_menu, popUpMenu.menu)
        popUpMenu.setOnMenuItemClickListener(this)
    }

    fun floatingButtonTiklandi(view: View) {
        /* val popUpMenu=PopupMenu(requireContext(),binding.floatingActionButton)
          val inflater=popUpMenu.menuInflater
          inflater.inflate(R.menu.my_popup_menu,popUpMenu.menu)*/
        popUpMenu.show()

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFeedBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onMenuItemClick(item: MenuItem?): Boolean {
        if (item?.itemId == R.id.yuklemeItem) {
            val action = FeedFragmentDirections.actionFeedFragmentToUploadFragment()
            Navigation.findNavController(requireView()).navigate(action)
        } else if (item?.itemId == R.id.cikisItem) {
            //çıkış işlemi
            auth.signOut()

            val action = FeedFragmentDirections.actionFeedFragmentToKullaniciFragment()
            Navigation.findNavController(requireView()).navigate(action)

        }
        return true
    }

}
