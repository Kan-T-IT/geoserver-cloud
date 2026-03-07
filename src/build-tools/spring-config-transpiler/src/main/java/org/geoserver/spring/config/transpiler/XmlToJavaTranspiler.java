/* (c) 2026 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.spring.config.transpiler;

import com.palantir.javapoet.JavaFile;
import java.io.IOException;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import org.geoserver.spring.config.annotations.TranspileXmlConfig;
import org.geoserver.spring.config.transpiler.context.TranspilationContext;
import org.geoserver.spring.config.transpiler.generator.JavaFileGenerator;

/**
 * Core transpiler that converts XML Spring configurations to Java {@code @Configuration} classes.
 *
 * <p>This class orchestrates the transpilation process by:
 *
 * <ul>
 *   <li>Creating a transpilation context from the annotation parameters
 *   <li>Loading and parsing XML resources
 *   <li>Using bean method generators to generate Java code
 *   <li>Writing the generated files using the annotation processing API
 * </ul>
 *
 * <p>The transpiler follows a strategy pattern where different aspects of code generation are handled by specialized
 * generators, making the system extensible and maintainable.
 *
 * @since 3.0.0
 * @see TranspileXmlConfig
 * @see TranspilationContext
 * @see JavaFileGenerator
 */
public class XmlToJavaTranspiler {

    private final ProcessingEnvironment processingEnv;
    private final Messager messager;
    private final Filer filer;

    private final JavaFileGenerator javaFileGenerator;

    /**
     * Create a new transpiler with the given processing environment.
     *
     * @param processingEnv the annotation processing environment
     */
    public XmlToJavaTranspiler(ProcessingEnvironment processingEnv) {
        this.processingEnv = processingEnv;
        this.messager = processingEnv.getMessager();
        this.filer = processingEnv.getFiler();

        // Initialize the generator hierarchy
        this.javaFileGenerator = new JavaFileGenerator();
    }

    /**
     * Transpile XML configuration to Java code for the given annotated element.
     *
     * @param annotatedElement the class annotated with @TranspileXmlConfig
     * @param annotation the annotation instance containing configuration
     */
    public void transpile(TypeElement annotatedElement, TranspileXmlConfig annotation) {
        try {
            // Create transpilation context
            TranspilationContext context = createTranspilationContext(annotatedElement, annotation);

            // Generate Java file using strategy pattern
            JavaFile javaFile = javaFileGenerator.generateJavaFile(context);

            // Write the generated file
            writeJavaFile(javaFile);

            messager.printMessage(
                    Diagnostic.Kind.NOTE,
                    "Generated " + javaFile.packageName() + "."
                            + javaFile.typeSpec().name() + " from XML configuration transpilation");

        } catch (Exception e) {
            messager.printMessage(Diagnostic.Kind.ERROR, "Transpilation failed: " + e.getMessage(), annotatedElement);
        }
    }

    /** Create a transpilation context from the annotation and annotated element. */
    private TranspilationContext createTranspilationContext(
            TypeElement annotatedElement, TranspileXmlConfig annotation) {

        return TranspilationContext.builder()
                .annotatedElement(annotatedElement)
                .annotation(annotation)
                .processingEnvironment(processingEnv)
                .build();
    }

    /** Write the generated Java file using the annotation processing API. */
    private void writeJavaFile(JavaFile javaFile) throws IOException {
        javaFile.writeTo(filer);
    }
}
