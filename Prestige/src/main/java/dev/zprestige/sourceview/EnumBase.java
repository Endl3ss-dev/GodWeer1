package dev.zprestige.sourceview;
/** Compile-time superclass. Restored to java.lang.Enum by the source linker. */
public abstract class EnumBase implements java.io.Serializable,Comparable<EnumBase> {
 private final String name;private final int ordinal;
 protected EnumBase(String name,int ordinal){this.name=name;this.ordinal=ordinal;}
 public final String name(){return name;}public final int ordinal(){return ordinal;}
 public final int compareTo(EnumBase other){return Integer.compare(ordinal,other.ordinal);}
 public String toString(){return name;}
}
