package com.delicap.schema

import com.google.auto.value.AutoValue
import com.google.gson.Gson
import com.google.gson.TypeAdapter
import com.google.gson.annotations.SerializedName
import com.squareup.javapoet.*
import groovy.json.JsonException
import groovy.json.JsonSlurper
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.internal.impldep.com.google.common.base.CaseFormat
import org.gradle.internal.impldep.org.apache.http.HttpEntity
import org.gradle.internal.impldep.org.apache.http.HttpHost
import org.gradle.internal.impldep.org.apache.http.auth.AuthScope
import org.gradle.internal.impldep.org.apache.http.auth.UsernamePasswordCredentials
import org.gradle.internal.impldep.org.apache.http.client.CredentialsProvider
import org.gradle.internal.impldep.org.apache.http.client.methods.CloseableHttpResponse
import org.gradle.internal.impldep.org.apache.http.client.methods.HttpGet
import org.gradle.internal.impldep.org.apache.http.conn.ssl.NoopHostnameVerifier
import org.gradle.internal.impldep.org.apache.http.impl.client.BasicCredentialsProvider
import org.gradle.internal.impldep.org.apache.http.impl.client.CloseableHttpClient
import org.gradle.internal.impldep.org.apache.http.impl.client.HttpClientBuilder

import javax.lang.model.SourceVersion
import javax.lang.model.element.Modifier

class SchemaPluginExtension {
    String username = "M6E6T38GMYHV47ENWE9Y6CLYE3BNQ1SJ"
    String hostUrl = "https://www.delicap.es"
    String apiUrl = "/pstest/api/"
    String packageName = "com.delicap.schema"
}

class SchemaPlugin implements Plugin<Project> {
    // $output_Folder
    // $output_package
    // $parent_abstract_class_fully_qualified_name
    // $interface_fully_qualified_name + ID TypeName

    private void fetchResource(Project project, CloseableHttpClient client, HttpHost target, String resource) {
        String apiUrl = project.schema.apiUrl + resource + "?output_format=XML&schema=synopsis"
        CloseableHttpResponse response = client.execute(target, new HttpGet(apiUrl))
        try {
            def xmlFile = new File("./build/output/" + resource + ".xml")
            HttpEntity entity = response.getEntity()

            // Write XML response to file
            def xmlParser = new XmlParser()
            xmlFile.withPrintWriter { out ->
                def printer = new XmlNodePrinter(out)
                def node = xmlParser.parse(entity.getContent())
                printer.preserveWhitespace = true
                printer.print(node)
            }

            // Generate java source code
            def node = xmlParser.parse(xmlFile)
            String packageName = project.schema.packageName
            ClassName identifiable = ClassName.get(packageName, "Identifiable")
            ClassName nonnull = ClassName.get("android.support.annotation", "NonNull")
            ClassName nullable = ClassName.get("android.support.annotation", "Nullable")
            // Bypass <prestashop> node
            Node child = node.children().get(0)
            def className = CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.UPPER_CAMEL, child.name().toString())
            ClassName classRef = ClassName.get(packageName, className)
            // Gson TypeAdapter
            MethodSpec typeAdapterSpec = MethodSpec.methodBuilder("typeAdapter")
                    .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                    .returns(ParameterizedTypeName.get(ClassName.get(TypeAdapter.class), classRef))
                    .addParameter(Gson.class, "gson", Modifier.FINAL)
                    .addStatement('return new AutoValue_$T.GsonTypeAdapter(gson)', classRef)
                    .build()
            // public abstract <ID> id()
            AnnotationSpec idAnnotationSpec = AnnotationSpec.builder(SerializedName.class)
                    .addMember("value", '$S', "id")
                    .build()
            MethodSpec idMethodSpec = MethodSpec.methodBuilder("id")
                    .addAnnotation(idAnnotationSpec)
                    .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                    .returns(TypeName.INT.box())
                    .build()
            def typeSpecBuilder = TypeSpec.classBuilder(className)
                    .addSuperinterface(ParameterizedTypeName.get(identifiable, TypeName.INT.box()))
                    .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                    .addAnnotation(AutoValue.class)
                    .addMethod(typeAdapterSpec)
                    .addMethod(idMethodSpec)
            // Traverse children nodes
            child.children().each {
                String nodeName = it.name()
                String nodeFormat = it.attribute("format")
                String nodeRequired = it.attribute("required")
                String methodName = CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, nodeName)

                if (!SourceVersion.isName(methodName)) {
                    methodName = "_" + methodName
                }

                Class<?> returnType = String.class
                if (nodeFormat != null) {
                    try {
                        FormatValueType formatValueType = FormatValueType.valueOf(nodeFormat)
                        returnType = formatValueType.type
                    } catch (IllegalArgumentException e) {
                        println(e)
                    }
                }

                AnnotationSpec annotationSpec = AnnotationSpec.builder(SerializedName.class)
                        .addMember("value", '$S', nodeName)
                        .build()
                MethodSpec methodSpec = MethodSpec.methodBuilder(methodName)
                        .addModifiers(Modifier.ABSTRACT, Modifier.PUBLIC)
                        .addAnnotation((nodeRequired != null && nodeRequired.toBoolean()) ? nonnull : nullable)
                        .addAnnotation(annotationSpec)
                        .returns(returnType)
                        .build()
                typeSpecBuilder.addMethod(methodSpec)
            }

            TypeSpec nodeSpec = typeSpecBuilder.build()
            JavaFile javaFile = JavaFile.builder(packageName, nodeSpec).build()
            javaFile.writeTo(new File("./build/output/"))
        } finally {
            response.close()
        }
    }

    void apply(Project project) {
        // Add the 'schema' extension object
        project.extensions.create("schema", SchemaPluginExtension)

        CredentialsProvider credentialsProvider = new BasicCredentialsProvider()
        credentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(project.schema.username, ""))

        CloseableHttpClient client = HttpClientBuilder.create()
                .setSSLHostnameVerifier(new NoopHostnameVerifier())
                .setDefaultCredentialsProvider(credentialsProvider)
                .build()

        HttpHost target = HttpHost.create(project.schema.hostUrl)

        String apiUrl = project.schema.apiUrl + "?output_format=JSON&schema=blank"
        CloseableHttpResponse response = client.execute(target, new HttpGet(apiUrl))
        try {
            def slurper = new JsonSlurper()
            HttpEntity entity = response.getEntity()
            def result = slurper.parse(entity.getContent())
            result.each {
                println it
                try {
                    this.fetchResource(project, client, target, it.toString())
                } catch (JsonException e) {
                    print(e)
                }
            }
        } finally {
            response.close()
        }
    }
}
