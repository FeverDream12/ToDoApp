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
import com.example.todoapp.databinding.FragmentSignUpBinding
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth

class SignUpFragment : Fragment() {

    private lateinit var auth:FirebaseAuth
    private lateinit var navController: NavController
    private lateinit var binding: FragmentSignUpBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?): View? {

        binding = FragmentSignUpBinding.inflate(inflater,container,false)
        binding.emailInput.setBackgroundColor(binding.cardView.cardBackgroundColor.defaultColor)
        binding.passwordInput.setBackgroundColor(binding.cardView.cardBackgroundColor.defaultColor)
        binding.passwordReInput.setBackgroundColor(binding.cardView.cardBackgroundColor.defaultColor)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init(view)
        registerEvents()
    }

    private fun registerEvents() {

        binding.signInText.setOnClickListener{
            navController.navigate(R.id.action_signUpFragment_to_signInFragment)
        }
        binding.signUpNextButton.setOnClickListener{
            val email = binding.emailInput.text.toString().trim()
            val pass = binding.passwordInput.text.toString().trim()
            val repass = binding.passwordReInput.text.toString().trim()

            if(email.isNotEmpty() && pass.isNotEmpty() && repass.isNotEmpty()){
                if(pass == repass){
                    auth.createUserWithEmailAndPassword(email,pass).addOnCompleteListener(
                        OnCompleteListener {
                            if(it.isSuccessful){
                                Toast.makeText(context,"Успешная регистрация",Toast.LENGTH_SHORT).show()
                                navController.navigate(R.id.action_signUpFragment_to_mainActivity)
                                requireActivity().finish()
                            }else{
                                Toast.makeText(context,it.exception?.message,Toast.LENGTH_SHORT).show()
                            }
                        }
                    )
                }
            }
        }
    }

    private fun init(view: View) {
        navController = Navigation.findNavController(view)
        auth = FirebaseAuth.getInstance()

    }
}