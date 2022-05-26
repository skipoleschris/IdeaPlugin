package org.axonframework.intellij.ide.plugin.visualiser.ui

import com.intellij.openapi.ui.DialogWrapper
import org.axonframework.intellij.ide.plugin.visualiser.AxonProjectModel
import org.axonframework.intellij.ide.plugin.visualiser.EventModelBuilder

class EventModelDialogWrapper(structure: AxonProjectModel) : DialogWrapper(true) {

  private val modelBuilder = EventModelBuilder(structure)
  private val scrollPane = EventModelScrollPane()
  private val selectorPanel = EventModelSelectorPanel(structure)

  init {
    title = "Event Modelling Visualisation"
    init()

    selectorPanel.addListener { event ->
      if (event.initialCommand != null) {
        scrollPane.visualise(modelBuilder.build(event.initialCommand, event.exclude))
      } else scrollPane.clear()
    }
  }

  override fun createCenterPanel() = scrollPane

  override fun createNorthPanel() = selectorPanel
}
