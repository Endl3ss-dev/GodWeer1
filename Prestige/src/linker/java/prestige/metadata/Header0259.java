package prestige.metadata;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.Attribute;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.ConstantDynamic;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.Handle;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.RecordComponentVisitor;
import org.objectweb.asm.Type;
import org.objectweb.asm.TypePath;
public class Header0259 implements Opcodes {

public static byte[] dump () throws Exception {

ClassWriter classWriter = new ClassWriter(0);
FieldVisitor fieldVisitor;
RecordComponentVisitor recordComponentVisitor;
MethodVisitor methodVisitor;
AnnotationVisitor annotationVisitor0;

classWriter.visit(V21, ACC_PUBLIC | ACC_SUPER, "dev/zprestige/prestige/da", null, "java/lang/Object", new String[] { "net/minecraft/class_3262" });

classWriter.visitSource("da.java", null);

{
fieldVisitor = classWriter.visitField(ACC_PRIVATE | ACC_FINAL | ACC_STATIC, "a", "J", null, null);
fieldVisitor.visitEnd();
}
{
fieldVisitor = classWriter.visitField(ACC_PRIVATE | ACC_FINAL | ACC_STATIC, "b", "[Ljava/lang/String;", null, null);
fieldVisitor.visitEnd();
}
{
fieldVisitor = classWriter.visitField(ACC_PRIVATE | ACC_FINAL | ACC_STATIC, "c", "[Ljava/lang/String;", null, null);
fieldVisitor.visitEnd();
}
{
fieldVisitor = classWriter.visitField(ACC_PRIVATE | ACC_FINAL | ACC_STATIC, "d", "Ljava/util/Map;", null, null);
fieldVisitor.visitEnd();
}
{
fieldVisitor = classWriter.visitField(ACC_PRIVATE | ACC_FINAL | ACC_STATIC, "e", "[Ljava/lang/Object;", null, null);
fieldVisitor.visitEnd();
}
{
fieldVisitor = classWriter.visitField(ACC_PRIVATE | ACC_FINAL | ACC_STATIC, "f", "[Ljava/lang/String;", null, null);
fieldVisitor.visitEnd();
}
{
methodVisitor = classWriter.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null);
methodVisitor.visitEnd();
}
{
methodVisitor = classWriter.visitMethod(ACC_STATIC, "<clinit>", "()V", null, null);
methodVisitor.visitEnd();
}
{
methodVisitor = classWriter.visitMethod(ACC_PRIVATE | ACC_STATIC, "b", "(Ljava/lang/Class;Ljava/lang/String;Ljava/lang/Class;I[Ljava/lang/Class;)Ljava/lang/reflect/Method;", null, null);
methodVisitor.visitEnd();
}
{
methodVisitor = classWriter.visitMethod(ACC_PRIVATE | ACC_STATIC, "b", "(Ljava/lang/Class;Ljava/lang/String;Ljava/lang/Class;)Ljava/lang/reflect/Field;", null, null);
methodVisitor.visitEnd();
}
{
methodVisitor = classWriter.visitMethod(ACC_PUBLIC | ACC_STATIC, "b", "([Ljava/lang/Object;)Lnet/minecraft/class_3298;", null, null);
methodVisitor.visitEnd();
}
{
methodVisitor = classWriter.visitMethod(ACC_PRIVATE | ACC_STATIC, "b", "(JJ)Ljava/lang/Class;", null, null);
methodVisitor.visitEnd();
}
{
methodVisitor = classWriter.visitMethod(ACC_PRIVATE | ACC_STATIC, "b", "(Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/String;Ljava/lang/invoke/MethodType;)Ljava/lang/invoke/CallSite;", null, null);
methodVisitor.visitEnd();
}
{
methodVisitor = classWriter.visitMethod(ACC_PRIVATE | ACC_STATIC, "c", "(JJ)Ljava/lang/reflect/Field;", null, null);
methodVisitor.visitEnd();
}
{
methodVisitor = classWriter.visitMethod(ACC_PRIVATE | ACC_STATIC, "d", "(JJ)Ljava/lang/reflect/Method;", null, null);
methodVisitor.visitEnd();
}
{
methodVisitor = classWriter.visitMethod(ACC_PRIVATE | ACC_STATIC, "a", "(Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/invoke/MutableCallSite;Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/Object;", null, null);
methodVisitor.visitEnd();
}
{
methodVisitor = classWriter.visitMethod(ACC_PRIVATE | ACC_STATIC, "a", "(IJ)Ljava/lang/String;", null, null);
methodVisitor.visitEnd();
}
{
methodVisitor = classWriter.visitMethod(ACC_PRIVATE | ACC_STATIC, "a", "()V", null, null);
methodVisitor.visitEnd();
}
{
methodVisitor = classWriter.visitMethod(ACC_PRIVATE | ACC_STATIC, "a", "(JJ)I", null, null);
methodVisitor.visitEnd();
}
{
methodVisitor = classWriter.visitMethod(ACC_PRIVATE | ACC_STATIC, "a", "(Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/String;Ljava/lang/invoke/MethodType;)Ljava/lang/invoke/CallSite;", null, null);
methodVisitor.visitEnd();
}
{
methodVisitor = classWriter.visitMethod(ACC_PRIVATE | ACC_STATIC, "a", "(Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/invoke/MutableCallSite;Ljava/lang/String;Ljava/lang/invoke/MethodType;[Ljava/lang/Object;)Ljava/lang/Object;", null, null);
methodVisitor.visitEnd();
}
{
methodVisitor = classWriter.visitMethod(ACC_PRIVATE | ACC_STATIC, "a", "(Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/invoke/MutableCallSite;Ljava/lang/String;Ljava/lang/invoke/MethodType;JJ)Ljava/lang/invoke/MethodHandle;", null, null);
methodVisitor.visitEnd();
}
{
methodVisitor = classWriter.visitMethod(ACC_PRIVATE | ACC_STATIC, "a", "(Ljava/lang/Class;Ljava/lang/String;Ljava/lang/Class;I[Ljava/lang/Class;)Ljava/lang/reflect/Method;", null, null);
methodVisitor.visitEnd();
}
{
methodVisitor = classWriter.visitMethod(ACC_PRIVATE | ACC_STATIC, "a", "(Ljava/lang/Class;Ljava/lang/String;Ljava/lang/Class;)Ljava/lang/reflect/Field;", null, null);
methodVisitor.visitEnd();
}
{
methodVisitor = classWriter.visitMethod(ACC_PUBLIC | ACC_STATIC, "a", "([Ljava/lang/Object;)Lnet/minecraft/class_7367;", null, null);
methodVisitor.visitEnd();
}
{
methodVisitor = classWriter.visitMethod(ACC_PUBLIC | ACC_STATIC, "a", "([Ljava/lang/Object;)Lnet/minecraft/class_3298;", null, null);
methodVisitor.visitEnd();
}
{
methodVisitor = classWriter.visitMethod(ACC_PRIVATE | ACC_STATIC, "a", "([B)Ljava/lang/String;", null, null);
methodVisitor.visitEnd();
}
{
methodVisitor = classWriter.visitMethod(ACC_PUBLIC, "close", "()V", null, null);
methodVisitor.visitEnd();
}
{
methodVisitor = classWriter.visitMethod(ACC_PRIVATE | ACC_STATIC, "lambda$getSupplier$2", "(Ljava/lang/String;)Ljava/io/InputStream;", null, new String[] { "java/io/IOException" });
methodVisitor.visitEnd();
}
{
methodVisitor = classWriter.visitMethod(ACC_PRIVATE | ACC_STATIC, "lambda$getResource$1", "(Ljava/lang/String;)Ljava/io/InputStream;", null, new String[] { "java/io/IOException" });
methodVisitor.visitEnd();
}
{
methodVisitor = classWriter.visitMethod(ACC_PUBLIC, "method_14405", "(Lnet/minecraft/class_3264;Lnet/minecraft/class_2960;)Lnet/minecraft/class_7367;", null, null);
{
annotationVisitor0 = methodVisitor.visitAnnotation("Lorg/jetbrains/annotations/Nullable;", false);
annotationVisitor0.visitEnd();
}
{
annotationVisitor0 = methodVisitor.visitTypeAnnotation(335544320, null, "Lorg/jetbrains/annotations/Nullable;", false);
annotationVisitor0.visitEnd();
}
methodVisitor.visitEnd();
}
{
methodVisitor = classWriter.visitMethod(ACC_PUBLIC, "method_14406", "(Lnet/minecraft/class_3264;)Ljava/util/Set;", null, null);
methodVisitor.visitEnd();
}
{
methodVisitor = classWriter.visitMethod(ACC_PUBLIC, "method_14407", "(Lnet/minecraft/class_7677;)Ljava/lang/Object;", null, new String[] { "java/io/IOException" });
{
annotationVisitor0 = methodVisitor.visitAnnotation("Lorg/jetbrains/annotations/Nullable;", false);
annotationVisitor0.visitEnd();
}
{
annotationVisitor0 = methodVisitor.visitTypeAnnotation(335544320, null, "Lorg/jetbrains/annotations/Nullable;", false);
annotationVisitor0.visitEnd();
}
methodVisitor.visitEnd();
}
{
methodVisitor = classWriter.visitMethod(ACC_PUBLIC, "method_56926", "()Lnet/minecraft/class_9224;", null, null);
methodVisitor.visitEnd();
}
{
methodVisitor = classWriter.visitMethod(ACC_PUBLIC, "method_14408", "(Lnet/minecraft/class_3264;Ljava/lang/String;Ljava/lang/String;Lnet/minecraft/class_3262$class_7664;)V", null, null);
methodVisitor.visitEnd();
}
{
methodVisitor = classWriter.visitMethod(ACC_PUBLIC, "method_14409", "()Ljava/lang/String;", null, null);
methodVisitor.visitEnd();
}
{
methodVisitor = classWriter.visitMethod(ACC_PUBLIC, "method_14410", "([Ljava/lang/String;)Lnet/minecraft/class_7367;", null, null);
{
annotationVisitor0 = methodVisitor.visitAnnotation("Lorg/jetbrains/annotations/Nullable;", false);
annotationVisitor0.visitEnd();
}
{
annotationVisitor0 = methodVisitor.visitTypeAnnotation(335544320, null, "Lorg/jetbrains/annotations/Nullable;", false);
annotationVisitor0.visitEnd();
}
methodVisitor.visitEnd();
}
{
methodVisitor = classWriter.visitMethod(ACC_PUBLIC, "method_56929", "()Ljava/util/Optional;", null, null);
methodVisitor.visitEnd();
}
{
methodVisitor = classWriter.visitMethod(ACC_PRIVATE | ACC_STATIC, "lambda$getResource$0", "(Lnet/minecraft/class_2960;)Ljava/io/InputStream;", null, new String[] { "java/io/IOException" });
methodVisitor.visitEnd();
}
classWriter.visitEnd();

return classWriter.toByteArray();
}
}
