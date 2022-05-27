package org.axonframework.intellij.ide.plugin.actions

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.progress.runBackgroundableTask
import org.axonframework.intellij.ide.plugin.AxonIcons
import org.axonframework.intellij.ide.plugin.visualiser.ProjectModelBuilder
import org.axonframework.intellij.ide.plugin.visualiser.ui.EventModelDialogWrapper

/** Action that visualises the Axon Command/Event/Query data as event modelling diagrams. */
class VisualiseModelAction : AnAction(AxonIcons.Axon) {
  override fun update(e: AnActionEvent) {
    e.presentation.isEnabledAndVisible = true
  }

  override fun actionPerformed(e: AnActionEvent) {
    ApplicationManager.getApplication().executeOnPooledThread() {
      val project = e.project
      if (project != null) {
        runBackgroundableTask("Building Axon Structure Model", project) { indicator ->
          indicator.isIndeterminate = true
          ApplicationManager.getApplication().runReadAction {
            val structure = ProjectModelBuilder(project).build()

            ApplicationManager.getApplication().invokeLater {
              EventModelDialogWrapper(structure).show()
            }
          }
        }
      }
    }
  }
}
