package com.example.productos_api;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class AuthFlowTest {

    @Autowired
    private MockMvc mockMvc;

    private String registrarYObtenerToken() throws Exception {

        String username = "user-" + UUID.randomUUID();
        String credenciales = """
                {"username": "%s", "password": "clave12345"}
                """.formatted(username);

        mockMvc.perform(post("/usuarios/registro")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(credenciales))
                .andExpect(status().isOk());

        String respuesta = mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(credenciales))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists())
                .andReturn().getResponse().getContentAsString();

        return JsonPath.read(respuesta, "$.token");
    }

    @Test
    void conTokenRealDeLogin_accedeAProductos() throws Exception {
        String token = registrarYObtenerToken();

        mockMvc.perform(get("/productos")
                    .header("Authorization", "Bearer " + token ))
                .andExpect(status().isOk());
    }

    @Test
    void sinToken_productosResponde401() throws  Exception {

        mockMvc.perform(get("/productos"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void conTokenInvalido_productosResponde401ConMensaje() throws Exception {

        mockMvc.perform(get("/productos")
                    .header("Authorization", "Bearer abs123" ))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("Token inválido o expirado"));
    }

    @Test
    void loginConPasswordIncorrecta_responde401() throws Exception {

        String username = "user-" + UUID.randomUUID();

        mockMvc.perform(post("/usuarios/registro")
                .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                .content("""
                        {"username": "%s", "password": "clave12345"}
                        """.formatted(username)));

        mockMvc.perform(post("/login")
                    .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                    .content("""
                            {"username": "%s", "password": "incorrecta"}
                        """.formatted(username)))
                .andExpect(status().isUnauthorized());
    }
}
