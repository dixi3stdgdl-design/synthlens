package com.synthlens.app.viewmodel

import android.app.Application
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.synthlens.app.data.SynthRepository

@Composable
actual fun createSynthViewModel(): SynthViewModel {
    val context = LocalContext.current
    return viewModel(factory = object : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            val repository = SynthRepository.getInstance(context.applicationContext as Application)
            @Suppress("UNCHECKED_CAST")
            return SynthViewModel(repository) as T
        }
    })
}
