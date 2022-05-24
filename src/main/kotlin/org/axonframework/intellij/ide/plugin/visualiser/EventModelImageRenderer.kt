package org.axonframework.intellij.ide.plugin.visualiser

import java.awt.Color
import java.awt.Dimension
import java.awt.Graphics2D
import java.awt.RenderingHints
import java.awt.geom.Rectangle2D
import java.awt.image.BufferedImage

internal class EventModelImageRenderer(private val model: AxonEventModel) {

  private val postItSize: Int = 150
  private val horizontalSpace: Int = 50
  private val verticalSpace: Int = 50
  private val horizontalSize = postItSize + horizontalSpace
  private val verticalSize = postItSize + verticalSpace

  fun renderImage(): BufferedImage {
    val columns = model.columns()
    val rows = model.rows()

    val canvasSize =
        Dimension(
            (horizontalSize * columns) + horizontalSpace, (verticalSize * rows) + verticalSpace)

    // val gc =
    // GraphicsEnvironment.getLocalGraphicsEnvironment().defaultScreenDevice.defaultConfiguration
    // val image = UIUtil.createImage(gc, canvasSize.width.toDouble(), canvasSize.height.toDouble(),
    // BufferedImage.TYPE_INT_ARGB, PaintUtil.RoundingMode.CEIL)
    val image = BufferedImage(canvasSize.width, canvasSize.height, BufferedImage.TYPE_INT_ARGB)
    val canvas = image.graphics as Graphics2D
    canvas.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
    canvas.setRenderingHint(
        RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON)

    canvas.color = Color.white
    canvas.fillRect(0, 0, canvasSize.width, canvasSize.height)

    val viewPostIts = model.postIts.filterIsInstance<ViewPostIt>()

    model.postIts.forEach {
      drawPostIt(canvas, it)
      routeLines(canvas, viewPostIts, it)
    }

    return image
  }

  private fun drawPostIt(canvas: Graphics2D, postIt: PostIt) {
    val x = ((horizontalSpace + postItSize) * postIt.columnIndex) + horizontalSpace
    val y = ((verticalSpace + postItSize) * (postIt.swimLane.rowIndex - 1)) + verticalSpace
    canvas.color = postIt.color
    canvas.fillRect(x, y, postItSize, postItSize)
    canvas.color = Color.black

    val lines = divideStringsIntoPartsThatFitOnAPostIt(canvas, postIt.text)
    val totalHeight = lines.sumOf { it.second.height }.toInt()
    var yPosition = if (totalHeight >= postItSize) y + 14 else y + ((postItSize - totalHeight) / 2)
    lines.forEach {
      canvas.drawString(it.first, (x + ((postItSize - it.second.width.toInt()) / 2)), yPosition)
      yPosition += it.second.height.toInt()
    }
  }

  private fun divideStringsIntoPartsThatFitOnAPostIt(
      canvas: Graphics2D,
      s: String
  ): List<Pair<String, Rectangle2D>> {
    val area = canvas.font.getStringBounds(s, canvas.fontRenderContext)
    if (area.width <= postItSize) return listOf(Pair(s, area))

    return s.split(" ").fold(listOf()) { result, word ->
      if (result.isEmpty()) {
        listOf(Pair(word, canvas.font.getStringBounds(word, canvas.fontRenderContext)))
      } else {
        val (last, _) = result.last()
        val next = "$last $word"
        val nextArea = canvas.font.getStringBounds(next, canvas.fontRenderContext)
        if (nextArea.width <= postItSize) result.dropLast(1).plus(Pair(next, nextArea))
        else result.plus(Pair(word, canvas.font.getStringBounds(word, canvas.fontRenderContext)))
      }
    }
  }

  private fun routeLines(canvas: Graphics2D, viewPostIts: List<ViewPostIt>, postIt: PostIt) {
    canvas.color = Color.black
    postIt.linksFrom.forEach { routeLine(canvas, viewPostIts, it, postIt) }
  }

  private fun routeLine(
      canvas: Graphics2D,
      viewPostIts: List<ViewPostIt>,
      from: PostIt,
      to: PostIt
  ) {
    if (isSameRow(from, to)) {
      routeDifferentColumnSameRow(canvas, from, to)
    } else if (isDown(from, to) && isSameColumn(from, to)) {
      routeSameColumnDownwards(canvas, from, to)
    } else if (to is EventPostIt) {
      routeDifferentColumnToEvent(canvas, from, to)
    } else if (to is CommandPostIt) {
      routeToCommandOnTimeline(canvas, from, to)
    } else {
      routeToViewOnTimeline(canvas, viewPostIts, from, to)
    }
  }

  private fun isSameRow(from: PostIt, to: PostIt) = from.swimLane.rowIndex == to.swimLane.rowIndex
  private fun isDown(from: PostIt, to: PostIt) = from.swimLane.rowIndex < to.swimLane.rowIndex
  private fun isSameColumn(from: PostIt, to: PostIt) = from.columnIndex == to.columnIndex
  private fun isForward(from: PostIt, to: PostIt) = from.columnIndex < to.columnIndex

  private fun routeDifferentColumnSameRow(canvas: Graphics2D, from: PostIt, to: PostIt) {
    val fromX = horizontalCenterOfPostIt(from)
    val toX =
        if (from.columnIndex < to.columnIndex) horizontalLeftQuarterOfPostIt(to)
        else horizontalRightQuarterOfPostIt(to)
    val y = bottomOfPostIt(from)
    val offsetY = 10

    canvas.color = Color.black
    canvas.drawLineSequence(
        Pair(fromX, y), Pair(fromX, y + offsetY), Pair(toX, y + offsetY), Pair(toX, y))
    canvas.drawVerticalArrow(toX, y, false)
  }

  private fun routeSameColumnDownwards(canvas: Graphics2D, from: PostIt, to: PostIt) {
    val x = horizontalCenterOfPostIt(from)
    val fromY = bottomOfPostIt(from)
    val toY = topOfPostIt(to)

    canvas.color = Color.black
    canvas.drawLineSequence(Pair(x, fromY), Pair(x, toY))
    canvas.drawVerticalArrow(x, toY, true)
  }

  private fun routeDifferentColumnToEvent(canvas: Graphics2D, from: PostIt, to: PostIt) {
    val fromX = horizontalCenterOfPostIt(from)
    val fromY = bottomOfPostIt(from)
    val toX = horizontalCenterOfPostIt(to)
    val toY = topOfPostIt(to)
    val offsetX = if (isForward(from, to)) 85 else -115
    val offsetYTop = 10
    val offsetYBottom = 35

    canvas.color = Color.black
    canvas.drawLineSequence(
        Pair(fromX, fromY),
        Pair(fromX, fromY + offsetYTop),
        Pair(fromX + offsetX, fromY + offsetYTop),
        Pair(fromX + offsetX, toY - offsetYBottom),
        Pair(toX, toY - offsetYBottom),
        Pair(toX, toY))
    canvas.drawVerticalArrow(toX, toY, true)
  }

  private fun routeToCommandOnTimeline(canvas: Graphics2D, from: PostIt, to: PostIt) {
    val fromX = if (isForward(from, to)) rightOfPostIt(from) else leftOfPostIt(from)
    val fromY = topThirdOfPostIt(from)
    val toX = if (isForward(from, to)) leftOfPostIt(to) else rightOfPostIt(to)
    val toY = bottomQuarterOfPostIt(to)
    val offsetXFrom = if (isForward(from, to)) 40 else -10
    val offsetXTo = if (isForward(from, to)) -10 else 40
    val offsetY = (postItSize / 4) + 35

    canvas.color = Color(0x06, 0x74, 0x98)
    canvas.drawLineSequence(
        Pair(fromX, fromY),
        Pair(fromX + offsetXFrom, fromY),
        Pair(fromX + offsetXFrom, toY + offsetY),
        Pair(toX + offsetXTo, toY + offsetY),
        Pair(toX + offsetXTo, toY),
        Pair(toX, toY))
    canvas.drawHorizontalArrow(toX, toY, isForward(from, to))
    canvas.color = Color.black
  }

  private fun routeToViewOnTimeline(
      canvas: Graphics2D,
      viewPostIts: List<ViewPostIt>,
      from: PostIt,
      to: PostIt
  ) {
    val fromX = if (isForward(from, to)) rightOfPostIt(from) else leftOfPostIt(from)
    val fromY = topQuarterOfPostIt(from)
    val toX = horizontalCenterOfPostIt(to)
    val toY = bottomOfPostIt(to)
    val offsetXFrom = (if (isForward(from, to)) 25 else -25) + (3 * viewPostIts.indexOf(to))
      val offsetXTo = if (isForward(from, to)) -10 else 10
      val offsetXCircle = if (isForward(from, to)) -20 else 0
      val arcAngle = if (isForward(from, to)) 90 else -90
    val offsetY = 25 - (3 * viewPostIts.indexOf(to))

    canvas.color = Color(0x80, 0x90, 0x08)
    canvas.drawLineSequence(
        Pair(fromX, fromY),
        Pair(fromX + offsetXFrom, fromY),
        Pair(fromX + offsetXFrom, toY + offsetY),
        Pair(toX + offsetXTo, toY + offsetY))
    canvas.drawArc(toX + offsetXCircle, toY + offsetY - 20, 20, 20, 270, arcAngle)
      canvas.drawLine(toX, toY + offsetY - 10, toX, toY)
    canvas.drawVerticalArrow(toX, toY, false)
    canvas.color = Color.black
  }

  private fun Graphics2D.drawLineSequence(vararg points: Pair<Int, Int>) {
    if (points.size >= 2) {
      val from = points[0]
      val to = points[1]
      drawLine(from.first, from.second, to.first, to.second)
      drawLineSequence(*points.drop(1).toTypedArray())
    }
  }

  private fun Graphics2D.drawVerticalArrow(x: Int, y: Int, down: Boolean) {
    drawLine(x, y, x - 5, if (down) y - 5 else y + 5)
    drawLine(x, y, x + 5, if (down) y - 5 else y + 5)
  }

  private fun Graphics2D.drawHorizontalArrow(x: Int, y: Int, right: Boolean) {
    drawLine(x, y, if (right) x - 5 else x + 5, y - 5)
    drawLine(x, y, if (right) x - 5 else x + 5, y + 5)
  }

  private fun topOfPostIt(postIt: PostIt): Int =
      verticalSpace + ((postIt.swimLane.rowIndex - 1) * verticalSize)

  private fun topQuarterOfPostIt(postIt: PostIt): Int = topOfPostIt(postIt) + (postItSize / 4)

  private fun topThirdOfPostIt(postIt: PostIt): Int = topOfPostIt(postIt) + (postItSize / 3)

  private fun bottomOfPostIt(postIt: PostIt): Int = topOfPostIt(postIt) + postItSize

  private fun bottomQuarterOfPostIt(postIt: PostIt): Int = bottomOfPostIt(postIt) - (postItSize / 4)

  private fun leftOfPostIt(postIt: PostIt): Int =
      horizontalSpace + (postIt.columnIndex * horizontalSize)

  private fun rightOfPostIt(postIt: PostIt): Int = leftOfPostIt(postIt) + postItSize

  private fun horizontalCenterOfPostIt(postIt: PostIt): Int =
      leftOfPostIt(postIt) + (postItSize / 2)

  private fun horizontalLeftQuarterOfPostIt(postIt: PostIt): Int =
      leftOfPostIt(postIt) + (postItSize / 4)

  private fun horizontalRightQuarterOfPostIt(postIt: PostIt): Int =
      rightOfPostIt(postIt) - (postItSize / 4)
}
