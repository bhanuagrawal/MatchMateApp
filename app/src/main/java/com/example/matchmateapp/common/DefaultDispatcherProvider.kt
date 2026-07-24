package com.example.matchmateapp.common

import javax.inject.Inject
import kotlinx.coroutines.Dispatchers

class DefaultDispatcherProvider @Inject constructor() : DispatcherProvider {
    override val main = Dispatchers.Main
    override val io = Dispatchers.IO
    override val default = Dispatchers.Default
}
