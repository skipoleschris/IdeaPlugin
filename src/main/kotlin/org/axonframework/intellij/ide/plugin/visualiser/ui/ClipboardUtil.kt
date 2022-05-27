package org.axonframework.intellij.ide.plugin.visualiser.ui

import java.awt.Image
import java.awt.Toolkit
import java.awt.datatransfer.Clipboard
import java.awt.datatransfer.DataFlavor
import java.awt.datatransfer.Transferable
import java.awt.datatransfer.UnsupportedFlavorException
import java.awt.image.BufferedImage

object ClipboardUtil {
  private val clipboard: Clipboard = Toolkit.getDefaultToolkit().systemClipboard

  fun copyToClipboard(image: BufferedImage) {
    clipboard.setContents(TransferableImage(image), null)
  }

  private class TransferableImage(private val image: Image) : Transferable {
    @Throws(UnsupportedFlavorException::class)
    override fun getTransferData(flavor: DataFlavor): Any {
      return if (flavor.equals(DataFlavor.imageFlavor)) image
      else throw UnsupportedFlavorException(flavor)
    }

    override fun getTransferDataFlavors() = arrayOf(DataFlavor.imageFlavor)

    override fun isDataFlavorSupported(flavor: DataFlavor) = transferDataFlavors.contains(flavor)
  }
}
