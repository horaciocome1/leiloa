package io.github.horaciocome1.leiloa.ui.product

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import io.github.horaciocome1.leiloa.R
import io.github.horaciocome1.leiloa.data.company.Company
import io.github.horaciocome1.leiloa.databinding.FragmentProductBinding
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect

/**
 * Fragment responsible for displaying the product, listing participants
 * and handling participants interactions
 */
class ProductFragment : Fragment(), CompoundButton.OnCheckedChangeListener {

    private lateinit var binding: FragmentProductBinding

    private val viewModel: ProductViewModel by lazy {
        ViewModelProvider(this)[ProductViewModel::class.java]
    }

    private val recyclerViewAdapter: ParticipantsAdapter by lazy {
        ParticipantsAdapter()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentProductBinding
            .inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.viewModel = viewModel
        initUI()
    }

    @ExperimentalCoroutinesApi
    override fun onStart() {
        super.onStart()
        setArgsToViewModel()
        binding.company = Company(id = viewModel.companyDomain)
        collectProduct()
        collectParticipants()
        enableSwitch()
    }

    override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
        buttonView?.isEnabled = false
        lifecycleScope.launchWhenStarted {
            buttonView?.text = if (isChecked) {
                viewModel.setActiveStatusAsync()
                    .await()
                getString(R.string.active)
            } else {
                viewModel.setActiveStatusAsync(false)
                    .await()
                getString(R.string.inactive)
            }
            buttonView?.isEnabled = true
        }
    }

    private fun initUI() {
        binding.contentInclude.recyclerView.adapter = recyclerViewAdapter
        binding.contentInclude.recyclerView.layoutManager = LinearLayoutManager(context)
        binding.contentInclude.include.isActiveSwitch.setOnCheckedChangeListener(this)
    }

    private fun setArgsToViewModel() = arguments?.let {
        val args = ProductFragmentArgs.fromBundle(it)
        viewModel.companyDomain = args.companyDomain
        viewModel.productId = args.productId
        binding.viewModel = viewModel
    }

    @ExperimentalCoroutinesApi
    private fun collectProduct() =
        lifecycleScope.launchWhenStarted {
            viewModel.watchProduct().collect {
                viewModel.productActive = it.active
                binding.viewModel = viewModel
                binding.product = it
            }
        }

    @ExperimentalCoroutinesApi
    private fun collectParticipants() =
        lifecycleScope.launchWhenStarted {
            viewModel.watchParticipants().collect {
                recyclerViewAdapter.participants = it
            }
        }

    private fun enableSwitch() =
        lifecycleScope.launchWhenStarted {
            binding.contentInclude.include.isActiveSwitch.isEnabled =
                viewModel.doDomainBelongToMeAsync()
                    .await()
        }

    /**
     * Preciso de progress bars em cima dos buttons 16 dp
     */

}
