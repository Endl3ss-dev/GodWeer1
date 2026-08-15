package antileak.base.api.storages.implement.helpertstorages;

import lombok.Getter;
import lombok.Setter;
import antileak.base.Punch;
import antileak.base.api.QClient;
import antileak.base.api.utils.color.ColorUtils;

@Getter
@Setter
public class Theme implements QClient {
    private String name;
    public int[] color;

    public Theme(String name, int... color) {
        this.name = name;
        this.color = color;
    }

    public int getColor(int index) {
        // Rainbow тема - радужный градиент
        if (this.name.equals("Rainbow")) {
            return ColorUtils.rainbow(10, index, 0.6f, 1, 1);
        }
        
        // Обычные темы
        return ColorUtils.gradient(5, index, color);
    }
}
