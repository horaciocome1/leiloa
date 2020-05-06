package io.github.horaciocome1.leiloa.ui.main

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.Navigation
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.dynamiclinks.ktx.dynamicLinks
import com.google.firebase.ktx.Firebase
import io.github.horaciocome1.leiloa.R
import io.github.horaciocome1.leiloa.databinding.ActivityMainBinding
import io.github.horaciocome1.leiloa.util.myCrashlytics
import kotlinx.coroutines.tasks.await

class MainActivity : AppCompatActivity(), NavController.OnDestinationChangedListener,
    FirebaseAuth.AuthStateListener {

    companion object {

        private const val ANALYTICS_LOG_OUT_ITEM_ID = "logOut"
        private const val ANALYTICS_LOG_OUT_ITEM_NAME = "Logging out"
        private const val ANALYTICS_LOG_OUT_CONTENT_TYPE = "logOut"

        private const val ANALYTICS_ABOUT_ITEM_ID = "openAbout"
        private const val ANALYTICS_ABOUT_ITEM_NAME = "Opening about"
        private const val ANALYTICS_ABOUT_CONTENT_TYPE = "openAbout"

        private const val ANALYTICS_SCREEN_NAME_UNKNOWN = "unknownScreen"
        private const val ANALYTICS_PROPERTY_USER_NAME = "userName"
        private const val ANALYTICS_PROPERTY_USER_EMAIL = "userName"

    }

    private lateinit var binding: ActivityMainBinding

    private val auth: FirebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }

    private val navController: NavController by lazy {
        Navigation.findNavController(this, R.id.mainNavHost)
    }

    private val analytics: FirebaseAnalytics by lazy {
        FirebaseAnalytics.getInstance(this)
    }

    /**
     * holds data taken from dynamic link
     * if it is not null and auth state changed to logged
     *  => then user is coming from a dynamic link
     *  => and should be redirected after logging in
     */
    private var productBundle: Bundle? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.MyTheme)
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        handleDynamicLink()
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
        when {
            auth.currentUser == null ->
                navController.navigate(R.id.destination_sign_in)
            productBundle != null -> {
                navController.navigate(R.id.destination_product, productBundle!!)
                productBundle = null
            }
            navController.currentDestination?.id == R.id.destination_sign_in ->
                navController.navigateUp()
            else -> setAnalyticsUser(auth.currentUser!!)
        }
    }

    override fun onDestinationChanged(
        controller: NavController,
        destination: NavDestination,
        arguments: Bundle?
    ) {
        logScreenTrack(destinationId = destination.id)
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
            R.id.item_logout -> signOut()
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

    private fun signOut() {
        auth.signOut()
        logEvent(
            itemId = ANALYTICS_LOG_OUT_ITEM_ID,
            itemName = ANALYTICS_LOG_OUT_ITEM_NAME,
            contentType = ANALYTICS_LOG_OUT_CONTENT_TYPE
        )
    }

    private fun  openAboutPage() {
        val url = getString(R.string.project_url)
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        startActivity(intent)
        logEvent(
            itemId = ANALYTICS_ABOUT_ITEM_ID,
            itemName = ANALYTICS_ABOUT_ITEM_NAME,
            contentType = ANALYTICS_ABOUT_CONTENT_TYPE
        )
    }

    private fun handleDynamicLink() =
        lifecycleScope.launchWhenStarted {
            val link = Firebase.dynamicLinks.getDynamicLink(intent)
                .await()
                ?.link
                ?: return@launchWhenStarted
            val companyDomain = link.getQueryParameter("d")
            val productId = link.getQueryParameter("p")
            productBundle = Bundle().apply {
                putString("company_domain", companyDomain)
                putString("product_id", productId)
            }
            if (auth.currentUser != null) {
                navController.navigate(R.id.destination_product, productBundle!!)
                productBundle = null
            }
        }

    private fun logEvent(
        itemId: String,
        itemName: String,
        contentType: String
    ) = lifecycleScope.launchWhenStarted {
        val bundle = Bundle().apply {
            putString(FirebaseAnalytics.Param.ITEM_ID, itemId)
            putString(FirebaseAnalytics.Param.ITEM_NAME, itemName)
            putString(FirebaseAnalytics.Param.CONTENT_TYPE, contentType)
        }
        analytics.logEvent(FirebaseAnalytics.Event.SELECT_ITEM, bundle)
    }

    private fun logScreenTrack(destinationId: Int) =
        lifecycleScope.launchWhenStarted {
            val screenName = when (destinationId) {
                R.id.destination_sign_in ->
                    getString(R.string.sign_in)
                R.id.destination_company_domain ->
                    getString(R.string.company_domain)
                R.id.destination_company_domain_register ->
                    getString(R.string.register) +
                            " " +
                            getString(R.string.company_domain)
                R.id.destination_product_id ->
                    getString(R.string.product_id)
                R.id.destination_product_id_register ->
                    getString(R.string.register) +
                            " " +
                            getString(R.string.company_domain)
                R.id.destination_product ->
                    getString(R.string.product)
                else -> ANALYTICS_SCREEN_NAME_UNKNOWN
            }
            analytics.setCurrentScreen(this@MainActivity, screenName, null)
        }

    private fun setAnalyticsUser(user: FirebaseUser) =
        lifecycleScope.launchWhenStarted {
            analytics.setUserId(user.uid)
            analytics.setUserProperty(ANALYTICS_PROPERTY_USER_NAME, user.displayName)
            analytics.setUserProperty(ANALYTICS_PROPERTY_USER_EMAIL, user.email)
        }

}
