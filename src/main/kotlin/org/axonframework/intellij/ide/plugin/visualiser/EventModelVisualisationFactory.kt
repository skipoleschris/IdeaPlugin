package org.axonframework.intellij.ide.plugin.visualiser

import com.intellij.ui.components.JBScrollPane
import java.awt.Dimension
import java.awt.Graphics
import java.awt.image.BufferedImage
import javax.swing.JPanel
import javax.swing.JScrollPane

class EventModelVisualisationFactory(model: AxonEventModel) {

  private val maxWidth: Int = 1500
  private val maxHeight: Int = 900
  private val image = EventModelImageRenderer(model).renderImage()

  fun createVisualisation(): JScrollPane {
    val panel = ImagePanel(image)

    val paneSize = Dimension(minOf(maxWidth, image.width), minOf(maxHeight, image.height))
    val pane = JBScrollPane(panel)
    pane.preferredSize = paneSize
    pane.horizontalScrollBarPolicy = JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS
    pane.verticalScrollBarPolicy = JScrollPane.VERTICAL_SCROLLBAR_ALWAYS

    return pane
  }

  class ImagePanel(private val image: BufferedImage) : JPanel(true) {
    init {
      preferredSize = Dimension(image.width, image.height)
    }

    override fun paintComponent(g: Graphics?) {
      super.paintComponent(g)
      g?.drawImage(image, 0, 0, null)
    }
  }
}
