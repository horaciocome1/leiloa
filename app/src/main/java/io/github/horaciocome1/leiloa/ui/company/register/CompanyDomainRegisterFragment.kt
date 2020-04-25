package io.github.horaciocome1.leiloa.ui.company.register

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import io.github.horaciocome1.leiloa.R
import io.github.horaciocome1.leiloa.databinding.FragmentCompanyDomainRegisterBinding

/**
 * This fragment is responsible for handling the registering of new company domains
 */
class CompanyDomainRegisterFragment : Fragment() {

    private lateinit var binding: FragmentCompanyDomainRegisterBinding

    private val viewModel: CompanyDomainRegisterViewModel by lazy {
        ViewModelProvider(this)[CompanyDomainRegisterViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
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
    }

    private fun alertIfEmpty(companyDomain: String) {
        binding.companyDomainTextInputLayout.error =
            if (companyDomain.isNotBlank())
                null
            else
                getString(R.string.cannot_be_blank)
    }

    private fun register(view: View) {
        view.isEnabled = false
        binding.progressBar.visibility = View.VISIBLE
        lifecycleScope.launchWhenStarted {
            val isSuccessful = viewModel.registerDomainAsync(view)
                .await()
            if (!isSuccessful)
                binding.companyDomainTextInputLayout.error =
                    getString(R.string.domain_is_not_available)
            binding.progressBar.visibility = View.GONE
            view.isEnabled = true
        }
    }

}
