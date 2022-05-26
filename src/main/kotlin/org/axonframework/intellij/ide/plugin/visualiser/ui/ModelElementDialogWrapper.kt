package org.axonframework.intellij.ide.plugin.visualiser.ui

import com.intellij.openapi.ui.DialogWrapper
import com.intellij.ui.components.JBScrollPane
import java.awt.Component
import java.awt.Dimension
import javax.swing.BoxLayout
import javax.swing.JButton
import javax.swing.JCheckBox
import javax.swing.JLabel
import javax.swing.JPanel
import org.axonframework.intellij.ide.plugin.visualiser.AxonProjectModel

class ModelElementDialogWrapper(model: AxonProjectModel, exclusions: List<String>) :
    DialogWrapper(true) {

  private val itemsPanel: JPanel = JPanel()
  private val scroller: JBScrollPane = JBScrollPane(itemsPanel)
  private val buttonPanel: JPanel = JPanel()
  private val checkBoxes = mutableListOf<JCheckBox>()

  init {
    title = "Customise Model Elements"
    scroller.preferredSize = Dimension(500, 800)

    itemsPanel.layout = BoxLayout(itemsPanel, BoxLayout.PAGE_AXIS)
    itemsPanel.alignmentX = Component.LEFT_ALIGNMENT

    itemsPanel.add(JLabel("Commands:"))
    model.commandNames().forEach {
      val checkBox = JCheckBox(it)
      checkBoxes.add(checkBox)
      itemsPanel.add(checkBox)
    }

    itemsPanel.add(JLabel("Events:"))
    model.eventNames().forEach {
      val checkBox = JCheckBox(it)
      checkBoxes.add(checkBox)
      itemsPanel.add(checkBox)
    }

    itemsPanel.add(JLabel("Views:"))
    model.viewNames().forEach {
      val checkBox = JCheckBox(it)
      checkBoxes.add(checkBox)
      itemsPanel.add(checkBox)
    }

    val selectAll = JButton("Select All")
    selectAll.addActionListener { checkBoxes.forEach { it.isSelected = true } }
    val deselectAll = JButton("Deselect All")
    deselectAll.addActionListener { checkBoxes.forEach { it.isSelected = false } }
    buttonPanel.add(selectAll)
    buttonPanel.add(deselectAll)

    checkBoxes.forEach { it.isSelected = !exclusions.contains(it.text) }
    init()
  }

  override fun createCenterPanel() = scroller

  override fun createNorthPanel() = buttonPanel

  fun deselectedItems(): List<String> =
      checkBoxes.filter { !it.isSelected }.map { it.text }.toList()
}
