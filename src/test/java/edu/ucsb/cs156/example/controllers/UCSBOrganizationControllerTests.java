package edu.ucsb.cs156.example.controllers;

import edu.ucsb.cs156.example.repositories.UserRepository;
import edu.ucsb.cs156.example.testconfig.TestConfig;
import edu.ucsb.cs156.example.ControllerTestCase;
import edu.ucsb.cs156.example.entities.UCSBDiningCommons;
import edu.ucsb.cs156.example.entities.UCSBOrganization;
import edu.ucsb.cs156.example.repositories.UCSBOrganizationRepository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@WebMvcTest(controllers = UCSBOrganizationController.class)
@Import(TestConfig.class)
public class UCSBOrganizationControllerTests extends ControllerTestCase {
    @MockBean
    UCSBOrganizationRepository ucsbOrganizationRepository;

    @MockBean
    UserRepository userRepository;

    @Test
    public void logged_out_users_cannot_get_all() throws Exception {
            mockMvc.perform(get("/api/ucsborganizations/all"))
                            .andExpect(status().is(403)); // logged out users can't get all
    }

    @WithMockUser(roles = { "USER" })
    @Test
    public void logged_in_users_can_get_all() throws Exception {
            mockMvc.perform(get("/api/ucsborganizations/all"))
                            .andExpect(status().is(200)); // logged
    }

    @Test
    public void logged_out_users_cannot_post() throws Exception {
            mockMvc.perform(post("/api/ucsborganizations/post"))
                            .andExpect(status().is(403));
    }

    @WithMockUser(roles = { "USER" })
    @Test
    public void logged_in_regular_users_cannot_post() throws Exception {
            mockMvc.perform(post("/api/ucsborganizations/post"))
                            .andExpect(status().is(403)); // only admins can post
    }

    @WithMockUser(roles = { "ADMIN", "USER" })
    @Test
    public void an_admin_user_can_post_a_new_organization() throws Exception {
            // arrange

            UCSBOrganization sigmanu = UCSBOrganization.builder()
                .orgCode("SNU")
                .orgTranslationShort("SIGMA NU")
                .orgTranslation("SIGMA NU FRATERNITY")
                .inactive(true)
                .build();

            when(ucsbOrganizationRepository.save(eq(sigmanu))).thenReturn(sigmanu);

            // act
            MvcResult response = mockMvc.perform(
                            post("/api/ucsborganizations/post?orgCode=SNU&orgTranslationShort=SIGMA NU&orgTranslation=SIGMA NU FRATERNITY&inactive=true")
                                            .with(csrf()))
                            .andExpect(status().isOk()).andReturn();

            // assert
            verify(ucsbOrganizationRepository, times(1)).save(sigmanu);
            String expectedJson = mapper.writeValueAsString(sigmanu);
            String responseString = response.getResponse().getContentAsString();
            assertEquals(expectedJson, responseString);
    }

    @WithMockUser(roles = { "USER" })
    @Test
    public void logged_in_user_can_get_all_ucsborganizations() throws Exception {

            // arrange

            UCSBOrganization skydiving = UCSBOrganization.builder()
                .orgCode("SKY")
                .orgTranslationShort("SKYDIVING CLUB")
                .orgTranslation("SKYDIVING CLUB AT UCSB")
                .inactive(false)
                .build();

            UCSBOrganization sigmanu = UCSBOrganization.builder()
                .orgCode("SNU")
                .orgTranslationShort("SIGMA NU")
                .orgTranslation("SIGMA NU FRATERNITY")
                .inactive(true)
                .build();

            ArrayList<UCSBOrganization> expectedOrgs = new ArrayList<>();
            expectedOrgs.addAll(Arrays.asList(skydiving, sigmanu));

            when(ucsbOrganizationRepository.findAll()).thenReturn(expectedOrgs);

            // act
            MvcResult response = mockMvc.perform(get("/api/ucsborganizations/all"))
                            .andExpect(status().isOk()).andReturn();

            // assert

            verify(ucsbOrganizationRepository, times(1)).findAll();
            String expectedJson = mapper.writeValueAsString(expectedOrgs);
            String responseString = response.getResponse().getContentAsString();
            assertEquals(expectedJson, responseString);
    }
    
}