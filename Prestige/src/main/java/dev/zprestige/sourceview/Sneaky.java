package dev.zprestige.sourceview;
public final class Sneaky {
 private Sneaky(){}
 public static RuntimeException raise(Throwable error){Sneaky.<RuntimeException>rethrow(error);return null;}
 @SuppressWarnings("unchecked") private static <T extends Throwable>void rethrow(Throwable error)throws T{throw (T)error;}
}
