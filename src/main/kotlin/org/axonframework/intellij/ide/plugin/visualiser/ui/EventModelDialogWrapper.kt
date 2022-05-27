package org.axonframework.intellij.ide.plugin.visualiser.ui

import com.google.gson.GsonBuilder
import com.intellij.openapi.fileChooser.FileChooserFactory
import com.intellij.openapi.fileChooser.FileSaverDescriptor
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.openapi.vfs.VirtualFile
import java.io.FileWriter
import javax.imageio.ImageIO
import org.axonframework.intellij.ide.plugin.visualiser.AxonProjectModel
import org.axonframework.intellij.ide.plugin.visualiser.EventModelBuilder

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
  }

  override fun createCenterPanel() = scrollPane

  override fun createNorthPanel() = selectorPanel
}
