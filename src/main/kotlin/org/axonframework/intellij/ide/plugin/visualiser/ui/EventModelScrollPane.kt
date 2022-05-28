package org.axonframework.intellij.ide.plugin.visualiser.ui

import com.intellij.ui.components.JBScrollPane
import java.awt.Dimension
import java.awt.Graphics
import java.awt.event.MouseEvent
import java.awt.event.MouseListener
import java.awt.image.BufferedImage
import javax.swing.JPanel
import javax.swing.SwingUtilities
import org.axonframework.intellij.ide.plugin.visualiser.AxonEventModel
import org.axonframework.intellij.ide.plugin.visualiser.PostIt
import org.axonframework.intellij.ide.plugin.visualiser.SizedAndScaledSvgImage
import org.axonframework.intellij.ide.plugin.visualiser.SvgDocumentGenerator

class EventModelScrollPane : JBScrollPane() {

  private val maxWidth: Int = 1000
  private val maxHeight: Int = 600

  private val listeners: MutableList<(PostItSelectedEvent) -> Unit> = mutableListOf()
  private var model: AxonEventModel? = null
  private var svg: SizedAndScaledSvgImage? = null

  init {
    setViewportView(JPanel())
    preferredSize = Dimension(maxWidth, maxHeight)
  }

  fun visualise(model: AxonEventModel, scale: Double = 1.0) {
    SwingUtilities.invokeLater {
      val renderer = SvgDocumentGenerator(model)
      this.model = model
      svg = renderer.renderDocument(scale)

      val imagePanel = ImagePanel(svg!!.asPNG())
      setViewportView(imagePanel)

      imagePanel.addMouseListener(
          object : MouseListener {
            override fun mouseClicked(e: MouseEvent?) {}

            override fun mousePressed(e: MouseEvent?) {}

            override fun mouseReleased(e: MouseEvent?) {
              if (e != null) {
                val postIt = renderer.postItAtPosition(e.x, e.y, svg!!.scale)
                if (postIt != null) listeners.forEach { it(PostItSelectedEvent(postIt, e)) }
              }
            }

            override fun mouseEntered(e: MouseEvent?) {}

            override fun mouseExited(e: MouseEvent?) {}
          })
    }
  }

  fun clear() {
    model = null
    setViewportView(JPanel())
  }

  fun unscaledSvgVisualisation(): SizedAndScaledSvgImage? =
      if (model != null) SvgDocumentGenerator(model!!).renderDocument(1.0) else null

  fun currentCommandFocus(): String? = model?.initialCommand

  fun addListener(f: (PostItSelectedEvent) -> Unit) {
    listeners.add(f)
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

data class PostItSelectedEvent(val postIt: PostIt, val mouseEvent: MouseEvent)
