package org.axonframework.intellij.ide.plugin.visualiser

import java.awt.Color
import java.awt.Dimension
import java.awt.Graphics2D
import java.awt.Point
import java.awt.image.BufferedImage
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.StringReader
import javax.imageio.ImageIO
import kotlin.math.abs
import org.apache.batik.transcoder.TranscoderInput
import org.apache.batik.transcoder.TranscoderOutput
import org.apache.batik.transcoder.image.PNGTranscoder
import org.axonframework.intellij.ide.plugin.visualiser.ui.drawArrow
import org.axonframework.intellij.ide.plugin.visualiser.ui.drawPostIt
import org.jfree.graphics2d.svg.SVGGraphics2D

class SvgDocumentGenerator(private val model: AxonEventModel) {

  private val postItSize: Int = 150
  private val horizontalSpace: Int = 75
  private val verticalSpace: Int = 75
  private val horizontalSize = postItSize + horizontalSpace
  private val verticalSize = postItSize + verticalSpace

  fun renderDocument(scale: Double = 1.0): SizedAndScaledSvgImage {
    val size = imageSize()
    val graphics = SVGGraphics2D(size.width, size.height)

    val boxes =
        model.postIts.fold(listOf<PostItBox>()) { result, postIt ->
          result + drawPostIt(graphics, postIt)
        }

    val lines = routeLines(model.postIts.flatMap(::generateDirectLines), boxes)
    lines.forEach { drawLine(graphics, it) }

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

  private fun drawPostIt(graphics: Graphics2D, postIt: PostIt): PostItBox {
    val x = ((horizontalSpace + postItSize) * postIt.columnIndex) + horizontalSpace
    val y = ((verticalSpace + postItSize) * (postIt.swimLane.rowIndex - 1)) + verticalSpace
    graphics.drawPostIt(postIt, x, y, postItSize)
    println("($x, $y) ${postIt.text}")
    return PostItBox(x, y, postItSize, postItSize)
  }

  private fun routeLines(lines: List<Line>, boxes: List<PostItBox>): List<Line> {
    val (linesNeedingRouting, linesAlreadyRouted) =
        lines.map { lineCrossesPostIt(boxes, it) }.partition { it.isIntersect() }

    val routedLines = linesAlreadyRouted.map { it.line }
    return if (linesNeedingRouting.isEmpty()) routedLines
    else {
      val splitLines = linesNeedingRouting.flatMap(::splitLine)
      routedLines + routeLines(splitLines, boxes)
    }
  }

  private fun splitLine(intersection: IntersectionResult): List<Line> {
    if (intersection.postIt == null ||
        intersection.entryPoint == null ||
        intersection.exitPoint == null)
        return listOf(intersection.line)

    val splitPoints =
        intersection.postIt.moveOutside(intersection.entryPoint, intersection.exitPoint)

    println("Intersection: $intersection, split at: $splitPoints")
    val line = intersection.line
    return if (splitPoints.size == 1) {
      val point = splitPoints.first()
      listOf(
          Line(line.fromX, line.fromY, point.x, point.y, false),
          Line(point.x, point.y, line.toX, line.toY, true))
    } else {
      val point1 = splitPoints.first()
      val point2 = splitPoints.last()
      listOf(
          Line(line.fromX, line.fromY, point1.x, point1.y, false),
          Line(point1.x, point1.y, point2.x, point2.y, false),
          Line(point2.x, point2.y, line.toX, line.toY, true))
    }
  }

  private fun generateDirectLines(postIt: PostIt): List<Line> =
      model.links[postIt]?.map { generateDirectLine(it, postIt) } ?: emptyList()

  private fun generateDirectLine(from: PostIt, to: PostIt): Line {
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

    return Line(fromX, fromY, toX, toY, true)
  }

  private fun drawLine(graphics: Graphics2D, line: Line) {
    graphics.drawLine(line.fromX, line.fromY, line.toX, line.toY)
    if (line.hasArrow) graphics.drawArrow(line.toX, line.fromX, line.toY, line.fromY)
  }

  private fun lineCrossesPostIt(boxes: List<PostItBox>, line: Line): IntersectionResult {
    val notConnectedBoxes = boxes.filterNot { it.contains(line.start()) || it.contains(line.end()) }
    val pointsInsidePostIts =
        line.points().mapNotNull { point ->
          val postIt = notConnectedBoxes.find { it.contains(point) }
          if (postIt != null) Pair(postIt, point) else null
        }
    return if (pointsInsidePostIts.isEmpty()) IntersectionResult(line, null, null, null)
    else {
      val firstPostItCrossed = pointsInsidePostIts.first().first
      val allPointsInsidePostIt =
          pointsInsidePostIts.filter { it.first == firstPostItCrossed }.map { it.second }
      IntersectionResult(
          line, firstPostItCrossed, allPointsInsidePostIt.first(), allPointsInsidePostIt.last())
    }
  }

  private fun topOfPostIt(postIt: PostIt): Int =
      verticalSpace + ((postIt.swimLane.rowIndex - 1) * verticalSize)

  private fun bottomOfPostIt(postIt: PostIt): Int = topOfPostIt(postIt) + postItSize

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

  data class PostItBox(val x: Int, val y: Int, val width: Int, val height: Int) {
    fun contains(point: Point): Boolean =
        (point.x >= x && point.x < (x + width) && point.y >= y && point.y < (y + height))

    fun moveOutside(entryPoint: Point, exitPoint: Point): List<Point> =
        when (sectorFor(entryPoint)) {
          BoxSector.LeftUpper ->
              when (sectorFor(exitPoint)) {
                BoxSector.RightUpper -> listOf(topLeft(), topRight())
                BoxSector.RightLower -> listOf(topLeft(), topRight())
                BoxSector.BottomLeft -> listOf(bottomLeft())
                BoxSector.BottomRight -> listOf(bottomLeft())
                else -> listOf(topLeft())
              }
          BoxSector.RightUpper ->
              when (sectorFor(exitPoint)) {
                BoxSector.LeftUpper -> listOf(topRight(), topLeft())
                BoxSector.LeftLower -> listOf(topRight(), topLeft())
                BoxSector.BottomLeft -> listOf(bottomRight())
                BoxSector.BottomRight -> listOf(bottomRight())
                else -> listOf(topRight())
              }
          BoxSector.TopLeft ->
              when (sectorFor(exitPoint)) {
                BoxSector.RightUpper -> listOf(topRight())
                BoxSector.RightLower -> listOf(topRight())
                BoxSector.BottomLeft -> listOf(topLeft(), bottomLeft())
                BoxSector.BottomRight -> listOf(topLeft(), bottomLeft())
                else -> listOf(topLeft())
              }
          BoxSector.TopRight ->
              when (sectorFor(exitPoint)) {
                BoxSector.LeftUpper -> listOf(topLeft())
                BoxSector.LeftLower -> listOf(topLeft())
                BoxSector.BottomLeft -> listOf(topRight(), bottomRight())
                BoxSector.BottomRight -> listOf(topRight(), bottomRight())
                else -> listOf(topRight())
              }
          BoxSector.LeftLower ->
              when (sectorFor(exitPoint)) {
                BoxSector.RightUpper -> listOf(bottomLeft(), bottomRight())
                BoxSector.TopLeft -> listOf(topLeft())
                BoxSector.TopRight -> listOf(topLeft())
                BoxSector.RightLower -> listOf(bottomLeft(), bottomRight())
                else -> listOf(bottomLeft())
              }
          BoxSector.RightLower ->
              when (sectorFor(exitPoint)) {
                BoxSector.LeftUpper -> listOf(bottomRight(), bottomLeft())
                BoxSector.TopLeft -> listOf(topRight())
                BoxSector.TopRight -> listOf(topRight())
                BoxSector.LeftLower -> listOf(bottomRight(), bottomLeft())
                else -> listOf(bottomRight())
              }
          BoxSector.BottomLeft ->
              when (sectorFor(exitPoint)) {
                BoxSector.RightUpper -> listOf(bottomRight())
                BoxSector.TopLeft -> listOf(bottomLeft(), topLeft())
                BoxSector.TopRight -> listOf(bottomLeft(), topLeft())
                BoxSector.RightLower -> listOf(bottomRight())
                else -> listOf(bottomLeft())
              }
          BoxSector.BottomRight ->
              when (sectorFor(exitPoint)) {
                BoxSector.LeftUpper -> listOf(bottomLeft())
                BoxSector.TopLeft -> listOf(bottomRight(), topRight())
                BoxSector.TopRight -> listOf(bottomRight(), topRight())
                BoxSector.LeftLower -> listOf(bottomLeft())
                else -> listOf(bottomRight())
              }
        }

    private fun sectorFor(point: Point): BoxSector {
      val (midX, midY) = Pair(x + (width / 2), y + (height / 2))
      return if (point.x == x) {
        if (point.y < midY) BoxSector.LeftUpper else BoxSector.LeftLower
      } else if (point.x == (x + width)) {
        if (point.y < midY) BoxSector.RightUpper else BoxSector.RightLower
      } else if (point.x < midX) {
        if (point.y == y) BoxSector.TopLeft else BoxSector.BottomLeft
      } else { // point.x >= midX && point.x < x + width
        if (point.y == y) BoxSector.TopRight else BoxSector.BottomRight
      }
    }

    enum class BoxSector {
      LeftUpper,
      RightUpper,
      TopLeft,
      TopRight,
      LeftLower,
      RightLower,
      BottomLeft,
      BottomRight
    }

    private val moveDistance = 5
    private fun topLeft() = Point(x - moveDistance, y - moveDistance)
    private fun topRight() = Point(x + width + moveDistance, y - moveDistance)
    private fun bottomLeft() = Point(x - moveDistance, y + height + moveDistance)
    private fun bottomRight() = Point(x + width + moveDistance, y + height + moveDistance)
  }

  data class Line(
      val fromX: Int,
      val fromY: Int,
      val toX: Int,
      val toY: Int,
      val hasArrow: Boolean
  ) {
    fun start() = Point(fromX, fromY)

    fun end() = Point(toX, toY)

    fun points(): List<Point> {
      // Bresenham algorithm
      val points = mutableListOf<Point>()

      var x = fromX
      var y = fromY

      val dx = abs(toX - fromX)
      val dy = abs(toY - fromY)

      val sx = if (fromX < toX) 1 else -1
      val sy = if (fromY < toY) 1 else -1

      var err = dx - dy
      var e2: Int

      while (true) {
        points.add(Point(x, y))
        if (x == toX && y == toY) break

        e2 = 2 * err
        if (e2 > -dy) {
          err -= dy
          x += sx
        }

        if (e2 < dx) {
          err += dx
          y += sy
        }
      }

      return points.toList()
    }
  }

  data class IntersectionResult(
      val line: Line,
      val postIt: PostItBox?,
      val entryPoint: Point?,
      val exitPoint: Point?
  ) {
    fun isIntersect() = postIt != null
  }
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
