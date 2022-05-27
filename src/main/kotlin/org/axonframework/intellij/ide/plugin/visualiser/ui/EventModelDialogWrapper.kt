package org.axonframework.intellij.ide.plugin.visualiser.ui

import com.google.gson.GsonBuilder
import com.intellij.openapi.fileChooser.FileChooserFactory
import com.intellij.openapi.fileChooser.FileSaverDescriptor
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.openapi.ui.popup.JBPopupFactory
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.ui.awt.RelativePoint
import java.io.FileWriter
import javax.imageio.ImageIO
import javax.swing.BorderFactory
import javax.swing.JTextArea
import org.axonframework.intellij.ide.plugin.visualiser.AxonProjectModel
import org.axonframework.intellij.ide.plugin.visualiser.CommandPostIt
import org.axonframework.intellij.ide.plugin.visualiser.EventModelBuilder
import org.axonframework.intellij.ide.plugin.visualiser.EventPostIt
import org.axonframework.intellij.ide.plugin.visualiser.HandlerType
import org.axonframework.intellij.ide.plugin.visualiser.ViewPostIt

class EventModelDialogWrapper(structure: AxonProjectModel) : DialogWrapper(true) {

  private val modelBuilder = EventModelBuilder(structure)
  private val scrollPane = EventModelScrollPane()
  private val selectorPanel = EventModelSelectorPanel(structure)

  init {
    title = "Event Modelling Visualisation"
    init()

    selectorPanel.addVisualiseListener { event ->
      if (event.initialCommand != null) {
        scrollPane.visualise(modelBuilder.build(event.initialCommand, event.exclude))
      } else scrollPane.clear()
    }

    selectorPanel.addActionListener { event ->
      when (event.action) {
        EventModelActions.CopyImageToClipboard -> {
          val image = scrollPane.currentImage()
          if (image != null) ClipboardUtil.copyToClipboard(image)
        }
        EventModelActions.ExportImage -> {
          val descriptor =
              FileSaverDescriptor(
                  "Export Image", "Choose the location to export the event model image", "png")
          val nullFile: VirtualFile? = null
          val wrapper =
              FileChooserFactory.getInstance()
                  .createSaveFileDialog(descriptor, scrollPane)
                  .save(nullFile, "${scrollPane.currentCommandFocus()}.png")

          if (wrapper != null) {
            ImageIO.write(scrollPane.currentImage(), "png", wrapper.file)
          }
        }
        EventModelActions.ExportModel -> {
          val descriptor =
              FileSaverDescriptor(
                  "Export Model", "Choose the location to export json event model document", "json")
          val nullFile: VirtualFile? = null
          val wrapper =
              FileChooserFactory.getInstance()
                  .createSaveFileDialog(descriptor, scrollPane)
                  .save(nullFile, "model.json")

          if (wrapper != null) {
            with(FileWriter(wrapper.file)) {
              val gson = GsonBuilder().setPrettyPrinting().create()
              gson.toJson(structure, this)
            }
          }
        }
      }
    }

    scrollPane.addListener { evt ->
      when (evt.postIt) {
        is CommandPostIt -> {
          val command = evt.postIt.command
          val builder = StringBuilder()

          builder.append("Created by:")
          command.createdBy.names.forEach {
            builder.append("\n  - ")
            builder.append(it)
          }

          builder.append("\n\nHandled by:")
          builder.append("\n  ")
          when (command.handledBy.type) {
            HandlerType.EventHandler -> builder.append("[Event Handler] ")
            HandlerType.CommandHandler -> builder.append("[Command Handler] ")
            HandlerType.Saga -> builder.append("[Saga] ")
            HandlerType.Aggregate -> builder.append("[Aggregate] ")
            HandlerType.AggregateEventSource -> builder.append("[Aggregate Event Source] ")
          }
          builder.append(command.handledBy.name)

          if (command.handledBy.events.isNotEmpty()) {
            builder.append("\n\nGenerated events:")
            command.handledBy.events.forEach {
              builder.append("\n  - ")
              builder.append(it.name)
            }
          }

          if (command.handledBy.commands.isNotEmpty()) {
            builder.append("\n\nIssued commands:")
            command.handledBy.commands.forEach {
              builder.append("\n  - ")
              builder.append(it.name)
            }
          }

          val detail = JTextArea(builder.toString())
          detail.isEditable = false
          detail.border = BorderFactory.createEmptyBorder(10, 10, 10, 10)
          JBPopupFactory.getInstance()
              .createComponentPopupBuilder(detail, null)
              .setTitle("Command: ${command.name}")
              .createPopup()
              .show(RelativePoint(evt.mouseEvent))
        }
        is EventPostIt -> {
          val event = evt.postIt.event
          val builder = StringBuilder()

          builder.append("Created by:")
          event.createdBy.names.forEach {
            builder.append("\n  - ")
            builder.append(it)
          }

          builder.append("\n\nHandled by:")

          event.handledBy.forEach { handler ->
            builder.append("\n  ")
            when (handler.type) {
              HandlerType.EventHandler -> {
                if (handler.isViewModel()) builder.append("[View] ")
                else builder.append("[Event Handler] ")
              }
              HandlerType.CommandHandler -> builder.append("[Command Handler] ")
              HandlerType.Saga -> builder.append("[Saga] ")
              HandlerType.Aggregate -> builder.append("[Aggregate] ")
              HandlerType.AggregateEventSource -> builder.append("[Aggregate Event Source] ")
            }
            builder.append(handler.name)

            if (handler.events.isNotEmpty()) {
              builder.append("\n    Generated events:")
              handler.events.forEach {
                builder.append("\n      - ")
                builder.append(it.name)
              }
            }

            if (handler.commands.isNotEmpty()) {
              builder.append("\n    Issued commands:")
              handler.commands.forEach {
                builder.append("\n      - ")
                builder.append(it.name)
              }
            }
          }

          val detail = JTextArea(builder.toString())
          detail.isEditable = false
          detail.border = BorderFactory.createEmptyBorder(10, 10, 10, 10)
          JBPopupFactory.getInstance()
              .createComponentPopupBuilder(detail, null)
              .setTitle("Event: ${event.name}")
              .createPopup()
              .show(RelativePoint(evt.mouseEvent))
        }
        is ViewPostIt -> {
          val view = evt.postIt.name
          val builder = StringBuilder()

          builder.append("Updated by:")
          structure.eventsReferencingView(view).forEach {
            builder.append("\n  - ")
            builder.append(it)
          }

          val detail = JTextArea(builder.toString())
          detail.isEditable = false
          detail.border = BorderFactory.createEmptyBorder(10, 10, 10, 10)
          JBPopupFactory.getInstance()
              .createComponentPopupBuilder(detail, null)
              .setTitle("View: $view")
              .createPopup()
              .show(RelativePoint(evt.mouseEvent))
        }
        else -> {}
      }
    }
  }

  override fun createCenterPanel() = scrollPane

  override fun createNorthPanel() = selectorPanel
}
