package org.axonframework.intellij.ide.plugin.visualiser.ui

import java.awt.Dimension
import javax.swing.BorderFactory
import javax.swing.JButton
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.JSeparator
import javax.swing.JTextField
import javax.swing.event.DocumentEvent
import javax.swing.event.DocumentListener

class SearchPanel(private val possibleValues: List<String>) : JPanel() {

  private val search = JTextField(20)
  private val countLabel = JLabel("")
  private val previousButton = JButton("\u1431")
  private val nextButton = JButton("\u142f")

  private val matchedValues: MutableList<String> = mutableListOf()
  private var matchIndex: Int = 0

  private val listeners: MutableList<(SearchSelectionEvent) -> Unit> = mutableListOf()

  init {

    add(search)
    add(countLabel)
    add(JSeparator(JSeparator.VERTICAL))
    add(previousButton)
    add(nextButton)

    countLabel.preferredSize = Dimension(50, 20)
    previousButton.border = BorderFactory.createEmptyBorder()
    nextButton.border = BorderFactory.createEmptyBorder()

    previousButton.isEnabled = false
    nextButton.isEnabled = false

    search.document.addDocumentListener(
        object : DocumentListener {
          override fun insertUpdate(e: DocumentEvent?) {
            updateSearch()
          }

          override fun removeUpdate(e: DocumentEvent?) {
            updateSearch()
          }

          override fun changedUpdate(e: DocumentEvent?) {
            updateSearch()
          }
        })

    nextButton.addActionListener {
      matchIndex++
      if (matchIndex == matchedValues.size) nextButton.isEnabled = false
      previousButton.isEnabled = true
      countLabel.text = "$matchIndex / ${matchedValues.size}"
      listeners.forEach { it(SearchSelectionEvent(matchedValues[matchIndex - 1])) }
    }

    previousButton.addActionListener {
      matchIndex--
      if (matchIndex == 1) previousButton.isEnabled = false
      if (matchedValues.size > 1) nextButton.isEnabled = true
      countLabel.text = "$matchIndex / ${matchedValues.size}"
      listeners.forEach { it(SearchSelectionEvent(matchedValues[matchIndex - 1])) }
    }
  }

  private fun updateSearch() {
    val matches = possibleValues.filter { it.split(".").last().contains(search.text, true) }
    if (matches.isEmpty()) {
      countLabel.text = ""
      nextButton.isEnabled = false
      previousButton.isEnabled = false
    } else {
      matchedValues.clear()
      matchedValues.addAll(matches)
      matchIndex = 1
      countLabel.text = "$matchIndex / ${matchedValues.size}"
      if (matchedValues.size > 1) nextButton.isEnabled = true
      listeners.forEach { it(SearchSelectionEvent(matchedValues[matchIndex - 1])) }
    }
  }

  fun addSearchSelectionListener(f: (SearchSelectionEvent) -> Unit) {
    listeners.add(f)
  }
}

data class SearchSelectionEvent(val item: String)
