package com.example.demo.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
public class GlobalExceptionHandlerFilter extends OncePerRequestFilter {

    // TODO : Exceptionhandler 쓰기

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            filterChain.doFilter(request, response);
        } catch (CustomException e) {


            ObjectMapper objectMapper = new ObjectMapper();

            //직렬화 문제 해결 코드
            objectMapper.registerModule(new JavaTimeModule());
            objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            var errorResponse = ErrorResponse.toResponseEntity(e.getErrorCode(), e.getValue()).getBody();


            try {
                String json = objectMapper.writeValueAsString(errorResponse);

                // 응답 상태 코드 및 응답 내용 설정

                response.setStatus(errorResponse.getStatus());
                response.getWriter().write(json);

//                response.getWriter().write(json);
            } catch (IOException er) {
                er.printStackTrace();
            }

        }
    }
}
