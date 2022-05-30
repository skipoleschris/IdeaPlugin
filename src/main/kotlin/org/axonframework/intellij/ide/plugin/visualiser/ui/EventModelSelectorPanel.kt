package org.axonframework.intellij.ide.plugin.visualiser.ui

import com.intellij.openapi.ui.ComboBox
import com.intellij.openapi.util.IconLoader
import java.awt.Component
import java.awt.Dimension
import java.awt.event.ItemEvent
import javax.swing.JButton
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.JSlider
import org.axonframework.intellij.ide.plugin.visualiser.AxonProjectModel

class EventModelSelectorPanel(model: AxonProjectModel) : JPanel() {

  private val defaultCommand = "Command..."
  private var selectedCommand: String? = null
  private val visualiseListeners: MutableList<(VisualiseModelEvent) -> Unit> = mutableListOf()
  private val actionListeners: MutableList<(EventModelActionEvent) -> Unit> = mutableListOf()
  private val exclusions: MutableList<String> = mutableListOf()

  private val customiseIcon =
      IconLoader.getIcon("/icons/customize.png", EventModelSelectorPanel::class.java)
  private val copyIcon = IconLoader.getIcon("/icons/copy.png", EventModelSelectorPanel::class.java)
  private val exportPNGIcon =
      IconLoader.getIcon("/icons/export-png.png", EventModelSelectorPanel::class.java)
  private val exportSVGIcon =
      IconLoader.getIcon("/icons/export-svg.png", EventModelSelectorPanel::class.java)
  private val exportModelIcon =
      IconLoader.getIcon("/icons/export-model.png", EventModelSelectorPanel::class.java)

  init {
    alignmentX = Component.LEFT_ALIGNMENT

    val commandSelector = ComboBox(arrayOf(defaultCommand) + model.commandNames().toTypedArray())
    commandSelector.isEditable = false
    commandSelector.setMinimumAndPreferredWidth(500)

    val customiseButton = JButton(customiseIcon)
    customiseButton.preferredSize = Dimension(40, 40)
    customiseButton.toolTipText = "Customise items..."
    customiseButton.isEnabled = true

    val copyButton = JButton(copyIcon)
    copyButton.preferredSize = Dimension(40, 40)
    copyButton.toolTipText = "Copy image to clipboard"
    copyButton.isEnabled = false

    val exportPNGButton = JButton(exportPNGIcon)
    exportPNGButton.preferredSize = Dimension(40, 40)
    exportPNGButton.toolTipText = "Export PNG image..."
    exportPNGButton.isEnabled = false

    val exportSVGButton = JButton(exportSVGIcon)
    exportSVGButton.preferredSize = Dimension(40, 40)
    exportSVGButton.toolTipText = "Export SVG image..."
    exportSVGButton.isEnabled = false

    val exportModelButton = JButton(exportModelIcon)
    exportModelButton.preferredSize = Dimension(40, 40)
    exportModelButton.toolTipText = "Export model..."
    exportModelButton.isEnabled = true

    val zoomSlider = JSlider(JSlider.HORIZONTAL, 25, 100, 100)
    zoomSlider.majorTickSpacing = 25
    zoomSlider.paintTicks = true
    val labels =
        java.util.Hashtable(
            mapOf(
                Pair(25, JLabel("25%")),
                Pair(50, JLabel("50%")),
                Pair(75, JLabel("75%")),
                Pair(100, JLabel("100%"))))
    zoomSlider.labelTable = labels
    zoomSlider.paintLabels = true
    zoomSlider.snapToTicks = true

    add(JLabel("Initial command:"))
    add(commandSelector)
    add(customiseButton)
    add(copyButton)
    add(exportPNGButton)
    add(exportSVGButton)
    add(exportModelButton)
    add(zoomSlider)
    zoomSlider.isEnabled = false

    commandSelector.addItemListener { event ->
      selectedCommand =
          if (event.stateChange == ItemEvent.SELECTED && event.item != defaultCommand) {
            event.item as String
          } else null

      val publish =
          VisualiseModelEvent(selectedCommand, exclusions.toList(), zoomSlider.value / 100.0)
      visualiseListeners.forEach { it(publish) }
      copyButton.isEnabled = (selectedCommand != null)
      exportPNGButton.isEnabled = (selectedCommand != null)
      exportSVGButton.isEnabled = (selectedCommand != null)
      zoomSlider.isEnabled = (selectedCommand != null)
    }

    customiseButton.addActionListener {
      val modelElementSelector = ModelElementDialogWrapper(model, exclusions.toList())
      if (modelElementSelector.showAndGet()) {
        exclusions.clear()
        exclusions.addAll(modelElementSelector.deselectedItems())
        val publish =
            VisualiseModelEvent(selectedCommand, exclusions.toList(), zoomSlider.value / 100.0)
        visualiseListeners.forEach { it(publish) }
      }
    }

    copyButton.addActionListener {
      actionListeners.forEach { it(EventModelActionEvent(EventModelActions.CopyImageToClipboard)) }
    }

    exportPNGButton.addActionListener {
      actionListeners.forEach { it(EventModelActionEvent(EventModelActions.ExportPNG)) }
    }

    exportSVGButton.addActionListener {
      actionListeners.forEach { it(EventModelActionEvent(EventModelActions.ExportSVG)) }
    }

    exportModelButton.addActionListener {
      actionListeners.forEach { it(EventModelActionEvent(EventModelActions.ExportModel)) }
    }

    zoomSlider.addChangeListener {
      val publish =
          VisualiseModelEvent(selectedCommand, exclusions.toList(), zoomSlider.value / 100.0)
      visualiseListeners.forEach { it(publish) }
    }
  }

  fun addVisualiseListener(listener: (VisualiseModelEvent) -> Unit) {
    visualiseListeners.add(listener)
  }

  fun addActionListener(listener: (EventModelActionEvent) -> Unit) {
    actionListeners.add(listener)
  }
}

data class VisualiseModelEvent(
    val initialCommand: String?,
    val exclude: List<String>,
    val scale: Double
)

enum class EventModelActions {
  CopyImageToClipboard,
  ExportPNG,
  ExportSVG,
  ExportModel
}

data class EventModelActionEvent(val action: EventModelActions)
