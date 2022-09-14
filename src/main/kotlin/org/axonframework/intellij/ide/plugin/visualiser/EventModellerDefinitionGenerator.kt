package org.axonframework.intellij.ide.plugin.visualiser

import uk.co.skipoles.eventmodeller.definition.ModelDefinitionEntry
import uk.co.skipoles.eventmodeller.definition.ModelDefinitionItem
import uk.co.skipoles.eventmodeller.definition.ModelDefinitionItemType

class EventModellerDefinitionGenerator(private val model: AxonProjectModel) {

  fun build(initialCommandName: String, exclude: List<String>): AxonEventModelDefinition {
    val initialCommand =
        model.findCommand(initialCommandName)
            ?: throw java.lang.IllegalArgumentException(
                "Unknown initial command: $initialCommandName")

    val state = EventModelState()
    addCommandAndChildren(state, initialCommand, exclude)
    return AxonEventModelDefinition(state.allEntries())
  }

  private fun addCommandAndChildren(
      state: EventModelState,
      command: Command,
      exclude: List<String>,
  ) {
    if (state.alreadyProcessedCommand(command)) return

    val commandItem = state.getOrCreateForCommand(command)

    // Add events that result from handling this command
    val events = command.handledBy.events.mapNotNull { model.findEvent(it.name) }
    events.forEach {
      if (!exclude.contains(it.name)) {
        val aggregate =
            when (command.handledBy.type) {
              HandlerType.Aggregate -> command.handledBy.name
              HandlerType.AggregateEventSource -> command.handledBy.name
              else -> null
            }
        val targetEventItem = state.getOrCreateForEvent(it, aggregate)
        state.addEntry(commandItem, targetEventItem)
        addEventAndChildren(state, it, aggregate, exclude)
      }
    }

    // Add additional commands created by this command
    val commands = command.handledBy.commands.mapNotNull { model.findCommand(it.name) }
    commands.forEach {
      if (!exclude.contains(it.name)) {
        val targetCommandItem = state.getOrCreateForCommand(it)
        state.addEntry(commandItem, targetCommandItem)
        addCommandAndChildren(state, it, exclude)
      }
    }
  }

  private fun addEventAndChildren(
      state: EventModelState,
      event: Event,
      eventAggregate: String?,
      exclude: List<String>
  ) {
    val eventItem = state.getOrCreateForEvent(event, eventAggregate)

    for (handler in event.handledBy) {
      if (handler.isViewModel() && !exclude.contains(handler.name)) {
        val targetViewItem = state.getOrCreateForView(handler.name)
        state.addEntry(eventItem, targetViewItem)
      } else {
        val (item, aggregate) =
            if (handler.isSaga() && !exclude.contains(handler.name)) {
              val targetSagaItem = state.getOrCreateForSaga(handler.name)
              state.addEntry(eventItem, targetSagaItem)
              Pair(targetSagaItem, null)
            } else Pair(eventItem, eventAggregate)

        handler.commands.forEach {
          val nextCommand = model.findCommand(it.name)
          if (nextCommand != null && !exclude.contains(nextCommand.name)) {
            val targetCommandItem = state.getOrCreateForCommand(nextCommand)
            state.addEntry(item, targetCommandItem)
            addCommandAndChildren(state, nextCommand, exclude)
          }
        }

        handler.events.forEach {
          val nextEvent = model.findEvent(it.name)
          if (nextEvent != null && !exclude.contains(nextEvent.name)) {
            val targetEventItem = state.getOrCreateForEvent(nextEvent, aggregate)
            state.addEntry(item, targetEventItem)
            addEventAndChildren(state, nextEvent, aggregate, exclude)
          }
        }
      }
    }
  }

  class EventModelState {

    private val entries = mutableListOf<ModelDefinitionEntry>()
    private val items = mutableSetOf<ModelDefinitionItem>()

    fun alreadyProcessedCommand(command: Command): Boolean =
        entries.find {
          it.item.type == ModelDefinitionItemType.Command && it.item.name == command.name
        } != null

    fun getOrCreateForCommand(command: Command): ModelDefinitionItem {
      val item =
          items.find { it.type == ModelDefinitionItemType.Command && it.name == command.name }
      return if (item == null) {
        val newItem = ModelDefinitionItem(ModelDefinitionItemType.Command, command.name)
        items.add(newItem)
        newItem
      } else item
    }

    fun getOrCreateForEvent(event: Event, aggregate: String?): ModelDefinitionItem {
      val item = items.find { it.type == ModelDefinitionItemType.Event && it.name == event.name }
      return if (item == null) {
        val newItem = ModelDefinitionItem(ModelDefinitionItemType.Event, event.name, aggregate)
        items.add(newItem)
        newItem
      } else item
    }

    fun getOrCreateForView(name: String): ModelDefinitionItem {
      val item = items.find { it.type == ModelDefinitionItemType.View && it.name == name }
      return if (item == null) {
        val newItem = ModelDefinitionItem(ModelDefinitionItemType.View, name)
        items.add(newItem)
        newItem
      } else item
    }

    fun getOrCreateForSaga(name: String): ModelDefinitionItem {
      val item = items.find { it.type == ModelDefinitionItemType.Saga && it.name == name }
      return if (item == null) {
        val newItem = ModelDefinitionItem(ModelDefinitionItemType.Saga, name)
        items.add(newItem)
        newItem
      } else item
    }

    fun addEntry(from: ModelDefinitionItem, to: ModelDefinitionItem) =
        entries.add(ModelDefinitionEntry(from, to))

    fun allEntries(): List<ModelDefinitionEntry> = entries.toList()
  }
}

data class AxonEventModelDefinition(val entries: List<ModelDefinitionEntry>) {
  fun commands() =
      entries
          .filter { it.item.type == ModelDefinitionItemType.Command }
          .map { it.item.name }
          .sorted()

  fun findCommandEntries(commandName: String) =
      entries.filter {
        it.item.type == ModelDefinitionItemType.Command && it.item.name == commandName
      }
}

// data class ModelDefinitionEntry(
//    val item: ModelDefinitionItem,
//    val target: ModelDefinitionItem,
//    val aggregateName: String? = null,
//    val context: ModelDefinitionContext? = determineContext(item, aggregateName)
// ) {
//  fun getOrDeriveContext(): ModelDefinitionContext =
//      context
//          ?: when (item.type) {
//            ModelDefinitionItemType.Command -> TIME_LINE_CONTEXT
//            ModelDefinitionItemType.View -> TIME_LINE_CONTEXT
//            ModelDefinitionItemType.Saga -> PROCESS_CONTEXT
//            ModelDefinitionItemType.Event ->
//                if (aggregateName != null) aggregateContext(aggregateName)
//                else EVENT_HANDLER_CONTEXT
//          }
//
//  override fun toString(): String =
//      if (context != null) "$context :: $item -> $target" else "$item -> $target"
//
//  companion object {
//    val TIME_LINE_CONTEXT = ModelDefinitionContext(ModelDefinitionContextType.Timeline,
// "Timeline")
//    val PROCESS_CONTEXT = ModelDefinitionContext(ModelDefinitionContextType.Saga, "Process")
//    val EVENT_HANDLER_CONTEXT =
//        ModelDefinitionContext(ModelDefinitionContextType.EventHandler, "EventHandlers")
//
//    fun aggregateContext(aggregateName: String) =
//        ModelDefinitionContext(ModelDefinitionContextType.Aggregate, aggregateName)
//
//    fun determineContext(item: ModelDefinitionItem, aggregateName: String?) =
//        if (item.type == ModelDefinitionItemType.Event && aggregateName != null)
//            aggregateContext(aggregateName)
//        else null
//
//    fun isDefaultContext(context: ModelDefinitionContext) =
//        context == TIME_LINE_CONTEXT ||
//            context == PROCESS_CONTEXT ||
//            context == EVENT_HANDLER_CONTEXT
//  }
// }
//
// enum class ModelDefinitionItemType(val symbol: String) {
//  Command("c"),
//  Event("e"),
//  View("v"),
//  Saga("s")
// }
//
// data class ModelDefinitionItem(
//    val type: ModelDefinitionItemType,
//    val name: String,
//    val aggregate: Boolean = false
// ) {
//  override fun toString(): String = "[${type.symbol}] $name"
// }
//
// enum class ModelDefinitionContextType(val symbol: String) {
//  Saga("s"),
//  Timeline("t"),
//  EventHandler("eh"),
//  Aggregate("a")
// }
//
// data class ModelDefinitionContext(val type: ModelDefinitionContextType, val name: String) {
//  override fun toString(): String = "[${type.symbol}] $name"
// }
