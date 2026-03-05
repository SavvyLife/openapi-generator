/*
 * Copyright 2018 OpenAPI-Generator Contributors (https://openapi-generator.tech)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.openapitools.codegen.languages;

import org.openapitools.codegen.*;
import org.openapitools.codegen.meta.GeneratorMetadata;
import org.openapitools.codegen.meta.Stability;
import org.openapitools.codegen.meta.features.*;

import java.io.File;
import java.util.EnumSet;

public class PythonUnboundClientCodegen extends AbstractPythonCodegen implements CodegenConfig {

    public static final String GENERATOR_NAME = "python-unbound";
    protected String apiDocPath = "docs/";
    protected String modelDocPath = "docs/";

    public PythonUnboundClientCodegen() {
        super();

        // Force sortParamsByRequiredFlag to true
        sortParamsByRequiredFlag = true;

        modifyFeatureSet(features -> features
                .includeDocumentationFeatures(DocumentationFeature.Readme)
                .wireFormatFeatures(EnumSet.of(WireFormatFeature.JSON))
                .excludeGlobalFeatures(
                        GlobalFeature.XMLStructureDefinitions,
                        GlobalFeature.Callbacks,
                        GlobalFeature.LinkObjects,
                        GlobalFeature.ParameterStyling
                )
                .includeSchemaSupportFeatures(
                        SchemaSupportFeature.Polymorphism,
                        SchemaSupportFeature.allOf,
                        SchemaSupportFeature.oneOf,
                        SchemaSupportFeature.anyOf
                )
                .excludeParameterFeatures(
                        ParameterFeature.Cookie
                )
        );

        generatorMetadata = GeneratorMetadata.newBuilder(generatorMetadata)
                .stability(Stability.BETA)
                .build();

        // Clear import mapping
        importMapping.clear();

        // Type mappings
        typeMapping.put("array", "List");
        typeMapping.put("set", "List");
        typeMapping.put("map", "Dict");
        typeMapping.put("decimal", "decimal.Decimal");
        typeMapping.put("file", "bytearray");
        typeMapping.put("binary", "bytearray");
        typeMapping.put("ByteArray", "bytearray");

        languageSpecificPrimitives.remove("file");
        languageSpecificPrimitives.add("decimal.Decimal");
        languageSpecificPrimitives.add("bytearray");
        languageSpecificPrimitives.add("none_type");

        useAliasGenerator = true;

        supportsInheritance = true;
        modelPackage = "models";
        apiPackage = "api";
        outputFolder = "generated-code" + File.separatorChar + "python-unbound";

        modelTemplateFiles.put("model.mustache", ".py");
        apiTemplateFiles.put("api.mustache", ".py");

        embeddedTemplateDir = templateDir = "python-unbound";

        modelDocTemplateFiles.put("model_doc.mustache", ".md");
        apiDocTemplateFiles.put("api_doc.mustache", ".md");

        // Default HIDE_GENERATION_TIMESTAMP to true
        hideGenerationTimestamp = Boolean.TRUE;

        // CLI options
        cliOptions.clear();
        cliOptions.add(new CliOption(CodegenConstants.PACKAGE_NAME, "python package name (convention: snake_case).")
                .defaultValue("openapi_client"));
        cliOptions.add(new CliOption(CodegenConstants.PROJECT_NAME, "python project name in setup.py (e.g. petstore-api)."));
        cliOptions.add(new CliOption(CodegenConstants.PACKAGE_VERSION, "python package version.")
                .defaultValue("1.0.0"));
        cliOptions.add(new CliOption(CodegenConstants.HIDE_GENERATION_TIMESTAMP, CodegenConstants.HIDE_GENERATION_TIMESTAMP_DESC)
                .defaultValue(Boolean.TRUE.toString()));
        cliOptions.add(new CliOption(CodegenConstants.SOURCECODEONLY_GENERATION, CodegenConstants.SOURCECODEONLY_GENERATION_DESC)
                .defaultValue(Boolean.FALSE.toString()));
    }

    @Override
    public void processOpts() {
        this.setLegacyDiscriminatorBehavior(false);

        super.processOpts();

        // Map to Dot instead of Period
        specialCharReplacements.put(".", "Dot");

        if (additionalProperties.containsKey(CodegenConstants.PACKAGE_NAME)) {
            setPackageName((String) additionalProperties.get(CodegenConstants.PACKAGE_NAME));
        }

        if (additionalProperties.containsKey(CodegenConstants.PROJECT_NAME)) {
            setProjectName((String) additionalProperties.get(CodegenConstants.PROJECT_NAME));
        } else {
            setProjectName(packageName.replaceAll("_", "-"));
        }

        if (additionalProperties.containsKey(CodegenConstants.PACKAGE_VERSION)) {
            setPackageVersion((String) additionalProperties.get(CodegenConstants.PACKAGE_VERSION));
        }

        additionalProperties.put(CodegenConstants.PROJECT_NAME, projectName);
        additionalProperties.put(CodegenConstants.PACKAGE_NAME, packageName);
        additionalProperties.put(CodegenConstants.PACKAGE_VERSION, packageVersion);

        Boolean generateSourceCodeOnly = false;
        if (additionalProperties.containsKey(CodegenConstants.SOURCECODEONLY_GENERATION)) {
            generateSourceCodeOnly = Boolean.valueOf(additionalProperties.get(CodegenConstants.SOURCECODEONLY_GENERATION).toString());
        }

        // Set doc paths - only nest in package when generating source code only
        if (generateSourceCodeOnly) {
            apiDocPath = packagePath() + "/" + apiDocPath;
            modelDocPath = packagePath() + "/" + modelDocPath;
        }
        additionalProperties.put("apiDocPath", apiDocPath);
        additionalProperties.put("modelDocPath", modelDocPath);

        String modelPath = packagePath() + File.separatorChar + modelPackage.replace('.', File.separatorChar);
        String apiPath = packagePath() + File.separatorChar + apiPackage.replace('.', File.separatorChar);

        // Supporting files
        if (!generateSourceCodeOnly) {
            supportingFiles.add(new SupportingFile("README.mustache", "", "README.md"));
            supportingFiles.add(new SupportingFile("pyproject.mustache", "", "pyproject.toml"));
            supportingFiles.add(new SupportingFile("gitignore.mustache", "", ".gitignore"));
            supportingFiles.add(new SupportingFile("py.typed.mustache", packagePath(), "py.typed"));
        }

        supportingFiles.add(new SupportingFile("configuration.mustache", packagePath(), "configuration.py"));
        supportingFiles.add(new SupportingFile("exceptions.mustache", packagePath(), "exceptions.py"));
        supportingFiles.add(new SupportingFile("utils.mustache", packagePath(), "utils.py"));
        supportingFiles.add(new SupportingFile("__init__package.mustache", packagePath(), "__init__.py"));
        supportingFiles.add(new SupportingFile("__init__model.mustache", modelPath, "__init__.py"));
        supportingFiles.add(new SupportingFile("__init__api.mustache", apiPath, "__init__.py"));

        // Handle nested package names
        String[] packageNameSplits = packageName.split("\\.");
        String currentPackagePath = "";
        for (int i = 0; i < packageNameSplits.length - 1; i++) {
            if (i > 0) {
                currentPackagePath = currentPackagePath + File.separatorChar;
            }
            currentPackagePath = currentPackagePath + packageNameSplits[i];
            supportingFiles.add(new SupportingFile("__init__.mustache", currentPackagePath, "__init__.py"));
        }

        modelPackage = this.packageName + "." + modelPackage;
        apiPackage = this.packageName + "." + apiPackage;
    }

    @Override
    public String toModelImport(String name) {
        String modelImport;
        if (name.startsWith("import") || name.startsWith("from")) {
            modelImport = name;
        } else {
            modelImport = "from ";
            if (!"".equals(modelPackage())) {
                modelImport += modelPackage() + ".";
            }
            modelImport += toModelFilename(name) + " import " + name;
        }
        return modelImport;
    }

    @Override
    public CodegenType getTag() {
        return CodegenType.CLIENT;
    }

    @Override
    public String getName() {
        return GENERATOR_NAME;
    }

    @Override
    public String getHelp() {
        return "Generates a Python client library using RequestClient for NATS-based communication.";
    }

    @Override
    public String apiFileFolder() {
        return outputFolder + File.separatorChar + apiPackage().replace('.', File.separatorChar);
    }

    @Override
    public String modelFileFolder() {
        return outputFolder + File.separatorChar + modelPackage().replace('.', File.separatorChar);
    }

    public String packagePath() {
        return packageName.replace('.', File.separatorChar);
    }

    @Override
    public String generatorLanguageVersion() {
        return "3.9+";
    }

    @Override
    public String apiDocFileFolder() {
        return outputFolder + File.separator + apiDocPath;
    }

    @Override
    public String modelDocFileFolder() {
        return outputFolder + File.separator + modelDocPath;
    }
}
