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
public class Header0405 implements Opcodes {

public static byte[] dump () throws Exception {

ClassWriter classWriter = new ClassWriter(0);
FieldVisitor fieldVisitor;
RecordComponentVisitor recordComponentVisitor;
MethodVisitor methodVisitor;
AnnotationVisitor annotationVisitor0;

classWriter.visit(V21, ACC_PUBLIC | ACC_SUPER, "dev/zprestige/prestige/ft", null, "dev/zprestige/prestige/dV", null);

classWriter.visitSource("ft.java", null);

{
fieldVisitor = classWriter.visitField(ACC_PRIVATE, "a", "Ldev/zprestige/prestige/dO;", null, null);
fieldVisitor.visitEnd();
}
{
fieldVisitor = classWriter.visitField(ACC_PRIVATE, "c", "Ldev/zprestige/prestige/dO;", null, null);
fieldVisitor.visitEnd();
}
{
fieldVisitor = classWriter.visitField(ACC_PRIVATE, "d", "Ldev/zprestige/prestige/dO;", null, null);
fieldVisitor.visitEnd();
}
{
fieldVisitor = classWriter.visitField(ACC_PRIVATE, "e", "Ldev/zprestige/prestige/dM;", null, null);
fieldVisitor.visitEnd();
}
{
fieldVisitor = classWriter.visitField(ACC_PRIVATE, "f", "Ldev/zprestige/prestige/dM;", null, null);
fieldVisitor.visitEnd();
}
{
fieldVisitor = classWriter.visitField(ACC_PRIVATE, "g", "Ldev/zprestige/prestige/dM;", null, null);
fieldVisitor.visitEnd();
}
{
fieldVisitor = classWriter.visitField(ACC_PRIVATE, "h", "Ldev/zprestige/prestige/dP;", null, null);
fieldVisitor.visitEnd();
}
{
fieldVisitor = classWriter.visitField(ACC_PRIVATE, "i", "Ldev/zprestige/prestige/dM;", null, null);
fieldVisitor.visitEnd();
}
{
fieldVisitor = classWriter.visitField(ACC_PRIVATE, "j", "Ldev/zprestige/prestige/dM;", null, null);
fieldVisitor.visitEnd();
}
{
fieldVisitor = classWriter.visitField(ACC_PRIVATE, "k", "I", null, null);
fieldVisitor.visitEnd();
}
{
fieldVisitor = classWriter.visitField(ACC_PRIVATE | ACC_STATIC, "l", "Z", null, null);
fieldVisitor.visitEnd();
}
{
fieldVisitor = classWriter.visitField(ACC_PRIVATE | ACC_FINAL | ACC_STATIC, "m", "J", null, null);
fieldVisitor.visitEnd();
}
{
fieldVisitor = classWriter.visitField(ACC_PRIVATE | ACC_FINAL | ACC_STATIC, "n", "[Ljava/lang/Object;", null, null);
fieldVisitor.visitEnd();
}
{
fieldVisitor = classWriter.visitField(ACC_PRIVATE | ACC_FINAL | ACC_STATIC, "o", "[Ljava/lang/String;", null, null);
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
methodVisitor = classWriter.visitMethod(ACC_PUBLIC, "e", "([Ljava/lang/Object;)V", null, null);
methodVisitor.visitEnd();
}
{
methodVisitor = classWriter.visitMethod(ACC_PRIVATE, "b", "([Ljava/lang/Object;)Lnet/minecraft/class_1657;", null, null);
methodVisitor.visitEnd();
}
{
methodVisitor = classWriter.visitMethod(ACC_PRIVATE | ACC_STATIC, "b", "(Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/String;Ljava/lang/invoke/MethodType;)Ljava/lang/invoke/CallSite;", null, null);
methodVisitor.visitEnd();
}
{
methodVisitor = classWriter.visitMethod(ACC_PRIVATE | ACC_STATIC, "n", "(JJ)Ljava/lang/Class;", null, null);
methodVisitor.visitEnd();
}
{
methodVisitor = classWriter.visitMethod(ACC_PRIVATE | ACC_STATIC, "h", "(Ljava/lang/Class;Ljava/lang/String;Ljava/lang/Class;)Ljava/lang/reflect/Field;", null, null);
methodVisitor.visitEnd();
}
{
methodVisitor = classWriter.visitMethod(ACC_PRIVATE | ACC_STATIC, "h", "(Ljava/lang/Class;Ljava/lang/String;Ljava/lang/Class;I[Ljava/lang/Class;)Ljava/lang/reflect/Method;", null, null);
methodVisitor.visitEnd();
}
{
methodVisitor = classWriter.visitMethod(ACC_PRIVATE | ACC_STATIC, "f", "()V", null, null);
methodVisitor.visitEnd();
}
{
methodVisitor = classWriter.visitMethod(ACC_PRIVATE | ACC_STATIC, "d", "(Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/invoke/MutableCallSite;Ljava/lang/String;Ljava/lang/invoke/MethodType;[Ljava/lang/Object;)Ljava/lang/Object;", null, null);
methodVisitor.visitEnd();
}
{
methodVisitor = classWriter.visitMethod(ACC_PUBLIC | ACC_STATIC, "d", "([Ljava/lang/Object;)Z", null, null);
methodVisitor.visitEnd();
}
{
methodVisitor = classWriter.visitMethod(ACC_PRIVATE | ACC_STATIC, "d", "(Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/invoke/MutableCallSite;Ljava/lang/String;Ljava/lang/invoke/MethodType;JJ)Ljava/lang/invoke/MethodHandle;", null, null);
methodVisitor.visitEnd();
}
{
methodVisitor = classWriter.visitMethod(ACC_PUBLIC, "d", "([Ljava/lang/Object;)V", null, null);
methodVisitor.visitEnd();
}
{
methodVisitor = classWriter.visitMethod(ACC_PRIVATE | ACC_STATIC, "a", "(Ljava/lang/MatchException;)Ljava/lang/MatchException;", null, null);
methodVisitor.visitEnd();
}
{
methodVisitor = classWriter.visitMethod(ACC_PUBLIC, "a", "(Ldev/zprestige/prestige/bG;)V", null, null);
{
annotationVisitor0 = methodVisitor.visitAnnotation("Ldev/zprestige/prestige/bP;", true);
annotationVisitor0.visitEnd();
}
methodVisitor.visitEnd();
}
{
methodVisitor = classWriter.visitMethod(ACC_PRIVATE, "a", "([Ljava/lang/Object;)Lnet/minecraft/class_1657;", null, null);
methodVisitor.visitEnd();
}
{
methodVisitor = classWriter.visitMethod(ACC_PUBLIC, "a", "([Ljava/lang/Object;)Ljava/util/Set;", null, null);
methodVisitor.visitEnd();
}
{
methodVisitor = classWriter.visitMethod(ACC_PRIVATE | ACC_STATIC, "m", "(JJ)I", null, null);
methodVisitor.visitEnd();
}
{
methodVisitor = classWriter.visitMethod(ACC_PRIVATE | ACC_STATIC, "o", "(JJ)Ljava/lang/reflect/Field;", null, null);
methodVisitor.visitEnd();
}
{
methodVisitor = classWriter.visitMethod(ACC_PRIVATE | ACC_STATIC, "p", "(JJ)Ljava/lang/reflect/Method;", null, null);
methodVisitor.visitEnd();
}
{
methodVisitor = classWriter.visitMethod(ACC_PRIVATE | ACC_STATIC, "g", "(Ljava/lang/Class;Ljava/lang/String;Ljava/lang/Class;)Ljava/lang/reflect/Field;", null, null);
methodVisitor.visitEnd();
}
{
methodVisitor = classWriter.visitMethod(ACC_PRIVATE | ACC_STATIC, "g", "(Ljava/lang/Class;Ljava/lang/String;Ljava/lang/Class;I[Ljava/lang/Class;)Ljava/lang/reflect/Method;", null, null);
methodVisitor.visitEnd();
}
{
methodVisitor = classWriter.visitMethod(ACC_PRIVATE, "j", "([Ljava/lang/Object;)V", null, null);
methodVisitor.visitEnd();
}
{
methodVisitor = classWriter.visitMethod(ACC_PRIVATE, "lambda$new$0", "(Ljava/lang/Boolean;)Z", null, null);
methodVisitor.visitEnd();
}
{
methodVisitor = classWriter.visitMethod(ACC_PRIVATE, "lambda$new$1", "(Ljava/lang/Boolean;)Z", null, null);
methodVisitor.visitEnd();
}
classWriter.visitEnd();

return classWriter.toByteArray();
}
}
