package net.skycore.client.ac;

/**
 * Семейства античитов, которые реально встречаются на HvH-серверах,
 * и что делать со спидами при каждом из них.
 */
public enum AcFamily {
   GRIM("GrimAC", new String[]{"grim", "grimac", "grimac"},
      "Таймер почти не трогать. Работать по конверту предсказания: Speedv3 с Strictness 0.0008-0.0011, "
         + "First Air Boost 1.0, Momentum 1.0. На Grim флагует не скорость сама по себе, а расхождение "
         + "с предсказанным смещением и задержка транзакций."),
   MATRIX("Matrix", new String[]{"matrix", "matrixac", "matrix-antihack"},
      "Matrix терпит постоянную скорость и ловит скачки и телепорты. Подходит Nuclear [Matrix] и "
         + "Speedv3 с Max/Air Factor 1.0-1.03 и Offset 0.0002. Не бустить сразу после сета."),
   VULCAN("Vulcan", new String[]{"vulcan", "vulcanac"},
      "Vulcan жёстко считает таймер. Множитель таймера <= 1.05, фазовые бусты Releon не годятся. "
         + "Подходит Swerve [Множитель] на 1.10-1.15 и Speedv3 с Momentum 0.95."),
   INTAVE("Intave", new String[]{"intave"},
      "Intave валит любые подмены delta после интеграции: буст нужно добавлять до travel. "
         + "Speedv3 c Air Factor 0.85-0.95, либо Speed > Private [NCP]."),
   VERUS("Verus", new String[]{"verus"},
      "Verus ловит повторяемые значения. Обязателен джиттер: Micro Jitter вкл, Jitter Strength 0.00003, "
         + "Strictness 0.0006."),
   SPARTAN("Spartan", new String[]{"spartan", "spartanac"},
      "Spartan терпит прыжковые бусты. Speed > Private [Spartan]/[Matrix703] по стилю, "
         + "или Speedv3 с First Air Boost 1.03 и Bhop Boost 1.02."),
   NCP("NoCheatPlus", new String[]{"nocheatplus", "ncp"},
      "NCP ловит sustained speed. Работает схема с прыжком + множителем (Speed > Private [NCP]) "
         + "и пауза 1-2 тика каждые 20."),
   AAC("AAC", new String[]{"aac", "aac5", "advancedanticheat"},
      "AAC ловит телепорты позиции. Speed > Private [AAC5] / [Matrix703]."),
   KARHU("Karhu", new String[]{"karhu"},
      "Karhu близок к Grim по предсказанию, но с более широким конвертом. Speedv3 Strictness 0.0010-0.0013."),
   POLAR("Polar", new String[]{"polar", "polarac"},
      "Polar агрессивен на таймере и на entity-бустах. Только чистая скорость без коллизий: "
         + "Speed > Nuclear [MetaHvH] без бафа."),
   THEMIS("Themis", new String[]{"themis"},
      "Themis чувствителен к transaction-обману. Не задерживать понги, работать Speedv3."),
   NEGATIVITY("Negativity", new String[]{"negativity", "negativityac"},
      "Negativity (старый Grim-форк) — как Grim, но конверт шире: Strictness 0.0013."),
   FIONA("Fiona", new String[]{"fiona"},
      "Fiona ловит таймер и NoSlow. Таймер 1.0, использовать Speedv3/Factor 1.0."),
   KAURI("Kauri", new String[]{"kauri"},
      "Kauri — предсказание + пакетный анализ. Как Grim: Speedv3, без Releon-фаз."),
   HYLEX("Hylex", new String[]{"hylex"},
      "Hylex ловит повторяемость. Джиттер обязателен."),
   ALOHA("Aloha", new String[]{"aloha"},
      "Aloha — мягкий предсказатель, терпит Speedv3 с Air Factor 1.03-1.06."),
   ERRATA("Errata", new String[]{"errata"},
      "Errata — Grim-подобный форк, работает конверт предсказания."),
   WATCHDOG("Watchdog", new String[]{"watchdog", "hypixel"},
      "Watchdog смотрит на таймер и на повторяемость пакетов. Таймер 1.0, джиттер вкл."),
   UNKNOWN("не определён", new String[]{"anticheat", "antihack", "античит"},
      "Определить не удалось. Прогоните режимы с включённой телеметрией (.ac speed) и пришлите отчёт.");

   private final String display;
   private final String[] keywords;
   private final String recommendation;

   AcFamily(String display, String[] keywords, String recommendation) {
      this.display = display;
      this.keywords = keywords;
      this.recommendation = recommendation;
   }

   public String display() {
      return this.display;
   }

   public String[] keywords() {
      return this.keywords;
   }

   public String recommendation() {
      return this.recommendation;
   }
}
