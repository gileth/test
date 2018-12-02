// 
// Decompiled by Procyon v0.5.30
// 

package org.takeback.mvc.controller.core;

import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestMapping;
import org.takeback.core.schema.Schema;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SchemaLocator
{
    @RequestMapping(value = { "/**/{id}.sc" }, method = { RequestMethod.GET })
    public Schema get(@PathVariable final String id) {
        return null;
    }
}
