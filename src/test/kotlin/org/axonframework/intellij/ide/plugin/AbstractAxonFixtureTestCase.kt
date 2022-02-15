/*
 *  Copyright (c) 2022. Axon Framework
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package org.axonframework.intellij.ide.plugin

import com.intellij.codeInsight.daemon.GutterMark
import com.intellij.codeInsight.daemon.LineMarkerInfo
import com.intellij.codeInsight.daemon.LineMarkerProvider
import com.intellij.codeInsight.daemon.RelatedItemLineMarkerInfo
import com.intellij.codeInsight.daemon.impl.LineMarkersPass
import com.intellij.openapi.editor.LogicalPosition
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.testFramework.LightProjectDescriptor
import com.intellij.testFramework.PsiTestUtil
import com.intellij.testFramework.fixtures.LightJavaCodeInsightFixtureTestCase
import com.intellij.util.PathUtil
import org.axonframework.config.ProcessingGroup
import org.axonframework.eventhandling.EventHandler
import org.axonframework.eventsourcing.EventSourcingHandler
import org.axonframework.intellij.ide.plugin.util.toContainerText
import org.axonframework.intellij.ide.plugin.util.toElementText
import org.axonframework.intellij.ide.plugin.util.toIcon
import org.axonframework.modelling.command.AggregateMember
import org.jetbrains.kotlin.utils.addToStdlib.firstNotNullResult
import javax.swing.Icon

abstract class AbstractAxonFixtureTestCase : LightJavaCodeInsightFixtureTestCase() {
    override fun setUp() {
        super.setUp()

        PsiTestUtil.addLibrary(module, PathUtil.getJarPathForClass(ProcessingGroup::class.java))
        PsiTestUtil.addLibrary(module, PathUtil.getJarPathForClass(EventHandler::class.java))
        PsiTestUtil.addLibrary(module, PathUtil.getJarPathForClass(AggregateMember::class.java))
        PsiTestUtil.addLibrary(module, PathUtil.getJarPathForClass(EventSourcingHandler::class.java))

        /* Mock the Instant class, or some tests won't run */
        addFile("Instant.java", """
            public class Instant {
                public static Instant now() {
                    return null;
                }
            }
        """.trimIndent(), "java.time")
    }

    override fun getTestDataPath(): String {
        return "src/test/testData/"
    }

    override fun getProjectDescriptor(): LightProjectDescriptor {
        return JAVA_11
    }

    /**
     * Automatically added to source files in testcases, for convenience
     */
    private val autoImports = listOf(
            "org.axonframework.config.ProcessingGroup",
            "org.axonframework.eventhandling.EventHandler",
            "org.axonframework.eventsourcing.EventSourcingHandler",
            "org.axonframework.modelling.command.AggregateLifecycle",
            "org.axonframework.modelling.command.AggregateRoot",
            "org.axonframework.commandhandling.CommandHandler",
            "org.axonframework.commandhandling.RoutingKey",
            "org.axonframework.modelling.command.CommandHandlerInterceptor",
            "org.axonframework.modelling.command.TargetAggregateIdentifier",
            "org.axonframework.modelling.command.AggregateIdentifier",
            "org.axonframework.modelling.command.EntityId",
            "org.axonframework.queryhandling.QueryHandler",
            "org.axonframework.modelling.saga.SagaEventHandler",
            "org.axonframework.deadline.annotation.DeadlineHandler",
            "org.axonframework.deadline.DeadlineManager",
    )

    /**
     * Adds a file to the project with given name/path and content.
     * You can skipp importing any Axon annotations defined in autoImports, they are added automagically.
     *
     * @see autoImports
     */
    fun addFile(name: String, text: String, pckg: String = "test"): VirtualFile {
        val newLineChar = if (name.endsWith(".java")) ";" else ""
        var content = ""
        content += "package $pckg$newLineChar\n\n"
        autoImports.forEach { content += "import $it$newLineChar\n" }
        content += ("\n" + text)
        return myFixture.addFileToProject(name, content).virtualFile
    }

    private val offset = autoImports.size + 3

    /**
     * Retrieve marker options on a certain line number. The line number is the line number in the 'text' param you pass
     * to the `addFile` method.
     * The offset based on that is calculated automagically.
     */
    fun getOptionsGivenByMarkerProviderAtCaretPosition(lineNum: Int, clazz: Class<out LineMarkerProvider>): List<OptionSummary> {
        myFixture.editor.caretModel.moveToLogicalPosition(LogicalPosition(offset + lineNum - 1, 0))
        val gutters = myFixture.findGuttersAtCaret()
        val marker = gutters.firstNotNullResult { getHandlerMethodMakerProviders(it, clazz) }
                ?: throw IllegalStateException("No gutter found")
        val items = marker.createGotoRelatedItems()
        return items.map {
            val element = it.element!!
            OptionSummary(element.toElementText(), element.toContainerText(), element.toIcon())
        }
    }

    private fun getHandlerMethodMakerProviders(gutter: GutterMark, clazz: Class<out LineMarkerProvider>): RelatedItemLineMarkerInfo<*>? {
        val renderer = gutter as? LineMarkerInfo.LineMarkerGutterIconRenderer<*> ?: return null
        val element = renderer.lineMarkerInfo.element!!
        val project = element.project
        val provider = LineMarkersPass.getMarkerProviders(element.containingFile.language, project)
                .filterIsInstance(clazz)
                .firstOrNull() ?: return null

        return provider.getLineMarkerInfo(element) as RelatedItemLineMarkerInfo?
    }

    data class OptionSummary(
            val text: String,
            val containerText: String?,
            val icon: Icon
    )
}
