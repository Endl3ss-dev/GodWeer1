package never.lose.modules.api;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface ModuleInfo {
    String name();
    String desc() default "";
    ModuleCategory category();
    int key() default -1;
}
