// deobf by pusia
package su.catlean.api.enums;

import kotlin.enums.EnumEntries;
import kotlin.enums.EnumEntriesKt;
import org.jetbrains.annotations.NotNull;

public enum ParticleType {
   SMOKE(0.0F, 0.0F, 0.3906F, 0.3906F),
   CIRCLE(0.3906F, 0.0F, 0.6406F, 0.25F),
   CRACK(0.6406F, 0.0F, 0.8906F, 0.25F),
   CLOUD(0.8906F, 0.0F, 0.9531F, 0.0625F),
   DOLLAR(0.8906F, 0.0625F, 0.9531F, 0.125F),
   DROP(0.8906F, 0.125F, 0.9531F, 0.1875F),
   FIREFLY(0.8906F, 0.1875F, 0.9531F, 0.25F),
   FIREFLY_ALT(0.3906F, 0.25F, 0.4531F, 0.3125F),
   HEART(0.4531F, 0.25F, 0.5156F, 0.3125F),
   SNOWFLAKE(0.5156F, 0.25F, 0.5781F, 0.3125F),
   STAR(0.5781F, 0.25F, 0.6406F, 0.3125F),
   TRIANGLE1(0.6406F, 0.25F, 0.7031F, 0.3125F),
   TRIANGLE2(0.7031F, 0.25F, 0.7656F, 0.3125F),
   TRIANGLE3(0.7656F, 0.25F, 0.8281F, 0.3125F),
   TRIANGLE4(0.8281F, 0.25F, 0.8906F, 0.3125F),
   HALO(0.8906F, 0.25F, 0.9531F, 0.3096F),
   TRIANGLE5(0.3906F, 0.3125F, 0.4531F, 0.3701F);

   private final float amount;
   private final float amount2;
   private final float amount3;
   private final float amount4;
   private static final ParticleType[] particleTypes = $values();
   private static final EnumEntries<ParticleType> enumEntries = EnumEntriesKt.enumEntries(particleTypes);

   private ParticleType(float amount, float amount2, float amount3, float amount4) {
      this.amount = amount;
      this.amount2 = amount2;
      this.amount3 = amount3;
      this.amount4 = amount4;
   }

   public final float getAmount() {
      return this.amount;
   }

   public final float getAmount2() {
      return this.amount2;
   }

   public final float getAmount3() {
      return this.amount3;
   }

   public final float getAmount4() {
      return this.amount4;
   }

   @NotNull
   public static EnumEntries<ParticleType> getEnumEntries() {
      return enumEntries;
   }

   private static final ParticleType[] $values() {
      return new ParticleType[]{
         SMOKE, CIRCLE, CRACK, CLOUD, DOLLAR, DROP, FIREFLY, FIREFLY_ALT, HEART, SNOWFLAKE, STAR, TRIANGLE1, TRIANGLE2, TRIANGLE3, TRIANGLE4, HALO, TRIANGLE5
      };
   }
}