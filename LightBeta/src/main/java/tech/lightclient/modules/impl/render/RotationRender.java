package tech.lightclient.modules.impl.render;

import tech.lightclient.modules.api.Category;
import tech.lightclient.modules.api.Function;
import tech.lightclient.modules.api.FunctionAdd;

@FunctionAdd(
        name = "RotationRender",
        alias = "Rotation Render",
        category = Category.Render,
        description = "Скрывает визуальное вращение модели в F5 (аура не наводится)"
)
public class RotationRender extends Function {
}
