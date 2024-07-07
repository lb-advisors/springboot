package com.lbadvisors.pffc.controllers.info;

import lombok.Data;

@Data
public class AppInfo {
    String environment;
    String builtTime;
    String commitMessage;
    String commitHash;

}
