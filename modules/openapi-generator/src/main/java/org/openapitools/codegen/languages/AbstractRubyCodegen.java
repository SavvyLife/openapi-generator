/*
 * Copyright 2018 OpenAPI-Generator Contributors (https://openapi-generator.tech)
 * Copyright 2018 SmartBear Software
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

import io.swagger.v3.oas.models.media.Schema;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.openapitools.codegen.CodegenConfig;
import org.openapitools.codegen.DefaultCodegen;
import org.openapitools.codegen.GeneratorLanguage;
import org.openapitools.codegen.utils.ModelUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;

import static org.openapitools.codegen.utils.StringUtils.underscore;

abstract public class AbstractRubyCodegen extends DefaultCodegen implements CodegenConfig {
    private final Logger LOGGER = LoggerFactory.getLogger(AbstractRubyCodegen.class);

    public AbstractRubyCodegen() {
        super();

        setReservedWordsLowerCase(
                Arrays.asList(
                        "__FILE__", "and", "def", "end", "in", "or", "self", "unless", "__LINE__",
                        "begin", "defined?", "ensure", "module", "redo", "super", "until", "BEGIN",
                        "break", "do", "false", "next", "rescue", "then", "when", "END", "case",
                        "else", "for", "nil", "retry", "true", "while", "alias", "class", "elsif",
                        "if", "not", "return", "undef", "yield")
        );

        languageSpecificPrimitives.clear();
        languageSpecificPrimitives.add("String");
        languageSpecificPrimitives.add("Boolean");
        languageSpecificPrimitives.add("Integer");
        languageSpecificPrimitives.add("Float");
        languageSpecificPrimitives.add("Date");
        languageSpecificPrimitives.add("Time");
        languageSpecificPrimitives.add("Array");
        languageSpecificPrimitives.add("Hash");
        languageSpecificPrimitives.add("File");
        languageSpecificPrimitives.add("Object");

        typeMapping.clear();
        typeMapping.put("string", "String");
        typeMapping.put("boolean", "Boolean");
        typeMapping.put("char", "String");
        typeMapping.put("int", "Integer");
        typeMapping.put("integer", "Integer");
        typeMapping.put("long", "Integer");
        typeMapping.put("short", "Integer");
        typeMapping.put("float", "Float");
        typeMapping.put("double", "Float");
        typeMapping.put("number", "Float");
        typeMapping.put("decimal", "Float");
        typeMapping.put("date", "Date");
        typeMapping.put("DateTime", "Time");
        typeMapping.put("array", "Array");
        typeMapping.put("set", "Array");
        typeMapping.put("List", "Array");
        typeMapping.put("map", "Hash");
        typeMapping.put("object", "Object");
        typeMapping.put("AnyType", "Object");
        typeMapping.put("file", "File");
        typeMapping.put("binary", "String");
        typeMapping.put("ByteArray", "String");
        typeMapping.put("UUID", "String");
        typeMapping.put("URI", "String");

        instantiationTypes.put("map", "Hash");
        instantiationTypes.put("array", "Array");
        instantiationTypes.put("set", "Set");
    }

    @Override
    public void processOpts() {
        super.processOpts();

        if (StringUtils.isEmpty(System.getenv("RUBY_POST_PROCESS_FILE"))) {
            LOGGER.info("Hint: Environment variable 'RUBY_POST_PROCESS_FILE' (optional) not defined. E.g. to format the source code, please try 'export RUBY_POST_PROCESS_FILE=\"/usr/local/bin/rubocop -a\"' (Linux/Mac)");
        } else if (!this.isEnablePostProcessFile()) {
            LOGGER.info("Warning: Environment variable 'RUBY_POST_PROCESS_FILE' is set but file post-processing is not enabled. To enable file post-processing, 'enablePostProcessFile' must be set to `true` (--enable-post-process-file for CLI).");
        }
    }

    @Override
    public String escapeReservedWord(String name) {
        if (this.reservedWordsMappings().containsKey(name)) {
            return this.reservedWordsMappings().get(name);
        }
        return "_" + name;
    }

    @Override
    public String getTypeDeclaration(Schema schema) {
        if (ModelUtils.isArraySchema(schema)) {
            Schema inner = ModelUtils.getSchemaItems(schema);
            return getSchemaType(schema) + "<" + getTypeDeclaration(inner) + ">";
        } else if (ModelUtils.isMapSchema(schema)) {
            Schema inner = ModelUtils.getAdditionalProperties(schema);
            return getSchemaType(schema) + "<String, " + getTypeDeclaration(inner) + ">";
        }

        return super.getTypeDeclaration(schema);
    }

    @Override
    public String toInstantiationType(Schema schema) {
        if (ModelUtils.isMapSchema(schema)) {
            return instantiationTypes.get("map");
        } else if (ModelUtils.isArraySchema(schema)) {
            String parentType;
            if (ModelUtils.isSet(schema)) {
                parentType = "set";
            } else {
                parentType = "array";
            }
            return instantiationTypes.get(parentType);
        }
        return super.toInstantiationType(schema);
    }

    @Override
    public String toDefaultValue(Schema p) {
        p = ModelUtils.getReferencedSchema(this.openAPI, p);
        if (ModelUtils.isIntegerSchema(p) || ModelUtils.isNumberSchema(p) || ModelUtils.isBooleanSchema(p)) {
            if (p.getDefault() != null) {
                return p.getDefault().toString();
            }
        } else if (ModelUtils.isStringSchema(p)) {
            if (p.getDefault() != null) {
                if (p.getDefault() instanceof Date) {
                    Date date = (Date) p.getDefault();
                    LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                    return "Date.parse(\"" + String.format(Locale.ROOT, localDate.toString(), "") + "\")";
                } else if (p.getDefault() instanceof java.time.OffsetDateTime) {
                    return "Time.parse(\"" + String.format(Locale.ROOT, ((java.time.OffsetDateTime) p.getDefault()).atZoneSameInstant(ZoneId.systemDefault()).toString(), "") + "\")";
                } else {
                    return "'" + escapeText((String.valueOf(p.getDefault()))) + "'";
                }
            }
        }

        return null;
    }

    @Override
    public String toEnumDefaultValue(String value, String datatype) {
        return datatype + "::" + value;
    }

    @Override
    public String toVarName(final String name) {
        // obtain the name from nameMapping directly if provided
        if (nameMapping.containsKey(name)) {
            return nameMapping.get(name);
        }

        String varName = sanitizeName(name);
        // if it's all upper case, convert to lower case
        if (name.matches("^[A-Z_]*$")) {
            varName = varName.toLowerCase(Locale.ROOT);
        }

        // camelize (lower first character) the variable name
        // petId => pet_id
        varName = underscore(varName);

        // for reserved word or word starting with number, append _
        if (isReservedWord(varName) || varName.matches("^\\d.*")) {
            varName = escapeReservedWord(varName);
        }

        return varName;
    }

    @Override
    public String toRegularExpression(String pattern) {
        return addRegularExpressionDelimiter(pattern);
    }

    @Override
    public String addRegularExpressionDelimiter(String pattern) {
        if (StringUtils.isEmpty(pattern)) {
            return pattern;
        }

        if (!pattern.matches("^/.*")) {
            // Perform a negative lookbehind on each `/` to ensure that it is escaped.
            return "/" + pattern.replaceAll("(?<!\\\\)\\/", "\\\\/") + "/";
        }

        return pattern;
    }

    @Override
    public String toParamName(String name) {
        // obtain the name from parameterNameMapping directly if provided
        if (parameterNameMapping.containsKey(name)) {
            return parameterNameMapping.get(name);
        }

        // should be the same as variable name
        return toVarName(name);
    }

    @Override
    public String toOperationId(String operationId) {
        // method name cannot use reserved keyword, e.g. return
        if (isReservedWord(operationId)) {
            String newOperationId = underscore("call_" + operationId);
            LOGGER.warn("{} (reserved word) cannot be used as method name. Renamed to {}", operationId, newOperationId);
            return newOperationId;
        }

        return underscore(operationId);
    }

    @Override
    public String escapeQuotationMark(String input) {
        // remove ' to avoid code injection
        return input.replace("'", "");
    }

    @Override
    public String escapeUnsafeCharacters(String input) {
        return input.replace("=end", "=_end").replace("=begin", "=_begin").replace("#{", "\\#{");
    }

    @Override
    public void postProcessFile(File file, String fileType) {
        super.postProcessFile(file, fileType);
        if (file == null) {
            return;
        }
        String rubyPostProcessFile = System.getenv("RUBY_POST_PROCESS_FILE");
        if (StringUtils.isEmpty(rubyPostProcessFile)) {
            return; // skip if RUBY_POST_PROCESS_FILE env variable is not defined
        }
        // only process files with rb extension
        if ("rb".equals(FilenameUtils.getExtension(file.toString()))) {
            this.executePostProcessor(new String[]{rubyPostProcessFile, file.toString()});
        }
    }

    @Override
    public GeneratorLanguage generatorLanguage() {
        return GeneratorLanguage.RUBY;
    }
}
