package never.lose.modules.api.setting;

import java.util.function.Supplier;

public interface ISetting {
    Setting setVisible(Supplier<Boolean> visible);
}
