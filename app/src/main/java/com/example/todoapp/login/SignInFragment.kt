package com.example.todoapp.login

import android.app.Activity
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.example.todoapp.R
import com.example.todoapp.databinding.FragmentSignInBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider

class SignInFragment : Fragment() {

    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignClient: GoogleSignInClient
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

        binding.signUpText.setOnClickListener{
            navController.navigate(R.id.action_signInFragment_to_signUpFragment)
        }



        init(view)
        googleLogin()

        binding.signInNextButton.setOnClickListener{
            mailLogin()
        }
    }


    private fun googleLogin() {

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignClient = GoogleSignIn.getClient(requireActivity(),gso)

        val launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
                result ->
            if(result.resultCode == Activity.RESULT_OK){
                val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)

                if(task.isSuccessful){
                    val acc :GoogleSignInAccount? = task.result
                    if(acc!= null){
                        val credential = GoogleAuthProvider.getCredential(acc.idToken, null)
                        auth.signInWithCredential(credential).addOnCompleteListener{
                            if(it.isSuccessful){
                                Toast.makeText(context,"Успешный вход", Toast.LENGTH_SHORT).show()
                                navController.navigate(R.id.action_signInFragment_to_mainActivity)
                                requireActivity().finish()
                            }else{
                                Toast.makeText(context,it.exception?.message, Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }else{
                    Toast.makeText(requireContext(), task.exception.toString(),Toast.LENGTH_SHORT).show()
                }
            }else{
                Toast.makeText(requireContext(), "Ошибка входа",Toast.LENGTH_SHORT).show()
            }
        }

        binding.googleLoginButton.setOnClickListener{
            val signInIntent = googleSignClient.signInIntent
            launcher.launch(signInIntent)
        }
    }

    private fun mailLogin() {

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

    private fun init(view: View) {
        navController = Navigation.findNavController(view)
        auth = FirebaseAuth.getInstance()
    }

}