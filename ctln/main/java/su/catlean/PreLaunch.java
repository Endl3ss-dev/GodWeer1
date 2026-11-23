// deobf by pusia
package su.catlean;

import java.awt.GridLayout;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import net.fabricmc.loader.api.entrypoint.PreLaunchEntrypoint;
import org.apache.commons.lang3.SystemUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class PreLaunch implements PreLaunchEntrypoint {
   @NotNull
   public static final PreLaunch INSTANCE = new PreLaunch();
   @Nullable
   private static JFrame jFrame;

   // [deobf] private in the Kotlin original (object); public here so the default Java
   // entrypoint adapter can instantiate it (javac output has no @kotlin.Metadata).
   public PreLaunch() {
   }

   public final void applySetVisible() {
      if (jFrame != null) {
         jFrame.setVisible(false);
      }
   }

   public void onPreLaunch() {
      if (SystemUtils.IS_OS_WINDOWS) {
         SwingUtilities.invokeLater(() -> {
            JFrame jFrame2 = new JFrame("CatLean");
            jFrame2.setDefaultCloseOperation(2);
            jFrame2.getContentPane().add(LoadingPanel.INSTANCE);
            jFrame2.pack();
            jFrame2.setVisible(true);
            jFrame2.setResizable(false);
            jFrame2.setLayout(new GridLayout());
            jFrame2.setLocationRelativeTo(null);
            jFrame = jFrame2;
            Timer timer = new Timer(100, arg -> {
               if (jFrame != null ? jFrame.isVisible() : false) {
                  if (jFrame != null) {
                     jFrame.repaint();
                  }
               }
            });
            timer.setRepeats(true);
            timer.start();
         });
      }
   }
}