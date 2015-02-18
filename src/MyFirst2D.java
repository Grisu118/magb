// -----------  Minimales 2D JOGL-Programm  ------------------------------
//
import javax.media.opengl.*;
public class MyFirst2D extends GLMinimal
{

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