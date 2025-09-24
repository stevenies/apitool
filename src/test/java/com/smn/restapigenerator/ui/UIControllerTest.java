package com.smn.restapigenerator.ui;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.smn.restapigenerator.exception.ExceptionAccessTokenInUse;
import com.smn.restapigenerator.exception.ExceptionUserExists;
import com.smn.restapigenerator.model.ApiCode;
import com.smn.restapigenerator.model.ApiSpec;
import com.smn.restapigenerator.model.User;
import com.smn.restapigenerator.service.Service;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.*;
import java.nio.file.Files;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UIControllerTest {

    @InjectMocks
    private UIController controller;

    @Mock
    private Service service;

    @Mock
    private HttpSession session;

    @Mock
    private HttpServletResponse response;

    @Mock
    private Model model;

    @Mock
    private MultipartFile multipartFile;

    @Captor
    private ArgumentCaptor<List<String>> errorsCaptor;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // --- register ---
    @Test
    void register_success() throws Exception {
        User user = mock(User.class);
        ApiSpec apiSpec = mock(ApiSpec.class);
        ApiCode apiCode = mock(ApiCode.class);
        when(service.createUser(any(), any(), any(), any(), any(), any())).thenReturn(user);
        when(user.getApiSpec()).thenReturn(apiSpec);
        when(user.getApiCode()).thenReturn(apiCode);

        String result = controller.register("John", "Doe", "ACME", "john@acme.com", "basic", "token", session, response);
        assertTrue(result.equals("apiSpecForm") || result.equals("apiCodeForm"));
        verify(session).setAttribute(eq("user"), eq(user));
    }

    @Test
    void register_missingFields() {
        String result = controller.register("", "", "", "", "", "", session, response);
        assertEquals("registration", result);
        verify(session).setAttribute(eq("registrationErrors"), any());
    }

    @Test
    void register_userExists() throws Exception {
        doThrow(new ExceptionUserExists("exists")).when(service).createUser(any(), any(), any(), any(), any(), any());
        String result = controller.register("John", "Doe", "ACME", "john@acme.com", "basic", "token", session, response);
        assertEquals("registration", result);
    }

    @Test
    void register_accessTokenInUse() throws Exception {
        doThrow(new ExceptionAccessTokenInUse("in use")).when(service).createUser(any(), any(), any(), any(), any(), any());
        String result = controller.register("John", "Doe", "ACME", "john@acme.com", "basic", "token", session, response);
        assertEquals("registration", result);
    }

    // --- login ---
    @Test
    void login_success() {
        User user = mock(User.class);
        ApiSpec apiSpec = mock(ApiSpec.class);
        ApiCode apiCode = mock(ApiCode.class);
        when(service.findUser(any())).thenReturn(user);
        when(user.getEmail()).thenReturn("john@acme.com");
        when(user.getApiSpec()).thenReturn(apiSpec);
        when(user.getApiCode()).thenReturn(apiCode);

        String result = controller.login("john@acme.com", "token", session, response);
        assertTrue(result.equals("apiSpecForm") || result.equals("apiCodeForm"));
        verify(session).setAttribute(eq("user"), eq(user));
    }

    @Test
    void login_invalidEmail() {
        String result = controller.login("bademail", "token", session, response);
        assertEquals("registration", result);
    }

    @Test
    void login_invalidPassword() {
        when(service.findUser(any())).thenReturn(null);
        String result = controller.login("john@acme.com", "", session, response);
        assertEquals("registration", result);
    }

    // --- viewAPISpecForm ---
    @Test
    void viewAPISpecForm_success() {
        User user = mock(User.class);
        ApiSpec apiSpec = mock(ApiSpec.class);
        when(session.getAttribute("user")).thenReturn(user);
        when(user.getAccessToken()).thenReturn("token");
        when(user.getApiSpec()).thenReturn(apiSpec);

        String result = controller.viewAPISpecForm(model, session);
        assertEquals("apiSpecForm", result);
    }

    @Test
    void viewAPISpecForm_invalidSession() {
        when(session.getAttribute("user")).thenReturn(null);
        String result = controller.viewAPISpecForm(model, session);
        assertEquals("registration", result);
    }

    // --- doApiSpecForm ---
    @Test
    void doApiSpecForm_success() throws Exception {
        User user = mock(User.class);
        ApiSpec apiSpec = mock(ApiSpec.class);
        when(session.getAttribute("user")).thenReturn(user);
        when(user.getAccessToken()).thenReturn("token");
        when(user.getApiSpec()).thenReturn(apiSpec);
        when(multipartFile.getOriginalFilename()).thenReturn("domain.uml");
        when(multipartFile.getBytes()).thenReturn(new byte[0]);
        Service.DtoReadUMLFile dto = mock(Service.DtoReadUMLFile.class);
        when(dto.getError()).thenReturn(null);
        when(dto.getEntities()).thenReturn(Collections.emptyList());
        when(service.readUMLFile(any(), any())).thenReturn(dto);
        when(service.generateSwagger(any(), any(), anyBoolean(), anyBoolean(), anyBoolean(), anyBoolean(), anyBoolean(), anyBoolean(), any(), any(), any(), any())).thenReturn(apiSpec);

        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> formFields = new HashMap<>();
        formFields.put("action", "generate");
        formFields.put("title", "API");
        formFields.put("description", "desc");
        formFields.put("version", "1.0");
        formFields.put("makeGET", true);
        formFields.put("makePOST", true);
        formFields.put("makePUT", true);
        formFields.put("makePATCH", true);
        formFields.put("makeDELETE", true);
        formFields.put("makeSEARCH", true);
        formFields.put("serverDomain", "localhost");
        formFields.put("contextRoot", "/api");
        formFields.put("port", "8080");
        String json = mapper.writeValueAsString(formFields);

        controller.doApiSpecForm(multipartFile, json, session, response);
        verify(response).setStatus(HttpServletResponse.SC_OK);
    }

    @Test
    void doApiSpecForm_invalidSession() {
        when(session.getAttribute("user")).thenReturn(null);
        controller.doApiSpecForm(multipartFile, "{}", session, response);
        verify(response).setStatus(HttpServletResponse.SC_FORBIDDEN);
    }

    @Test
    void doApiSpecForm_invalidAction() {
        User user = mock(User.class);
        when(session.getAttribute("user")).thenReturn(user);
        when(user.getAccessToken()).thenReturn("token");
        controller.doApiSpecForm(multipartFile, "{\"action\":\"invalid\"}", session, response);
        verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
    }

    // --- viewAPICodeForm ---
    @Test
    void viewAPICodeForm_success() {
        User user = mock(User.class);
        ApiSpec apiSpec = mock(ApiSpec.class);
        ApiCode apiCode = mock(ApiCode.class);
        when(session.getAttribute("user")).thenReturn(user);
        when(user.getAccessToken()).thenReturn("token");
        when(user.getApiSpec()).thenReturn(apiSpec);
        when(user.getApiCode()).thenReturn(apiCode);

        String result = controller.viewAPICodeForm(session);
        assertEquals("apiCodeForm", result);
    }

    @Test
    void viewAPICodeForm_invalidSession() {
        when(session.getAttribute("user")).thenReturn(null);
        String result = controller.viewAPICodeForm(session);
        assertEquals("registration", result);
    }

    // --- doApiCodeForm ---
    @Test
    void doApiCodeForm_success() throws Exception {
        User user = mock(User.class);
        ApiCode apiCode = mock(ApiCode.class);
        when(session.getAttribute("user")).thenReturn(user);
        when(user.getAccessToken()).thenReturn("token");
        when(service.generateCode(any(), any(), any())).thenReturn(apiCode);

        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> formFields = new HashMap<>();
        formFields.put("action", "generate");
        String json = mapper.writeValueAsString(formFields);

        controller.doApiCodeForm(json, session, response);
        verify(response).setStatus(HttpServletResponse.SC_OK);
    }

    @Test
    void doApiCodeForm_invalidSession() {
        when(session.getAttribute("user")).thenReturn(null);
        controller.doApiCodeForm("{}", session, response);
        verify(response).setStatus(HttpServletResponse.SC_FORBIDDEN);
    }

    @Test
    void doApiCodeForm_invalidAction() {
        User user = mock(User.class);
        when(session.getAttribute("user")).thenReturn(user);
        when(user.getAccessToken()).thenReturn("token");
        controller.doApiCodeForm("{\"action\":\"invalid\"}", session, response);
        verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
    }

    // --- getApiSpecFile ---
    @SuppressWarnings("deprecation")
    @Test
    void getApiSpecFile_success() throws Exception {
        User user = mock(User.class);
        ApiSpec apiSpec = mock(ApiSpec.class);
        File file = File.createTempFile("swagger", ".json");
        file.deleteOnExit();
        when(session.getAttribute("user")).thenReturn(user);
        when(user.getAccessToken()).thenReturn("token");
        when(user.getApiSpec()).thenReturn(apiSpec);
        when(apiSpec.isValid()).thenReturn(true);
        when(apiSpec.getSwaggerFile()).thenReturn(file);

        ResponseEntity<InputStreamResource> responseEntity = controller.getApiSpecFile(session);
        assertEquals(200, responseEntity.getStatusCodeValue());
    }

    @SuppressWarnings("deprecation")
    @Test
    void getApiSpecFile_invalidSession() {
        when(session.getAttribute("user")).thenReturn(null);
        ResponseEntity<InputStreamResource> responseEntity = controller.getApiSpecFile(session);
        assertEquals(404, responseEntity.getStatusCodeValue());
    }

    // --- saveApiSpecFile ---
    @SuppressWarnings("deprecation")
    @Test
    void saveApiSpecFile_success() throws Exception {
        User user = mock(User.class);
        ApiSpec apiSpec = mock(ApiSpec.class);
        File file = File.createTempFile("swagger", ".json");
        file.deleteOnExit();
        when(session.getAttribute("user")).thenReturn(user);
        when(user.getAccessToken()).thenReturn("token");
        when(user.getApiSpec()).thenReturn(apiSpec);
        when(apiSpec.isValid()).thenReturn(true);
        when(apiSpec.getSwaggerFile()).thenReturn(file);

        ResponseEntity<String> responseEntity = controller.saveApiSpecFile("{}", session);
        assertEquals(200, responseEntity.getStatusCodeValue());
    }

    @SuppressWarnings("deprecation")
    @Test
    void saveApiSpecFile_invalidSession() {
        when(session.getAttribute("user")).thenReturn(null);
        ResponseEntity<String> responseEntity = controller.saveApiSpecFile("{}", session);
        assertEquals(403, responseEntity.getStatusCodeValue());
    }

    // --- apiCodeZipFile ---
    @SuppressWarnings("deprecation")
    @Test
    void apiCodeZipFile_success() throws Exception {
        User user = mock(User.class);
        ApiCode apiCode = mock(ApiCode.class);
        File dir = Files.createTempDirectory("apicode").toFile();
        dir.deleteOnExit();
        when(session.getAttribute("user")).thenReturn(user);
        when(user.getAccessToken()).thenReturn("token");
        when(user.getApiCode()).thenReturn(apiCode);
        when(apiCode.isValid()).thenReturn(true);
        when(apiCode.getApiCodeDir()).thenReturn(dir);

        ResponseEntity<StreamingResponseBody> responseEntity = controller.apiCodeZipFile(session);
        assertEquals(200, responseEntity.getStatusCodeValue());
    }

    @SuppressWarnings("deprecation")
    @Test
    void apiCodeZipFile_invalidSession() {
        when(session.getAttribute("user")).thenReturn(null);
        ResponseEntity<StreamingResponseBody> responseEntity = controller.apiCodeZipFile(session);
        assertEquals(404, responseEntity.getStatusCodeValue());
    }

    // --- jsonToBoolean & jsonToString ---
    @Test
    void jsonToBoolean_and_jsonToString() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        String json = "{\"flag\":true,\"text\":\"hello\"}";
        var node = mapper.readTree(json);
        assertTrue(controller.jsonToBoolean(node, "flag"));
        assertEquals("hello", controller.jsonToString(node, "text"));
        assertFalse(controller.jsonToBoolean(node, "missing"));
        assertEquals("", controller.jsonToString(node, "missing"));
    }
}