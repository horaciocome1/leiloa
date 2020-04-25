package io.github.horaciocome1.leiloa.ui.signin


import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment.findNavController
import androidx.navigation.fragment.findNavController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import io.github.horaciocome1.leiloa.R
import io.github.horaciocome1.leiloa.databinding.FragmentSignInBinding
import io.github.horaciocome1.leiloa.ui.main.MainActivity

class SignInFragment : Fragment(), View.OnClickListener {

    private lateinit var binding: FragmentSignInBinding

    private val auth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }

    private val googleSignInIntent: Intent by lazy {
        val googleSignInOptions = GoogleSignInOptions
            .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        val googleSignInClient = GoogleSignIn
            .getClient(activity as MainActivity, googleSignInOptions)
        googleSignInClient.signInIntent
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSignInBinding
            .inflate(inflater, container, false)
        return  binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.signInButton.setOnClickListener(this)
    }

    override fun onClick(view: View) {
        binding.signInButton.isEnabled = false
        startActivityForResult(googleSignInIntent, RC_SIGN_IN)
        binding.errorTextView.visibility = View.GONE
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            try {
                val task = GoogleSignIn.getSignedInAccountFromIntent(data)
                val account = task.getResult(ApiException::class.java)
                account?.firebaseAuthWithGoogle()
            } catch (exception: ApiException) {
                binding.signInButton.isEnabled = true
                binding.errorTextView.visibility = View.VISIBLE
            }
        }
    }

    private fun GoogleSignInAccount.firebaseAuthWithGoogle() {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener {
                if (it.isSuccessful)
                    return@addOnCompleteListener
                binding.signInButton.isEnabled = true
                binding.errorTextView.visibility = View.VISIBLE
            }
    }

    companion object {

        private const val RC_SIGN_IN = 100

    }

}
