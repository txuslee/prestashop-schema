package com.delicap.schema

import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Before
import org.junit.Test

class SchemaPluginTest {
    @Before
    void setUp() throws Exception {

    }

    @Test
    void apply() throws Exception {
        Project project = ProjectBuilder.builder().build()
        project.pluginManager.apply 'com.delicap.schema'

    }
}