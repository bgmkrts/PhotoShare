package com.begumkaratas.photoshare

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.Navigation
import com.begumkaratas.photoshare.databinding.FragmentKullaniciBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth

class KullaniciFragment : Fragment() {

    private var _binding: FragmentKullaniciBinding? = null
    private val binding get() = _binding!!
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
                auth = Firebase.auth
    }
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

        val guncelKullanici=auth.currentUser
        if(guncelKullanici!=null){
            //kullanıcı daha önce giriş yapmış
            val action = KullaniciFragmentDirections.actionKullaniciFragmentToGirisFragment()
            Navigation.findNavController(view).navigate(action)
        }
    }

    private fun kayitOl(view: View) {

        val email=binding.emailText.text.toString()
        val password=binding.editTextTextPassword.text.toString()

        if(email.isNotEmpty()&& password.isNotEmpty()){
            auth.createUserWithEmailAndPassword(email,password).addOnCompleteListener { task->
                if(task.isSuccessful){
                    //kullanıcı oluşturuldu

                    val action=KullaniciFragmentDirections.actionKullaniciFragmentToGirisFragment()
                    Navigation.findNavController(view).navigate(action)

                }
            }.addOnFailureListener { exception->
                Toast.makeText(requireContext(),exception.localizedMessage,Toast.LENGTH_LONG).show()
            }
        }


    }

    private fun giris(view: View) {
        val email = binding.emailText.text.toString()
        val password = binding.editTextTextPassword.text.toString()

        if(email.isNotEmpty() && password.isNotEmpty()) {
            auth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener {
                    val action = KullaniciFragmentDirections.actionKullaniciFragmentToGirisFragment()
                    Navigation.findNavController(view).navigate(action)
                }
                .addOnFailureListener { exception ->
                    Toast.makeText(requireContext(), exception.localizedMessage, Toast.LENGTH_LONG).show()
                }
        } else {
            Toast.makeText(requireContext(), "Email ve şifre boş olamaz", Toast.LENGTH_SHORT).show()
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}
