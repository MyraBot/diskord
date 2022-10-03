package bot.myra.diskord.gateway.events

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.asCoroutineDispatcher
import org.reflections.Reflections
import java.util.concurrent.ForkJoinPool

object Events {

    val coroutineScope = CoroutineScope(ForkJoinPool.commonPool().asCoroutineDispatcher())

    /**
     * Load listeners by reflection.
     * Finds and loads all listeners in a specific package.
     *
     * @param packageName The package name to search for listeners.
     */
    fun findListeners(packageName: String) {
        Reflections(packageName).getSubTypesOf(EventListener::class.java)
            .mapNotNull { it.kotlin.objectInstance }
            .forEach { it.loadListeners() }
    }

}