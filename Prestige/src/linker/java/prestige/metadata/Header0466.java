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
public class Header0466 implements Opcodes {

public static byte[] dump () throws Exception {

ClassWriter classWriter = new ClassWriter(0);
FieldVisitor fieldVisitor;
RecordComponentVisitor recordComponentVisitor;
MethodVisitor methodVisitor;
AnnotationVisitor annotationVisitor0;

classWriter.visit(V21, ACC_FINAL | ACC_SUPER | ACC_RECORD, "dev/zprestige/prestige/gq", null, "java/lang/Record", null);

classWriter.visitSource("gq.java", null);

classWriter.visitNestHost("dev/zprestige/prestige/en");

{
recordComponentVisitor = classWriter.visitRecordComponent("al", "Lnet/minecraft/class_2338;", null);
recordComponentVisitor.visitEnd();
}
{
recordComponentVisitor = classWriter.visitRecordComponent("am", "Lnet/minecraft/class_3965;", null);
recordComponentVisitor.visitEnd();
}
{
recordComponentVisitor = classWriter.visitRecordComponent("an", "D", null);
recordComponentVisitor.visitEnd();
}
{
fieldVisitor = classWriter.visitField(ACC_PRIVATE, "al", "Lnet/minecraft/class_2338;", null, null);
fieldVisitor.visitEnd();
}
{
fieldVisitor = classWriter.visitField(ACC_PRIVATE, "am", "Lnet/minecraft/class_3965;", null, null);
fieldVisitor.visitEnd();
}
{
fieldVisitor = classWriter.visitField(ACC_PRIVATE, "an", "D", null, null);
fieldVisitor.visitEnd();
}
{
methodVisitor = classWriter.visitMethod(ACC_PRIVATE, "<init>", "(Lnet/minecraft/class_2338;Lnet/minecraft/class_3965;D)V", null, null);
methodVisitor.visitEnd();
}
{
methodVisitor = classWriter.visitMethod(ACC_PUBLIC | ACC_FINAL, "equals", "(Ljava/lang/Object;)Z", null, null);
methodVisitor.visitEnd();
}
{
methodVisitor = classWriter.visitMethod(ACC_PUBLIC | ACC_FINAL, "toString", "()Ljava/lang/String;", null, null);
methodVisitor.visitEnd();
}
{
methodVisitor = classWriter.visitMethod(ACC_PUBLIC | ACC_FINAL, "hashCode", "()I", null, null);
methodVisitor.visitEnd();
}
{
methodVisitor = classWriter.visitMethod(ACC_PUBLIC, "am", "()Lnet/minecraft/class_3965;", null, null);
methodVisitor.visitEnd();
}
{
methodVisitor = classWriter.visitMethod(ACC_PUBLIC, "al", "()Lnet/minecraft/class_2338;", null, null);
methodVisitor.visitEnd();
}
{
methodVisitor = classWriter.visitMethod(ACC_PUBLIC, "an", "()D", null, null);
methodVisitor.visitEnd();
}
classWriter.visitEnd();

return classWriter.toByteArray();
}
}
