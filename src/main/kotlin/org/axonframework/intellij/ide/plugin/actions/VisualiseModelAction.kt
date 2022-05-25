package org.axonframework.intellij.ide.plugin.actions

import com.google.gson.GsonBuilder
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.progress.runBackgroundableTask
import com.intellij.openapi.ui.Messages
import com.intellij.openapi.ui.popup.JBPopupFactory
import com.intellij.ui.awt.RelativePoint
import org.axonframework.intellij.ide.plugin.AxonIcons
import org.axonframework.intellij.ide.plugin.visualiser.EventModelBuilder
import org.axonframework.intellij.ide.plugin.visualiser.EventModelVisualisationFactory
import org.axonframework.intellij.ide.plugin.visualiser.ProjectModelBuilder
import java.awt.Point

/**
 * Action that visualises the Axon Command/Event/Query data as event modelling diagrams.
 */
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
            println("Starting to build model....")
            val structure = ProjectModelBuilder(project).build()

            val gson = GsonBuilder().setPrettyPrinting().create()
            println(gson.toJson(structure))

            ApplicationManager.getApplication().invokeLater {
              val selectedCommand =
                  Messages.showEditableChooseDialog(
                      "Select the command to initiate the event model with",
                      "Select Starting Command",
                      null,
                      structure.commandNames().toTypedArray(),
                      null,
                      null)
              println("Initial command: $selectedCommand")
              if (selectedCommand != null) {
                val eventModel = EventModelBuilder(structure).build(selectedCommand)
                ApplicationManager.getApplication().invokeLater {
                    val factory = EventModelVisualisationFactory(eventModel)
                    val builder = JBPopupFactory.getInstance().createComponentPopupBuilder(factory.createVisualisation(), null)
                    builder.createPopup().show(RelativePoint.fromScreen(Point(0, 0)))
                }
              }
            }
          }
        }
      }
    }
  }
}