package nuclear.module.api;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import nuclear.module.api.ModuleCategory;

@Retention(RetentionPolicy.RUNTIME)
public @interface ModuleDescriptor {
    String name();

    ModuleCategory type();

    String desc() default "";

    int key() default 168754412;
}
