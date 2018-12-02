// 
// Decompiled by Procyon v0.5.30
// 

package org.takeback.chat.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.takeback.chat.service.RedService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.stereotype.Controller;

@Controller("redController")
@RequestMapping({ "/red" })
public class RedController
{
    @Autowired
    private RedService redService;
}
