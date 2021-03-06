package com.andreyyurko.firstapp.ui.userlist

import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.andreyyurko.firstapp.R
import com.andreyyurko.firstapp.databinding.FragmentUserListBinding
import com.andreyyurko.firstapp.ui.base.BaseFragment
import com.google.android.exoplayer2.util.Log
import dagger.hilt.android.AndroidEntryPoint
import dev.chrisbanes.insetter.applyInsetter
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class UserListFragment : BaseFragment(R.layout.fragment_user_list) {
    companion object {
        val LOG_TAG = "MyFirstLogTag"
    }

    private lateinit var viewModel: UserListViewModel
    //private val viewModel: MainViewModel by viewModels()

    private val viewBinding by viewBinding(FragmentUserListBinding::bind)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this)[UserListViewModel::class.java]

    }

    //@SuppressLint("UnsafeRepeatOnLifecycleDetector")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        //val viewModel : MainViewModel = ViewModelProvider(this)[MainViewModel::class.java]
        Log.d(LOG_TAG, "onCreate()")
        super.onViewCreated(view, savedInstanceState)

        viewBinding.usersRecyclerView.applyInsetter {
            type(statusBars = true) { margin() }
        }
        //setContentView(android.R.layout.fragment_user_list)
        setupRecyclerView()

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.loadUsersActionState.collect { viewState ->
                    Log.d(LOG_TAG, "$viewState")
                    renderViewState(viewState)
                }
            }
        }
    }

    private fun renderViewState(viewState: UserListViewModel.LoadUsersActionState) {
        when(viewState) {
            is UserListViewModel.LoadUsersActionState.Loading -> {
                viewBinding.usersRecyclerView.isVisible = false
                viewBinding.progressBar.isVisible = true
            }
            is UserListViewModel.LoadUsersActionState.Data -> {
                viewBinding.usersRecyclerView.isVisible = true
                (viewBinding.usersRecyclerView.adapter as UserAdapter).apply {
                    userList = viewState.userList
                    notifyDataSetChanged()
                }
                viewBinding.progressBar.isVisible = false
            }
        }
    }

    private fun setupRecyclerView(): UserAdapter {
        val recyclerView = viewBinding.usersRecyclerView
        recyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        val adapter = UserAdapter()
        recyclerView.adapter = adapter
        val dividerImage = resources.getDrawable(R.drawable.divider_vertical, context?.theme)
        dividerImage?.let { CustomDividerItemDecoration(it) }?.let {
            recyclerView.addItemDecoration(
                it
            )
        }
        //recyclerView.addItemDecoration(DividerItemDecoration(context, LinearLayoutManager.VERTICAL))
        return adapter
    }
}