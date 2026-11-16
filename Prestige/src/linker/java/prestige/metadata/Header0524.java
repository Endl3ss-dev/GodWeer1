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
public class Header0524 implements Opcodes {

public static byte[] dump () throws Exception {

ClassWriter classWriter = new ClassWriter(0);
FieldVisitor fieldVisitor;
RecordComponentVisitor recordComponentVisitor;
MethodVisitor methodVisitor;
AnnotationVisitor annotationVisitor0;

classWriter.visit(V21, ACC_PUBLIC | ACC_SUPER, "dev/zprestige/prestige/ix", null, "java/lang/Object", null);

classWriter.visitSource("TransformServerWorld.java", null);

{
annotationVisitor0 = classWriter.visitAnnotation("Lnet/lenni0451/classtransform/annotations/CTransformer;", true);
{
AnnotationVisitor annotationVisitor1 = annotationVisitor0.visitArray("value");
annotationVisitor1.visit(null, Type.getType("Lnet/minecraft/class_3218;"));
annotationVisitor1.visitEnd();
}
annotationVisitor0.visitEnd();
}
classWriter.visitInnerClass("net/minecraft/class_1937$class_7867", "net/minecraft/class_1937", "class_7867", ACC_PUBLIC | ACC_FINAL | ACC_STATIC | ACC_ENUM);

{
methodVisitor = classWriter.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null);
methodVisitor.visitEnd();
}
{
methodVisitor = classWriter.visitMethod(ACC_PUBLIC, "a", "(Lnet/minecraft/class_1297;Lnet/minecraft/class_1282;Lnet/minecraft/class_5362;DDDFZLnet/minecraft/class_1937$class_7867;Lnet/minecraft/class_2394;Lnet/minecraft/class_2394;Lnet/minecraft/class_6880;Lnet/lenni0451/classtransform/InjectionCallback;)V", "(Lnet/minecraft/class_1297;Lnet/minecraft/class_1282;Lnet/minecraft/class_5362;DDDFZLnet/minecraft/class_1937$class_7867;Lnet/minecraft/class_2394;Lnet/minecraft/class_2394;Lnet/minecraft/class_6880<Lnet/minecraft/class_3414;>;Lnet/lenni0451/classtransform/InjectionCallback;)V", null);
methodVisitor.visitParameter("entity", 0);
methodVisitor.visitParameter("damageSource", 0);
methodVisitor.visitParameter("behavior", 0);
methodVisitor.visitParameter("x", 0);
methodVisitor.visitParameter("y", 0);
methodVisitor.visitParameter("z", 0);
methodVisitor.visitParameter("power", 0);
methodVisitor.visitParameter("createFire", 0);
methodVisitor.visitParameter("explosionSourceType", 0);
methodVisitor.visitParameter("smallParticle", 0);
methodVisitor.visitParameter("largeParticle", 0);
methodVisitor.visitParameter("soundEvent", 0);
methodVisitor.visitParameter("ci", 0);
{
annotationVisitor0 = methodVisitor.visitAnnotation("Lnet/lenni0451/classtransform/annotations/injection/CInject;", false);
{
AnnotationVisitor annotationVisitor1 = annotationVisitor0.visitArray("method");
annotationVisitor1.visit(null, "createExplosion");
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
classWriter.visitEnd();

return classWriter.toByteArray();
}
}
