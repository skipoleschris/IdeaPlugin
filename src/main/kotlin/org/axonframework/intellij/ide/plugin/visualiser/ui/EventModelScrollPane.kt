package org.axonframework.intellij.ide.plugin.visualiser.ui

import com.intellij.ui.components.JBScrollPane
import java.awt.Dimension
import java.awt.Graphics
import java.awt.event.MouseEvent
import java.awt.event.MouseListener
import java.awt.image.BufferedImage
import javax.swing.JPanel
import org.axonframework.intellij.ide.plugin.visualiser.AxonEventModel

class EventModelScrollPane : JBScrollPane() {

  private val maxWidth: Int = 1500
  private val maxHeight: Int = 900

  private var renderer: EventModelImageRenderer? = null
  private var initialCommand: String? = null
  private var image: BufferedImage? = null

  init {
    setViewportView(JPanel())
    preferredSize = Dimension(maxWidth, maxHeight)
  }

  fun visualise(model: AxonEventModel) {
    renderer = EventModelImageRenderer(model)
    initialCommand = model.initialCommand
    image = renderer!!.renderImage()

    val imagePanel = ImagePanel(image!!)
    setViewportView(imagePanel)

    imagePanel.addMouseListener(
        object : MouseListener {
          override fun mouseClicked(e: MouseEvent?) {}

          override fun mousePressed(e: MouseEvent?) {}

          override fun mouseReleased(e: MouseEvent?) {
            if (e != null && renderer != null) {
              println(renderer!!.postItAtPosition(e.x, e.y))
            }
          }

          override fun mouseEntered(e: MouseEvent?) {}

          override fun mouseExited(e: MouseEvent?) {}
        })
  }

  fun clear() {
    setViewportView(JPanel())
  }

  fun currentCommandFocus(): String? = initialCommand

  fun currentImage(): BufferedImage? = image

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
