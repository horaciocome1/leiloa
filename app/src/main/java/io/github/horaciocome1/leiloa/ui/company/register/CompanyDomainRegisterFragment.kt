package io.github.horaciocome1.leiloa.ui.company.register

import android.os.Bundle
import android.text.InputFilter
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.google.firebase.analytics.FirebaseAnalytics
import io.github.horaciocome1.leiloa.R
import io.github.horaciocome1.leiloa.databinding.FragmentCompanyDomainRegisterBinding
import kotlinx.coroutines.ExperimentalCoroutinesApi

/**
 * This fragment is responsible for handling the registering of new company domains
 */
class CompanyDomainRegisterFragment : Fragment() {

    companion object {

        private const val ANALYTICS_ITEM_ID = "registration"
        private const val ANALYTICS_ITEM_NAME = "Registering company"
        private const val ANALYTICS_CONTENT_TYPE = "registration"

    }

    private lateinit var binding: FragmentCompanyDomainRegisterBinding

    private val viewModel: CompanyDomainRegisterViewModel by lazy {
        ViewModelProvider(this)[CompanyDomainRegisterViewModel::class.java]
    }

    private val analytics: FirebaseAnalytics by lazy {
        FirebaseAnalytics.getInstance(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCompanyDomainRegisterBinding
            .inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.viewModel = viewModel
        binding.registerButton.setOnClickListener(this::register)
    }

    override fun onStart() {
        super.onStart()
        viewModel.companyDomain.observe(this, Observer { alertIfEmpty(it) })
        updateMaxTextLength()
    }

    private fun alertIfEmpty(companyDomain: String) {
        binding.companyDomainTextInputLayout.error =
            if (companyDomain.isNotBlank())
                null
            else
                getString(R.string.cannot_be_blank)
    }

    private fun register(view: View) {
        if (viewModel.companyDomain.value.isNullOrBlank())
            return
        view.isEnabled = false
        binding.progressBar.visibility = View.VISIBLE
        lifecycleScope.launchWhenStarted {
            val isSuccessful = viewModel.registerDomainAsync(view)
                .await()
            if (!isSuccessful)
                binding.companyDomainTextInputLayout.error =
                    getString(R.string.domain_is_not_available)
            else
                logEvent()
            binding.progressBar.visibility = View.GONE
            view.isEnabled = true
        }
    }

    private fun updateMaxTextLength() = lifecycleScope.launchWhenStarted {
        val maxLength = viewModel.retrieveCompanyDomainMaxLengthAsync()
            .await()
            .toInt()
        binding.companyDomainTextInputLayout.counterMaxLength = maxLength
        binding.companyDomainTextInputLayout.editText?.filters = arrayOf(
            InputFilter.LengthFilter(maxLength)
        )
    }

    private fun logEvent() = lifecycleScope.launchWhenStarted {
        val bundle = Bundle().apply {
            putString(FirebaseAnalytics.Param.ITEM_ID, ANALYTICS_ITEM_ID)
            putString(FirebaseAnalytics.Param.ITEM_NAME, ANALYTICS_ITEM_NAME)
            putString(FirebaseAnalytics.Param.CONTENT_TYPE, ANALYTICS_CONTENT_TYPE)
        }
        analytics.logEvent(FirebaseAnalytics.Event.SELECT_ITEM, bundle)
    }

}
