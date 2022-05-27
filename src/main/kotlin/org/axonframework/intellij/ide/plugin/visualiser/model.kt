package org.axonframework.intellij.ide.plugin.visualiser

import java.awt.Color

data class AxonProjectModel(
    val commands: List<Command>,
    val events: List<Event>,
    val queries: List<Query>
) {
  fun commandNames(): Set<String> = commands.map { it.name }.toSortedSet()

  fun findCommand(name: String) = commands.find { it.name == name }

  fun eventNames(): Set<String> = events.map { it.name }.toSortedSet()

  fun findEvent(name: String) = events.find { it.name == name }

  fun viewNames(): Set<String> =
      events.flatMap { it.handledBy }.filter { it.isViewModel() }.map { it.name }.toSortedSet()

  fun eventsReferencingView(view: String): List<String> =
      events.filter { event -> event.handledBy.find { it.name == view } != null }.map { it.name }
}

interface Message {
  val name: String
  val shortName: String
}

fun makeShortName(name: String): String {
  val className = name.split('.').last()
  val messageName =
      if (className.endsWith("Command")) className.dropLast(7)
      else if (className.endsWith("Event")) className.dropLast(5)
      else if (className.endsWith("Query")) className.dropLast(5) else className
  return messageName
      .split("(?<!(^|[A-Z]))(?=[A-Z])|(?<!^)(?=[A-Z][a-z])".toRegex())
      .joinToString(" ")
}

data class Command(
    override val name: String,
    override val shortName: String = makeShortName(name),
    val createdBy: CommandCreatorDetail,
    val handledBy: CommandHandlerDetail
) : Message

data class CommandCreatorDetail(val names: Set<String>)

enum class HandlerType {
  Aggregate,
  CommandHandler,
  AggregateEventSource,
  EventHandler,
  Saga
}

data class CommandHandlerDetail(
    val type: HandlerType,
    val name: String,
    val shortName: String = makeShortName(name),
    val events: Set<EventReference>,
    val commands: Set<CommandReference>
)

data class Event(
    override val name: String,
    override val shortName: String = makeShortName(name),
    val createdBy: EventCreatorDetail,
    val handledBy: Set<EventHandlerDetail>
) : Message

data class EventCreatorDetail(val names: Set<String>)

data class EventHandlerDetail(
    val type: HandlerType,
    val name: String,
    val shortName: String = makeShortName(name),
    val events: Set<EventReference>,
    val commands: Set<CommandReference>
) {
  fun isViewModel() = type == HandlerType.EventHandler && events.isEmpty() && commands.isEmpty()
}

data class Query(
    override val name: String,
    override val shortName: String = makeShortName(name),
    val handledBy: QueryHandlerDetail
) : Message

enum class QueryHandlerType {
  QueryHandler
}

data class QueryHandlerDetail(
    val type: QueryHandlerType,
    val name: String,
    val events: Set<EventReference>,
    val commands: Set<CommandReference>
)

data class EventReference(val name: String)

data class CommandReference(val name: String)

data class AxonEventModel(
    val initialCommand: String,
    val postIts: List<PostIt>,
    val links: Map<PostIt, List<PostIt>>
) {
  fun rows() =
      postIts.map { it.swimLane }.filter { it.type == SwimLaneType.Aggregate }.toSet().size + 2
  fun columns() = postIts.maxOf { it.columnIndex } + 1
}

enum class SwimLaneType {
  Timeline,
  Events,
  Aggregate
}

data class SwimLane(
    val type: SwimLaneType,
    val rowIndex: Int,
    val name: String? = null,
    val shortName: String? = if (name != null) makeShortName(name) else null
)

interface PostIt {
  val swimLane: SwimLane
  val columnIndex: Int
  val text: String
  val color: Color
}

data class CommandPostIt(
    val command: Command,
    override val swimLane: SwimLane,
    override val columnIndex: Int,
    override val text: String = command.shortName,
    override val color: Color = Color(0x56, 0xC4, 0xE8)
) : PostIt

data class EventPostIt(
    val event: Event,
    override val swimLane: SwimLane,
    override val columnIndex: Int,
    override val text: String = event.shortName,
    override val color: Color = Color(0xFA, 0xA4, 0x57)
) : PostIt

data class ViewPostIt(
    val name: String,
    override val swimLane: SwimLane,
    override val columnIndex: Int,
    override val text: String = makeShortName(name),
    override val color: Color = Color(0xD0, 0xE0, 0x68)
) : PostIt

data class LabelPostIt(
    override val swimLane: SwimLane,
    override val columnIndex: Int,
    override val text: String,
    override val color: Color = Color(0xFF, 0xE4, 0x76)
) : PostIt
