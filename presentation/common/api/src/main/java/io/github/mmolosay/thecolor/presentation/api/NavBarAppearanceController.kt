package io.github.mmolosay.thecolor.presentation.api

import io.github.mmolosay.thecolor.utils.CoroutineScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.launch
import java.util.Optional

/**
 * Controller of navigation bar's appearance.
 * It supports "layering" appearances, so that if the latest appearance is cancelled,
 * then the one that was before it will be used (as when going back to previous screen).
 */
class NavBarAppearanceController(
    private val coroutineScope: CoroutineScope,
) : NavBarAppearanceStack {

    private val appearanceStack = mutableListOf<NavBarAppearance.WithTag>()
    private val subControllers = mutableListOf<NavBarAppearanceController>()
    private var subControllersCollectionJob: Job? = null

    private val _appearanceFlow = MutableStateFlow<NavBarAppearance.WithTag?>(null)
    val appearanceFlow = _appearanceFlow.asStateFlow()

    override fun push(appearance: NavBarAppearance.WithTag) {
        appearanceStack += appearance
        emitLatestAppearance()
    }

    override fun peel() {
        appearanceStack.removeLastOrNull()
        emitLatestAppearance()
    }

    override fun remove(tag: Any) {
        val indexOfLatestWithTag = appearanceStack.indexOfLast { it.tag == tag }
        if (indexOfLatestWithTag != -1) {
            appearanceStack.removeAt(indexOfLatestWithTag)
        }
        emitLatestAppearance()
    }

    override fun clear() {
        appearanceStack.clear()
        emitLatestAppearance()
    }

    // TODO: add unit tests
    override fun subStack(): NavBarAppearanceStack {
        val newSubController = NavBarAppearanceController(
            coroutineScope = CoroutineScope(parent = coroutineScope),
        )
        subControllers += newSubController
        val mergedFlowsOfSubControllers: Flow<NavBarAppearance.WithTag?> =
            subControllers.map { it.appearanceFlow }
                .toTypedArray()
                .let { merge(*it) }
        subControllersCollectionJob?.cancel()
        subControllersCollectionJob = coroutineScope.launch {
            collectMergedFlowsOfSubControllers(mergedFlowsOfSubControllers)
        }
        return newSubController
    }

    private fun emitLatestAppearance() {
        val latest = appearanceStack.lastOrNull()
        _appearanceFlow.value = latest
    }

    private suspend fun collectMergedFlowsOfSubControllers(
        mergedFlows: Flow<NavBarAppearance.WithTag?>,
    ) {
        mergedFlows.collect { subControllerAppearance ->
            val ownAppearance = appearanceFlow.value
            val newAppearance = if (ownAppearance != null && subControllerAppearance == null) {
                ownAppearance
            } else {
                subControllerAppearance
            }
            _appearanceFlow.value = newAppearance
        }
    }
}

/**
 * An interface to share with UI with only functionality that UI needs.
 * Interface Segregation Principle.
 *
 * @see NavBarAppearanceController
 */
interface NavBarAppearanceStack {

    /**
     * Adds an [appearance] to the top of the stack.
     */
    fun push(appearance: NavBarAppearance.WithTag)

    /**
     * Removes an appearance from the top of the stack.
     * Does nothing if there's not a single appearance in the stack.
     */
    fun peel()

    /**
     * Removes first appearance with a [tag] searching top to bottom.
     * Does nothing if there's no such appearance in the stack.
     */
    fun remove(tag: Any)

    /**
     * Removes all appearances from the stack.
     */
    fun clear()

    /**
     * Creates a [NavBarAppearanceStack] that can be used by a component to [push] and [peel]
     * appearances safely.
     * For example, once component is removed, and it wants to remove all appearances it has [push]ed,
     * it may do so using [clear].
     */
    fun subStack(): NavBarAppearanceStack
}

/**
 * Adds an [appearance] with `null` tag to the top of the stack.
 */
fun NavBarAppearanceStack.push(appearance: NavBarAppearance) {
    val tagged = appearance withTag null
    push(tagged)
}

/**
 * A "no-operation" implementation of [NavBarAppearanceStack].
 * Useful in Compose Previews.
 */
object NoopNavBarAppearanceStack : NavBarAppearanceStack {
    override fun push(appearance: NavBarAppearance.WithTag) {}
    override fun peel() {}
    override fun remove(tag: Any) {}
    override fun clear() {}
    override fun subStack(): NavBarAppearanceStack = NoopNavBarAppearanceStack
}

/**
 * Platform-agnostic model of navigation bar's appearance.
 *
 * @param color a color integer in `ARGB` format.
 * @param useLightTintForControls whether the controls should be light to contrast against dark [color].
 */
data class NavBarAppearance(
    val color: Optional<Int>,
    val useLightTintForControls: Optional<Boolean>,
) {

    data class WithTag(
        val appearance: NavBarAppearance,
        val tag: Any?,
    )
}

/**
 * A builder function for [NavBarAppearance] with values by default.
 */
fun navBarAppearance(
    color: Optional<Int> = Optional.empty(),
    useLightTintForControls: Optional<Boolean> = Optional.empty(),
) =
    NavBarAppearance(
        color = color,
        useLightTintForControls = useLightTintForControls,
    )

infix fun NavBarAppearance.withTag(tag: Any?): NavBarAppearance.WithTag =
    NavBarAppearance.WithTag(appearance = this, tag = tag)