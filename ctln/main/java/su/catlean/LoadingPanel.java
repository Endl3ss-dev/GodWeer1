// deobf by pusia
package su.catlean;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.JPanel;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import org.jetbrains.annotations.NotNull;

public final class LoadingPanel extends JPanel {
   @NotNull
   public static final LoadingPanel INSTANCE = new LoadingPanel();
   @NotNull
   private static final ModContainer modContainer = (ModContainer)FabricLoader.getInstance().getModContainer("catlean").orElseThrow();

   private LoadingPanel() {
   }

   @NotNull
   public final ModContainer getModContainer() {
      return modContainer;
   }

   private final Object readResolve() {
      return INSTANCE;
   }

   @Override
   public void paintComponent(Graphics g) {
      super.paintComponent(g);
      Graphics2D graphics2D = (Graphics2D)g;
      graphics2D.setColor(Color.GREEN);
      graphics2D.setFont(new Font("Monospaced", 1, 12));
      graphics2D.drawString("  /$$$$$$              /$$     /$$                                    ", 10, 20);
      graphics2D.drawString(" /$$__  $$            | $$    | $$                                    \n", 10, 30);
      graphics2D.drawString("| $$  \\__/  /$$$$$$  /$$$$$$  | $$        /$$$$$$   /$$$$$$  /$$$$$$$ \n", 10, 40);
      graphics2D.drawString("| $$       |____  $$|_  $$_/  | $$       /$$__  $$ |____  $$| $$__  $$\n", 10, 50);
      graphics2D.drawString("| $$        /$$$$$$$  | $$    | $$      | $$$$$$$$  /$$$$$$$| $$  \\ $$\n", 10, 60);
      graphics2D.drawString("| $$    $$ /$$__  $$  | $$ /$$| $$      | $$_____/ /$$__  $$| $$  | $$\n", 10, 70);
      graphics2D.drawString("|  $$$$$$/|  $$$$$$$  |  $$$$/| $$$$$$$$|  $$$$$$$|  $$$$$$$| $$  | $$\n", 10, 80);
      graphics2D.drawString(" \\______/  \\_______/   \\___/  |________/ \\_______/ \\_______/|__/  |__/\n", 10, 90);
      short shortValue = 250;
      byte byteValue = 110;
      graphics2D.drawString("v" + modContainer.getMetadata().getVersion(), 10, 110);
      graphics2D.drawString("Welcome back, " + System.getProperty("user.name") + "! =^.^=", 10, 210);
      if (System.currentTimeMillis() % 1000 > 500L) {
         graphics2D.drawString("                                   \n", (int)shortValue, (int)byteValue);
         graphics2D.drawString("                                    \n", shortValue, byteValue + 10);
         graphics2D.drawString("                    @@@@@           \n", shortValue, byteValue + 20);
         graphics2D.drawString("                   @@@@@@@          \n", shortValue, byteValue + 30);
         graphics2D.drawString("                 @@@@@@@@           \n", shortValue, byteValue + 40);
         graphics2D.drawString("                @@@@@@@@@@          \n", shortValue, byteValue + 50);
         graphics2D.drawString("               @@@@@@@@@@@@@        \n", shortValue, byteValue + 60);
         graphics2D.drawString("                 @@@    @@@@@@@     \n", shortValue, byteValue + 70);
         graphics2D.drawString("                          @@@@@@@   \n", shortValue, byteValue + 80);
         graphics2D.drawString("                             @@@    \n", shortValue, byteValue + 90);
         graphics2D.drawString("                                    \n", shortValue, byteValue + 100);
         graphics2D.drawString("            @@@@@@@@@@@@@@@@@@@     \n", shortValue, byteValue + 110);
         graphics2D.drawString("      @@@@@@@%@%%%%%@@@@@@@         \n", shortValue, byteValue + 120);
         graphics2D.drawString("         %@@@@@@@@%@@@@=            \n", shortValue, byteValue + 130);
         graphics2D.drawString("              @@@@@@@@              \n", shortValue, byteValue + 140);
         graphics2D.drawString("               @@@@@@               \n", shortValue, byteValue + 150);
         graphics2D.drawString("               @@@@@@               \n", shortValue, byteValue + 160);
         graphics2D.drawString("              @@@@@@@@              \n", shortValue, byteValue + 170);
         graphics2D.drawString("           .@@@@-  @@@@@            ", shortValue, byteValue + 180);
      } else {
         graphics2D.drawString("                                    \n", (int)shortValue, (int)byteValue);
         graphics2D.drawString("                                    \n", shortValue, byteValue + 10);
         graphics2D.drawString("                                    \n", shortValue, byteValue + 20);
         graphics2D.drawString("                                    \n", shortValue, byteValue + 30);
         graphics2D.drawString("                                    \n", shortValue, byteValue + 40);
         graphics2D.drawString("                  @@@@%             \n", shortValue, byteValue + 50);
         graphics2D.drawString("        &         @@@@@@            \n", shortValue, byteValue + 60);
         graphics2D.drawString("     %  '         @@@@@@@@@@@@@@@@  \n", shortValue, byteValue + 70);
         graphics2D.drawString("      %   .       @@@@@@@@@@@@@@@@  \n", shortValue, byteValue + 80);
         graphics2D.drawString("    %    &        @@@@@@            \n", shortValue, byteValue + 90);
         graphics2D.drawString("       %          @@@@@@            \n", shortValue, byteValue + 100);
         graphics2D.drawString("            @@@@@@@@@@@@@@@@@@@     \n", shortValue, byteValue + 110);
         graphics2D.drawString("      @@@@@@@%@%%%%%@@@@@@@         \n", shortValue, byteValue + 120);
         graphics2D.drawString("         %@@@@@@@@%@@@@=            \n", shortValue, byteValue + 130);
         graphics2D.drawString("              @@@@@@@@              \n", shortValue, byteValue + 140);
         graphics2D.drawString("               @@@@@@               \n", shortValue, byteValue + 150);
         graphics2D.drawString("               @@@@@@               \n", shortValue, byteValue + 160);
         graphics2D.drawString("              @@@@@@@@              \n", shortValue, byteValue + 170);
         graphics2D.drawString("           .@@@@-  @@@@@            ", shortValue, byteValue + 180);
      }
   }

   static {
      INSTANCE.setPreferredSize(new Dimension(510, 300));
      INSTANCE.setBackground(new Color(10, 10, 10));
   }
}