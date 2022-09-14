package uk.co.skipoles.eventmodeller.definition

object DefinitionParser {

  fun parse(definition: String): Result<EventModelDefinition> =
      try {
        Result.success(EventModelDefinition(definition.split("\n").map(::lineToEntry)))
      } catch (e: DefinitionParseException) {
        Result.failure(e)
      }

  private fun lineToEntry(line: String): ModelDefinitionEntry {
    val itemRegex = """(\[([stea])\]\s*(.*)\s*::)?\s*\[([cevs])\]\s*(.*)"""
    val regex = """$itemRegex\s*->\s*$itemRegex""".toRegex()
    //
    // """(\[([stea])\]\s*(.*)\s*::)?\s*\[([cevs])\]\s*(.*)\s*->\s*\[([cevs])\]\s*(.*)""".toRegex()
    val match =
        regex.matchEntire(line.trim())
            ?: throw DefinitionParseException(line, "Line does not match required structure")

    println(match.groupValues)
    val fromContext = Pair(match.groupValues[2], match.groupValues[3].trim())
    val from = Pair(match.groupValues[4], match.groupValues[5].trim())
    val toContext = Pair(match.groupValues[7], match.groupValues[8].trim())
    val to = Pair(match.groupValues[9], match.groupValues[10].trim())

    try {
      val fromItemContext =
          if (fromContext.first.isNotEmpty() && fromContext.second.isNotEmpty())
              ModelDefinitionContext(
                  fromContext.first.toModelDefinitionContextType(), fromContext.second)
          else null

      val toItemContext =
          if (toContext.first.isNotEmpty() && toContext.second.isNotEmpty())
              ModelDefinitionContext(
                  toContext.first.toModelDefinitionContextType(), toContext.second)
          else null

      return ModelDefinitionEntry(
          ModelDefinitionItem(
              from.first.toModelDefinitionItemType(),
              from.second,
              fromItemContext?.name,
              fromItemContext),
          ModelDefinitionItem(
              to.first.toModelDefinitionItemType(), to.second, toItemContext?.name, toItemContext))
    } catch (e: IllegalStateException) {
      throw DefinitionParseException(line, e.message ?: "Unknown type")
    }
  }

  private fun String.toModelDefinitionItemType() =
      when (this) {
        "c" -> ModelDefinitionItemType.Command
        "e" -> ModelDefinitionItemType.Event
        "v" -> ModelDefinitionItemType.View
        "s" -> ModelDefinitionItemType.Saga
        else -> throw IllegalStateException("Unknown item type: $this")
      }

  private fun String.toModelDefinitionContextType() =
      when (this) {
        "s" -> ModelDefinitionContextType.Saga
        "t" -> ModelDefinitionContextType.Timeline
        "e" -> ModelDefinitionContextType.EventHandler
        "a" -> ModelDefinitionContextType.Aggregate
        else -> throw IllegalStateException("Unknown context type: $this")
      }

  class DefinitionParseException(private val line: String, private val detail: String) :
      Exception() {

    override val message: String
      get() = "Invalid line: $line. Cause: $detail"
  }
}
