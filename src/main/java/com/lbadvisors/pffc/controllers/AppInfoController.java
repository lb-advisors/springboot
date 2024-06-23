package com.lbadvisors.pffc.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lbadvisors.pffc.configuration.AppInfo;
import com.lbadvisors.pffc.configuration.Test;
import org.springframework.core.env.Environment;

@RestController
@RequestMapping("/info")
public class AppInfoController {

    @Autowired
    private Environment env;

    @GetMapping(value = "/")
    public String getAppInfo() {

        String variableValue = env.getProperty("COMMIT_MESSAGE");
        // return new ResponseEntity<>(appInfo, HttpStatus.OK);
        return "Commit message: " + variableValue;
    }

}
