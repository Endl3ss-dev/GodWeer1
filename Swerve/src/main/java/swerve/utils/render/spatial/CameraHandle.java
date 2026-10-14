package swerve.utils.render.spatial;

/** Реализуется миксином Camera: доступ к protected setPosition/setRotation без отражения. */
public interface CameraHandle {

    void swerve$applyCamera(double x, double y, double z, float yaw, float pitch);
}
