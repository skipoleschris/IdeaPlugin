package org.axonframework.intellij.ide.plugin.visualiser

class EventModelBuilder(private val structure: AxonProjectModel) {

  fun build(commandName: String, exclude: List<String>): AxonEventModel {
    val initialCommand =
        structure.findCommand(commandName)
            ?: throw java.lang.IllegalArgumentException("Unknown initial command: $commandName")

    val state = EventModelState()
    addCommandAndChildren(state, initialCommand, exclude)
    return AxonEventModel(state.allPostIts(), state.allLinks())
  }

  private fun addCommandAndChildren(
      state: EventModelState,
      command: Command,
      exclude: List<String>,
      linkFrom: PostIt? = null
  ) {
    if (state.updateExistingCommandWithNewLink(command, linkFrom)) return

    // New postIt
    val postIt = state.addNewCommandPostIt(command, linkFrom)

    // Add events that result from handling this command
    val events = command.handledBy.events.mapNotNull { structure.findEvent(it.name) }
    events.forEach {
      if (!exclude.contains(it.name))
          addEventAndChildren(
              state, it, command.handledBy.type, command.handledBy.name, exclude, postIt)
    }

    // Add additional commands created by this command
    val commands = command.handledBy.commands.mapNotNull { structure.findCommand(it.name) }
    commands.forEach {
      if (!exclude.contains(it.name)) addCommandAndChildren(state, it, exclude, postIt)
    }
  }

  private fun addEventAndChildren(
      state: EventModelState,
      event: Event,
      handlerType: HandlerType,
      handlerName: String,
      exclude: List<String>,
      linkFrom: PostIt? = null
  ) {
    if (!state.updateExistingEventWithNewLink(event, linkFrom)) {
      state.addNewEventPostIt(event, handlerType, handlerName, linkFrom)

      for (handler in event.handledBy) {
        if (handler.isViewModel()) {
          if (!state.updateExistingViewWithNewLink(handler.name, event)) {
            if (!exclude.contains(handler.name)) state.addNewViewPostIt(handler.name, event)
          }
        } else {
          handler.commands.forEach {
            val nextCommand = structure.findCommand(it.name)
            if (nextCommand != null) {
              if (!exclude.contains(nextCommand.name))
                  addCommandAndChildren(state, nextCommand, exclude, state.findPostIt(event))
            }
          }

          handler.events.forEach {
            val nextEvent = structure.findEvent(it.name)
            if (nextEvent != null) {
              if (!exclude.contains(nextEvent.name))
                  addEventAndChildren(
                      state,
                      nextEvent,
                      handler.type,
                      handler.name,
                      exclude,
                      state.findPostIt(event))
            }
          }
        }
      }
    }
  }

  class EventModelState {
    private val timelineSwimLane = SwimLane(SwimLaneType.Timeline, 1)
    private val eventsSwimLane = SwimLane(SwimLaneType.Events, 2)

    private var currentColumn: Int = 1
    private val postIts: MutableList<PostIt> = mutableListOf()
    private val aggregateSwimLanes: MutableList<SwimLane> = mutableListOf()

    private var links: MutableMap<PostIt, List<PostIt>> = mutableMapOf()

    fun allPostIts(): List<PostIt> =
        aggregateSwimLanes.map { LabelPostIt(it, 0, it.shortName ?: "") } + postIts.toList()

    fun allLinks(): Map<PostIt, List<PostIt>> = links.toMap()

    fun updateExistingCommandWithNewLink(command: Command, linkFrom: PostIt?) =
        updatePostItWithNewLink(findPostIt(command), linkFrom)

    fun addNewCommandPostIt(command: Command, linkFrom: PostIt?): CommandPostIt {
      val postIt = CommandPostIt(command, timelineSwimLane, currentColumn)
      postIts.add(postIt)
      updatePostItWithNewLink(postIt, linkFrom)
      return postIt
    }

    fun updateExistingEventWithNewLink(event: Event, linkFrom: PostIt?) =
        updatePostItWithNewLink(findPostIt(event), linkFrom)

    fun addNewEventPostIt(
        event: Event,
        handlerType: HandlerType,
        handlerName: String,
        linkFrom: PostIt?
    ): EventPostIt {
      val swimLane = swimLaneForHandler(handlerType, handlerName)
      val postIt = EventPostIt(event, swimLane, currentColumn++)
      postIts.add(postIt)
      updatePostItWithNewLink(postIt, linkFrom)
      return postIt
    }

    private fun swimLaneForHandler(type: HandlerType, name: String): SwimLane {
      return if (type == HandlerType.CommandHandler ||
          type == HandlerType.Saga ||
          type == HandlerType.EventHandler)
          eventsSwimLane
      else {
        val existing = aggregateSwimLanes.find { it.name == name }
        return if (existing != null) existing
        else {
          val new = SwimLane(SwimLaneType.Aggregate, 3 + aggregateSwimLanes.size, name)
          aggregateSwimLanes.add(new)
          return new
        }
      }
    }

    fun updateExistingViewWithNewLink(name: String, event: Event) =
        updatePostItWithNewLink(findPostIt(name), findPostIt(event))

    fun addNewViewPostIt(name: String, event: Event): ViewPostIt {
      val linkFrom = findPostIt(event)
      val postIt = ViewPostIt(name, timelineSwimLane, currentColumn++)
      postIts.add(postIt)
      updatePostItWithNewLink(postIt, linkFrom)
      return postIt
    }

    private fun updatePostItWithNewLink(postIt: PostIt?, linkFrom: PostIt?): Boolean =
        if (postIt != null) {
          if (linkFrom != null) {
            val linksFrom = links.getOrDefault(postIt, listOf())
            links[postIt] = linksFrom + linkFrom
          }
          true
        } else false

    private fun findPostIt(command: Command): CommandPostIt? =
        postIts.filterIsInstance<CommandPostIt>().find { it.command == command }

    fun findPostIt(event: Event): EventPostIt? =
        postIts.filterIsInstance<EventPostIt>().find { it.event == event }

    private fun findPostIt(name: String): ViewPostIt? =
        postIts.filterIsInstance<ViewPostIt>().find { it.name == name }
  }
}
