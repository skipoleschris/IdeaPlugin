package org.axonframework.intellij.ide.plugin.visualiser.ui

import com.intellij.ui.paint.PaintUtil
import com.intellij.util.ui.UIUtil
import java.awt.Color
import java.awt.Dimension
import java.awt.Graphics2D
import java.awt.GraphicsEnvironment
import java.awt.RenderingHints
import java.awt.image.BufferedImage
import org.axonframework.intellij.ide.plugin.visualiser.AxonEventModel
import org.axonframework.intellij.ide.plugin.visualiser.CommandPostIt
import org.axonframework.intellij.ide.plugin.visualiser.PostIt
import org.axonframework.intellij.ide.plugin.visualiser.ViewPostIt

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

    val gc =
        GraphicsEnvironment.getLocalGraphicsEnvironment().defaultScreenDevice.defaultConfiguration
    val image =
        UIUtil.createImage(
            gc,
            canvasSize.width.toDouble(),
            canvasSize.height.toDouble(),
            BufferedImage.TYPE_INT_ARGB,
            PaintUtil.RoundingMode.CEIL)
    val canvas = image.graphics as Graphics2D
    canvas.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
    canvas.setRenderingHint(
        RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON)

    canvas.color = Color.white
    canvas.fillRect(0, 0, canvasSize.width, canvasSize.height)

    model.postIts.forEach {
      drawPostIt(canvas, it)
      routeLines(canvas, it)
    }

    return image
  }

  private fun drawPostIt(canvas: Graphics2D, postIt: PostIt) {
    val x = ((horizontalSpace + postItSize) * postIt.columnIndex) + horizontalSpace
    val y = ((verticalSpace + postItSize) * (postIt.swimLane.rowIndex - 1)) + verticalSpace
    canvas.drawPostIt(postIt, x, y, postItSize)
  }

  private fun routeLines(canvas: Graphics2D, postIt: PostIt) {
    canvas.color = Color.black
    model.links[postIt]?.forEach { routeLine(canvas, it, postIt) }
  }

  private fun routeLine(canvas: Graphics2D, from: PostIt, to: PostIt) {
    val fromPosition = Pair(from.columnIndex, from.swimLane.rowIndex)
    val toPosition = Pair(to.columnIndex, to.swimLane.rowIndex)

    val (fromX, toX) =
        if (fromPosition.first == toPosition.first) { // Same column
          Pair(horizontalCenterOfPostIt(from), horizontalCenterOfPostIt(to))
        } else if (fromPosition.first < toPosition.first) { // Left to right
          if (fromPosition.second == toPosition.second) // Same row
           Pair(rightOfPostIt(from), leftOfPostIt(to))
          else Pair(horizontalRightQuarterOfPostIt(from), horizontalLeftQuarterOfPostIt(to))
        } else { // Right to left
          if (fromPosition.second == toPosition.second) // Same row
           Pair(leftOfPostIt(from), rightOfPostIt(to))
          else Pair(horizontalLeftQuarterOfPostIt(from), horizontalRightQuarterOfPostIt(to))
        }

    val (fromY, toY) =
        if (fromPosition.second == toPosition.second) { // Same row
          Pair(verticalCenterOfPostIt(from), verticalCenterOfPostIt(to))
        } else if (fromPosition.second < toPosition.second) { // Top to bottom
          Pair(bottomOfPostIt(from), topOfPostIt(to))
        } else { // Bottom to top
          Pair(topOfPostIt(from), bottomOfPostIt(to))
        }

    canvas.color =
        when (to) {
          is CommandPostIt -> to.color.darker()
          is ViewPostIt -> to.color.darker()
          else -> Color.black
        }
    canvas.drawLine(fromX, fromY, toX, toY)
    canvas.drawArrow(toX, fromX, toY, fromY)
  }

  private fun isSameRow(from: PostIt, to: PostIt) = from.swimLane.rowIndex == to.swimLane.rowIndex
  private fun isDown(from: PostIt, to: PostIt) = from.swimLane.rowIndex < to.swimLane.rowIndex
  private fun isSameColumn(from: PostIt, to: PostIt) = from.columnIndex == to.columnIndex
  private fun isForward(from: PostIt, to: PostIt) = from.columnIndex < to.columnIndex

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

  private fun verticalCenterOfPostIt(postIt: PostIt): Int = topOfPostIt(postIt) + (postItSize / 2)

  private fun verticalTopQuarterOfPostIt(postIt: PostIt): Int =
      topOfPostIt(postIt) + (postItSize / 4)

  private fun verticalBottomQuarterOfPostIt(postIt: PostIt): Int =
      bottomOfPostIt(postIt) - (postItSize / 4)
}
