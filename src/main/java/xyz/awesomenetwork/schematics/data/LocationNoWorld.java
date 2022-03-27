package xyz.awesomenetwork.schematics.data;

public class LocationNoWorld {

    private final int x, y, z;
    private final float pitch, yaw;

    public LocationNoWorld(int x, int y, int z) {
        this(x, y, z, 0f, 0f);
    }

    public LocationNoWorld(int x, int y, int z, float pitch, float yaw) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.pitch = pitch;
        this.yaw = yaw;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getZ() {
        return z;
    }

    public float getPitch() {
        return pitch;
    }

    public float getYaw() {
        return yaw;
    }
}
