package bot.myra.diskord.gateway.events

import bot.myra.diskord.common.Diskord
import kotlin.reflect.KClass
import kotlin.reflect.KClassifier
import kotlin.reflect.KFunction
import kotlin.reflect.full.declaredFunctions
import kotlin.reflect.full.hasAnnotation
import kotlin.reflect.full.isSuperclassOf
import kotlin.reflect.full.valueParameters

interface EventListener {

    fun findEventFunction(): List<KFunction<*>> {
        return this::class.declaredFunctions
            .filter { it.hasAnnotation<ListenTo>() }
            .filter {
                val klass: KClassifier = it.valueParameters.firstOrNull()?.type?.classifier ?: return@filter true
                Event::class.isSuperclassOf(klass as KClass<*>)
            }
    }

    /**
     * Loads all functions of this listener in [Diskord.listeners].
     */
    fun loadListeners() {
        Diskord.listeners[this] = findEventFunction()
    }

}