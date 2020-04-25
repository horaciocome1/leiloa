package io.github.horaciocome1.leiloa.ui.main

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.Navigation
import com.google.firebase.auth.FirebaseAuth
import io.github.horaciocome1.leiloa.R
import io.github.horaciocome1.leiloa.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity(), NavController.OnDestinationChangedListener,
    FirebaseAuth.AuthStateListener {

    private lateinit var binding: ActivityMainBinding

    private val auth: FirebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }

    private val navController: NavController by lazy {
        Navigation.findNavController(this, R.id.mainNavHost)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initUI()
    }

    override fun onStart() {
        super.onStart()
        auth.addAuthStateListener(this)
    }

    override fun onStop() {
        super.onStop()
        auth.removeAuthStateListener(this)
    }

    override fun onAuthStateChanged(auth: FirebaseAuth) {
        if (auth.currentUser == null)
            navController.navigate(R.id.destination_sign_in)
        else if (navController.currentDestination?.id == R.id.destination_sign_in)
            navController.navigateUp()
    }

    override fun onDestinationChanged(
        controller: NavController,
        destination: NavDestination,
        arguments: Bundle?
    ) {
        when (destination.id) {
            R.id.destination_sign_in ->
                supportActionBar?.hide()
            R.id.destination_company_domain ->
                supportActionBar?.setDisplayHomeAsUpEnabled(false)
            else -> {
                supportActionBar?.setDisplayHomeAsUpEnabled(true)
                supportActionBar?.show()
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean = navController.navigateUp()

    override fun onBackPressed() {
        super.onBackPressed()
        if (auth.currentUser == null)
            finish()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.item_logout -> auth.signOut()
            R.id.item_about -> openAboutPage()
            else -> return false
        }
        return true
    }

    private fun initUI() {
        setSupportActionBar(binding.materialToolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        navController.addOnDestinationChangedListener(this)
    }

    private fun  openAboutPage() {
        val url = getString(R.string.project_url)
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        startActivity(intent)
    }

}
