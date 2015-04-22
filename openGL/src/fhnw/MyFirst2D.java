package fhnw;// -----------  Minimales 2D JOGL-Programm  ------------------------------
//
import javax.media.opengl.*;
public class MyFirst2D extends GLMinimal
{

    float left = -1;
    float right = 1;
    float bottom;
    float top;
    float near = -100;
    float far = 100;

   void zeichneDreieck(GL4 gl)
   {  vertexBuf.rewind();
      setColor(1,0,0,1);          // Zeichenfarbe (ROT)
      putVertex(-0.5f,-0.5f,0);     // Eckpunkte in VertexArray speichern
      putVertex(0.5f,-0.5f,0);
      putVertex(0,0.5f,0);
      int nVertices = 3;
      copyBuffer(gl,nVertices);   // VertexArray in OpenGL-Buffer kopieren
      gl.glDrawArrays(GL4.GL_TRIANGLES,0,nVertices);  // Dreieck zeichnen
   }

    @Override
    public void reshape(GLAutoDrawable drawable, int x, int y,
                        int width, int height)
    {  GL4 gl = drawable.getGL().getGL4();
        // Set the viewport to be the entire window
        gl.glViewport(0, 0, width, height);
        float aspect = (float)height/width;
        top = right * aspect;
        bottom = left * aspect;
        setProjection(gl, left, right, bottom, top, near, far);
    }

    @Override
    public void display(GLAutoDrawable drawable)          //  render image
    {  GL4 gl = drawable.getGL().getGL4();
       gl.glClear(GL4.GL_COLOR_BUFFER_BIT | GL4.GL_DEPTH_BUFFER_BIT);
       setColor(0,1,1,1);
       zeichneDreieck(gl);
    }

    //  -----------  main-Methode  ---------------------------

    public static void main(String[] args)
    { MyFirst2D sample = new MyFirst2D();
    }

}