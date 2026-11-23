// deobf by pusia
package su.catlean.interfaces;

import net.minecraft.world.phys.Vec3;

public interface Resolvable {
   void savePosition(Vec3 vec3);

   Vec3 getSavedPosition();
}