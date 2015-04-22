package fhnw.magb;// -----------  LookAt-Test  (Quadrate entlang einer Geraden)  ------------------------------
import javax.media.opengl.*;

import fhnw.GLMinimal;
import ch.fhnw.util.math.*;
import fhnw.util.math.Vec3;

public class LookAtTest extends GLMinimal
{
    // --------------------  Globale Daten  ------------------------------

    float r=10, elevation=10, azimut=20;        // Lage des Kamera-Systems
    float left = -5, right = 5;                 // ViewingVolume im KameraSystem
    float bottom, top;
    float near = -10, far = 100;


    // -----------------------------  Methoden  --------------------------------

    void zeichneQuadrat(GL4 gl, float a)
    {  vertexBuf.rewind();
       putVertex(-a,-a,0);     // Eckpunkte
       putVertex(a,-a,0);
       putVertex(a,a,0);
       putVertex(-a,a,0);
       int nVertices = 4;
       copyBuffer(gl,nVertices);
       gl.glDrawArrays(GL4.GL_LINE_LOOP, 0, nVertices);
    }


    @Override
    public void display(GLAutoDrawable drawable)
    { GL4 gl = drawable.getGL().getGL4();
      gl.glClear(GL4.GL_COLOR_BUFFER_BIT | GL4.GL_DEPTH_BUFFER_BIT);
  //    setCameraSystem(gl, r, elevation, azimut);

      Vec3 A = new Vec3(0.6643f,0.5f,-0.5567f);    // LookAt-Parameter
      Vec3 B = new Vec3(0,0,0);
      Vec3 up = new Vec3(0,1,0);
      setCameraSystem(gl, A, B, up);

      setColor(0, 1, 1, 1);
      zeichneAchsen(gl, 4, 4, 4);
      setColor(1, 1, 0, 1);

      rotate(gl, 131.8141f, new Vec3(-0.4663f, 3.7321f, 1.0000f));
      for (int i=0; i < 6; i++)
      {  zeichneQuadrat(gl,0.5f);
         translate(gl,new Vec3(0,0,1));
      }
    }


    @Override
    public void reshape(GLAutoDrawable drawable, int x, int y,
                        int width, int height)
    {  GL4 gl = drawable.getGL().getGL4();
       // Set the viewport to be the entire window
       gl.glViewport(0, 0, width, height);
       float aspect = (float)height / width;
       bottom = aspect * left;
       top = aspect * right;
       // Set ViewingVolume for Orthogonalprojection
       setProjection(gl,left,right,bottom,top,near,far);
    }


    //  -----------  main-Methode  ---------------------------

    public static void main(String[] args)
    { LookAtTest sample = new LookAtTest();
    }

}