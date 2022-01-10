package com.ordolabs.feature_home.ui.fragment.colordata.base

/**
 * Interface for fragment that populates data [D] in its views.
 */
interface IColorDataFragment<D> {

    fun populateViews(data: D)
}