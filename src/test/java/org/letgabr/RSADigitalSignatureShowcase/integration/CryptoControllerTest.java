package org.letgabr.RSADigitalSignatureShowcase.integration;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.letgabr.RSADigitalSignatureShowcase.dto.RSAPrimes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigInteger;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureMockMvc
public class CryptoControllerTest
{
    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;

    @Test
    public void getPrimesTest() throws Exception
    {
        mockMvc.perform(get("/primes"))
                .andDo(print())
                .andExpectAll(jsonPath("$.p").isNotEmpty(), jsonPath("$.q").isNotEmpty());
    }

    @Test
    public void primeCheckSuccessTest() throws Exception
    {
        String p = "17";
        String q = "32";
        RSAPrimes rsaPrimes = new RSAPrimes(p, q);
        mockMvc.perform(post("/primes-check").contentType("application/json").content(objectMapper.writeValueAsString(rsaPrimes)))
                .andDo(print())
                .andExpect(jsonPath("$.p").value("true"))
                .andExpect(jsonPath("$.q").value("false"));
    }

    @Test
    public void primeCheckSuccessWithBigNumberTest() throws Exception
    {
        String p = "6703903964971298549787012499102923063739682910296196688861780721860882015036889280490174470407579092461170659254855642123074390729713186390953696842544691";
        String q = "6703903964971298549787012499102923063739682910296196688861780721860882015036889280490174470407579092461170659254855642123074390729713186390953696842544692";
        RSAPrimes rsaPrimes = new RSAPrimes(p, q);
        mockMvc.perform(post("/primes-check").contentType("application/json").content(objectMapper.writeValueAsString(rsaPrimes)))
                .andDo(print())
                .andExpect(jsonPath("$.p").value("true"))
                .andExpect(jsonPath("$.q").value("false"));
    }

    @Test
    public void primeCheckFailureTest() throws Exception
    {
        String p = "670390396497129854978701249910292306373968291029619668886178072186088201503688928049017447040757909246117065925485564212307439072971318639095369684254469167039039649712985497870124991029230637396829102961966888617807218608820150368892804901744704075790924611706592548556421230743907297131863909536968425446916703903964971298549787012499102923063739682910296196688861780721860882015036889280490174470407579092461170659254855642123074390729713186390953696842544691";
        String q = "6703903964971298549787012499102923063739682910296196688861780721860882015036889280490174470407579092461170659254855642123074390729713186390953696842544692";
        RSAPrimes rsaPrimes = new RSAPrimes(p, q);
        mockMvc.perform(post("/primes-check").contentType("application/json").content(objectMapper.writeValueAsString(rsaPrimes)))
                .andDo(print())
                .andExpect(content().string("p or q too big"));
    }

    @Test
    public void getKeysByPrimesSuccessTest() throws Exception
    {
        String p = "17";
        String q = "37";
        RSAPrimes rsaPrimes = new RSAPrimes(p, q);
        mockMvc.perform(post("/keys").contentType("application/json").content(objectMapper.writeValueAsString(rsaPrimes)))
                .andDo(print())
                .andExpect(jsonPath("$.privateKey").isNotEmpty())
                .andExpect(jsonPath("$.publicKey").isNotEmpty())
                .andExpect(jsonPath("$.primesMultiplication").isNotEmpty());
    }

    @Test
    public void getKeysByPrimesFailureTest() throws Exception
    {
        String p = "17";
        String q = "32";
        RSAPrimes rsaPrimes = new RSAPrimes(p, q);
        mockMvc.perform(post("/keys").contentType("application/json").content(objectMapper.writeValueAsString(rsaPrimes)))
                .andDo(print())
                .andExpect(content().string("q isn't prime"));
    }
}
