package com.lbadvisors.pffc.info;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

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

        // Parse UTC string to LocalDateTime
        ZonedDateTime utcDateTime = ZonedDateTime.parse(env.getProperty("BUILT_TIME"),
                DateTimeFormatter.ISO_INSTANT.withZone(java.time.ZoneOffset.UTC));

        ZonedDateTime estDateTime = utcDateTime.withZoneSameInstant(ZoneId.of("America/New_York"));

        // Define a formatter for the output format
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss EST");

        // Format LocalDateTime to human-readable string
        String formattedDateTime = estDateTime.format(formatter);

        AppInfo appInfo = new AppInfo();
        appInfo.builtTime = formattedDateTime;
        appInfo.commitMessage = env.getProperty("COMMIT_MESSAGE");
        appInfo.commitHash = env.getProperty("COMMIT_HASH");

        // return new ResponseEntity<>(appInfo, HttpStatus.OK);
        return ResponseEntity.ok(appInfo);

    }

}
