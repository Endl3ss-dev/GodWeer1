package never.lose.cosmetics;

public enum CosmeticSection {

    ALL("Все"),
    AVATARS("Аватары"),
    ACCESSORIES("Аксессуары"),
    WINGS("Крылья"),
    PETS("Питомцы");

    private final String title;

    CosmeticSection(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }
}
