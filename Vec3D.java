package structures;

public class Vec3D {
	
	public float x,y,z,w=1;
	public Vec3D(float x,float y,float z) {
		
		
		this.x=x;
		this.y=y;
		this.z=z;
	
	}
	
	
	
	public static Vec3D vectorAdd(Vec3D v1, Vec3D v2) {
		return new Vec3D( v1.x + v2.x, v1.y + v2.y, v1.z + v2.z );
	}
	
	public static Vec3D vectorSub(Vec3D v1, Vec3D v2) {
		return new Vec3D( v1.x - v2.x, v1.y - v2.y, v1.z - v2.z );
	}
	
	public static Vec3D vectorMul(Vec3D v1, float k) {
		return new Vec3D( v1.x * k, v1.y * k, v1.z * k );
	}
	
	public static Vec3D vectorDiv(Vec3D v1, float k) {
		return new Vec3D( v1.x / k, v1.y / k, v1.z / k );
	}
	
	public static float vectorDotProduct(Vec3D v1, Vec3D v2) {
		return (v1.x*v2.x + v1.y*v2.y + v1.z * v2.z);
	}
	
	public static float vectorLength(Vec3D v) {
		return (float)(Math.sqrt(vectorDotProduct(v,v)));
	}
	
	public static Vec3D vectorNormalise(Vec3D v) {
		float l = vectorLength(v);
		return new Vec3D(v.x/l, v.y/l, v.z/l);
	}
	
	public static Vec3D vectorCrossProduct(Vec3D v1, Vec3D v2) {
		Vec3D v = new Vec3D(0,0,0);
		v.x = v1.y * v2.z - v1.z * v2.y;
		v.y = v1.z * v2.x - v1.x * v2.z;
		v.z = v1.x * v2.y - v1.y * v2.x;
		return v;
	}
	
	public static Vec3D vectorIntersectPlane(Vec3D plane_p, Vec3D plane_n, Vec3D lineStart, Vec3D lineEnd){
		plane_n = vectorNormalise(plane_n);
		float plane_d = -vectorDotProduct(plane_n, plane_p);
		float ad = vectorDotProduct(lineStart, plane_n);
		float bd = vectorDotProduct(lineEnd, plane_n);
		float t = (-plane_d - ad) / (bd - ad);
		Vec3D lineStartToEnd = vectorSub(lineEnd, lineStart);
		Vec3D lineToIntersect = vectorMul(lineStartToEnd, t);
		return vectorAdd(lineStart, lineToIntersect);
	}
	
	
	public float getX() {
		return x;
	}
	public float getY() {
		return y;
	}
	public float getZ() {
		return z;
	}
	public void setX(float x) {
		this.x = x;
	}
	public void setY(float y) {
		this.y = y;
	}
	public void setZ(float z) {
		this.z = z;
	}
	
	
	
}