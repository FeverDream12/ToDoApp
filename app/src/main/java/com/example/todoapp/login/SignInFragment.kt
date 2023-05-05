package com.example.todoapp.login

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.example.todoapp.R
import com.example.todoapp.databinding.FragmentSignInBinding
import com.example.todoapp.databinding.FragmentSignUpBinding
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth

class SignInFragment : Fragment() {

    private lateinit var auth: FirebaseAuth
    private lateinit var navController: NavController
    private lateinit var binding: FragmentSignInBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?): View? {

        binding = FragmentSignInBinding.inflate(inflater,container,false)
        binding.emailLogInInput.setBackgroundColor(binding.cardView.cardBackgroundColor.defaultColor)
        binding.passwordLogInInput.setBackgroundColor(binding.cardView.cardBackgroundColor.defaultColor)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init(view)
        registerEvents()
    }

    private fun registerEvents() {

        binding.signUpText.setOnClickListener{
            navController.navigate(R.id.action_signInFragment_to_signUpFragment)
        }
        binding.signInNextButton.setOnClickListener{
            val email = binding.emailLogInInput.text.toString().trim()
            val pass = binding.passwordLogInInput.text.toString().trim()

            if(email.isNotEmpty() && pass.isNotEmpty() ){
                auth.signInWithEmailAndPassword(email,pass).addOnCompleteListener(
                    OnCompleteListener {
                        if(it.isSuccessful){
                            Toast.makeText(context,"Успешный вход", Toast.LENGTH_SHORT).show()
                            navController.navigate(R.id.action_signInFragment_to_mainActivity)
                            requireActivity().finish()
                        }else{
                            Toast.makeText(context,it.exception?.message, Toast.LENGTH_SHORT).show()
                        }
                    }
                )
            }
        }
    }

    private fun init(view: View) {
        navController = Navigation.findNavController(view)
        auth = FirebaseAuth.getInstance()
    }

}