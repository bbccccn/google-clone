package com.gclone.engine.controller;

import com.gclone.engine.exception.IndexFolderNotAccessibleException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@ControllerAdvice
public class SearchEngineErrorController {

    @ExceptionHandler(IndexFolderNotAccessibleException.class)
    public ModelAndView handleIndexAccessError(IndexFolderNotAccessibleException e) {
        return new ModelAndView("error").addObject("additionalErrorMessage", e.getMessage());
    }

    @RequestMapping("/error")
    public String displayDefaultError() {
        return "error";
    }
}
