package edu.ucsb.cs156.example.controllers;

import edu.ucsb.cs156.example.repositories.UserRepository;
import edu.ucsb.cs156.example.testconfig.TestConfig;
import edu.ucsb.cs156.example.ControllerTestCase;
import edu.ucsb.cs156.example.entities.Articles;
import edu.ucsb.cs156.example.repositories.ArticlesRepository;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@WebMvcTest(controllers = ArticlesController.class)
@Import(TestConfig.class)
public class ArticlesControllerTests extends ControllerTestCase {

	@MockBean
	ArticlesRepository articlesRepository;

	@MockBean
	UserRepository userRepository;

	// Authorization tests for /api/articles/admin/all

	@Test
	public void logged_out_users_cannot_get_all() throws Exception {
		mockMvc.perform(get("/api/articles/all"))
				.andExpect(status().is(403)); // logged out users can't get all
	}

	@WithMockUser(roles = { "USER" })
	@Test
	public void logged_in_users_can_get_all() throws Exception {
		mockMvc.perform(get("/api/articles/all"))
				.andExpect(status().is(200)); // logged
	}

	// Authorization tests for /api/articles/post
	// (Perhaps should also have these for put and delete)

	@Test
	public void logged_out_users_cannot_post() throws Exception {
		mockMvc.perform(post("/api/articles/post"))
				.andExpect(status().is(403));
	}

	@WithMockUser(roles = { "USER" })
	@Test
	public void logged_in_regular_users_cannot_post() throws Exception {
		mockMvc.perform(post("/api/articles/post"))
				.andExpect(status().is(403)); // only admins can post
	}

	@WithMockUser(roles = { "ADMIN", "USER" })
	@Test
	public void an_admin_user_can_post_a_new_article() throws Exception {
		// arrange

		LocalDateTime ldt1 = LocalDateTime.parse("2022-01-03T00:00:00");
		Articles article1 = Articles.builder().id(0L).title("TestTitle").url("ajayliu.com")
				.explanation("TestExplanation")
				.email("test@email.com").dateAdded(ldt1).build();

		when(articlesRepository.save(eq(article1))).thenReturn(article1);

		// act
		MvcResult response = mockMvc.perform(
				post("/api/articles/post?title=TestTitle&url=ajayliu.com&explanation=TestExplanation&email=test@email.com&dateAdded=2022-01-03T00:00:00")
						.with(csrf()))
				.andExpect(status().isOk()).andReturn();

		// assert
		verify(articlesRepository, times(1)).save(article1);

		String expectedJson = mapper.writeValueAsString(article1);
		String responseString = response.getResponse().getContentAsString();
		assertEquals(expectedJson, responseString);
	}
}
