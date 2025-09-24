package com.smn.restapigenerator.service;

import com.smn.restapigenerator.exception.ExceptionAccessTokenInUse;
import com.smn.restapigenerator.exception.ExceptionUserExists;
import com.smn.restapigenerator.model.ApiCode;
import com.smn.restapigenerator.model.ApiSpec;
import com.smn.restapigenerator.model.User;
import com.smn.restapigenerator.model.uml.DomainModel;
import com.smn.restapigenerator.model.uml.Entity;
import org.junit.jupiter.api.*;
import org.mockito.Mockito;
import org.springframework.mock.web.MockMultipartFile;

import java.io.File;
import java.nio.file.Files;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ServiceTest {

    private Service service;

    @BeforeEach
    void setUp() {
        service = new Service();
    }

    @Test
    void testCreateUserSuccess() throws Exception {
        User user = service.createUser("John", "Doe", "Acme", "john@acme.com", "basic", "token123");
        assertNotNull(user);
        assertEquals("John", user.getNameFirst());
        assertEquals("Doe", user.getNameLast());
        assertEquals("Acme", user.getCompany());
        assertEquals("john@acme.com", user.getEmail());
        assertEquals("token123", user.getAccessToken());
    }

    @Test
    void testCreateUserDuplicateThrowsException() {
        assertDoesNotThrow(() -> service.createUser("Jane", "Smith", "Acme", "jane@acme.com", "basic", "tokenABC"));
        assertThrows(ExceptionUserExists.class, () ->
                service.createUser("Jane", "Smith", "Acme", "jane@acme.com", "basic", "tokenXYZ"));
    }

    @Test
    void testCreateUserAccessTokenInUseThrowsException() throws Exception {
        service.createUser("Bob", "Jones", "Acme", "bob@acme.com", "basic", "tokenZZZ");
        assertThrows(ExceptionAccessTokenInUse.class, () ->
                service.createUser("Alice", "Brown", "Acme", "alice@acme.com", "basic", "tokenZZZ"));
    }

    @Test
    void testFindUserSuccess() throws Exception {
        service.createUser("Sam", "Green", "Acme", "sam@acme.com", "basic", "tokenSAM");
        User user = service.findUser("tokenSAM");
        assertNotNull(user);
        assertEquals("Sam", user.getNameFirst());
    }

    @Test
    void testFindUserNotFound() {
        User user = service.findUser("nonexistent");
        assertNull(user);
    }

    @Test
    void testDeleteSwaggerSuccess() throws Exception {
        User user = service.createUser("Del", "User", "Acme", "del@acme.com", "basic", "tokenDEL");
        ApiSpec apiSpec = user.getApiSpec();
        File swaggerFile = apiSpec.getSwaggerFile();
        if (swaggerFile != null) {
            Files.createFile(swaggerFile.toPath());
            assertTrue(swaggerFile.exists());
            service.deleteSwagger(apiSpec);
            assertFalse(swaggerFile.exists());
        }
    }

    @Test
    void testReadUMLFileSuccess() throws Exception {
        String umlJson = "{\"entities\":[]}";
        Service.DtoReadUMLFile dto = service.readUMLFile("test.uml", umlJson.getBytes());
        assertNotNull(dto);
        assertNull(dto.getError());
        assertNotNull(dto.getEntities());
    }

    @Test
    void testReadUMLFileInvalidJson() {
        String invalidJson = "not a json";
        Service.DtoReadUMLFile dto = service.readUMLFile("test.uml", invalidJson.getBytes());
        assertNotNull(dto);
        assertNotNull(dto.getError());
    }

    @Test
    void testGenerateSwaggerSuccess() throws Exception {
        User user = service.createUser("Api", "Dev", "Acme", "api@acme.com", "basic", "tokenAPI");
        List<Entity> entities = new ArrayList<>();
        DomainModel domainModel = new DomainModel("title", "desc", "1.0", entities);
        ApiSpec apiSpec = service.generateSwagger(user, domainModel, true, true, true, true, true, true, "localhost", "/api", "8080", Collections.emptyList());
        assertNotNull(apiSpec);
        assertEquals("title", apiSpec.getTitle());
    }

    @Test
    void testGenerateSwaggerWithIssues() throws Exception {
        User user = service.createUser("Api", "Dev", "Acme", "api@acme.com", "basic", "tokenAPI2");
        List<Entity> entities = new ArrayList<>();
        DomainModel domainModel = new DomainModel("title", "desc", "1.0", entities);
        List<String> issues = Arrays.asList("Issue1", "Issue2");
        ApiSpec apiSpec = service.generateSwagger(user, domainModel, true, true, true, true, true, true, "localhost", "/api", "8080", issues);
        assertNotNull(apiSpec);
        assertEquals("title", apiSpec.getTitle());
    }

    @Test
    void testFilePathToUri() {
        File file = new File("test.txt");
        String uri = Service.filePathToUri(file);
        assertTrue(uri.startsWith("file:"));
    }

    @Test
    void testZipDirectoryAndDownloadZipFile() throws Exception {
        File dir = Files.createTempDirectory("testdir").toFile();
        File file = new File(dir, "file.txt");
        Files.writeString(file.toPath(), "test");
        File zipFile = File.createTempFile("test", ".zip");
        Service.zipDirectory(dir, zipFile);
        assertTrue(zipFile.exists() && zipFile.length() > 0);
        zipFile.delete();
        file.delete();
        dir.delete();
    }
}