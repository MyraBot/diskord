package bot.myra.diskord.gateway.events

import kotlin.reflect.KClass

/**
 * Annotation used to determine to which event the corresponding function has to listen.
 *
 * @property event The event to listen to.
 */
annotation class ListenTo(val event: KClass<out Event>)
