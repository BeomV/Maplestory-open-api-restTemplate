package com.dev.api;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import lombok.extern.log4j.Log4j;

@ControllerAdvice // 해당 객체가 스프링의 컨트롤러에서 발생하는 예외를 처리하는 존재임을 명시.
public class CommonExceptionAdvice {
    @ExceptionHandler(Exception.class)

    public String except(Exception e, Model model) {
        // 예외가 발생하게 되면 해당 예외 필드가 메모리에 할당된다.
        // 할당된 예외 필드의 주소 값을 받을 객체가 필요하므로 매개변수에 Exception타입의
        // e 객체를 선언해놓는다.

        model.addAttribute("exception", e);
        return "home";
    }

}