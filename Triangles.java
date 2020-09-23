package structures;

import java.awt.Color;

public class Triangles {

	//float[] triangles;
	public Vec3D[] point = {new Vec3D(0.0f,0.0f,0.0f),new Vec3D(0.0f,0.0f,0.0f),new Vec3D(0.0f,0.0f,0.0f)};
	public Color color;
	
	public Triangles(Vec3D vec1,Vec3D vec2,Vec3D vec3) {
		this.color=Color.CYAN;
		
		point[0]=vec1;
		point[1]=vec2;
		point[2]=vec3;
		
		//triangles[0]=vec1.x;
		//triangles[1]=vec1.y;
		//triangles[2]=vec1.z;
		
	}
	
	public void setColor(Color c) {
		this.color=c;
	}

	public Vec3D[] getPoint() {
		return point;
	}
	
	public Vec3D getCentroid() {
		Vec3D centroid = new Vec3D((point[0].x+point[1].x+point[2].x)/3.0f, (point[0].y+point[1].y+point[2].y)/3.0f, (point[0].z+point[1].z+point[2].z)/3.0f);
		return centroid;
	}
	
	public static int trianglesClipAgainstPlane(Vec3D plane_p, Vec3D plane_n, Triangles in_tri, Triangles out_tri1, Triangles out_tri2) {
		// Make sure plane normal is indeed normal
		plane_n = Vec3D.vectorNormalise(plane_n);

		// Create two temporary storage arrays to classify points either side of plane
		// If distance sign is positive, point lies on "inside" of plane
		Vec3D[] inside_points = new Vec3D[3];
		int nInsidePointCount = 0;
		Vec3D[] outside_points = new Vec3D[3];
		int nOutsidePointCount = 0;

		// Get signed distance of each point in triangle to plane
		float d0 = dist(in_tri.point[0], plane_n, plane_p);
		float d1 = dist(in_tri.point[1], plane_n, plane_p);
		float d2 = dist(in_tri.point[2], plane_n, plane_p);

		//System.out.println(d0 + "\n" + d1 + "\n" + d2);

		if (d0 >= 0) {
			inside_points[nInsidePointCount] = in_tri.point[0];
			nInsidePointCount++;
		} else {
			outside_points[nOutsidePointCount] = in_tri.point[0];
			nOutsidePointCount++;
		}
		if (d1 >= 0) {
			inside_points[nInsidePointCount] = in_tri.point[1];
			nInsidePointCount++;
		} else {
			outside_points[nOutsidePointCount] = in_tri.point[1];
			nOutsidePointCount++;
		}
		if (d2 >= 0) {
			inside_points[nInsidePointCount] = in_tri.point[2];
			nInsidePointCount++;
		} else {
			outside_points[nOutsidePointCount] = in_tri.point[2];
			nOutsidePointCount++;
		}

		// Now classify triangle points, and break the input triangle into
		// smaller output triangles if required. There are four possible
		// outcomes...

		if (nInsidePointCount == 0) {
			// All points lie on the outside of plane, so clip whole triangle
			// It ceases to exist

			return 0; // No returned triangles are valid
		}

		if (nInsidePointCount == 3) {
			// All points lie on the inside of plane, so do nothing
			// and allow the triangle to simply pass through
			out_tri1.point[0] = inside_points[0];
			out_tri1.point[1] = inside_points[1];
			out_tri1.point[2] = inside_points[2];
			
			out_tri1.color=in_tri.color;

			return 1; // Just the one returned original triangle is valid
		}

		if (nInsidePointCount == 1 && nOutsidePointCount == 2) {
			// Triangle should be clipped. As two points lie outside
			// the plane, the triangle simply becomes a smaller triangle

			// Copy appearance info to new triangle
			//out_tri1.color = in_tri.color;
			out_tri1.setColor(Color.RED);;

			// The inside point is valid, so keep that...
			out_tri1.point[0] = inside_points[0];

			// but the two new points are at the locations where the
			// original sides of the triangle (lines) intersect with the plane
			out_tri1.point[1] = Vec3D.vectorIntersectPlane(plane_p, plane_n, inside_points[0], outside_points[0]);
			out_tri1.point[2] = Vec3D.vectorIntersectPlane(plane_p, plane_n, inside_points[0], outside_points[1]);

			return 1; // Return the newly formed single triangle
		}

		if (nInsidePointCount == 2 && nOutsidePointCount == 1) {
			// Triangle should be clipped. As two points lie inside the plane,
			// the clipped triangle becomes a "quad". Fortunately, we can
			// represent a quad with two new triangles

			// Copy appearance info to new triangles
			out_tri1.setColor(Color.PINK);;

			out_tri2.setColor(Color.BLUE);

			// The first triangle consists of the two inside points and a new
			// point determined by the location where one side of the triangle
			// intersects with the plane
			out_tri1.point[0] = inside_points[0];
			out_tri1.point[1] = inside_points[1];
			out_tri1.point[2] = Vec3D.vectorIntersectPlane(plane_p, plane_n, inside_points[0], outside_points[0]);

			// The second triangle is composed of one of he inside points, a
			// new point determined by the intersection of the other side of the
			// triangle and the plane, and the newly created point above
			out_tri2.point[0] = inside_points[1];
			out_tri2.point[1] = Vec3D.vectorIntersectPlane(plane_p, plane_n, inside_points[0], outside_points[0]);
			out_tri2.point[2] = Vec3D.vectorIntersectPlane(plane_p, plane_n, inside_points[1], outside_points[0]);

			return 2; // Return two newly formed triangles which form a quad
		}
		System.out.println(" no case");
		return 0;
	}

	private static float dist(Vec3D p, Vec3D plane_n, Vec3D plane_p) {
		// Return signed shortest distance from point to plane, plane normal must be

		return (plane_n.x * p.x + plane_n.y * p.y + plane_n.z * p.z - Vec3D.vectorDotProduct(plane_n, plane_p));

	}
	
}