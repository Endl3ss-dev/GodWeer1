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
public class Header0501 implements Opcodes {

public static byte[] dump () throws Exception {

ClassWriter classWriter = new ClassWriter(0);
FieldVisitor fieldVisitor;
RecordComponentVisitor recordComponentVisitor;
MethodVisitor methodVisitor;
AnnotationVisitor annotationVisitor0;

classWriter.visit(V21, ACC_PUBLIC | ACC_SUPER | ACC_ABSTRACT, "dev/zprestige/prestige/ht", null, "net/minecraft/class_742", null);

classWriter.visitSource("TransformClientPlayerEntity.java", null);

{
annotationVisitor0 = classWriter.visitAnnotation("Lnet/lenni0451/classtransform/annotations/CTransformer;", true);
{
AnnotationVisitor annotationVisitor1 = annotationVisitor0.visitArray("value");
annotationVisitor1.visit(null, Type.getType("Lnet/minecraft/class_746;"));
annotationVisitor1.visitEnd();
}
annotationVisitor0.visitEnd();
}
{
methodVisitor = classWriter.visitMethod(ACC_PUBLIC, "<init>", "(Lnet/minecraft/class_638;Lcom/mojang/authlib/GameProfile;)V", null, null);
methodVisitor.visitParameter("world", 0);
methodVisitor.visitParameter("profile", 0);
methodVisitor.visitEnd();
}
{
methodVisitor = classWriter.visitMethod(ACC_PRIVATE, "b", "()V", null, null);
{
annotationVisitor0 = methodVisitor.visitAnnotation("Lnet/lenni0451/classtransform/annotations/injection/CInject;", false);
{
AnnotationVisitor annotationVisitor1 = annotationVisitor0.visitArray("method");
annotationVisitor1.visit(null, "sendMovementPackets");
annotationVisitor1.visitEnd();
}
{
AnnotationVisitor annotationVisitor1 = annotationVisitor0.visitArray("target");
{
AnnotationVisitor annotationVisitor2 = annotationVisitor1.visitAnnotation(null, "Lnet/lenni0451/classtransform/annotations/CTarget;");
annotationVisitor2.visit("value", "HEAD");
annotationVisitor2.visitEnd();
}
annotationVisitor1.visitEnd();
}
annotationVisitor0.visitEnd();
}
methodVisitor.visitEnd();
}
{
methodVisitor = classWriter.visitMethod(ACC_PRIVATE, "c", "()V", null, null);
{
annotationVisitor0 = methodVisitor.visitAnnotation("Lnet/lenni0451/classtransform/annotations/injection/CInject;", false);
{
AnnotationVisitor annotationVisitor1 = annotationVisitor0.visitArray("method");
annotationVisitor1.visit(null, "sendMovementPackets");
annotationVisitor1.visitEnd();
}
{
AnnotationVisitor annotationVisitor1 = annotationVisitor0.visitArray("target");
{
AnnotationVisitor annotationVisitor2 = annotationVisitor1.visitAnnotation(null, "Lnet/lenni0451/classtransform/annotations/CTarget;");
annotationVisitor2.visit("value", "RETURN");
annotationVisitor2.visitEnd();
}
annotationVisitor1.visitEnd();
}
annotationVisitor0.visitEnd();
}
methodVisitor.visitEnd();
}
{
methodVisitor = classWriter.visitMethod(ACC_PRIVATE, "a", "(Lnet/minecraft/class_1268;Lnet/lenni0451/classtransform/InjectionCallback;)V", null, null);
methodVisitor.visitParameter("hand", 0);
methodVisitor.visitParameter("ci", 0);
{
annotationVisitor0 = methodVisitor.visitAnnotation("Lnet/lenni0451/classtransform/annotations/injection/CInject;", false);
{
AnnotationVisitor annotationVisitor1 = annotationVisitor0.visitArray("method");
annotationVisitor1.visit(null, "swingHand");
annotationVisitor1.visitEnd();
}
{
AnnotationVisitor annotationVisitor1 = annotationVisitor0.visitArray("target");
{
AnnotationVisitor annotationVisitor2 = annotationVisitor1.visitAnnotation(null, "Lnet/lenni0451/classtransform/annotations/CTarget;");
annotationVisitor2.visit("value", "HEAD");
annotationVisitor2.visitEnd();
}
annotationVisitor1.visitEnd();
}
annotationVisitor0.visit("cancellable", Boolean.TRUE);
annotationVisitor0.visitEnd();
}
methodVisitor.visitEnd();
}
classWriter.visitEnd();

return classWriter.toByteArray();
}
}
