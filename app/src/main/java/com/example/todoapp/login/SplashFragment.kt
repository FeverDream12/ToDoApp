package com.example.todoapp.login

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.example.todoapp.R
import com.example.todoapp.repeatedTasks.RepeatedTasksActivity
import com.example.todoapp.utilities.MainActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class SplashFragment : Fragment() {

    private lateinit var auth: FirebaseAuth
    private lateinit var navController: NavController
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?): View? {
        Firebase.database.setPersistenceEnabled(true)
        return inflater.inflate(R.layout.fragment_splash, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Firebase.database.setPersistenceEnabled(true)
        super.onViewCreated(view, savedInstanceState)

        auth = FirebaseAuth.getInstance()
        navController = Navigation.findNavController(view)

        Handler(Looper.myLooper()!!).postDelayed(Runnable {
            if(auth.currentUser != null){
                navController.navigate(R.id.action_splashFragment_to_mainActivity)
                requireActivity().finish()
            }else{
                navController.navigate(R.id.action_splashFragment_to_signInFragment)
            }
        },1500)
    }

}