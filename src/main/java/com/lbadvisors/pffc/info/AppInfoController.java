package com.lbadvisors.pffc.info;

import java.time.Instant;
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

        /*
         * Instant instant = Instant.parse(env.getProperty("BUILT_TIME"));
         * 
         * // Convert Instant to ZonedDateTime with UTC timezone
         * ZonedDateTime utcDateTime = ZonedDateTime.ofInstant(instant,
         * ZoneId.of("UTC"));
         * 
         * // Convert UTC ZonedDateTime to Eastern Standard Time (EST) or Eastern
         * Daylight
         * // Time (EDT)
         * ZonedDateTime estDateTime =
         * utcDateTime.withZoneSameInstant(ZoneId.of("America/New_York"));
         * 
         * // Define a formatter for the output format
         * DateTimeFormatter formatter =
         * DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
         * 
         * // Format EST ZonedDateTime to human-readable string
         * String formattedDateTime = estDateTime.format(formatter) + " EST";
         * 
         */

        AppInfo appInfo = new AppInfo();
        appInfo.builtTime = env.getProperty("BUILT_TIME");// formattedDateTime;
        appInfo.commitMessage = env.getProperty("COMMIT_MESSAGE");
        appInfo.commitHash = env.getProperty("COMMIT_HASH");

        // return new ResponseEntity<>(appInfo, HttpStatus.OK);
        return ResponseEntity.ok(appInfo);

    }

}