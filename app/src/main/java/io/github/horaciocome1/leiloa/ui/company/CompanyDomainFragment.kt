package io.github.horaciocome1.leiloa.ui.company

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import io.github.horaciocome1.leiloa.R
import io.github.horaciocome1.leiloa.databinding.FragmentCompanyDomainBinding

/**
 * fragment responsible for handling the informing domain step
 * it checks if domain is real
 * and navigates to the next step if it is
 */
class CompanyDomainFragment : Fragment() {

    private lateinit var binding: FragmentCompanyDomainBinding

    private val viewModel: CompanyDomainViewModel by lazy {
        ViewModelProvider(this)[CompanyDomainViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentCompanyDomainBinding
            .inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.viewModel = viewModel
        binding.nextButton.setOnClickListener(this::navigate)
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

    private fun navigate(view: View) {
        if (viewModel.companyDomain.value == null)
            return
        view.isEnabled = false
        binding.progressBar.visibility = View.VISIBLE
        lifecycleScope.launchWhenStarted {
            val isSuccessful = viewModel.navigateToProductIdAsync(view)
                .await()
            if (!isSuccessful)
                binding.companyDomainTextInputLayout.error =
                    getString(R.string.domain_is_not_real)
            binding.progressBar.visibility = View.GONE
            view.isEnabled = true
        }
    }

}
