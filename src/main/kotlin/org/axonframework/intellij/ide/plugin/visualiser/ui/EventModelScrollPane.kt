package org.axonframework.intellij.ide.plugin.visualiser.ui

import com.intellij.ui.components.JBScrollPane
import java.awt.Dimension
import java.awt.Graphics
import java.awt.image.BufferedImage
import javax.swing.JPanel
import org.axonframework.intellij.ide.plugin.visualiser.AxonEventModel

class EventModelScrollPane : JBScrollPane() {

  private val maxWidth: Int = 1500
  private val maxHeight: Int = 900

  private var image: BufferedImage? = null

  init {
    setViewportView(JPanel())
    preferredSize = Dimension(maxWidth, maxHeight)
  }

  fun visualise(model: AxonEventModel) {
    image = EventModelImageRenderer(model).renderImage()
    setViewportView(ImagePanel(image!!))
  }

  fun clear() {
    setViewportView(JPanel())
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
