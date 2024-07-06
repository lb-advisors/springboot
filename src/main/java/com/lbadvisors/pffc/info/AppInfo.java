package com.lbadvisors.pffc.info;

import lombok.Data;

@Data
public class AppInfo {
    String environment;
    String builtTime;
    String commitMessage;
    String commitHash;

}
