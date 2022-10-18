package bot.myra.diskord.gateway.events.types

abstract class EventAction {
    abstract suspend fun handle()
}