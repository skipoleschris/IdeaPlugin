package org.axonframework.intellij.ide.plugin.visualiser

class VisualisationModelGenerator(private val definition: AxonEventModelDefinition) {

  //  fun build(): AxonEventModel {
  //    val state = VisualisationState()
  //    state.determineOrderedSwimLanes(definition.entries)
  //
  //    val postIts = generatePostIts(definition.entries)
  //
  //    return AxonEventModel("UNKNOWN", state.swimLanePostIts() + postIts.values.toList(), mapOf())
  //  }
  //
  //  private fun generatePostIts(
  //      state: VisualisationState,
  //      entries: List<ModelDefinitionEntry>
  //  ): Map<String, PostIt> {
  //    entries.map {
  //      when (it.item.type) {
  //        ModelDefinitionItemType.Command -> state.addCommandPostIt(it.item)
  //        ModelDefinitionItemType.Event -> EventPostIt()
  //        ModelDefinitionItemType.View -> ViewPostIt()
  //        ModelDefinitionItemType.Saga -> SagaPostIt()
  //      }
  //    }
  //    return mapOf()
  //  }
  //
  //  class VisualisationState {
  //    private val swimLanes = mutableMapOf<ModelDefinitionContext, SwimLane>()
  //    private var currentColumn: Int = 1
  //
  //    fun determineOrderedSwimLanes(entries: List<ModelDefinitionEntry>) {
  //      swimLanes.putAll(
  //          entries
  //              .map { it.getOrDeriveContext() }
  //              .toSet()
  //              .sortedWith { leftContext, rightContext ->
  //                when (val result = leftContext.type.compareTo(rightContext.type)) {
  //                  0 -> {
  //                    if (leftContext == rightContext) 0 // Should never happen in a set
  //                    else if (ModelDefinitionEntry.isDefaultContext(leftContext)) -1
  //                    else if (ModelDefinitionEntry.isDefaultContext(rightContext)) 1
  //                    else leftContext.name.compareTo(rightContext.name)
  //                  }
  //                  else -> result
  //                }
  //              }
  //              .mapIndexed { index, context ->
  //                Pair(
  //                    context,
  //                    SwimLane(
  //                        context.type.toSwimLaneType(),
  //                        index + 1,
  //                        !ModelDefinitionEntry.isDefaultContext(context),
  //                        context.name,
  //                        makeShortName(context.name)))
  //              })
  //    }
  //
  //    private fun ModelDefinitionContextType.toSwimLaneType() =
  //        when (this) {
  //          ModelDefinitionContextType.Saga -> SwimLaneType.Saga
  //          ModelDefinitionContextType.Timeline -> SwimLaneType.Timeline
  //          ModelDefinitionContextType.EventHandler -> SwimLaneType.Events
  //          ModelDefinitionContextType.Aggregate -> SwimLaneType.Aggregate
  //        }
  //
  //    fun swimLanePostIts(): List<PostIt> =
  //        swimLanes.value.filter { it.requiresLabel }.map { LabelPostIt(it, 0, it.shortName ?: "")
  // }
  //
  //    private fun swimLaneFor(entry: ModelDefinitionEntry) = swimLanes[entry.getOrDeriveContext()]
  //
  //    fun addCommandPostIt(item: ModelDefinitionEntry) {
  //      CommandPostIt(swimLaneFor(item), currentColumn, makeShortName(item.name))
  //    }
  //  }
}
