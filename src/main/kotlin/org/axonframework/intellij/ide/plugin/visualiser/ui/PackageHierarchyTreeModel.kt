package org.axonframework.intellij.ide.plugin.visualiser.ui

import com.intellij.packageDependencies.ui.TreeModel
import javax.swing.event.TreeModelEvent
import javax.swing.event.TreeModelListener
import javax.swing.tree.DefaultMutableTreeNode
import javax.swing.tree.TreeNode
import org.scijava.swing.checkboxtree.CheckBoxNodeData

class PackageHierarchyTreeModel(fullyQualifiedClassNames: List<String>, exclusions: List<String>) :
    TreeModel(buildNodes(fullyQualifiedClassNames)) {

  private val repaintListeners: MutableList<() -> Unit> = mutableListOf()

  init {
    addTreeModelListener(
        object : TreeModelListener {
          override fun treeNodesChanged(e: TreeModelEvent?) {
            if (e != null) {
              val node =
                  (e.treePath.lastPathComponent as DefaultMutableTreeNode).getChildAt(
                      e.childIndices.first()) as DefaultMutableTreeNode

              if (!node.isLeaf) {
                val state = ((node.userObject) as CheckBoxNodeData).isChecked
                visitAllNodes(node, leafsOnly = false) {
                  (it.userObject as CheckBoxNodeData).isChecked = state
                }
              }

              checkParentNodesMarkedCorrectly(node)
              repaintListeners.forEach { it() }
            }
          }

          override fun treeNodesInserted(e: TreeModelEvent?) {}

          override fun treeNodesRemoved(e: TreeModelEvent?) {}

          override fun treeStructureChanged(e: TreeModelEvent?) {}
        })

    visitAllNodes(root as DefaultMutableTreeNode, leafsOnly = true) {
      if (exclusions.contains(pathToPackageName(it.path))) {
        (it.userObject as CheckBoxNodeData).isChecked = false
        checkParentNodesMarkedCorrectly(it)
      }
    }
  }

  fun addRepaintListener(f: () -> Unit) {
    repaintListeners.add(f)
  }

  fun selectAllNodes() {
    visitAllNodes(root as DefaultMutableTreeNode, leafsOnly = false) {
      (it.userObject as CheckBoxNodeData).isChecked = true
    }
  }

  fun deselectAllNodes() {
    visitAllNodes(root as DefaultMutableTreeNode, leafsOnly = false) {
      (it.userObject as CheckBoxNodeData).isChecked = false
    }
  }

  fun deselectedClassNames(): List<String> =
      visitAllNodes(root as DefaultMutableTreeNode) {
        val data = it.userObject as CheckBoxNodeData
        if (data.isChecked) null else pathToPackageName(it.path)
      }

  companion object PackageHierarchyTreeModelUtils {

    private fun buildNodes(classNames: List<String>): DefaultMutableTreeNode {
      val root = DefaultMutableTreeNode("root")
      addNodes(classNames.toList().map { it.split(".") }, root)
      return root
    }

    private fun addNodes(paths: List<List<String>>, parent: DefaultMutableTreeNode) {
      val packages = paths.groupBy { it.first() }.toSortedMap()
      packages.forEach { (currentPackage, fullPaths) ->
        val data = CheckBoxNodeData(currentPackage, true)
        val node = DefaultMutableTreeNode(data)
        parent.add(node)

        addNodes(fullPaths.map { it.drop(1) }.filterNot { it.isEmpty() }, node)
      }
    }

    private fun pathToPackageName(path: Array<TreeNode>): String =
        path.drop(1).joinToString(".") {
          ((it as DefaultMutableTreeNode).userObject as CheckBoxNodeData).text
        }
  }

  private fun <T> visitAllNodes(
      node: DefaultMutableTreeNode,
      leafsOnly: Boolean = true,
      f: (DefaultMutableTreeNode) -> T?
  ): List<T> {
    val result = mutableListOf<T>()
    if ((!leafsOnly || node.isLeaf) && (node.userObject is CheckBoxNodeData)) {
      val itemResult = f(node)
      if (itemResult != null) result.add(itemResult)
    }

    node.children().toList().forEach {
      result.addAll(visitAllNodes(it as DefaultMutableTreeNode, leafsOnly, f))
    }
    return result.toList()
  }

  private fun checkParentNodesMarkedCorrectly(node: DefaultMutableTreeNode) {
    val parent = node.parent as DefaultMutableTreeNode
    if (parent.isRoot) return

    val data = parent.userObject as CheckBoxNodeData
    data.isChecked =
        (parent.children().toList().any {
          ((it as DefaultMutableTreeNode).userObject as CheckBoxNodeData).isChecked
        })

    checkParentNodesMarkedCorrectly(parent)
  }
}
