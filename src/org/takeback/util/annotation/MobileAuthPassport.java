// 
// Decompiled by Procyon v0.5.30
// 

package org.takeback.util.annotation;

import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Retention;
import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
import java.lang.annotation.Inherited;
import java.lang.annotation.Annotation;

@Inherited
@Target({ ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface MobileAuthPassport {
    MobileAuthPassportType value() default MobileAuthPassportType.APP;
}
