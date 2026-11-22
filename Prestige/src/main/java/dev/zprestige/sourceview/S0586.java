package dev.zprestige.sourceview;

import java.io.InputStream;
import java.net.URL;
import java.security.SecureClassLoader;

public class S0586 extends SecureClassLoader {
   public S0586(ClassLoader parentClassLoader)  throws Throwable {
      super("PrestigeClassLoader", parentClassLoader);
try {

   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   @Override
   public native Class<?> loadClass(String var1);

   @Override
   public native InputStream getResourceAsStream(String var1);

   @Override
   public native URL getResource(String var1);

   @Override
   public native Class<?> findClass(String var1);
}
