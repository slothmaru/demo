package com.example.demo.controller;

import com.example.demo.dto.ResponseDTO;
import com.example.demo.dto.TodoDTO;
import com.example.demo.model.TodoEntity;
import com.example.demo.service.TodoService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RequestMapping("todo")
@RequiredArgsConstructor
@RestController
public class TodoController {
    private final TodoService service;
    private final ModelMapper modelMapper;

    @GetMapping("/test")
    public ResponseEntity<?> testTodo() {
        String str = service.testService();
        List<String> list = new ArrayList<>();
        list.add(str);
        ResponseDTO<String> response = ResponseDTO.<String>builder()
                .items(list)
                .build();
        return ResponseEntity.ok().body(response);
    }

    @PostMapping
    public ResponseEntity<?> createTodo(@RequestBody TodoDTO dto) {
        try {
            String temporaryUserId = "temporary-user";
            // (1) TodoEntity로 변환
            TodoEntity entity = modelMapper.map(dto, TodoEntity.class);
            // (2) id를 null로 초기화 한다. 생성 당시에는 id가 없어야 하기 때문
            entity.setId(null);
            // (3) 임시 유저 아이디를 설정 > 4장에서 인증과 인가에서 수정
            entity.setUserId(temporaryUserId);
            // (4) 서비스를 이용해 Todo 엔티티 생성
            List<TodoEntity> entities = service.create(entity);
            // (5) 자바 스트림을 이용해 리턴된 엔티티 리스트를 TodoDTO 리스트로 변환
            List<TodoDTO> dtos = entities.stream().map(item -> modelMapper.map(item, TodoDTO.class)).collect(Collectors.toList());
            // (6) 변환된 TodoDTO 리스트를 이용해 ResponseDTO를 초기화
            ResponseDTO<TodoDTO> response = ResponseDTO.<TodoDTO>builder().items(dtos).build();
            // (7) ResponseDTO를 리턴
            return ResponseEntity.ok().body(response);
        } catch (Exception e) {
            // (8) 혹시 예외가 나는 경우 dto 대신 message를 넣어 리턴
            ResponseDTO<TodoDTO> response = ResponseDTO.<TodoDTO>builder().message(e.getMessage()).build();
            return ResponseEntity.badRequest().body(response);
        }
    }
}

