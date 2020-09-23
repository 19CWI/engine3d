package main;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferStrategy;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import mechanics.Keys;
import mechanics.Mouse;
import mechanics.Robo;
import structures.Vec3D;
import structures.Mat4x4;
import structures.Mesh;
import structures.Triangles;


public class Main  extends Canvas implements Runnable{

	private Thread thread;
	boolean running=false;
	private Robo robo;
	private Mouse mouse;
	
	public boolean changeAngle=false;
	float velx=0,vely=0,velz=0;
	
	private Vec3D vCamera = new Vec3D(0,0,0),vCameraRay = new Vec3D(0,0,0),vLookDir = new Vec3D(0,0,0), vUp = new Vec3D(0,1,0), vTarget=new Vec3D(0,0,1), lightDirection= new Vec3D(0,0,0);
	private Triangles triProjected=new Triangles(new Vec3D(0, 0, 0), new Vec3D(0, 0, 0), new Vec3D(0, 0, 0)),
			triTransformed=new Triangles(new Vec3D(0, 0, 0), new Vec3D(0, 0, 0), new Vec3D(0, 0, 0)), 
			triViewed=new Triangles(new Vec3D(0, 0, 0), new Vec3D(0, 0, 0), new Vec3D(0, 0, 0)); 
	private ArrayList<Triangles> listTriangles=new ArrayList<Triangles>();
	private Mesh mesh= new Mesh(),trisToRaster = new Mesh();
	float zfTheta=0, xfTheta=0, yfTheta=0, fyaw=0, fpitch=0, ftempY=0, ftempP=0;
	private Mat4x4 matproj=new Mat4x4(), matRotZ=new Mat4x4(), matRotX=new Mat4x4(), matRotY=new Mat4x4(), matTrans= new Mat4x4(), matWorld= new Mat4x4(),
			matCameraRot=new Mat4x4(), matCamera=new Mat4x4(), matView=new Mat4x4();
	
	
	public static void main(String[] args) {
		OpenWindow w=new OpenWindow("Cube projection", new Main());
	}
	public synchronized void start() {
		if(running) {
			return;
		}
		running=true;
		thread=new Thread(this);
		thread.start();
	}

	public void render() {
		BufferStrategy bs= this.getBufferStrategy();
		if(bs==null) {
			this.createBufferStrategy(3);
			return;
		}
		Graphics g=bs.getDrawGraphics();
		
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, getWidth(), getHeight());
		
		//world transform
		//rotates
		matRotZ = Mat4x4.matrixMakeRotationZ(zfTheta);
		matRotX = Mat4x4.matrixMakeRotationX(xfTheta);
		matRotY = Mat4x4.matrixMakeRotationY(yfTheta);
		
		matTrans = Mat4x4.matrixMakeTranslation(0, 0, 0);
		
		matWorld= Mat4x4.matrixMakeIdentity();
		matWorld= Mat4x4.matrixMultiplyMatrix(matRotZ, matRotX);
		matWorld= Mat4x4.matrixMultiplyMatrix(matWorld, matRotY);
		matWorld= Mat4x4.matrixMultiplyMatrix(matWorld, matTrans); 
		
		//point at matrix for camera
		matCameraRot = Mat4x4.matrixMakeRotationY(fyaw);
		matCameraRot = Mat4x4.matrixMultiplyMatrix(matCameraRot, Mat4x4.matrixMakeRotationX(fpitch));
		vLookDir = Mat4x4.multiplyMatrixVector(vTarget, matCameraRot);
		vTarget = Vec3D.vectorAdd(vCamera, vLookDir);
		matCamera = Mat4x4.matrixPointAt(vCamera, vTarget, vUp);
		
		//System.out.println("vLookDir x: "+vLookDir.x+" y: "+vLookDir.y+" z: "+vLookDir.z);
		
		//view matrix for camera
		matView = Mat4x4.matrixQuickInverse(matCamera);
		
		
		trisToRaster = new Mesh();			

		Object[] index = mesh.v.keySet().toArray();
		
		
		//loops through and transforms triangles before rastering
		for (int j=0;j<mesh.v.size();j++) {
			
			for (int i=0; i<3; i++) {
				triTransformed.point[i] = Mat4x4.multiplyMatrixVector(mesh.v.get(index[j]).point[i], matWorld);
			}
		
			//TODO change frame of reference with rotation as to deal with multiple rotations and translations//rotate translation vector
			
			//checks similarity of normal to camera
			Vec3D normal= new Vec3D(0,0,0), line1 = new Vec3D(0,0,0), line2= new Vec3D(0,0,0);
			
			line1 = Vec3D.vectorSub(triTransformed.point[1], triTransformed.point[0]);
			line2 = Vec3D.vectorSub(triTransformed.point[2], triTransformed.point[0]);
			normal = Vec3D.vectorCrossProduct(line1, line2);
			normal = Vec3D.vectorNormalise(normal);
			
			vCameraRay = Vec3D.vectorSub(triTransformed.point[0], vCamera);
			
			
			if(Vec3D.vectorDotProduct(normal, vCameraRay)<0){
				
				//illumination
				lightDirection.x=0.0f;lightDirection.y=-0.5f;lightDirection.z=-0.5f;
				lightDirection = Vec3D.vectorNormalise(lightDirection);
				lightDirection = Vec3D.vectorAdd(lightDirection,Vec3D.vectorNormalise(vCamera));
				lightDirection = Vec3D.vectorNormalise(lightDirection);
				
				//alignment of light direction with surface of the normal
				float dp = Math.max(0.01f,Vec3D.vectorDotProduct(lightDirection, normal));
				
				Color c =  triTransformed.color;
				//Color c = new Color(50,50,50);
				float[] HSB = Color.RGBtoHSB(c.getRed(), c.getGreen(), c.getBlue(), null);
				c= Color.getHSBColor(HSB[0], HSB[1], (float) (dp));
				
				//converts world space to view space
				for(int i=0;i<3;i++) {
					triViewed.point[i] = Mat4x4.multiplyMatrixVector(triTransformed.point[i], matView);
				}
				triViewed.color=c;
				
				//clip viewed triangles against near plane, could form two additional triangles
				int nClippedTriangles=0;
				Triangles[] clipped = {new Triangles(new Vec3D(0,0,0), new Vec3D(0,0,0), new Vec3D(0,0,0)),
						new Triangles(new Vec3D(0,0,0), new Vec3D(0,0,0), new Vec3D(0,0,0))};
				nClippedTriangles = Triangles.trianglesClipAgainstPlane(new Vec3D(0,0,-1.0f), new Vec3D(0,0,-1.0f), triViewed, clipped[0], clipped[1]);
				
				//variable number of triangles so project as required
				for(int n=0; n<nClippedTriangles; n++) {
					
					
					float x1,x2,x3,y1,y2,y3,z1,z2,z3;
					x1=clipped[n].point[0].x;
					y1=clipped[n].point[0].y;
					z1=clipped[n].point[0].z;
					x2=clipped[n].point[1].x;
					y2=clipped[n].point[1].y;
					z2=clipped[n].point[1].z;
					x3=clipped[n].point[2].x;
					y3=clipped[n].point[2].y;
					z3=clipped[n].point[2].z;
					
					float h=0.0f;
					while (true) {
						if (!trisToRaster.v.containsKey(clipped[n].getCentroid().z+h)) {
							trisToRaster.v.put(clipped[n].getCentroid().z+h, new Triangles(new Vec3D(x1, y1, z1), new Vec3D(x2, y2, z2), new Vec3D(x3, y3, z3)));
							trisToRaster.v.get(clipped[n].getCentroid().z+h).color=c;
							break;
						}
						h+=0.000001f;
					}
				}
			}
		}
		//System.out.println(trisToRaster.v.size());
		
		index= trisToRaster.v.keySet().toArray();
		for(int j=0; j<trisToRaster.v.size();j++) {
			
			//project 3D to 2D
			for(int i=0;i<3;i++) {
				triProjected.point[i] = Mat4x4.multiplyMatrixVector(trisToRaster.v.get(index[j]).point[i], matproj);
			}
			triProjected.color = trisToRaster.v.get(index[j]).color;
			
			//scale into view
			for(int i=0;i<3;i++) {
				triProjected.point[i]=Vec3D.vectorDiv(triProjected.point[i], triProjected.point[i].w);
			}
			
			//x,y are inverted so flip em back
			for(int i=0;i<3;i++) {
				triProjected.point[i].x*=-1;
				triProjected.point[i].y*=-1;
			}
			
			//offset verts into visible normalised space
			Vec3D vOffsetView= new Vec3D(1,1,0);
			for(int i=0;i<3;i++) {
				triProjected.point[i]=Vec3D.vectorAdd(triProjected.point[i], vOffsetView);
			}
			for(int i=0;i<3;i++) {
				triProjected.point[i].x *= 0.5f * (float)getWidth();
				triProjected.point[i].y *= 0.5f * (float)getHeight();
			}
				
			Triangles[] clipped= {new Triangles(new Vec3D(0,0,0), new Vec3D(0,0,0), new Vec3D(0,0,0)),
					new Triangles(new Vec3D(0,0,0), new Vec3D(0,0,0), new Vec3D(0,0,0))};
			listTriangles=new ArrayList<Triangles>();

			// Add initial triangle
			listTriangles.add(triProjected);
			int nNewTriangles = 1;

			for (int p = 0; p < 4; p++){
				int nTrisToAdd = 0;
				while (nNewTriangles > 0){
					// Take triangle from front of queue
					Triangles test = listTriangles.get(0);
					listTriangles.remove(0);
					nNewTriangles--;

					// Clip it against a plane. We only need to test each 
					// subsequent plane, against subsequent new triangles
					// as all triangles after a plane clip are guaranteed
					// to lie on the inside of the plane. I like how this
					// comment is almost completely and utterly justified
					switch (p)
					{
					case 0:	nTrisToAdd = Triangles.trianglesClipAgainstPlane(new Vec3D(0.0f, 100.0f, 0.0f ), new Vec3D(0.0f, 1.0f, 0.0f), test, clipped[0], clipped[1]); 
						break;
					case 1:	nTrisToAdd = Triangles.trianglesClipAgainstPlane(new Vec3D( 0.0f, getHeight() - 100, 0.0f ), new Vec3D( 0.0f, -1.0f, 0.0f ), test, clipped[0], clipped[1]);
						break;
					case 2:	nTrisToAdd = Triangles.trianglesClipAgainstPlane(new Vec3D(100.0f, 0.0f, 0.0f ), new Vec3D( 1.0f, 0.0f, 0.0f ), test, clipped[0], clipped[1]); 
						break;
					case 3:	nTrisToAdd = Triangles.trianglesClipAgainstPlane(new Vec3D( getWidth() - 100, 0.0f, 0.0f ), new Vec3D( -1.0f, 0.0f, 0.0f ), test, clipped[0], clipped[1]); 
						break;
					}

					// Clipping may yield a variable number of triangles, so
					// add these new ones to the back of the queue for subsequent
					// clipping against next planes
					for (int w = 0; w < nTrisToAdd; w++) {
						float x1,x2,x3,y1,y2,y3,z1,z2,z3;
						x1=clipped[w].point[0].x;
						y1=clipped[w].point[0].y;
						z1=clipped[w].point[0].z;
						x2=clipped[w].point[1].x;
						y2=clipped[w].point[1].y;
						z2=clipped[w].point[1].z;
						x3=clipped[w].point[2].x;
						y3=clipped[w].point[2].y;
						z3=clipped[w].point[2].z;
						listTriangles.add(new Triangles(new Vec3D(x1, y1, z1), new Vec3D(x2, y2, z2), new Vec3D(x3, y3, z3)));
					}
				}
				nNewTriangles = listTriangles.size();
			}
			
			for(int k=0;k<listTriangles.size();k++) {
				
				Triangles triRaster= listTriangles.get(k);
				
				//converts x,y cords into arrays for fillpolygon
				int[] xPoints=new int[3], yPoints=new int[3];
				for(int i=0;i<3;i++) {
					xPoints[i]=(int) triRaster.point[i].x;
					yPoints[i]=(int) triRaster.point[i].y;
				}
				
				g.setColor(triProjected.color);
				g.fillPolygon(xPoints, yPoints, 3);
				
				g.setColor(Color.WHITE);
				//draws triangle wireframe
//				g.drawLine((int)triRaster.point[0].x, (int)triRaster.point[0].y, (int)triRaster.point[1].x, (int)triRaster.point[1].y);
//				g.drawLine((int)triRaster.point[1].x, (int)triRaster.point[1].y, (int)triRaster.point[2].x, (int)triRaster.point[2].y);
//				g.drawLine((int)triRaster.point[2].x, (int)triRaster.point[2].y, (int)triRaster.point[0].x, (int)triRaster.point[0].y);
			}
			
		}
		g.dispose();
		bs.show();
			
	}

	public void init() {
		
		//x,y,z points
		Vec3D vec1 = new Vec3D(0,0,10);
		Vec3D vec2 = new Vec3D(1,0,10);
		Vec3D vec3 = new Vec3D(0,1,10);
		Vec3D vec4 = new Vec3D(0,0,11);
		Vec3D vec5 = new Vec3D(0,1,11);
		Vec3D vec6 = new Vec3D(1,0,11);
		Vec3D vec7 = new Vec3D(1,1,10);
		Vec3D vec8 = new Vec3D(1,1,11);
		
		//triangles of cube
		//south
		Triangles tri1 = new Triangles(vec1,vec3,vec7);
		Triangles tri2 = new Triangles(vec1,vec7,vec2);
		//north
		Triangles tri3 = new Triangles(vec6,vec8,vec5);
		Triangles tri4 = new Triangles(vec6,vec5,vec4);
		//east
		Triangles tri5 = new Triangles(vec2,vec7,vec8);
		Triangles tri6 = new Triangles(vec2,vec8,vec6);
		//west
		Triangles tri7 = new Triangles(vec4,vec5,vec3);
		Triangles tri8 = new Triangles(vec4,vec3,vec1);
		//top
		Triangles tri9 = new Triangles(vec3,vec5,vec8);
		Triangles tri10 = new Triangles(vec3,vec8,vec7);
		//bottom
		Triangles tri11 = new Triangles(vec4,vec1,vec2);
		Triangles tri12 = new Triangles(vec4,vec2,vec6);
		
		float h=0;
		mesh.v.put(tri1.getCentroid().z, tri1);
		while (true) {
			if (!mesh.v.containsKey(tri2.getCentroid().z+h)) {
				mesh.v.put(tri2.getCentroid().z+h, tri2);
				break;
			}
			h+=0.000001f;
		}
		while (true) {
			if (!mesh.v.containsKey(tri3.getCentroid().z+h)) {
				mesh.v.put(tri3.getCentroid().z+h, tri3);
				break;
			}
			h+=0.000001f;
		}
		while (true) {
			if (!mesh.v.containsKey(tri4.getCentroid().z+h)) {
				mesh.v.put(tri4.getCentroid().z+h, tri4);
				break;
			}
			h+=0.000001f;
		}
		while (true) {
			if (!mesh.v.containsKey(tri5.getCentroid().z+h)) {
				mesh.v.put(tri5.getCentroid().z+h, tri5);
				break;
			}
			h+=0.000001f;
		}
		while (true) {
			if (!mesh.v.containsKey(tri6.getCentroid().z+h)) {
				mesh.v.put(tri6.getCentroid().z+h, tri6);
				break;
			}
			h+=0.000001f;
		}
		while (true) {
			if (!mesh.v.containsKey(tri7.getCentroid().z+h)) {
				mesh.v.put(tri7.getCentroid().z+h, tri7);
				break;
			}
			h+=0.000001f;
		}
		while (true) {
			if (!mesh.v.containsKey(tri8.getCentroid().z+h)) {
				mesh.v.put(tri8.getCentroid().z+h, tri8);
				break;
			}
			h+=0.000001f;
		}
		while (true) {
			if (!mesh.v.containsKey(tri9.getCentroid().z+h)) {
				mesh.v.put(tri9.getCentroid().z+h, tri9);
				break;
			}
			h+=0.000001f;
		}
		while (true) {
			if (!mesh.v.containsKey(tri10.getCentroid().z+h)) {
				mesh.v.put(tri10.getCentroid().z+h, tri10);
				break;
			}
			h+=0.000001f;
		}
		while (true) {
			if (!mesh.v.containsKey(tri11.getCentroid().z+h)) {
				mesh.v.put(tri11.getCentroid().z+h, tri11);
				break;
			}
			h+=0.000001f;
		}
		while (true) {
			if (!mesh.v.containsKey(tri12.getCentroid().z+h)) {
				mesh.v.put(tri12.getCentroid().z+h, tri12);
				break;
			}
			h+=0.000001f;
		}
		
		mesh.parseFile("C:\\Users\\User\\3D Objects\\blender OBJs\\stanfordBunny.obj");
		
		matproj = Mat4x4.matrixMakeProjection(90.0f, (float)getHeight()/(float)getWidth(),0.1f, 1000.0f);
		
		this.requestFocus();
		
		robo = new Robo(this);
		mouse =new Mouse(robo,this);
		this.addKeyListener(new Keys(this));
		this.addMouseListener(mouse);
		this.addMouseMotionListener(mouse);

	}

	public void run() {
		init();
		long lastTime=System.nanoTime();
		double amountOfTicks=60.0;
		double ns=1000000000/amountOfTicks;
		double delta=0;
		long timer=System.currentTimeMillis();
		int updates=0;
		int frames= 0;
		while(running) {
			long now=System.nanoTime();
			delta+=(now-lastTime)/ns;
			lastTime=now;
			while(delta>=1) {
				tick();
				updates++;
				delta--;
			}
			render();
			frames++;
			
			if(System.currentTimeMillis()-timer>1000) {
				timer+=1000;
				System.out.println("FPS:"+frames+ " TICKS: "+updates);
				frames=0;
				updates=0;
			}
		}
	}

	
	public void tick() {
		//angle changes on time
		if(changeAngle) {
			xfTheta+=0.03;
		}
		//TODO have the angles rotation about the player change based on mouse movement
		if(ftempY==fyaw) {
			fyaw=0;
		}
		ftempY=fyaw;
		if(ftempP==fpitch) {
			fpitch=0;
		}
		ftempP=fpitch;
		
//		vCamera.x+=velx;
//		vCamera.y+=vely;
//		vCamera.z+=velz;
		Vec3D vVel = new Vec3D(velx,vely,velz);
		vVel = Mat4x4.multiplyMatrixVector(vVel, matCameraRot);
		vCamera = Vec3D.vectorAdd(vCamera, vVel);
	}
	
	
	public void setVelx(float velx) {
		this.velx = velx;
	}
	public void setVely(float vely) {
		this.vely = vely;
	}
	public void setVelz(float velz) {
		this.velz = velz;
	}
	public float getVelx() {
		return velx;
	}
	public float getVely() {
		return vely;
	}
	public float getVelz() {
		return velz;
	}
	public boolean isChangeAngle() {
		return changeAngle;
	}
	public void setChangeAngle(boolean changeAngle) {
		this.changeAngle = changeAngle;
	}
	public void setPitch(float pitch) {
		this.fpitch += pitch;
	}
	public void setYaw(float yaw) {
		this.fyaw += yaw;
	}
	public void setXRot(float xTheta) {
		this.xfTheta += xTheta;
	}
	public void setZRot(float zTheta) {
		this.zfTheta += zTheta;
	}
	public void setYRot(float yTheta) {
		this.yfTheta += yTheta;
	}
	
}
