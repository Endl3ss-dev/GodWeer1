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
public class Header0258 implements Opcodes {

public static byte[] dump () throws Exception {

ClassWriter classWriter = new ClassWriter(0);
FieldVisitor fieldVisitor;
RecordComponentVisitor recordComponentVisitor;
MethodVisitor methodVisitor;
AnnotationVisitor annotationVisitor0;

classWriter.visit(V21, ACC_PUBLIC | ACC_SUPER, "dev/zprestige/prestige/d_", null, "dev/zprestige/prestige/dV", new String[] { "dev/zprestige/prestige/dF" });

classWriter.visitSource("d_.java", null);

classWriter.visitNestMember("dev/zprestige/prestige/d");

classWriter.visitNestMember("dev/zprestige/prestige/e");

{
fieldVisitor = classWriter.visitField(ACC_PRIVATE, "a", "Ldev/zprestige/prestige/dP;", null, null);
fieldVisitor.visitEnd();
}
{
fieldVisitor = classWriter.visitField(ACC_PRIVATE, "c", "Ldev/zprestige/prestige/dP;", null, null);
fieldVisitor.visitEnd();
}
{
fieldVisitor = classWriter.visitField(ACC_PRIVATE, "d", "Ldev/zprestige/prestige/dP;", null, null);
fieldVisitor.visitEnd();
}
{
fieldVisitor = classWriter.visitField(ACC_PRIVATE, "e", "Ldev/zprestige/prestige/dP;", null, null);
fieldVisitor.visitEnd();
}
{
fieldVisitor = classWriter.visitField(ACC_PRIVATE, "f", "Ldev/zprestige/prestige/dL;", null, null);
fieldVisitor.visitEnd();
}
{
fieldVisitor = classWriter.visitField(ACC_PRIVATE, "g", "Ldev/zprestige/prestige/dP;", null, null);
fieldVisitor.visitEnd();
}
{
fieldVisitor = classWriter.visitField(ACC_PRIVATE, "h", "Ldev/zprestige/prestige/dL;", null, null);
fieldVisitor.visitEnd();
}
{
fieldVisitor = classWriter.visitField(ACC_PRIVATE, "i", "Ldev/zprestige/prestige/dQ;", null, null);
fieldVisitor.visitEnd();
}
{
fieldVisitor = classWriter.visitField(ACC_PRIVATE, "j", "Ldev/zprestige/prestige/dM;", null, null);
fieldVisitor.visitEnd();
}
{
fieldVisitor = classWriter.visitField(ACC_PRIVATE, "k", "Ldev/zprestige/prestige/dR;", null, null);
fieldVisitor.visitEnd();
}
{
fieldVisitor = classWriter.visitField(ACC_PRIVATE, "l", "Ldev/zprestige/prestige/dM;", null, null);
fieldVisitor.visitEnd();
}
{
fieldVisitor = classWriter.visitField(ACC_PRIVATE, "m", "Ldev/zprestige/prestige/dM;", null, null);
fieldVisitor.visitEnd();
}
{
fieldVisitor = classWriter.visitField(ACC_PRIVATE, "n", "Ldev/zprestige/prestige/dO;", null, null);
fieldVisitor.visitEnd();
}
{
fieldVisitor = classWriter.visitField(ACC_PRIVATE, "o", "Ldev/zprestige/prestige/dP;", null, null);
fieldVisitor.visitEnd();
}
{
fieldVisitor = classWriter.visitField(ACC_PRIVATE, "p", "Ldev/zprestige/prestige/dM;", null, null);
fieldVisitor.visitEnd();
}
{
fieldVisitor = classWriter.visitField(ACC_PRIVATE, "q", "Ldev/zprestige/prestige/dM;", null, null);
fieldVisitor.visitEnd();
}
{
fieldVisitor = classWriter.visitField(ACC_PRIVATE, "r", "Ldev/zprestige/prestige/dM;", null, null);
fieldVisitor.visitEnd();
}
{
fieldVisitor = classWriter.visitField(ACC_PRIVATE, "s", "Ldev/zprestige/prestige/dO;", null, null);
fieldVisitor.visitEnd();
}
{
fieldVisitor = classWriter.visitField(ACC_PRIVATE, "t", "Ldev/zprestige/prestige/dM;", null, null);
fieldVisitor.visitEnd();
}
{
fieldVisitor = classWriter.visitField(ACC_PRIVATE, "u", "Ldev/zprestige/prestige/dN;", null, null);
fieldVisitor.visitEnd();
}
{
fieldVisitor = classWriter.visitField(ACC_PRIVATE | ACC_FINAL | ACC_STATIC, "v", "[Lnet/minecraft/class_1792;", null, null);
fieldVisitor.visitEnd();
}
{
fieldVisitor = classWriter.visitField(ACC_PRIVATE | ACC_FINAL | ACC_STATIC, "w", "[Lnet/minecraft/class_2248;", null, null);
fieldVisitor.visitEnd();
}
{
fieldVisitor = classWriter.visitField(ACC_PRIVATE | ACC_FINAL | ACC_STATIC, "x", "Ljava/util/Set;", null, null);
fieldVisitor.visitEnd();
}
{
fieldVisitor = classWriter.visitField(ACC_PRIVATE, "y", "Ldev/zprestige/prestige/e;", null, null);
fieldVisitor.visitEnd();
}
{
fieldVisitor = classWriter.visitField(ACC_PRIVATE, "z", "Ldev/zprestige/prestige/d;", null, null);
fieldVisitor.visitEnd();
}
{
fieldVisitor = classWriter.visitField(ACC_PRIVATE, "A", "I", null, null);
fieldVisitor.visitEnd();
}
{
fieldVisitor = classWriter.visitField(ACC_PRIVATE, "B", "I", null, null);
fieldVisitor.visitEnd();
}
{
fieldVisitor = classWriter.visitField(ACC_PRIVATE, "C", "I", null, null);
fieldVisitor.visitEnd();
}
{
fieldVisitor = classWriter.visitField(ACC_PRIVATE, "D", "Lnet/minecraft/class_2338;", null, null);
fieldVisitor.visitEnd();
}
{
fieldVisitor = classWriter.visitField(ACC_PRIVATE, "E", "Z", null, null);
fieldVisitor.visitEnd();
}
{
fieldVisitor = classWriter.visitField(ACC_PRIVATE, "F", "I", null, null);
fieldVisitor.visitEnd();
}
{
fieldVisitor = classWriter.visitField(ACC_PRIVATE, "G", "I", null, null);
fieldVisitor.visitEnd();
}
{
fieldVisitor = classWriter.visitField(ACC_PRIVATE, "H", "I", null, null);
fieldVisitor.visitEnd();
}
{
fieldVisitor = classWriter.visitField(ACC_PRIVATE, "I", "Z", null, null);
fieldVisitor.visitEnd();
}
{
fieldVisitor = classWriter.visitField(ACC_PRIVATE, "J", "Z", null, null);
fieldVisitor.visitEnd();
}
{
fieldVisitor = classWriter.visitField(ACC_PRIVATE, "K", "Z", null, null);
fieldVisitor.visitEnd();
}
{
fieldVisitor = classWriter.visitField(ACC_PRIVATE, "L", "Z", null, null);
fieldVisitor.visitEnd();
}
{
fieldVisitor = classWriter.visitField(ACC_PRIVATE, "M", "I", null, null);
fieldVisitor.visitEnd();
}
{
fieldVisitor = classWriter.visitField(ACC_PRIVATE, "N", "Z", null, null);
fieldVisitor.visitEnd();
}
{
fieldVisitor = classWriter.visitField(ACC_PRIVATE, "O", "Ldev/zprestige/prestige/f5;", null, null);
fieldVisitor.visitEnd();
}
{
fieldVisitor = classWriter.visitField(ACC_PRIVATE, "P", "I", null, null);
fieldVisitor.visitEnd();
}
{
fieldVisitor = classWriter.visitField(ACC_PRIVATE, "Q", "Z", null, null);
fieldVisitor.visitEnd();
}
{
fieldVisitor = classWriter.visitField(ACC_PRIVATE, "R", "Lnet/minecraft/class_2338;", null, null);
fieldVisitor.visitEnd();
}
{
fieldVisitor = classWriter.visitField(ACC_PRIVATE, "S", "Z", null, null);
fieldVisitor.visitEnd();
}
{
fieldVisitor = classWriter.visitField(ACC_PRIVATE, "T", "Lnet/minecraft/class_1701;", null, null);
fieldVisitor.visitEnd();
}
{
fieldVisitor = classWriter.visitField(ACC_PRIVATE, "U", "Z", null, null);
fieldVisitor.visitEnd();
}
{
fieldVisitor = classWriter.visitField(ACC_PRIVATE, "V", "Ldev/zprestige/prestige/dC;", null, null);
fieldVisitor.visitEnd();
}
{
fieldVisitor = classWriter.visitField(ACC_PRIVATE, "W", "Z", null, null);
fieldVisitor.visitEnd();
}
{
fieldVisitor = classWriter.visitField(ACC_PRIVATE, "X", "I", null, null);
fieldVisitor.visitEnd();
}
{
fieldVisitor = classWriter.visitField(ACC_PRIVATE | ACC_FINAL | ACC_STATIC, "Y", "J", null, null);
fieldVisitor.visitEnd();
}
{
fieldVisitor = classWriter.visitField(ACC_PRIVATE | ACC_FINAL | ACC_STATIC, "Z", "[Ljava/lang/String;", null, null);
fieldVisitor.visitEnd();
}
{
fieldVisitor = classWriter.visitField(ACC_PRIVATE | ACC_FINAL | ACC_STATIC, "ab", "[Ljava/lang/String;", null, null);
fieldVisitor.visitEnd();
}
{
fieldVisitor = classWriter.visitField(ACC_PRIVATE | ACC_FINAL | ACC_STATIC, "bb", "Ljava/util/Map;", null, null);
fieldVisitor.visitEnd();
}
{
fieldVisitor = classWriter.visitField(ACC_PRIVATE | ACC_FINAL | ACC_STATIC, "hb", "[J", null, null);
fieldVisitor.visitEnd();
}
{
fieldVisitor = classWriter.visitField(ACC_PRIVATE | ACC_FINAL | ACC_STATIC, "ib", "[Ljava/lang/Integer;", null, null);
fieldVisitor.visitEnd();
}
{
fieldVisitor = classWriter.visitField(ACC_PRIVATE | ACC_FINAL | ACC_STATIC, "jb", "Ljava/util/Map;", null, null);
fieldVisitor.visitEnd();
}
{
fieldVisitor = classWriter.visitField(ACC_PRIVATE | ACC_FINAL | ACC_STATIC, "kb", "[Ljava/lang/Object;", null, null);
fieldVisitor.visitEnd();
}
{
fieldVisitor = classWriter.visitField(ACC_PRIVATE | ACC_FINAL | ACC_STATIC, "lb", "[Ljava/lang/String;", null, null);
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
methodVisitor = classWriter.visitMethod(ACC_PRIVATE, "e", "([Ljava/lang/Object;)I", null, null);
methodVisitor.visitEnd();
}
{
methodVisitor = classWriter.visitMethod(ACC_PRIVATE, "e", "([Ljava/lang/Object;)Z", null, null);
methodVisitor.visitEnd();
}
{
methodVisitor = classWriter.visitMethod(ACC_PRIVATE, "i", "([Ljava/lang/Object;)Z", null, null);
methodVisitor.visitEnd();
}
{
methodVisitor = classWriter.visitMethod(ACC_PRIVATE | ACC_STATIC, "b", "(Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/String;Ljava/lang/invoke/MethodType;)Ljava/lang/invoke/CallSite;", null, null);
methodVisitor.visitEnd();
}
{
methodVisitor = classWriter.visitMethod(ACC_PRIVATE, "b", "([Ljava/lang/Object;)F", null, null);
methodVisitor.visitEnd();
}
{
methodVisitor = classWriter.visitMethod(ACC_PRIVATE | ACC_STATIC, "b", "(Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/invoke/MutableCallSite;Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/Object;", null, null);
methodVisitor.visitEnd();
}
{
methodVisitor = classWriter.visitMethod(ACC_PRIVATE | ACC_STATIC, "b", "(IJ)Ljava/lang/String;", null, null);
methodVisitor.visitEnd();
}
{
methodVisitor = classWriter.visitMethod(ACC_PRIVATE, "b", "([Ljava/lang/Object;)Ldev/zprestige/prestige/dC;", null, null);
methodVisitor.visitEnd();
}
{
methodVisitor = classWriter.visitMethod(ACC_PRIVATE | ACC_STATIC, "b", "([B)Ljava/lang/String;", null, null);
methodVisitor.visitEnd();
}
{
methodVisitor = classWriter.visitMethod(ACC_PRIVATE | ACC_STATIC, "c", "(IJ)I", null, null);
methodVisitor.visitEnd();
}
{
methodVisitor = classWriter.visitMethod(ACC_PRIVATE | ACC_STATIC, "c", "(Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/String;Ljava/lang/invoke/MethodType;)Ljava/lang/invoke/CallSite;", null, null);
methodVisitor.visitEnd();
}
{
methodVisitor = classWriter.visitMethod(ACC_PRIVATE | ACC_STATIC, "c", "(Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/invoke/MutableCallSite;Ljava/lang/String;[Ljava/lang/Object;)I", null, null);
methodVisitor.visitEnd();
}
{
methodVisitor = classWriter.visitMethod(ACC_PRIVATE | ACC_STATIC, "n", "(JJ)Ljava/lang/Class;", null, null);
methodVisitor.visitEnd();
}
{
methodVisitor = classWriter.visitMethod(ACC_PRIVATE, "n", "([Ljava/lang/Object;)V", null, null);
methodVisitor.visitEnd();
}
{
methodVisitor = classWriter.visitMethod(ACC_PRIVATE | ACC_STATIC, "h", "(Ljava/lang/Class;Ljava/lang/String;Ljava/lang/Class;)Ljava/lang/reflect/Field;", null, null);
methodVisitor.visitEnd();
}
{
methodVisitor = classWriter.visitMethod(ACC_PRIVATE, "h", "([Ljava/lang/Object;)Z", null, null);
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
methodVisitor = classWriter.visitMethod(ACC_PRIVATE, "f", "([Ljava/lang/Object;)I", null, null);
methodVisitor.visitEnd();
}
{
methodVisitor = classWriter.visitMethod(ACC_PRIVATE, "f", "([Ljava/lang/Object;)Z", null, null);
methodVisitor.visitEnd();
}
{
methodVisitor = classWriter.visitMethod(ACC_PRIVATE, "l", "([Ljava/lang/Object;)V", null, null);
methodVisitor.visitEnd();
}
{
methodVisitor = classWriter.visitMethod(ACC_PRIVATE | ACC_STATIC, "d", "(Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/invoke/MutableCallSite;Ljava/lang/String;Ljava/lang/invoke/MethodType;[Ljava/lang/Object;)Ljava/lang/Object;", null, null);
methodVisitor.visitEnd();
}
{
methodVisitor = classWriter.visitMethod(ACC_PRIVATE | ACC_STATIC, "d", "(Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/String;Ljava/lang/invoke/MethodType;)Ljava/lang/invoke/CallSite;", null, null);
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
methodVisitor = classWriter.visitMethod(ACC_PRIVATE, "d", "([Ljava/lang/Object;)Z", null, null);
methodVisitor.visitEnd();
}
{
methodVisitor = classWriter.visitMethod(ACC_PRIVATE, "d", "([Ljava/lang/Object;)I", null, null);
methodVisitor.visitEnd();
}
{
methodVisitor = classWriter.visitMethod(ACC_PRIVATE | ACC_STATIC, "a", "(Ljava/lang/MatchException;)Ljava/lang/MatchException;", null, null);
methodVisitor.visitEnd();
}
{
methodVisitor = classWriter.visitMethod(ACC_PRIVATE, "a", "([Ljava/lang/Object;)Lnet/minecraft/class_2338;", null, null);
methodVisitor.visitEnd();
}
{
methodVisitor = classWriter.visitMethod(ACC_PUBLIC, "a", "(Ldev/zprestige/prestige/bt;)V", null, null);
{
annotationVisitor0 = methodVisitor.visitAnnotation("Ldev/zprestige/prestige/bP;", true);
annotationVisitor0.visitEnd();
}
methodVisitor.visitEnd();
}
{
methodVisitor = classWriter.visitMethod(ACC_PRIVATE, "a", "([Ljava/lang/Object;)Ljava/lang/Integer;", null, null);
methodVisitor.visitEnd();
}
{
methodVisitor = classWriter.visitMethod(ACC_PRIVATE, "a", "([Ljava/lang/Object;)Z", null, null);
methodVisitor.visitEnd();
}
{
methodVisitor = classWriter.visitMethod(ACC_PUBLIC, "a", "([Ljava/lang/Object;)Ljava/util/Set;", null, null);
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
methodVisitor = classWriter.visitMethod(ACC_PUBLIC, "a", "([Ljava/lang/Object;)Ldev/zprestige/prestige/dC;", null, null);
methodVisitor.visitEnd();
}
{
methodVisitor = classWriter.visitMethod(ACC_PUBLIC, "a", "(Ldev/zprestige/prestige/bh;)V", null, null);
{
annotationVisitor0 = methodVisitor.visitAnnotation("Ldev/zprestige/prestige/bP;", true);
annotationVisitor0.visitEnd();
}
methodVisitor.visitEnd();
}
{
methodVisitor = classWriter.visitMethod(ACC_PRIVATE, "a", "([Ljava/lang/Object;)Lnet/minecraft/class_1701;", null, null);
methodVisitor.visitEnd();
}
{
methodVisitor = classWriter.visitMethod(ACC_PRIVATE, "a", "([Ljava/lang/Object;)Lnet/minecraft/class_3965;", null, null);
methodVisitor.visitEnd();
}
{
methodVisitor = classWriter.visitMethod(ACC_PRIVATE, "m", "([Ljava/lang/Object;)V", null, null);
methodVisitor.visitEnd();
}
{
methodVisitor = classWriter.visitMethod(ACC_PRIVATE | ACC_STATIC, "m", "(JJ)I", null, null);
methodVisitor.visitEnd();
}
{
methodVisitor = classWriter.visitMethod(ACC_PRIVATE, "o", "([Ljava/lang/Object;)V", null, null);
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
methodVisitor = classWriter.visitMethod(ACC_PRIVATE, "k", "([Ljava/lang/Object;)V", null, null);
methodVisitor.visitEnd();
}
{
methodVisitor = classWriter.visitMethod(ACC_PRIVATE, "k", "([Ljava/lang/Object;)Z", null, null);
methodVisitor.visitEnd();
}
{
methodVisitor = classWriter.visitMethod(ACC_PRIVATE | ACC_STATIC, "g", "(Ljava/lang/Class;Ljava/lang/String;Ljava/lang/Class;I[Ljava/lang/Class;)Ljava/lang/reflect/Method;", null, null);
methodVisitor.visitEnd();
}
{
methodVisitor = classWriter.visitMethod(ACC_PRIVATE | ACC_STATIC, "g", "(Ljava/lang/Class;Ljava/lang/String;Ljava/lang/Class;)Ljava/lang/reflect/Field;", null, null);
methodVisitor.visitEnd();
}
{
methodVisitor = classWriter.visitMethod(ACC_PRIVATE, "g", "([Ljava/lang/Object;)I", null, null);
methodVisitor.visitEnd();
}
{
methodVisitor = classWriter.visitMethod(ACC_PRIVATE, "g", "([Ljava/lang/Object;)Z", null, null);
methodVisitor.visitEnd();
}
{
methodVisitor = classWriter.visitMethod(ACC_PRIVATE, "j", "([Ljava/lang/Object;)V", null, null);
methodVisitor.visitEnd();
}
{
methodVisitor = classWriter.visitMethod(ACC_PRIVATE, "j", "([Ljava/lang/Object;)Z", null, null);
methodVisitor.visitEnd();
}
{
methodVisitor = classWriter.visitMethod(ACC_PRIVATE, "lambda$new$0", "(Ljava/lang/Integer;)Z", null, null);
methodVisitor.visitEnd();
}
{
methodVisitor = classWriter.visitMethod(ACC_PRIVATE, "lambda$new$2", "(Ljava/lang/Boolean;)Z", null, null);
methodVisitor.visitEnd();
}
{
methodVisitor = classWriter.visitMethod(ACC_PRIVATE, "lambda$new$1", "(Ljava/lang/Float;)Z", null, null);
methodVisitor.visitEnd();
}
{
methodVisitor = classWriter.visitMethod(ACC_PRIVATE, "lambda$new$3", "(Ljava/lang/Float;)Z", null, null);
methodVisitor.visitEnd();
}
{
methodVisitor = classWriter.visitMethod(ACC_PRIVATE, "lambda$new$4", "(Ljava/lang/Integer;)Z", null, null);
methodVisitor.visitEnd();
}
{
methodVisitor = classWriter.visitMethod(ACC_PRIVATE, "lambda$new$6", "(Ljava/lang/Float;)Z", null, null);
methodVisitor.visitEnd();
}
{
methodVisitor = classWriter.visitMethod(ACC_PRIVATE, "lambda$new$7", "(Ljava/lang/Boolean;)Z", null, null);
methodVisitor.visitEnd();
}
{
methodVisitor = classWriter.visitMethod(ACC_PRIVATE, "lambda$new$8", "(Ljava/awt/Color;)Z", null, null);
methodVisitor.visitEnd();
}
{
methodVisitor = classWriter.visitMethod(ACC_PRIVATE, "lambda$new$5", "(Ljava/lang/Boolean;)Z", null, null);
methodVisitor.visitEnd();
}
{
methodVisitor = classWriter.visitMethod(ACC_PRIVATE | ACC_STATIC, "lambda$solveShotRotation$9", "(D[F)Z", null, null);
methodVisitor.visitEnd();
}
{
methodVisitor = classWriter.visitMethod(ACC_PRIVATE, "lambda$onTick$10", "(Lnet/minecraft/class_3965;)V", null, null);
methodVisitor.visitEnd();
}
classWriter.visitEnd();

return classWriter.toByteArray();
}
}
