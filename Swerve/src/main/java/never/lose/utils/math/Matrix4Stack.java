package never.lose.utils.math;

import org.joml.Matrix4f;

import java.util.ArrayDeque;
import java.util.Deque;

public class Matrix4Stack {
    private final Deque<Matrix4f> matrices   = new ArrayDeque<>();
    private final Matrix4f        empty      = new Matrix4f().identity();
    private       Matrix4f        current;

    public Matrix4Stack() {
        current = new Matrix4f().identity();
        matrices.addLast(current);
    }

    public Matrix4f pushMatrix() {
        if (matrices.size() > 128) throw new IllegalStateException("Matrix stack overflow");
        Matrix4f copy = new Matrix4f(current);
        matrices.addLast(copy);
        current = copy;
        return copy;
    }

    public Matrix4f popMatrix() {
        if (matrices.isEmpty()) throw new IllegalStateException("Matrix stack underflow");
        matrices.removeLast();
        current = matrices.isEmpty() ? empty : matrices.getLast();
        return current;
    }

    public Matrix4f matrix() {
        return current;
    }

    public void clear() {
        matrices.clear();
        current = new Matrix4f().identity();
        matrices.addLast(current);
    }
}