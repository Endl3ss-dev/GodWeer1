package fun.volon.client.modules.impl.render;

import fun.volon.client.modules.Module;

public class CustomModels extends Module {

    public static CustomModels INSTANCE = new CustomModels();

    public CustomModels() {
        super("CustomModels", "Изменяет пропорции модели игрока", ModuleCategory.RENDER);
    }

    public float getHeadScale() {
        return isEnable() ? 2.8f : 1.0f;  // Очень большая голова как на фото
    }

    public float getBodyScale() {
        return isEnable() ? 2.0f : 1.0f;  // Широкое тело как на фото
    }

    public float getLegsScale() {
        return isEnable() ? 0.35f : 1.0f;  // Очень короткие ноги как на фото
    }

    public float getArmsScale() {
        return isEnable() ? 1.4f : 1.0f;  // Широкие руки как на фото
    }
}
