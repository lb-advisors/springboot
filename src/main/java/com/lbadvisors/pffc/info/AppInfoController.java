package com.lbadvisors.pffc.info;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.core.env.Environment;

@RestController
@RequestMapping("/info")
public class AppInfoController {

    @Autowired
    private Environment env;

    @GetMapping(value = "")
    public ResponseEntity<AppInfo> getAppInfo() {

        AppInfo appInfo = new AppInfo();
        appInfo.builtTime = env.getProperty("BUILT_TIME");
        appInfo.commitMessage = env.getProperty("COMMIT_MESSAGE");
        appInfo.commitHash = env.getProperty("COMMIT_HASH");

        // return new ResponseEntity<>(appInfo, HttpStatus.OK);
        return ResponseEntity.ok(appInfo);

    }

}
