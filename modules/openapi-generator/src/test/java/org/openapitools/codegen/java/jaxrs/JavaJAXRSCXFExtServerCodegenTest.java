package org.openapitools.codegen.java.jaxrs;

import io.swagger.parser.OpenAPIParser;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.parser.core.models.ParseOptions;
import org.assertj.core.api.Assertions;
import org.openapitools.codegen.ClientOptInput;
import org.openapitools.codegen.CodegenConstants;
import org.openapitools.codegen.DefaultGenerator;
import org.openapitools.codegen.java.assertions.JavaFileAssert;
import org.openapitools.codegen.languages.AbstractJavaCodegen;
import org.openapitools.codegen.languages.AbstractJavaJAXRSServerCodegen;
import org.openapitools.codegen.languages.JavaCXFExtServerCodegen;
import org.openapitools.codegen.languages.features.*;
import org.openapitools.codegen.testutils.ConfigAssert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;

public class JavaJAXRSCXFExtServerCodegenTest extends JavaJaxrsBaseTest {
    private static class JavaCXFExtServerCodegenTester extends JavaCXFExtServerCodegen {

        // CodegenConstants.IMPL_FOLDER
        public String getImplFolder() {
            return implFolder;
        }

        // CXFServerFeatures.TEST_DATA_CONTROL_FILE
        public File getTestDataControlFile() {
            return testDataControlFile;
        }

        // CXFServerFeatures.ADD_CONSUMES_PRODUCES_JSON
        public boolean isAddConsumesProducesJson() {
            return addConsumesProducesJson;
        }

        // AbstractJavaCodegen.DISABLE_HTML_ESCAPING
        public boolean isDisableHtmlEscaping() {
            return disableHtmlEscaping;
        }

        // JbossFeature.GENERATE_JBOSS_DEPLOYMENT_DESCRIPTOR
        public boolean isGenerateJbossDeploymentDescriptor() {
            return generateJbossDeploymentDescriptor;
        }

        // CXFServerFeatures.GENERATE_NON_SPRING_APPLICATION
        public boolean isGenerateNonSpringApplication() {
            return generateNonSpringApplication;
        }

        /* Options processed by AbstractJavaJAXRSServerCodegen */

        // SpringFeatures.GENERATE_SPRING_APPLICATION
        public boolean isGenerateSpringApplication() {
            return generateSpringApplication;
        }

        /* Options processed by JavaCXFServerCodegen */

        // SpringFeatures.GENERATE_SPRING_BOOT_APPLICATION
        public boolean isGenerateSpringBootApplication() {
            return generateSpringBootApplication;
        }

        // CodegenConstants.SERIALIZE_BIG_DECIMAL_AS_STRING
        public boolean isSerializeBigDecimalAsString() {
            return serializeBigDecimalAsString;
        }

        // AbstractJavaCodegen.SUPPORT_ASYNC
        public boolean isSupportAsync() {
            return supportAsync;
        }

        // CXFServerFeatures.USE_ANNOTATED_BASE_PATH
        public boolean isUseAnnotatedBasePath() {
            return useAnnotatedBasePath;
        }


        // BeanValidationExtendedFeatures.USE_BEANVALIDATION_FEATURE
        public boolean isUseBeanValidationFeature() {
            return useBeanValidationFeature;
        }

        // UseGenericResponseFeatures.USE_GENERIC_RESPONSE
        public boolean isUseGenericResponse() {
            return useGenericResponse;
        }

        // GzipFeatures.USE_GZIP_FEATURE
        public boolean isUseGzipFeature() {
            return useGzipFeature;
        }

        // GzipTestFeatures.USE_GZIP_FEATURE_FOR_TESTS
        public boolean isUseGzipFeatureForTests() {
            return useGzipFeatureForTests;
        }

        // LoggingFeatures.USE_LOGGING_FEATURE
        public boolean isUseLoggingFeature() {
            return useLoggingFeature;
        }

        // LoggingTestFeatures.USE_LOGGING_FEATURE_FOR_TESTS
        public boolean isUseLoggingFeatureForTests() {
            return useLoggingFeatureForTests;
        }

        // CXFServerFeatures.USE_MULTIPART_FEATURE
        public boolean isUseMultipartFeature() {
            return useMultipartFeature;
        }

        // SpringFeatures.USE_SPRING_ANNOTATION_CONFIG
        public boolean isUseSpringAnnotationConfig() {
            return useSpringAnnotationConfig;
        }

        // SwaggerFeatures.USE_SWAGGER_FEATURE
        public boolean isUseSwaggerFeature() {
            return useSwaggerFeature;
        }

        // SwaggerUIFeatures.USE_SWAGGER_UI
        public boolean isUseSwaggerUI() {
            return useSwaggerUI;
        }

        // CXFServerFeatures.USE_WADL_FEATURE
        public boolean isUseWadlFeature() {
            return useWadlFeature;
        }

        // AbstractJavaCodegen.WITH_XML
        public boolean isWithXml() {
            return withXml;
        }

        /* Options processed by JavaCXFExtServerCodegen */

        // CXFExtServerFeatures.GENERATE_OPERATION_BODY
        public boolean isGenerateOperationBody() {
            return generateOperationBody;
        }

        // CXFExtServerFeatures.TEST_DATA_FILE
        public File getTestDataFile() {
            return testDataFile;
        }

        // CXFExtServerFeatures.LOAD_TEST_DATA_FROM_FILE
        public boolean isLoadTestDataFromFile() {
            return loadTestDataFromFile;
        }

        // CXFExtServerFeatures.SUPPORT_MULTIPLE_SPRING_SERVICES
        public boolean isSupportMultipleSpringServices() {
            return supportMultipleSpringServices;
        }
    }

    @BeforeMethod
    public void beforeMethod() {
        codegen = new JavaCXFExtServerCodegenTester();
    }

    @Test
    public void testAdditionalPropertiesPutForConfigValues() throws Exception {
        JavaCXFExtServerCodegenTester testerCodegen = (JavaCXFExtServerCodegenTester) this.codegen;
        Map<String, Object> additionalProperties = testerCodegen.additionalProperties();

        // Options processed by DefaultCodegen
        additionalProperties.put(CodegenConstants.ALLOW_UNICODE_IDENTIFIERS, "true");
        additionalProperties.put(CodegenConstants.API_PACKAGE, "xyz.yyyyy.aaaaa.api");
        additionalProperties.put(CodegenConstants.DOCEXTENSION, "doc");
        additionalProperties.put(CodegenConstants.ENSURE_UNIQUE_PARAMS, "true");
        additionalProperties.put(CodegenConstants.HIDE_GENERATION_TIMESTAMP, "true");
        additionalProperties.put(CodegenConstants.INVOKER_PACKAGE, "xyz.yyyyy.iiii.invoker");
        additionalProperties.put(CodegenConstants.MODEL_NAME_PREFIX, "MyPrefix");
        additionalProperties.put(CodegenConstants.MODEL_NAME_SUFFIX, "MySuffix");
        additionalProperties.put(CodegenConstants.MODEL_PACKAGE, "xyz.yyyyy.mmmmm.model");
        additionalProperties.put(CodegenConstants.PREPEND_FORM_OR_BODY_PARAMETERS, "true");
        additionalProperties.put(CodegenConstants.REMOVE_OPERATION_ID_PREFIX, "true");
        additionalProperties.put(CodegenConstants.SORT_PARAMS_BY_REQUIRED_FLAG, "true");
        additionalProperties.put(CodegenConstants.TEMPLATE_DIR, "MyTemplates");
        // Options processed by AbstractJavaCodegen
        additionalProperties.put(CodegenConstants.ARTIFACT_DESCRIPTION, "My description");
        additionalProperties.put(CodegenConstants.ARTIFACT_ID, "my-artifact");
        additionalProperties.put(CodegenConstants.ARTIFACT_VERSION, "9.9.9");
        additionalProperties.put(CodegenConstants.ARTIFACT_URL, "http://organisation.org/group/artifact");
        additionalProperties.put(CodegenConstants.DEVELOPER_EMAIL, "dchappie@organisation.org");
        additionalProperties.put(CodegenConstants.DEVELOPER_NAME, "Developer Chappie");
        additionalProperties.put(CodegenConstants.DEVELOPER_ORGANIZATION, "My Organisation");
        additionalProperties.put(CodegenConstants.DEVELOPER_ORGANIZATION_URL, "http://www.organisation.org/");
        additionalProperties.put(CodegenConstants.GROUP_ID, "org.organisation.group.id");
        additionalProperties.put(CodegenConstants.LICENSE_NAME, "Apache 2.0");
        additionalProperties.put(CodegenConstants.LICENSE_URL, "https://www.apache.org/licenses/LICENSE-2.0");
        additionalProperties.put(CodegenConstants.SCM_CONNECTION, "http://svn.organisation.org/group/");
        additionalProperties.put(CodegenConstants.SCM_DEVELOPER_CONNECTION, "http://svn.organisation.org/dev/group/");
        additionalProperties.put(CodegenConstants.SCM_URL, "http://svn.organisation.org/group/");
        additionalProperties.put(CodegenConstants.SERIALIZE_BIG_DECIMAL_AS_STRING, "true");
        additionalProperties.put(CodegenConstants.SERIALIZABLE_MODEL, "true");
        additionalProperties.put(CodegenConstants.SOURCE_FOLDER, "src/main/java");
        additionalProperties.put(AbstractJavaCodegen.BOOLEAN_GETTER_PREFIX, "isIt");
        additionalProperties.put(AbstractJavaCodegen.DATE_LIBRARY, "MyDateLibrary");
        additionalProperties.put(AbstractJavaCodegen.DISABLE_HTML_ESCAPING, "true");
        additionalProperties.put(AbstractJavaCodegen.SUPPORT_ASYNC, "true");
        additionalProperties.put(AbstractJavaCodegen.WITH_XML, "true");
        additionalProperties.put(AbstractJavaCodegen.OPENAPI_NULLABLE, "false");
        // Options processed by AbstractJavaJAXRSServerCodegen
        additionalProperties.put(CodegenConstants.IMPL_FOLDER, "myimpl");
        additionalProperties.put(BeanValidationFeatures.USE_BEANVALIDATION, "true");
        additionalProperties.put(AbstractJavaJAXRSServerCodegen.SERVER_PORT, "8088");
        // Options processed by JavaCXFServerCodegen
        additionalProperties.put(BeanValidationExtendedFeatures.USE_BEANVALIDATION_FEATURE, Boolean.TRUE);
        additionalProperties.put(GzipFeatures.USE_GZIP_FEATURE, Boolean.TRUE);
        additionalProperties.put(GzipTestFeatures.USE_GZIP_FEATURE_FOR_TESTS, "true");
        additionalProperties.put(JbossFeature.GENERATE_JBOSS_DEPLOYMENT_DESCRIPTOR, Boolean.TRUE);
        additionalProperties.put(LoggingFeatures.USE_LOGGING_FEATURE, Boolean.TRUE);
        additionalProperties.put(LoggingTestFeatures.USE_LOGGING_FEATURE_FOR_TESTS, "true");
        additionalProperties.put(SpringFeatures.GENERATE_SPRING_APPLICATION, Boolean.TRUE);
        additionalProperties.put(SpringFeatures.GENERATE_SPRING_BOOT_APPLICATION, Boolean.TRUE);
        additionalProperties.put(SpringFeatures.USE_SPRING_ANNOTATION_CONFIG, Boolean.TRUE);
        additionalProperties.put(SwaggerFeatures.USE_SWAGGER_FEATURE, Boolean.TRUE);
        additionalProperties.put(SwaggerUIFeatures.USE_SWAGGER_UI, Boolean.TRUE);
        additionalProperties.put(UseGenericResponseFeatures.USE_GENERIC_RESPONSE, "true");
        additionalProperties.put(CXFServerFeatures.ADD_CONSUMES_PRODUCES_JSON, Boolean.TRUE);
        additionalProperties.put(CXFServerFeatures.GENERATE_NON_SPRING_APPLICATION, Boolean.FALSE);
        additionalProperties.put(CXFServerFeatures.LOAD_TEST_DATA_FROM_FILE, Boolean.TRUE);
        additionalProperties.put(CXFServerFeatures.USE_ANNOTATED_BASE_PATH, Boolean.TRUE);
        additionalProperties.put(CXFServerFeatures.USE_MULTIPART_FEATURE, Boolean.TRUE);
        additionalProperties.put(CXFServerFeatures.USE_WADL_FEATURE, Boolean.TRUE);
        // Options processed by JavaCXFExtServerCodegen
        additionalProperties.put(CXFExtServerFeatures.GENERATE_OPERATION_BODY, Boolean.TRUE);
        additionalProperties.put(CXFExtServerFeatures.SUPPORT_MULTIPLE_SPRING_SERVICES, Boolean.TRUE);
        additionalProperties.put(CXFExtServerFeatures.TEST_DATA_FILE, "my/test-data.json");
        additionalProperties.put(CXFExtServerFeatures.TEST_DATA_CONTROL_FILE, "my/test-data-control.json");

        testerCodegen.processOpts();

        OpenAPI openAPI = new OpenAPI();
        openAPI.addServersItem(new Server().url("https://api.abcde.xy:8082/v2"));
        testerCodegen.preprocessOpenAPI(openAPI);

        // Options processed by DefaultCodegen
        assertEquals(testerCodegen.getAllowUnicodeIdentifiers(), Boolean.TRUE);
        assertEquals(testerCodegen.apiPackage(), "xyz.yyyyy.aaaaa.api");
        assertEquals(testerCodegen.getDocExtension(), "doc");
        assertEquals(testerCodegen.getEnsureUniqueParams(), Boolean.TRUE);
        assertEquals(testerCodegen.isHideGenerationTimestamp(), true);
        assertEquals(testerCodegen.getInvokerPackage(), "xyz.yyyyy.iiii.invoker");
        assertEquals(testerCodegen.getModelNamePrefix(), "MyPrefix");
        assertEquals(testerCodegen.getModelNameSuffix(), "MySuffix");
        assertEquals(testerCodegen.modelPackage(), "xyz.yyyyy.mmmmm.model");
        assertEquals(testerCodegen.getPrependFormOrBodyParameters(), Boolean.TRUE);
        assertEquals(testerCodegen.isRemoveOperationIdPrefix(), true);
        assertEquals(testerCodegen.getSortParamsByRequiredFlag(), Boolean.TRUE);
        assertEquals(testerCodegen.templateDir(), "MyTemplates");
        assertEquals(testerCodegen.getArtifactDescription(), "My description");
        // Options processed by AbstractJavaCodegen
        assertEquals(testerCodegen.getArtifactId(), "my-artifact");
        assertEquals(testerCodegen.getArtifactVersion(), "9.9.9");
        assertEquals(testerCodegen.getArtifactUrl(), "http://organisation.org/group/artifact");
        assertEquals(testerCodegen.getDeveloperEmail(), "dchappie@organisation.org");
        assertEquals(testerCodegen.getDeveloperName(), "Developer Chappie");
        assertEquals(testerCodegen.getDeveloperOrganization(), "My Organisation");
        assertEquals(testerCodegen.getDeveloperOrganizationUrl(), "http://www.organisation.org/");
        assertEquals(testerCodegen.getGroupId(), "org.organisation.group.id");
        assertEquals(testerCodegen.getLicenseName(), "Apache 2.0");
        assertEquals(testerCodegen.getLicenseUrl(), "https://www.apache.org/licenses/LICENSE-2.0");
        assertEquals(testerCodegen.getScmConnection(), "http://svn.organisation.org/group/");
        assertEquals(testerCodegen.getScmDeveloperConnection(), "http://svn.organisation.org/dev/group/");
        assertEquals(testerCodegen.getScmUrl(), "http://svn.organisation.org/group/");
        assertEquals(testerCodegen.isSerializeBigDecimalAsString(), true);
        assertEquals(testerCodegen.getSerializableModel(), Boolean.TRUE);
        assertEquals(testerCodegen.getSourceFolder(), "src/main/java");
        assertEquals(testerCodegen.getBooleanGetterPrefix(), "isIt");
        assertEquals(testerCodegen.getDateLibrary(), "MyDateLibrary");
        assertEquals(testerCodegen.isDisableHtmlEscaping(), true);
        assertEquals(testerCodegen.isSupportAsync(), true);
        assertEquals(testerCodegen.isWithXml(), true);
        assertEquals(testerCodegen.isOpenApiNullable(), false);
        // Options processed by AbstractJavaJAXRSServerCodegen
        assertEquals(testerCodegen.getImplFolder(), "myimpl");
        assertEquals(testerCodegen.isUseBeanValidation(), true);
        // Options processed by JavaCXFServerCodegen
        File curdir = new File(System.getProperty("user.dir"));
        assertEquals(testerCodegen.isUseBeanValidationFeature(), true);
        assertEquals(testerCodegen.isUseGzipFeature(), true);
        assertEquals(testerCodegen.isUseGzipFeatureForTests(), true);
        assertEquals(testerCodegen.isGenerateJbossDeploymentDescriptor(), true);
        assertEquals(testerCodegen.isUseLoggingFeature(), true);
        assertEquals(testerCodegen.isUseLoggingFeatureForTests(), true);
        assertEquals(testerCodegen.isGenerateSpringApplication(), true);
        assertEquals(testerCodegen.isGenerateSpringBootApplication(), true);
        assertEquals(testerCodegen.isUseSpringAnnotationConfig(), true);
        assertEquals(testerCodegen.isUseSwaggerFeature(), true);
        assertEquals(testerCodegen.isUseSwaggerUI(), true);
        assertEquals(testerCodegen.isUseGenericResponse(), true);
        assertEquals(testerCodegen.isAddConsumesProducesJson(), true);
        assertEquals(testerCodegen.isGenerateNonSpringApplication(), false);
        assertEquals(testerCodegen.isUseAnnotatedBasePath(), true);
        assertEquals(testerCodegen.isUseMultipartFeature(), true);
        assertEquals(testerCodegen.isUseWadlFeature(), true);
        // Options processed by JavaCXFExtServerCodegen
        assertEquals(testerCodegen.isGenerateOperationBody(), true);
        assertEquals(testerCodegen.isLoadTestDataFromFile(), true);
        assertEquals(testerCodegen.isSupportMultipleSpringServices(), true);
        assertEquals(testerCodegen.getTestDataFile(), new File(curdir, "my/test-data.json"));
        assertEquals(testerCodegen.getTestDataControlFile(), new File(curdir, "my/test-data-control.json"));
    }

    @Test
    public void testGenerateOperationBodyWithCodedTestData() throws Exception {
        File output = Files.createTempDirectory("test").toFile().getCanonicalFile();
        output.deleteOnExit();
        String outputPath = output.getAbsolutePath().replace('\\', '/');

        OpenAPI openAPI = new OpenAPIParser()
                .readLocation("src/test/resources/3_0/petstore.yaml", null, new ParseOptions()).getOpenAPI();
        codegen.setOutputDir(output.getAbsolutePath());

        codegen.additionalProperties().put(CXFExtServerFeatures.GENERATE_OPERATION_BODY, "true");

        ClientOptInput input = new ClientOptInput();
        input.openAPI(openAPI);
        input.config(codegen);

        DefaultGenerator generator = new DefaultGenerator();
        generator.opts(input).generate();

        JavaFileAssert.assertThat(Paths.get(outputPath + "/src/main/java/org/openapitools/api/impl/PetApiServiceImpl.java"))
                .assertMethod("getPetById")
                .bodyContainsLines(
                        "Pet response = new Pet();",
                        "return response;"
                );

        JavaFileAssert.assertThat(Paths.get(outputPath + "/src/test/java/org/openapitools/api/PetApiTest.java"))
                .assertMethod("findPetsByStatusTest")
                .bodyContainsLines(
                        "List<String> status = new ArrayList<>();",
                        "// List<Pet> response = api.findPetsByStatus(status);",
                        "// validate(response);"
                );

        Assertions.assertThat(Paths.get(outputPath + "/src/main/resources/test-data.json"))
                .doesNotExist();
        Assertions.assertThat(Paths.get(outputPath + "/test-data-control.json"))
                .doesNotExist();
    }

    @Test
    public void testGenerateOperationBodyWithJsonTestData() throws Exception {
        File output = Files.createTempDirectory("test").toFile().getCanonicalFile();
        output.deleteOnExit();
        String outputPath = output.getAbsolutePath().replace('\\', '/');

        OpenAPI openAPI = new OpenAPIParser()
                .readLocation("src/test/resources/3_0/petstore.yaml", null, new ParseOptions()).getOpenAPI();
        codegen.setOutputDir(output.getAbsolutePath());

        codegen.additionalProperties().put(CXFExtServerFeatures.GENERATE_OPERATION_BODY, "true");
        codegen.additionalProperties().put(CXFServerFeatures.LOAD_TEST_DATA_FROM_FILE, "true");

        ClientOptInput input = new ClientOptInput();
        input.openAPI(openAPI);
        input.config(codegen);

        DefaultGenerator generator = new DefaultGenerator();
        generator.opts(input).generate();

        JavaFileAssert.assertThat(Paths.get(outputPath + "/src/main/java/org/openapitools/api/impl/PetApiServiceImpl.java"))
                .fileContains(
                        "File cacheFile = new File(System.getProperty(\"jaxrs.test.server.json\"",
                        "cache = JsonCache.Factory.instance.get(\"test-data\").load(cacheFile).child(\"/org.openapitools.api/PetApi\");"
                )
                .assertMethod("getPetById")
                .bodyContainsLines(
                        "Pet response = cache.getObject(\"/getPetById/response\", Pet.class);"
                );


        JavaFileAssert.assertThat(Paths.get(outputPath + "/src/test/java/org/openapitools/api/PetApiTest.java"))
                .assertMethod("beforeClass")
                .bodyContainsLines(
                        "File cacheFile = new File(System.getProperty(\"jaxrs.test.client.json\",",
                        "cache = JsonCache.Factory.instance.get(\"test-data\").load(cacheFile).child(\"/org.openapitools.api/PetApi\");",
                        "validator = Validation.buildDefaultValidatorFactory().getValidator();"
                )
                .toFileAssert()
                .assertMethod("addPetTest")
                .bodyContainsLines(
                        "Pet pet = cache.getObject(\"/addPet/pet\", Pet.class);"
                );

        Assertions.assertThat(Paths.get(outputPath + "/src/main/resources/test-data.json"))
                .exists();
        Assertions.assertThat(Paths.get(outputPath + "/test-data-control.json"))
                .exists();
    }

    @Test
    public void testInitialConfigValues() throws Exception {
        codegen.processOpts();

        OpenAPI openAPI = new OpenAPI();
        openAPI.addServersItem(new Server().url("https://api.abcde.xy:8082/v2"));
        codegen.preprocessOpenAPI(openAPI);

        Map<String, Object> additionalProperties = codegen.additionalProperties();
        ConfigAssert configAssert = new ConfigAssert(codegen.additionalProperties());
        // Options processed by DefaultCodegen
        assertNull(additionalProperties.get(CodegenConstants.ALLOW_UNICODE_IDENTIFIERS));
        // NOT WRITTEN BACK assertEquals(additionalProperties.get(CodegenConstants.API_PACKAGE), "org.openapitools.api");
        assertEquals(codegen.apiPackage(), "org.openapitools.api");
        assertNull(additionalProperties.get(CodegenConstants.DOCEXTENSION));
        assertNull(additionalProperties.get(CodegenConstants.ENSURE_UNIQUE_PARAMS));
        // NOT WRITTEN BACK assertEquals(additionalProperties.get(CodegenConstants.HIDE_GENERATION_TIMESTAMP), Boolean.FALSE);
        configAssert.assertValue(CodegenConstants.HIDE_GENERATION_TIMESTAMP, codegen::isHideGenerationTimestamp, false);

        assertEquals(codegen.isHideGenerationTimestamp(), false);
        configAssert.assertValue(CodegenConstants.INVOKER_PACKAGE, codegen::getInvokerPackage, "org.openapitools.api");
        assertNull(additionalProperties.get(CodegenConstants.MODEL_NAME_PREFIX));
        assertNull(additionalProperties.get(CodegenConstants.MODEL_NAME_SUFFIX));
        configAssert.assertValue(CodegenConstants.MODEL_PACKAGE, codegen::modelPackage, "org.openapitools.model");
        assertNull(additionalProperties.get(CodegenConstants.PREPEND_FORM_OR_BODY_PARAMETERS));
        assertNull(additionalProperties.get(CodegenConstants.REMOVE_OPERATION_ID_PREFIX));
        assertNull(additionalProperties.get(CodegenConstants.SORT_PARAMS_BY_REQUIRED_FLAG));
        assertNull(additionalProperties.get(CodegenConstants.TEMPLATE_DIR));
        configAssert.assertValue(CodegenConstants.ARTIFACT_ID, "openapi-cxf-server");
        configAssert.assertValue(CodegenConstants.ARTIFACT_VERSION, "1.0.0");
        configAssert.assertValue(CodegenConstants.ARTIFACT_URL, "https://github.com/openapitools/openapi-generator");
        configAssert.assertValue(CodegenConstants.DEVELOPER_EMAIL, "team@openapitools.org");
        configAssert.assertValue(CodegenConstants.DEVELOPER_NAME, "OpenAPI-Generator Contributors");
        configAssert.assertValue(CodegenConstants.DEVELOPER_ORGANIZATION, "OpenAPITools.org");
        configAssert.assertValue(CodegenConstants.DEVELOPER_ORGANIZATION_URL, "http://openapitools.org");
        configAssert.assertValue(CodegenConstants.GROUP_ID, "org.openapitools");
        configAssert.assertValue(CodegenConstants.LICENSE_NAME, "Unlicense");
        configAssert.assertValue(CodegenConstants.LICENSE_URL, "http://unlicense.org");
        configAssert.assertValue(CodegenConstants.SCM_CONNECTION, "scm:git:git@github.com:openapitools/openapi-generator.git");
        configAssert.assertValue(CodegenConstants.SCM_DEVELOPER_CONNECTION, "scm:git:git@github.com:openapitools/openapi-generator.git");
        configAssert.assertValue(CodegenConstants.SCM_URL, "https://github.com/openapitools/openapi-generator");
        assertNull(additionalProperties.get(CodegenConstants.SERIALIZE_BIG_DECIMAL_AS_STRING));
        configAssert.assertValue(CodegenConstants.SERIALIZABLE_MODEL, Boolean.FALSE);
        configAssert.assertValue(CodegenConstants.SOURCE_FOLDER, "src/gen/java");
        configAssert.assertValue(AbstractJavaCodegen.BOOLEAN_GETTER_PREFIX, "get");
        assertNull(additionalProperties.get(AbstractJavaCodegen.DATE_LIBRARY));
        configAssert.assertValue(AbstractJavaCodegen.DISABLE_HTML_ESCAPING, Boolean.FALSE);
        assertNull(additionalProperties.get(AbstractJavaCodegen.SUPPORT_ASYNC));
        configAssert.assertValue(AbstractJavaCodegen.WITH_XML, false);
        configAssert.assertValue(AbstractJavaCodegen.OPENAPI_NULLABLE, true);
        assertNull(additionalProperties.get(CodegenConstants.IMPL_FOLDER));
        configAssert.assertValue(BeanValidationFeatures.USE_BEANVALIDATION, Boolean.TRUE);
        configAssert.assertValue(AbstractJavaJAXRSServerCodegen.SERVER_PORT, "8082");
        assertNull(additionalProperties.get(BeanValidationExtendedFeatures.USE_BEANVALIDATION_FEATURE));
        assertNull(additionalProperties.get(GzipFeatures.USE_GZIP_FEATURE));
        assertNull(additionalProperties.get(GzipTestFeatures.USE_GZIP_FEATURE_FOR_TESTS));
        assertNull(additionalProperties.get(JbossFeature.GENERATE_JBOSS_DEPLOYMENT_DESCRIPTOR));
        assertNull(additionalProperties.get(LoggingFeatures.USE_LOGGING_FEATURE));
        assertNull(additionalProperties.get(LoggingTestFeatures.USE_LOGGING_FEATURE_FOR_TESTS));
        assertNull(additionalProperties.get(SpringFeatures.GENERATE_SPRING_APPLICATION));
        assertNull(additionalProperties.get(SpringFeatures.GENERATE_SPRING_BOOT_APPLICATION));
        assertNull(additionalProperties.get(SpringFeatures.USE_SPRING_ANNOTATION_CONFIG));
        assertNull(additionalProperties.get(SwaggerFeatures.USE_SWAGGER_FEATURE));
        assertNull(additionalProperties.get(SwaggerUIFeatures.USE_SWAGGER_UI));
        assertNull(additionalProperties.get(UseGenericResponseFeatures.USE_GENERIC_RESPONSE));
        assertNull(additionalProperties.get(CXFServerFeatures.ADD_CONSUMES_PRODUCES_JSON));
        assertNull(additionalProperties.get(CXFServerFeatures.GENERATE_NON_SPRING_APPLICATION));
        assertNull(additionalProperties.get(CXFExtServerFeatures.GENERATE_OPERATION_BODY));
        assertNull(additionalProperties.get(CXFServerFeatures.LOAD_TEST_DATA_FROM_FILE));
        assertNull(additionalProperties.get(CXFExtServerFeatures.SUPPORT_MULTIPLE_SPRING_SERVICES));
        assertNull(additionalProperties.get(CXFExtServerFeatures.TEST_DATA_FILE));
        assertNull(additionalProperties.get(CXFExtServerFeatures.TEST_DATA_CONTROL_FILE));
        assertNull(additionalProperties.get(CXFServerFeatures.USE_ANNOTATED_BASE_PATH));
        assertNull(additionalProperties.get(CXFServerFeatures.USE_MULTIPART_FEATURE));
        assertNull(additionalProperties.get(CXFServerFeatures.USE_WADL_FEATURE));
    }

    @Test
    public void testSettersForConfigValues() throws Exception {
        // It's apparent that most of these setters aren't useful to client code, only to the generator itself. The only
        // reliable way to set most features is through the additional properties, since CodegenConfig.processOpts()
        // overrides are typically coded to set config fields from the additional properties, not the other way round.
        codegen.setHideGenerationTimestamp(false);
        codegen.setModelPackage("xx.yyyyyyyy.model");
        codegen.setApiPackage("xx.yyyyyyyy.api");
        codegen.setInvokerPackage("xx.yyyyyyyy.invoker");
        codegen.processOpts();

        ConfigAssert configAssert = new ConfigAssert(codegen.additionalProperties());
        configAssert.assertValue(CodegenConstants.HIDE_GENERATION_TIMESTAMP, codegen::isHideGenerationTimestamp, false);
        configAssert.assertValue(CodegenConstants.MODEL_PACKAGE, codegen::modelPackage, "xx.yyyyyyyy.model");
        configAssert.assertValue(CodegenConstants.API_PACKAGE, codegen::apiPackage, "xx.yyyyyyyy.api");
        configAssert.assertValue(CodegenConstants.INVOKER_PACKAGE, codegen::getInvokerPackage, "xx.yyyyyyyy.invoker");
    }

}
