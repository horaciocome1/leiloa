package io.github.horaciocome1.leiloa.util

import android.view.View
import androidx.navigation.NavDirections
import androidx.navigation.findNavController

fun NavDirections.navigate(view: View) = view.findNavController()
    .navigate(this)