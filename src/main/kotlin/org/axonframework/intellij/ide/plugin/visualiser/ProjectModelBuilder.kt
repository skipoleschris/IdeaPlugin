package org.axonframework.intellij.ide.plugin.visualiser

import com.intellij.openapi.project.Project
import com.intellij.psi.PsiMethod
import org.axonframework.intellij.ide.plugin.api.Handler
import org.axonframework.intellij.ide.plugin.api.MessageCreator
import org.axonframework.intellij.ide.plugin.api.MessageHandlerType
import org.axonframework.intellij.ide.plugin.resolving.handlers.types.CommandHandler
import org.axonframework.intellij.ide.plugin.resolving.handlers.types.EventHandler
import org.axonframework.intellij.ide.plugin.resolving.handlers.types.EventSourcingHandler
import org.axonframework.intellij.ide.plugin.resolving.handlers.types.QueryHandler
import org.axonframework.intellij.ide.plugin.resolving.handlers.types.SagaEventHandler
import org.axonframework.intellij.ide.plugin.util.aggregateResolver
import org.axonframework.intellij.ide.plugin.util.creatorResolver
import org.axonframework.intellij.ide.plugin.util.handlerResolver

class ProjectModelBuilder(private val project: Project) {

  fun build(): AxonProjectModel {
    val creators = Creators(project)

    val commands = buildCommands(creators)
    val events = buildEvents(creators)
    val queries = buildQueries(creators)

    return AxonProjectModel(commands, events, queries)
  }

  private fun buildCommands(creators: Creators): List<Command> =
      project
          .handlerResolver()
          .findAllHandlers()
          .filterIsInstance<CommandHandler>()
          .distinctBy { it.payload }
          .map { handler ->
            Command(
                name = handler.payload,
                createdBy = creators.forCommand(handler),
                handledBy =
                    CommandHandlerDetail(
                        type =
                            if (isAggregate(project, handler.componentName)) HandlerType.Aggregate
                            else HandlerType.CommandHandler,
                        name = handler.componentName,
                        events = creators.eventsFor(handler),
                        commands = creators.commandsFor(handler)))
          }

  private fun buildEvents(creators: Creators): List<Event> =
      project
          .handlerResolver()
          .findAllHandlers()
          .filter(::isEventHandler)
          .distinctBy { it.payload }
          .map { handler ->
            Event(
                name = handler.payload,
                createdBy = creators.forEvent(handler),
                handledBy =
                    project
                        .handlerResolver()
                        .findHandlersForType(handler.payload)
                        .mapNotNull { eventHandler ->
                          handlerToDetail(project, creators, eventHandler)
                        }
                        .toSet())
          }

  private fun isEventHandler(handler: Handler) =
      handler is EventHandler || handler is EventSourcingHandler || handler is SagaEventHandler

  private fun handlerToDetail(
      project: Project,
      creators: Creators,
      handler: Handler
  ): EventHandlerDetail? {
    val (type, name) =
        when (handler) {
          is EventHandler ->
              Pair(
                  if (isAggregate(project, handler.payload)) HandlerType.Aggregate
                  else HandlerType.EventHandler,
                  handler.element.containingClass?.qualifiedName ?: handler.processingGroup)
          is EventSourcingHandler -> Pair(HandlerType.AggregateEventSource, handler.entity)
          is SagaEventHandler ->
              Pair(
                  HandlerType.Saga,
                  handler.element.containingClass?.qualifiedName ?: handler.processingGroup)
          else -> return null
        }
    return EventHandlerDetail(
        type, name, events = creators.eventsFor(handler), commands = creators.commandsFor(handler))
  }

  private fun buildQueries(creators: Creators): List<Query> =
      project
          .handlerResolver()
          .findAllHandlers()
          .filterIsInstance<QueryHandler>()
          .distinctBy { it.payload }
          .map { handler ->
            Query(
                name = handler.payload,
                handledBy =
                    QueryHandlerDetail(
                        QueryHandlerType.QueryHandler,
                        handler.element.containingClass?.qualifiedName ?: handler.componentName,
                        creators.eventsFor(handler),
                        creators.commandsFor(handler)))
          }

  companion object {
    private fun isAggregate(project: Project, name: String) =
        project.aggregateResolver().getEntityByName(name) != null
  }

  private class Creators(private val project: Project) {

    private val eventCreators = obtainMessageCreators(::eventCreatorsForHandler)
    private val commandCreators = obtainMessageCreators(::commandCreatorsForHandler)

    fun eventsFor(handler: Handler) =
        eventCreators
            .filter { it.parentHandler == handler }
            .map { EventReference(it.payload) }
            .toSet()

    fun commandsFor(handler: Handler) =
        commandCreators
            .filter { it.parentHandler == handler }
            .map { CommandReference(it.payload) }
            .toSet()

    fun forCommand(handler: Handler) =
        commandCreators
            .filter { it.payload == handler.payload }
            .mapNotNull {
              val name = parentHandlerClassName(it)
              val type = parentHandlerType(it, name)
              if (type == null || name == null) null else CommandCreatorDetail(type, name)
            }
            .toSet()

    fun forEvent(handler: Handler) =
        eventCreators
            .filter { it.payload == handler.payload }
            .mapNotNull {
              val name = parentHandlerClassName(it)
              val type = parentHandlerType(it, name)
              if (type == null || name == null) null else EventCreatorDetail(type, name)
            }
            .toSet()

    private fun parentHandlerType(creator: MessageCreator, name: String?): HandlerType? =
        when (creator.parentHandler?.handlerType) {
          MessageHandlerType.COMMAND -> HandlerType.CommandHandler
          MessageHandlerType.COMMAND_INTERCEPTOR -> HandlerType.CommandHandler
          MessageHandlerType.EVENT ->
              if (name != null && isAggregate(project, name)) HandlerType.Aggregate
              else HandlerType.EventHandler
          MessageHandlerType.EVENT_SOURCING -> HandlerType.AggregateEventSource
          MessageHandlerType.SAGA -> HandlerType.Saga
          else -> null
        }
    private fun parentHandlerClassName(creator: MessageCreator): String? =
        when (val element = creator.parentHandler?.element) {
          null -> null
          is PsiMethod -> element.containingClass?.qualifiedName
          else -> null
        }

    private fun obtainMessageCreators(
        creators: (Handler) -> List<MessageCreator>
    ): List<MessageCreator> = project.handlerResolver().findAllHandlers().flatMap(creators)

    private fun eventCreatorsForHandler(handler: Handler): List<MessageCreator> {
      val creationResolver = project.creatorResolver()
      return when (handler) {
        is EventHandler -> creationResolver.getCreatorsForPayload(handler.payload)
        is EventSourcingHandler -> creationResolver.getCreatorsForPayload(handler.payload)
        is SagaEventHandler -> creationResolver.getCreatorsForPayload(handler.payload)
        else -> listOf()
      }
    }

    private fun commandCreatorsForHandler(handler: Handler): List<MessageCreator> {
      val creationResolver = project.creatorResolver()
      return when (handler) {
        is CommandHandler -> creationResolver.getCreatorsForPayload(handler.payload)
        else -> listOf()
      }
    }
  }
}
