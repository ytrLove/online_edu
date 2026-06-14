package com.common.Controller;


import com.common.core.domain.AjaxResult;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BannerController {

    @GetMapping("/home/banner")
    @PreAuthorize("permitAll()")
    public AjaxResult getBanner(){
        //
        return null;
    }
}
