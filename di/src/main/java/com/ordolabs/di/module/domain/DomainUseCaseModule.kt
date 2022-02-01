package com.ordolabs.di.module.domain

import com.ordolabs.domain.repository.IColorRemoteRepository
import com.ordolabs.domain.repository.IColorValidatorRepository
import com.ordolabs.domain.usecase.local.ValidateColorHexBaseUseCase
import com.ordolabs.domain.usecase.local.ValidateColorHexUseCase
import com.ordolabs.domain.usecase.local.ValidateColorRgbBaseUseCase
import com.ordolabs.domain.usecase.local.ValidateColorRgbUseCase
import com.ordolabs.domain.usecase.remote.GetColorDetailsBaseUseCase
import com.ordolabs.domain.usecase.remote.GetColorDetailsUseCase
import com.ordolabs.domain.usecase.remote.GetColorSchemeBaseUseCase
import com.ordolabs.domain.usecase.remote.GetColorSchemeUseCase
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class DomainUseCaseModule {

    @Provides
    @Singleton
    fun provideValidateColorHexUseCase(
        repository: IColorValidatorRepository
    ): ValidateColorHexBaseUseCase =
        ValidateColorHexUseCase(repository)

    @Provides
    @Singleton
    fun provideValidateColorRgbUseCase(
        repository: IColorValidatorRepository
    ): ValidateColorRgbBaseUseCase =
        ValidateColorRgbUseCase(repository)

    @Provides
    @Singleton
    fun provideGetColorDetailsUseCase(
        repository: IColorRemoteRepository
    ): GetColorDetailsBaseUseCase =
        GetColorDetailsUseCase(repository)

    @Provides
    @Singleton
    fun provideGetColorSchemeUseCase(
        repository: IColorRemoteRepository
    ): GetColorSchemeBaseUseCase =
        GetColorSchemeUseCase(repository)

    companion object {
        // TODO: get rid of?
        const val VALIDATE_COLOR_HEX_USE_CASE = "validate_color_hex_use_case"
        const val VALIDATE_COLOR_RGB_USE_CASE = "validate_color_rgb_use_case"

        const val GET_COLOR_DETAILS_USE_CASE = "get_color_details_use_case"
        const val GET_COLOR_SCHEME_USE_CASE = "get_color_scheme_use_case"
    }
}