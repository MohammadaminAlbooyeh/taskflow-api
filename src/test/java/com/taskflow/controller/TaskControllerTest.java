package com.taskflow.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.taskflow.config.SecurityConfig;
import com.taskflow.model.dto.TaskDTO;
import com.taskflow.security.JwtTokenProvider;
import com.taskflow.service.TaskService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TaskController.class)
@Import(SecurityConfig.class)
@ActiveProfiles("test")
class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private TaskService taskService;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @MockBean
    private UserDetailsService userDetailsService;

    @Test
    @WithMockUser(username = "owner@taskflow.dev")
    void create_withValidTask_returns201() throws Exception {
        TaskDTO request = TaskDTO.builder().title("New task").projectId(1L).build();
        TaskDTO response = TaskDTO.builder().id(5L).title("New task").projectId(1L).build();

        when(taskService.create(any(), eq("owner@taskflow.dev"))).thenReturn(response);

        mockMvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser(username = "owner@taskflow.dev")
    void create_withBlankTitle_returns400() throws Exception {
        TaskDTO request = TaskDTO.builder().title("").projectId(1L).build();

        mockMvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "owner@taskflow.dev")
    void getForProject_returnsTaskList() throws Exception {
        when(taskService.getForProject(eq(1L), any())).thenReturn(new PageImpl<>(List.of(
                TaskDTO.builder().id(1L).title("Task 1").projectId(1L).build()
        )));

        mockMvc.perform(get("/api/tasks").param("projectId", "1"))
                .andExpect(status().isOk());
    }

    @Test
    void create_withoutAuth_returns403() throws Exception {
        TaskDTO request = TaskDTO.builder().title("New task").projectId(1L).build();

        mockMvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }
}
