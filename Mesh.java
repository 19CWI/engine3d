package structures;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;
import java.util.Vector;

public class Mesh {
	
	public TreeMap<Float, Triangles> v= new TreeMap<Float,Triangles>();
//	private Comparator<Triangles> c= new Comparator<Triangles>() {
//		@Override
//		public int compare(Triangles t1, Triangles t2) {
//			if((t1.point[0].z+t1.point[0].z+t1.point[0].z)/3.0f < (t2.point[0].z+t2.point[0].z+t2.point[0].z)/3.0f) {
//				return -1;
//			} else if ((t1.point[0].z+t1.point[0].z+t1.point[0].z)/3.0f > (t2.point[0].z+t2.point[0].z+t2.point[0].z)/3.0f){
//				return 1;
//			}
//			return 0;
//		}
//	};
	
	
	public Mesh() {
		
	}
	
	public void sortBackFront() {
		
	}
	
	public void parseFile(String path)  {
		Vector<Vec3D> points= new Vector<Vec3D>();
		File file = new File(path);
		if(file.exists() && !file.isDirectory()) {
			  BufferedReader br = null;
			try {
				br = new BufferedReader(new FileReader(file));
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
			  String st = null; 
			  while (true) {
				  try {
					st = br.readLine();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				  if(st == null) {
					  break;
				  }else {
					  if(st.startsWith("v ")) { //vertices
						  String withoutV = st.substring(2, st.length());
						  String[] split = withoutV.split(" ");
						  float x = Float.parseFloat(split[0].trim())*10f;
						  float y = Float.parseFloat(split[1].trim())*10f;
						  float z = Float.parseFloat(split[2].trim())*10f;
						  
						  Vec3D vec = new Vec3D(x,y,z);
						  points.add(vec);
						  
					  }else if(st.startsWith("f ")) { //faces
						  String withoutV = st.substring(2, st.length());
						  String[] split = withoutV.split(" ");
						  int p1 = Integer.parseInt(split[0].trim())-1;
						  int p2 = Integer.parseInt(split[1].trim())-1;
						  int p3 = Integer.parseInt(split[2].trim())-1;
						  
						  Triangles tris = new Triangles(points.get(p1),points.get(p2),points.get(p3));
						  float h=0.0f;
						  while (true) {
							  if (!v.containsKey(tris.getCentroid().z+h)) {
								  v.put(tris.getCentroid().z+h,tris);
								  break;
							  }
							  h+=0.000001f;
						  }
					  }
				  }
			  }
			  try {
				br.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}else {
			try {
				throw new Exception("file does not exist");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	/*public Vector<Triangles> visibleTris() {
		for (int i=0;i<v.size();i++) {
			
		}
		
		return null;
	}*/
	
}