package org.axonframework.intellij.ide.plugin.visualiser.ui

import com.intellij.openapi.ui.DialogWrapper
import com.intellij.ui.components.JBScrollPane
import com.intellij.ui.treeStructure.Tree
import java.awt.Component
import java.awt.Dimension
import javax.swing.BorderFactory
import javax.swing.BoxLayout
import javax.swing.JButton
import javax.swing.JLabel
import javax.swing.JPanel
import org.axonframework.intellij.ide.plugin.visualiser.AxonProjectModel
import org.scijava.swing.checkboxtree.CheckBoxNodeEditor
import org.scijava.swing.checkboxtree.CheckBoxNodeRenderer

class ModelElementDialogWrapper(model: AxonProjectModel, private val exclusions: List<String>) :
    DialogWrapper(true) {

  private val itemsPanel: JPanel = JPanel()
  private val buttonPanel: JPanel = JPanel()
  private val commandsTree = buildPackageTree(model.commandNames())
  private val eventsTree = buildPackageTree(model.eventNames())
  private val viewsTree = buildPackageTree(model.viewNames())

  init {
    title = "Customise Model Elements"

    itemsPanel.layout = BoxLayout(itemsPanel, BoxLayout.PAGE_AXIS)
    itemsPanel.alignmentX = Component.LEFT_ALIGNMENT
    itemsPanel.border = BorderFactory.createEmptyBorder(5, 5, 5, 5)

    val commandsLabel = JLabel("Commands:")
    commandsLabel.border = BorderFactory.createEmptyBorder(10, 0, 5, 0)
    itemsPanel.add(commandsLabel)
    val commandsScrollPane =
        JBScrollPane(
            commandsTree,
            JBScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
            JBScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED)
    commandsScrollPane.preferredSize = Dimension(450, 200)
    itemsPanel.add(commandsScrollPane)

    val eventsLabel = JLabel("Events:")
    eventsLabel.border = BorderFactory.createEmptyBorder(10, 0, 5, 0)
    itemsPanel.add(eventsLabel)
    val eventsScrollPane =
        JBScrollPane(
            eventsTree,
            JBScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
            JBScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED)
    eventsScrollPane.preferredSize = Dimension(450, 200)
    itemsPanel.add(eventsScrollPane)

    val viewsLabel = JLabel("Views:")
    viewsLabel.border = BorderFactory.createEmptyBorder(10, 0, 5, 0)
    itemsPanel.add(viewsLabel)
    val viewsScrollPane =
        JBScrollPane(
            viewsTree,
            JBScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
            JBScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED)
    viewsScrollPane.preferredSize = Dimension(450, 200)
    itemsPanel.add(viewsScrollPane)

    val selectAll = JButton("Select All")
    selectAll.addActionListener {
      (commandsTree.model as PackageHierarchyTreeModel).selectAllNodes()
      (eventsTree.model as PackageHierarchyTreeModel).selectAllNodes()
      (viewsTree.model as PackageHierarchyTreeModel).selectAllNodes()
      repaint()
    }
    val deselectAll = JButton("Deselect All")
    deselectAll.addActionListener {
      (commandsTree.model as PackageHierarchyTreeModel).deselectAllNodes()
      (eventsTree.model as PackageHierarchyTreeModel).deselectAllNodes()
      (viewsTree.model as PackageHierarchyTreeModel).deselectAllNodes()
      repaint()
    }
    buttonPanel.add(selectAll)
    buttonPanel.add(deselectAll)

    init()
  }

  private fun buildPackageTree(items: Set<String>): Tree {
    val treeModel = PackageHierarchyTreeModel(items.toList(), exclusions)
    val tree = Tree(treeModel)
    tree.cellRenderer = CheckBoxNodeRenderer()
    tree.cellEditor = CheckBoxNodeEditor(tree)
    tree.isEditable = true
    tree.isRootVisible = false
    expandAllNodes(tree)
    repaint()
    treeModel.addRepaintListener { tree.repaint() }

    return tree
  }

  private fun expandAllNodes(tree: Tree) {
    var j = tree.rowCount
    var i = 0
    while (i < j) {
      tree.expandRow(i)
      i += 1
      j = tree.rowCount
    }
  }

  override fun createCenterPanel() = itemsPanel

  override fun createNorthPanel() = buttonPanel

  fun deselectedItems(): List<String> =
      (commandsTree.model as PackageHierarchyTreeModel).deselectedClassNames() +
          (eventsTree.model as PackageHierarchyTreeModel).deselectedClassNames() +
          (viewsTree.model as PackageHierarchyTreeModel).deselectedClassNames()
}
