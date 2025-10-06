package com.careerit.isapp.appconfig;

import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityContextUserDetails {

    public static String username(){
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }
}
