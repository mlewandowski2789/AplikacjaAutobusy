package com.example.busviewer

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ImageView


class AnimationFragment : Fragment() {
    private lateinit var backgroundImg: ImageView
    private lateinit var foregroundImg: ImageView


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_animation, container, false)
        backgroundImg = view.findViewById(R.id.backgroundImageView)
        foregroundImg = view.findViewById(R.id.foregroundImageView)

        val animation = AnimationUtils.loadAnimation(context, R.anim.animation)


        backgroundImg.startAnimation(animation)

        return view
    }

    fun stopAnimation(){
        backgroundImg.visibility = View.GONE
        foregroundImg.visibility = View.GONE
        backgroundImg.clearAnimation()
    }

}