package se.gustavkarlsson.skylight.android.extensions

import android.app.Activity
import android.support.annotation.IdRes
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.ui.NavigationUI

fun Fragment.findNavController(): NavController =
	Navigation.findNavController(view!!)

fun Activity.findNavController(@IdRes resId: Int): NavController =
	Navigation.findNavController(this, resId)

fun AppCompatActivity.setupActionBarWithNavController(navController: NavController) =
	NavigationUI.setupActionBarWithNavController(this, navController)
