package structures;

public class Mat4x4 {

	public float[][] m = {
			{0,0,0,0},
			{0,0,0,0},
			{0,0,0,0},
			{0,0,0,0}
			};
	
	
	public Mat4x4() {

	}
	
	
	
	public static Mat4x4 matrixMakeIdentity() {
		Mat4x4 matrix= new Mat4x4();
		matrix.m[0][0] = 1.0f;
		matrix.m[1][1] = 1.0f;
		matrix.m[2][2] = 1.0f;
		matrix.m[3][3] = 1.0f;
		return matrix;
	}
	
	public static Mat4x4 matrixMakeRotationX(float fAngle) {
		//in radians
		Mat4x4 matrix= new Mat4x4();
		matrix.m[0][0] = 1;
		matrix.m[1][1] = (float) Math.cos(fAngle);
		matrix.m[1][2] = (float) Math.sin(fAngle);
		matrix.m[2][1] = (float) -Math.sin(fAngle);
		matrix.m[2][2] = (float) Math.cos(fAngle);
		matrix.m[3][3] = 1;
		return matrix;
	}
	
	public static Mat4x4 matrixMakeRotationY(float fAngle) {
		//in radians
		Mat4x4 matrix= new Mat4x4();
		matrix.m[0][0] = (float) Math.cos(fAngle);
		matrix.m[0][2] = (float) Math.sin(fAngle);
		matrix.m[2][0] = (float) -Math.sin(fAngle);
		matrix.m[1][1] = 1;
		matrix.m[2][2] = (float) Math.cos(fAngle);
		matrix.m[3][3] = 1;
		return matrix;
	}
	
	public static Mat4x4 matrixMakeRotationZ(float fAngle) {
		//in radians
		Mat4x4 matrix= new Mat4x4();
		matrix.m[0][0] = (float) Math.cos(fAngle);
		matrix.m[0][1] = (float) Math.sin(fAngle);
		matrix.m[1][0] = (float) -Math.sin(fAngle);
		matrix.m[1][1] = (float) Math.cos(fAngle);
		matrix.m[2][2] = 1;
		matrix.m[3][3] = 1;
		return matrix;
	}
	
	public static Mat4x4 matrixMakeTranslation(float x, float y, float z) {
		Mat4x4 matrix= new Mat4x4();
		matrix.m[0][0] = 1;
		matrix.m[1][1] = 1;
		matrix.m[2][2] = 1;
		matrix.m[3][3] = 1;
		matrix.m[3][0] = x;
		matrix.m[3][1] = y;
		matrix.m[3][2] = z;
		return matrix;
	}
	
	public static Mat4x4 matrixMakeProjection(float fFovDegrees, float fAspectRatio, float fNear, float fFar) {
		Mat4x4 matrix= new Mat4x4();
		float fFovRad=(float) (1.0f/Math.tan(fFovDegrees*0.5f/180.0f*Math.PI));
		matrix.m[0][0] = fAspectRatio * fFovRad;
		matrix.m[1][1] = fFovRad;
		matrix.m[2][2] = fFar / (fFar - fNear);
		matrix.m[3][2] = (-fFar * fNear) / (fFar - fNear);
		matrix.m[2][3] = 1.0f;
		matrix.m[3][3] = 0.0f;
		return matrix;
	}
	
	public static Mat4x4 matrixMultiplyMatrix(Mat4x4 m1, Mat4x4 m2) {
		Mat4x4 matrix= new Mat4x4();
		for (int i=0; i<4;i++) {
			for (int j=0; j<4;j++) {
				matrix.m[j][i]=m1.m[j][0] * m2.m[0][i] + m1.m[j][1] * m2.m[1][i] + m1.m[j][2] * m2.m[2][i] + m1.m[j][3] * m2.m[3][i];
			}
		}
		return matrix;
	}
	
	public static Mat4x4 matrixPointAt(Vec3D pos, Vec3D target, Vec3D up) {
		// Calculate new forward direction
		Vec3D newForward = Vec3D.vectorSub(target, pos);
		newForward = Vec3D.vectorNormalise(newForward);

		// Calculate new Up direction
		Vec3D a = Vec3D.vectorMul(newForward, Vec3D.vectorDotProduct(up, newForward));
		Vec3D newUp = Vec3D.vectorSub(up, a);
		newUp = Vec3D.vectorNormalise(newUp);

		// New Right direction is easy, its just cross product
		Vec3D newRight = Vec3D.vectorCrossProduct(newUp, newForward);

		// Construct Dimensioning and Translation Matrix	
		Mat4x4 matrix= new Mat4x4();
		matrix.m[0][0] = newRight.x;	matrix.m[0][1] = newRight.y;	matrix.m[0][2] = newRight.z;	matrix.m[0][3] = 0.0f;
		matrix.m[1][0] = newUp.x;		matrix.m[1][1] = newUp.y;		matrix.m[1][2] = newUp.z;		matrix.m[1][3] = 0.0f;
		matrix.m[2][0] = newForward.x;	matrix.m[2][1] = newForward.y;	matrix.m[2][2] = newForward.z;	matrix.m[2][3] = 0.0f;
		matrix.m[3][0] = pos.x;			matrix.m[3][1] = pos.y;			matrix.m[3][2] = pos.z;			matrix.m[3][3] = 1.0f;
		return matrix;
	}
	
	public static Mat4x4 matrixQuickInverse(Mat4x4 m) // Only for Rotation/Translation Matrices
	{
		Mat4x4 matrix=new Mat4x4();
		matrix.m[0][0] = m.m[0][0]; matrix.m[0][1] = m.m[1][0]; matrix.m[0][2] = m.m[2][0]; matrix.m[0][3] = 0.0f;
		matrix.m[1][0] = m.m[0][1]; matrix.m[1][1] = m.m[1][1]; matrix.m[1][2] = m.m[2][1]; matrix.m[1][3] = 0.0f;
		matrix.m[2][0] = m.m[0][2]; matrix.m[2][1] = m.m[1][2]; matrix.m[2][2] = m.m[2][2]; matrix.m[2][3] = 0.0f;
		matrix.m[3][0] = -(m.m[3][0] * matrix.m[0][0] + m.m[3][1] * matrix.m[1][0] + m.m[3][2] * matrix.m[2][0]);
		matrix.m[3][1] = -(m.m[3][0] * matrix.m[0][1] + m.m[3][1] * matrix.m[1][1] + m.m[3][2] * matrix.m[2][1]);
		matrix.m[3][2] = -(m.m[3][0] * matrix.m[0][2] + m.m[3][1] * matrix.m[1][2] + m.m[3][2] * matrix.m[2][2]);
		matrix.m[3][3] = 1.0f;
		return matrix;
	}
	
	public static Vec3D multiplyMatrixVector(Vec3D vi, Mat4x4 matrix) {
		Vec3D v = new Vec3D(0,0,0);
		v.x = vi.x * matrix.m[0][0] + vi.y * matrix.m[1][0] + vi.z * matrix.m[2][0] + vi.w * matrix.m[3][0];
		v.y = vi.x * matrix.m[0][1] + vi.y * matrix.m[1][1] + vi.z * matrix.m[2][1] + vi.w * matrix.m[3][1];
		v.z = vi.x * matrix.m[0][2] + vi.y * matrix.m[1][2] + vi.z * matrix.m[2][2] + vi.w * matrix.m[3][2];
		v.w = vi.x * matrix.m[0][3] + vi.y * matrix.m[1][3] + vi.z * matrix.m[2][3] + vi.w * matrix.m[3][3];
		
		return v;
	}


}