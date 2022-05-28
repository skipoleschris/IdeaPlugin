package org.axonframework.intellij.ide.plugin.visualiser

import java.awt.Color
import java.awt.Dimension
import java.awt.Graphics2D
import java.awt.image.BufferedImage
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.StringReader
import javax.imageio.ImageIO
import org.apache.batik.transcoder.TranscoderInput
import org.apache.batik.transcoder.TranscoderOutput
import org.apache.batik.transcoder.image.PNGTranscoder
import org.axonframework.intellij.ide.plugin.visualiser.ui.drawArrow
import org.axonframework.intellij.ide.plugin.visualiser.ui.drawPostIt
import org.jfree.graphics2d.svg.SVGGraphics2D

class SvgDocumentGenerator(private val model: AxonEventModel) {

  private val postItSize: Int = 150
  private val horizontalSpace: Int = 50
  private val verticalSpace: Int = 50
  private val horizontalSize = postItSize + horizontalSpace
  private val verticalSize = postItSize + verticalSpace

  fun renderDocument(scale: Double = 1.0): SizedAndScaledSvgImage {
    val size = imageSize()
    val graphics = SVGGraphics2D(size.width, size.height)

    model.postIts.forEach {
      drawPostIt(graphics, it)
      routeLines(graphics, it)
    }

    if (scale != 1.0) graphics.scale(scale, scale)

    return SizedAndScaledSvgImage(
        graphics.svgDocument, (size.width * scale).toInt(), (size.height * scale).toInt(), scale)
  }

  private fun imageSize(): Dimension {
    val columns = model.columns()
    val rows = model.rows()
    return Dimension(
        ((horizontalSize * columns) + horizontalSpace), ((verticalSize * rows) + verticalSpace))
  }

  fun postItAtPosition(x: Int, y: Int, scale: Double): PostIt? {
    val scaledX = (x / scale).toInt()
    val scaledY = (y / scale).toInt()
    return model.postIts.find {
      val xStart = (((horizontalSpace + postItSize) * it.columnIndex) + horizontalSpace)
      val yStart = (((verticalSpace + postItSize) * (it.swimLane.rowIndex - 1)) + verticalSpace)

      scaledX >= xStart &&
          scaledX <= (xStart + postItSize) &&
          scaledY >= yStart &&
          scaledY <= (yStart + postItSize)
    }
  }

  private fun drawPostIt(graphics: Graphics2D, postIt: PostIt) {
    val x = ((horizontalSpace + postItSize) * postIt.columnIndex) + horizontalSpace
    val y = ((verticalSpace + postItSize) * (postIt.swimLane.rowIndex - 1)) + verticalSpace
    graphics.drawPostIt(postIt, x, y, postItSize)
  }

  private fun routeLines(graphics: Graphics2D, postIt: PostIt) {
    graphics.color = Color.black
    model.links[postIt]?.forEach { routeLine(graphics, it, postIt) }
  }

  private fun routeLine(graphics: Graphics2D, from: PostIt, to: PostIt) {
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

    graphics.color =
        when (to) {
          is CommandPostIt -> to.color.darker()
          is ViewPostIt -> to.color.darker()
          else -> Color.black
        }
    graphics.drawLine(fromX, fromY, toX, toY)
    graphics.drawArrow(toX, fromX, toY, fromY)
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

data class SizedAndScaledSvgImage(
    val document: String,
    val width: Int,
    val height: Int,
    val scale: Double
) {

  fun asPNG(): BufferedImage {
    val transcoder = PNGTranscoder()
    transcoder.addTranscodingHint(PNGTranscoder.KEY_WIDTH, width.toFloat())
    transcoder.addTranscodingHint(PNGTranscoder.KEY_HEIGHT, height.toFloat())
    transcoder.addTranscodingHint(PNGTranscoder.KEY_BACKGROUND_COLOR, Color.white)

    val input = TranscoderInput(StringReader(document))
    val stream = ByteArrayOutputStream()
    val output = TranscoderOutput(stream)
    transcoder.transcode(input, output)

    stream.flush()
    stream.close()

    val imageData = stream.toByteArray()
    return ImageIO.read(ByteArrayInputStream(imageData))
  }
}
