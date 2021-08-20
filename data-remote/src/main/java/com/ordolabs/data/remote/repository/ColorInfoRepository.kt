package com.ordolabs.data.remote.repository

import com.ordolabs.data.remote.api.TheColorApiService
import com.ordolabs.domain.model.ColorHex
import com.ordolabs.domain.repository.IColorInfoRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class ColorInfoRepository(
    private val api: TheColorApiService
) : IColorInfoRepository {

    override suspend fun fetchColorInfo(color: String): Flow<ColorHex> = flow {
        // TODO: implement
    }
}