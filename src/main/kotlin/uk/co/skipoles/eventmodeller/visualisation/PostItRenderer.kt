package uk.co.skipoles.eventmodeller.visualisation

import java.awt.Color
import java.awt.Graphics2D
import java.awt.geom.Rectangle2D

internal fun Graphics2D.drawPostIt(postIt: PostIt, x: Int, y: Int, postItSize: Int) {
  color = postIt.color
  fillRect(x, y, postItSize, postItSize)
  color = Color.black

  val lines = divideStringsIntoPartsThatFitOnAPostIt(this, postIt.text, postItSize)
  val totalHeight = lines.sumOf { it.second.height }.toInt()
  var yPosition = if (totalHeight >= postItSize) y + 14 else y + ((postItSize - totalHeight) / 2)
  lines.forEach {
    drawString(it.first, (x + ((postItSize - it.second.width.toInt()) / 2)), yPosition)
    yPosition += it.second.height.toInt()
  }
}

private fun divideStringsIntoPartsThatFitOnAPostIt(
    canvas: Graphics2D,
    s: String,
    postItSize: Int
): List<Pair<String, Rectangle2D>> {
  val area = canvas.font.getStringBounds(s, canvas.fontRenderContext)
  if (area.width <= (postItSize - 2)) return listOf(Pair(s, area))

  return s.split(" ").fold(listOf()) { result, word ->
    if (result.isEmpty()) {
      listOf(Pair(word, canvas.font.getStringBounds(word, canvas.fontRenderContext)))
    } else {
      val (last, _) = result.last()
      val next = "$last $word"
      val nextArea = canvas.font.getStringBounds(next, canvas.fontRenderContext)
      if (nextArea.width <= (postItSize - 2)) result.dropLast(1).plus(Pair(next, nextArea))
      else result.plus(Pair(word, canvas.font.getStringBounds(word, canvas.fontRenderContext)))
    }
  }
}
