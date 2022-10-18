# Diskord
Diskord is a Discord wrapper written in Kotlin, making use of coroutines and Kotlins async model.

## TODO
- [ ] Make all events `open` classes
- [ ] Name function `fetchX` to show that rest is being used
- [ ] Do not put `Interaction` in `InteractionModifier.kt` - but still make it available to use in myra for finding out the language
- [ ] Use normal Instant class instead of custom time class
- [ ] Use https://kotlinlang.org/docs/type-safe-builders.html#scope-control-dslmarker

## Caching
Diskord has **Rest caching** as well as **Gateway caching**.
> Though note that if you turn of gateway caching the objects won't be in sync anymore since update events are missing!

## Event abstraction
If an even can be interpreted in different ways the event has the following structure

```
    FooEventBroker
          |
          V
    FooBarEvent ─┐
                 ├─ GenericFooEvent
    FooBazEvent ─┘
```

`FooEventBroker`: Event handler which calls `GenericFooEvent`s subclasses  
`GenericFooEvent`: Abstract class which shares common methods across its subclasses

If