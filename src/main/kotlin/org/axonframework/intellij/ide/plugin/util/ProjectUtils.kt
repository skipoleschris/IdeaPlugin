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

package org.axonframework.intellij.ide.plugin.util

import com.intellij.openapi.project.Project
import com.intellij.openapi.roots.OrderEnumerator

val versionRegex = Regex("^4\\.\\d+\\.\\d+$")

fun Project.isAxon4Project() = getAxonVersions().values.any { it.matches(versionRegex) }

fun Project.getAxonVersions() = OrderEnumerator.orderEntries(this)
    .librariesOnly()
    .productionOnly()
    .satisfying { it.presentableName.matches(Regex(".*(org\\.axonframework)+.*")) }
    .classes()
    .roots.associate {
        val name = it.name.replace(".jar", "").replace("-SNAPSHOT", "")
        val version = name.split("-").last()
        val actualName = name.replace("-${version}", "")
        actualName to version
    }
