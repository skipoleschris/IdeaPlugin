package org.axonframework.intellij.ide.plugin.visualiser.ui

import com.intellij.openapi.ui.ComboBox
import com.intellij.openapi.util.IconLoader
import java.awt.Component
import java.awt.event.ItemEvent
import javax.swing.JButton
import javax.swing.JLabel
import javax.swing.JPanel
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
  private val exportImageIcon =
      IconLoader.getIcon("/icons/export-image.png", EventModelSelectorPanel::class.java)
  private val exportModelIcon =
      IconLoader.getIcon("/icons/export-model.png", EventModelSelectorPanel::class.java)

  init {
    alignmentX = Component.LEFT_ALIGNMENT

    val commandSelector = ComboBox(arrayOf(defaultCommand) + model.commandNames().toTypedArray())
    commandSelector.isEditable = false
    commandSelector.setMinimumAndPreferredWidth(500)

    val customiseButton = JButton(customiseIcon)
    customiseButton.toolTipText = "Customise items..."
    customiseButton.isEnabled = false

    val copyButton = JButton(copyIcon)
    copyButton.toolTipText = "Copy image to clipboard"
    copyButton.isEnabled = false

    val exportImageButton = JButton(exportImageIcon)
    exportImageButton.toolTipText = "Export image..."
    exportImageButton.isEnabled = false

    val exportModelButton = JButton(exportModelIcon)
    exportModelButton.toolTipText = "Export model..."
    exportModelButton.isEnabled = false

    add(JLabel("Select initial command:"))
    add(commandSelector)
    add(customiseButton)
    add(copyButton)
    add(exportImageButton)
    add(exportModelButton)

    commandSelector.addItemListener { event ->
      selectedCommand =
          if (event.stateChange == ItemEvent.SELECTED && event.item != defaultCommand) {
            event.item as String
          } else null

      val publish = VisualiseModelEvent(selectedCommand, exclusions.toList())
      visualiseListeners.forEach { it(publish) }
      customiseButton.isEnabled = (selectedCommand != null)
      copyButton.isEnabled = (selectedCommand != null)
      exportImageButton.isEnabled = (selectedCommand != null)
      exportModelButton.isEnabled = (selectedCommand != null)
    }

    customiseButton.addActionListener {
      val modelElementSelector = ModelElementDialogWrapper(model, exclusions.toList())
      if (modelElementSelector.showAndGet()) {
        exclusions.clear()
        exclusions.addAll(modelElementSelector.deselectedItems())
        val publish = VisualiseModelEvent(selectedCommand, exclusions.toList())
        visualiseListeners.forEach { it(publish) }
      }
    }

    copyButton.addActionListener {
      actionListeners.forEach { it(EventModelActionEvent(EventModelActions.CopyImageToClipboard)) }
    }

    exportImageButton.addActionListener {
      actionListeners.forEach { it(EventModelActionEvent(EventModelActions.ExportImage)) }
    }

    exportModelButton.addActionListener {
      actionListeners.forEach { it(EventModelActionEvent(EventModelActions.ExportModel)) }
    }
  }

  fun addVisualiseListener(listener: (VisualiseModelEvent) -> Unit) {
    visualiseListeners.add(listener)
  }

  fun addActionListener(listener: (EventModelActionEvent) -> Unit) {
    actionListeners.add(listener)
  }
}

data class VisualiseModelEvent(val initialCommand: String?, val exclude: List<String>)

enum class EventModelActions {
  CopyImageToClipboard,
  ExportImage,
  ExportModel
}

data class EventModelActionEvent(val action: EventModelActions)
