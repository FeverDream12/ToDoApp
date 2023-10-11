package com.example.todoapp.login

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.example.todoapp.R
import com.example.todoapp.databinding.FragmentTutorialBinding
import java.io.File

class TutorialFragment : Fragment() {

    private lateinit var binding: FragmentTutorialBinding
    private lateinit var navController: NavController

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentTutorialBinding.inflate(inflater,container,false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)

        val path = requireContext().filesDir
        val letDirectory = File(path, "LET")
        letDirectory.mkdirs()

        val tutorialFile = File(letDirectory, "tutorial.txt")
        tutorialFile.delete()
        tutorialFile.appendText("skip")

        binding.nextButton.setOnClickListener{
            navController.navigate(R.id.action_tutorialFragment_to_mainActivity)
            requireActivity().finish()
        }
    }

}